package jbr.intern.codesummary.methods

import kotlin.math.min

import java.io.File
import java.time.Period
import java.lang.Exception
import java.time.LocalDate
import java.time.LocalTime

import jbr.intern.codesummary.git.GitUtils
import jbr.intern.codesummary.io.getFilesToSearch
import jbr.intern.codesummary.io.getAllNotHiddenDirs
import jbr.intern.codesummary.parsers.parseCodeTree

class MethodsFinder(val repoURL: String) {
    private val git = GitUtils()
    private val repoName = repoURL.substringAfterLast("/")
    private val mainDir = File(System.getProperty("user.dir"))
    private val reposDir = File(mainDir.absolutePath + File.separator + "git_repos")
    private val repoPath = File(reposDir.absolutePath + File.separator + repoName)
    private val dataDir = File(mainDir.absolutePath + File.separator + "results")

    private val commitsHashes = mutableListOf<String>()
    private val commitsHashesDates = mutableMapOf<String, Pair<LocalDate, LocalTime>>()
    private val data = mutableMapOf<String, MutableMap<String, MethodInfo>>()

    private val supportedExtensions = mutableMapOf<String, Language>()

    init {
        if (!reposDir.exists()) {
            reposDir.mkdir()
        }
        if (!dataDir.exists()) {
            dataDir.mkdir()
        }
        for (lang in Language.values()) {
            for (ext in lang.extensions) {
                supportedExtensions[ext] = lang
            }
        }
    }

    fun cloneRepo() {
        try {
            if (repoPath.exists()) {
                println("> repository already exists")
            } else {
                git.clone(repoURL, reposDir)
                if (repoPath.exists()) {
                    println("> cloning repository in 'git_repos' dir successful")
                }
            }
        } catch (e : Exception) {
            println("Repository was not cloned")
            e.printStackTrace()
        }
    }

    fun loadCommitsHistory() {
        git.getAllCommitsHashesAndDates(commitsHashes, commitsHashesDates, repoPath)
        commitsHashes.reverse() // reversing history
        println("> commits history collected (${commitsHashes.size} commits)")
    }

    fun searchMethods() {
        if (commitsHashes.isEmpty()) {
            println("> no commits for search")
            return
        }
        println("> methods search running ...")

        var currHash = commitsHashes.first()
        git.checkoutHash(currHash, repoPath)
        handleFirstCommit()

        for (i in 1 until commitsHashes.size) {
            val prevHash = currHash
            currHash = commitsHashes[i]
            val commitsFilesDiff = git.getFilesNameOnlyDiffTwoCommits(prevHash, currHash, repoPath)
            if (commitsFilesDiff.isEmpty()) {
                continue
            }
            // get supported diff part on PREV commit
            var supportedFiles = getSupportedFiles(commitsFilesDiff.map { File(repoPath.absolutePath + File.separator + it) })
            // get supported diff part on CURR commit
            git.checkoutHash(currHash, repoPath)
            supportedFiles = getSupportedFiles(commitsFilesDiff.map { File(repoPath.absolutePath + File.separator + it) })
            scanFiles(currHash, supportedFiles)

            doneMetric(i, commitsHashes.size)
        }
        println("> methods search done")
    }

    fun dumpDataToJSON() {
        val dump = mutableListOf("{")
        for ((path, map) in data) {
            dump.add("\t\"$path\" : {")
            for ((method, methodData) in map) {
                dump.add("\t\t\"$method\" : ${methodData.toJSON()},")
            }
            dump[dump.size - 1] = dump.last().removeSuffix(",")
            dump.add("\t},")
        }
        dump[dump.size - 1] = dump.last().removeSuffix(",")
        dump.add("}")
        File("${dataDir.absolutePath}${File.separator}${repoName}_DATA.json")
            .writeText(dump.joinToString(separator = System.lineSeparator()))
        println("> data was dumped in 'results' dir")
    }

    fun searchTop10() {
        val list = mutableListOf<MethodInfo>()
        data.forEach {
            it.value.forEach {entry ->
                list.add(entry.value)
            }
        }
        list.sortBy {
            Period.between(it.firstObserved.first, it.lastObserved.first).days
        }
        list.reverse()
        println("TOP METHODS:")
        var maxLenght = 30
        for (i in 0 until min(10, list.size)) {
            if (list[i].name.length > maxLenght) {
                maxLenght = list[i].name.length + 8
            }
            val tabsName = " ".repeat(maxLenght - list[i].name.length)
            val daysRange = Period.between(list[i].firstObserved.first, list[i].lastObserved.first).days
            println("${list[i].firstObserved.first} -> " +
                    "${list[i].lastObserved.first}\t" +
                    "days: $daysRange${" ".repeat(7 - daysRange.toString().length)}" +
                    "name: ${list[i].name}$tabsName" +
                    "hash: ${list[i].firstObservedHash.substring(0..5)} -> ${list[i].lastObservedHash.substring(0..5)}\t\t"+
                    "path: ${list[i].projectPath}")
        }
    }

    private fun scanFileForMethods(file: File, fileLanguage: Language) : List<String> {
        return parseCodeTree(file, fileLanguage)
    }

    private fun getSupportedFiles(files: List<File>) : List<Pair<File, Language>> {
        return files.filter {
            it.exists() &&
                    !it.isHidden &&
                    !it.isDirectory &&
                    it.extension in supportedExtensions.keys
        }.map {
            Pair(it, supportedExtensions[it.extension]!!)
        }
    }

    private fun scanFiles(currHash: String, supportedFiles: List<Pair<File, Language>>) {
        for ((file, language) in supportedFiles) {
            val methodsNames = scanFileForMethods(file, language)
            for (method in methodsNames) {
                val projectPath = file.absolutePath.removePrefix(repoPath.absolutePath)
                if (projectPath !in data.keys) {
                    data[projectPath] = mutableMapOf()
                }
                if (method !in data[projectPath]!!.keys) {
                    data[projectPath]?.set(method,
                        MethodInfo(
                            method,
                            commitsHashesDates[currHash]!!,
                            commitsHashesDates[currHash]!!,
                            projectPath,
                            currHash,
                            currHash
                        )
                    )
                } else {
                    data[projectPath]?.get(method)?.lastObserved = commitsHashesDates[currHash]!!
                    data[projectPath]?.get(method)?.lastObservedHash = currHash
                }
            }
        }
    }

    private fun handleFirstCommit() {
        val firstHash = commitsHashes.first()
        val firstFiles = getFilesToSearch(getAllNotHiddenDirs(repoPath))
        val supportedFiles = getSupportedFiles(firstFiles)
        scanFiles(firstHash, supportedFiles)
    }

    fun doneMetric(i: Int, size: Int) {
        when (i) {
            size / 4 -> println("> 25% done")
            size / 2 -> println("> 50% done")
            size / 4 * 3 -> println("> 75% done")
            size / 10 * 9 -> println("> 90% done")
            size / 99 * 98 -> println("> 99% done")
        }
    }
}
