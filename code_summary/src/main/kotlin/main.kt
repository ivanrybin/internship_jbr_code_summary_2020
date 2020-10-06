import java.lang.Exception

import jbr.intern.codesummary.methods.MethodsFinder

fun main() {
    println("Input repository URL:")
    val repoURL = readLine().toString()
    try {
        val finder = MethodsFinder(repoURL)
        finder.cloneRepo()
        finder.loadCommitsHistory()
        finder.searchMethods()
        finder.dumpDataToJSON()
        finder.searchTop10()
    } catch (e : Exception) {
        println("> something went wrong")
        e.printStackTrace()
    }
}
