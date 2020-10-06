package jbr.intern.codesummary.parsers.listeners

import jbr.intern.codesummary.antlr.kotlinANTLR.KotlinLexer
import jbr.intern.codesummary.antlr.kotlinANTLR.KotlinParser
import jbr.intern.codesummary.antlr.kotlinANTLR.KotlinParser.FunctionDeclarationContext
import jbr.intern.codesummary.antlr.kotlinANTLR.KotlinParserBaseListener
import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.Lexer
import org.antlr.v4.runtime.TokenStream
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.ParseTreeWalker

class KotlinListener : KotlinParserBaseListener() {
    var currClass = ""
    val methods = mutableListOf<String>()

    override fun enterClassDeclaration(ctx: KotlinParser.ClassDeclarationContext?) {
        val className = ctx?.simpleIdentifier()?.text
        currClass += "$className."
    }

    override fun exitClassDeclaration(ctx: KotlinParser.ClassDeclarationContext?) {
        val className = ctx?.simpleIdentifier()?.text
        currClass = currClass.removeSuffix("$className.")
    }

    override fun enterFunctionDeclaration(ctx: FunctionDeclarationContext?) {
        val funcName = ctx?.identifier()?.text
        methods.add("$currClass$funcName")
        println("$currClass$funcName")
    }
}

fun getKotlinMethods(input: CharStream): MutableList<String> {
    val lexer: Lexer = KotlinLexer(input)
    val stream: TokenStream = CommonTokenStream(lexer)
    val parser = KotlinParser(stream)
    val tree: ParseTree = parser.kotlinFile()
    val listener = KotlinListener()
    ParseTreeWalker.DEFAULT.walk(listener, tree)
    return listener.methods
}
