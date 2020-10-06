package jbr.intern.codesummary.git

import java.io.File
import java.io.IOException
import java.time.LocalDate
import java.time.LocalTime
import java.util.concurrent.TimeUnit

fun String.runCommand(workingDir: File): String? {
    return try {
        val parts = this.split("\\s".toRegex())
        val proc = ProcessBuilder(*parts.toTypedArray())
            .directory(workingDir)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()

        proc.waitFor(60, TimeUnit.MINUTES)
        proc.inputStream.bufferedReader().readText()
    } catch(e: IOException) {
        e.printStackTrace()
        null
    }
}

fun runCommand(commands : List<String>, workingDir: File) : String? {
    return try {
        val proc = ProcessBuilder(commands)
                .directory(workingDir)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()

        proc.waitFor(60, TimeUnit.MINUTES)
        proc.inputStream.bufferedReader().readText()
    } catch(e: IOException) {
        e.printStackTrace()
        null
    }
}

class GitUtils {

    fun clone(url: String, workingDir: File) = "git clone $url".runCommand(workingDir)

    fun status(workingDir: File) : String? = "git status".runCommand(workingDir)

    fun checkoutHash(hash: String, workingDir: File) = "git checkout $hash".runCommand(workingDir)

    fun checkoutBranch(branch: String, workingDir: File) = "git checkout $branch".runCommand(workingDir)

    fun getAllCommitsHashesAndDates(hashes: MutableList<String>,
                                    hashesDates: MutableMap<String, Pair<LocalDate, LocalTime>>,
                                    workingDir: File
    ) {
        val args = listOf("git", "log", "--pretty=format:\"%H %ad\"", "--date=iso")
        runCommand(args, workingDir)?.let {output ->
            output.split(System.lineSeparator())
                    .forEach{
                        val line = it.replace("\"", "")
                        val hashDateTimeSplit = line.split(" ")
                        val date = LocalDate.parse(hashDateTimeSplit[1])
                        val time = LocalTime.parse(hashDateTimeSplit[2])
                        hashes.add(hashDateTimeSplit[0])
                        hashesDates[hashDateTimeSplit[0]] = Pair<LocalDate, LocalTime>(date, time)
                    }
        }
    }

    fun getFilesNameOnlyDiffTwoCommits(hash1: String, hash2: String, workingDir: File) : List<String> {
        val args = listOf("git", "diff", "--name-only", hash1, hash2)
        runCommand(args, workingDir)?.let {output ->
            return output.split(System.lineSeparator())
                    .map{
                        it.replace("\"", "")
                    }
        }
        return listOf()
    }

    fun getBranchCommitsNumber(branch: String, workingDir: File): Int {
        val number = "git rev-list $branch --count".runCommand(workingDir)
        number?.let {
            return number.toInt()
        }
        return 0
    }

    fun getBranchesList(workingDir: File): List<String> {
        val branches = "git branch -a".runCommand(workingDir)
        branches?.let {
            return branches
                    .split(System.lineSeparator())
                    .filter {
                        !it.startsWith("*") && !it.contains("->")
                    }.map {
                        it.substringAfterLast("/", "")
                    }
        }
        return listOf()
    }
}