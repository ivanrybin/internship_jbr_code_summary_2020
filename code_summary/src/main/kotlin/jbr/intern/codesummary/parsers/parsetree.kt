package jbr.intern.codesummary.parsers

import java.io.File
import org.antlr.v4.runtime.CharStreams

import jbr.intern.codesummary.methods.Language
import jbr.intern.codesummary.parsers.listeners.*

fun parseCodeTree(path: File, language: Language) : List<String> {
    if (!path.exists()) {
        println("> file doesn't exist: ${path.absolutePath}")
        return listOf()
    }
    val input = CharStreams.fromFileName(path.absolutePath)
    return when (language) {
        Language.JAVA -> getJavaMethods(input)
        Language.KOTLIN -> getKotlinMethods(input)
        Language.PYTHON -> getPythonMethods(input)
        Language.CSHARP -> getCSharpMethods(input)
        else -> {
            println("$language is not supported")
            listOf()
        }
    }
}
