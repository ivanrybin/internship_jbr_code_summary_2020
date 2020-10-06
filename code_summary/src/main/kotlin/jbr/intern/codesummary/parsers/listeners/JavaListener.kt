package jbr.intern.codesummary.parsers.listeners

import jbr.intern.codesummary.antlr.java8ANTLR.Java8Lexer
import jbr.intern.codesummary.antlr.java8ANTLR.Java8Parser
import jbr.intern.codesummary.antlr.java8ANTLR.Java8ParserBaseListener

import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.ParseTreeWalker

class JavaListener : Java8ParserBaseListener() {
    var currClass = ""
    val methods = mutableListOf<String>()

    override fun enterClassDeclaration(ctx: Java8Parser.ClassDeclarationContext?) {
        val className = ctx?.normalClassDeclaration()?.Identifier()?.text
        className?.let {
            currClass += "$className."
        }
    }

    override fun exitClassDeclaration(ctx: Java8Parser.ClassDeclarationContext?) {
        val className = ctx?.normalClassDeclaration()?.Identifier()?.text
        className?.let {
            currClass = currClass.removeSuffix("$className.")
        }
    }

    override fun enterInterfaceDeclaration(ctx: Java8Parser.InterfaceDeclarationContext?) {
        val className = ctx?.normalInterfaceDeclaration()?.Identifier()?.text
        className?.let {
            currClass += "$className."
        }
    }

    override fun exitInterfaceDeclaration(ctx: Java8Parser.InterfaceDeclarationContext?) {
        val className = ctx?.normalInterfaceDeclaration()?.Identifier()?.text
        className?.let {
            currClass = currClass.removeSuffix("$className.")
        }
    }

    override fun enterMethodDeclarator(ctx: Java8Parser.MethodDeclaratorContext?) {
        val methodName = ctx?.Identifier()?.text
        methodName?.let {
            methods.add("$currClass$methodName")
        }
    }
}

fun getJavaMethods(input: CharStream): MutableList<String> {
    val lexer: Lexer = Java8Lexer(input)
    val stream: TokenStream = CommonTokenStream(lexer)
    val parser = Java8Parser(stream)
    val tree: ParseTree = parser.compilationUnit()
    val listener = JavaListener()
    ParseTreeWalker.DEFAULT.walk(listener, tree)
    return listener.methods
}
