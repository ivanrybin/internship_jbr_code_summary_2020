package jbr.intern.codesummary.parsers.listeners

import jbr.intern.codesummary.antlr.cpp14ANTLR.CPP14Lexer
import jbr.intern.codesummary.antlr.cpp14ANTLR.CPP14Parser
import jbr.intern.codesummary.antlr.cpp14ANTLR.CPP14Parser.*
import jbr.intern.codesummary.antlr.cpp14ANTLR.CPP14ParserBaseListener
import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.Lexer
import org.antlr.v4.runtime.TokenStream
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.ParseTreeWalker

class CPPListener : CPP14ParserBaseListener() {
    var currClass = ""
    val methods = mutableListOf<String>()

    override fun enterClassHeadName(ctx: ClassHeadNameContext?) {
        val className = ctx?.className()?.text
        className?.let {
            currClass += "$className."
        }
    }

    override fun exitClassHeadName(ctx: ClassHeadNameContext?) {
        val className = ctx?.className()?.text
        className?.let {
            currClass = currClass.removeSuffix("$className.")
        }
    }

    override fun enterFunctionDefinition(ctx: FunctionDefinitionContext?) {
        val funcDecl = ctx?.declarator()?.text
        funcDecl?.let {
            methods.add("$currClass$funcDecl")
            println("$currClass$funcDecl")
        }
    }
}

fun getCPPMethods(input: CharStream): MutableList<String> {
    val lexer: Lexer = CPP14Lexer(input)
    val stream: TokenStream = CommonTokenStream(lexer)
    val parser = CPP14Parser(stream)
    val tree: ParseTree = parser.translationUnit()
    val listener = CPPListener()
    ParseTreeWalker.DEFAULT.walk(listener, tree)
    return listener.methods
}
