package jbr.intern.codesummary.parsers.listeners

import jbr.intern.codesummary.antlr.csharpANTLR.CSharpLexer
import jbr.intern.codesummary.antlr.csharpANTLR.CSharpParser
import jbr.intern.codesummary.antlr.csharpANTLR.CSharpParserBaseListener
import org.antlr.v4.runtime.*

import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.ParseTreeWalker

class CSharpListener : CSharpParserBaseListener() {
    var currClass = ""
    val methods = mutableListOf<String>()

    override fun enterClass_definition(ctx: CSharpParser.Class_definitionContext) {
        val className = ctx.identifier().text
        currClass += "$className."
    }

    override fun exitClass_definition(ctx: CSharpParser.Class_definitionContext) {
        val className = ctx.identifier().text
        currClass = currClass.removeSuffix("$className.")
    }

    override fun enterMethod_declaration(ctx: CSharpParser.Method_declarationContext) {
        val methodName = ctx.method_member_name().text
        methods.add("$currClass$methodName")
        println("C#: method name -> $currClass$methodName")
    }
}

fun getCSharpMethods(input: CharStream): MutableList<String> {
    val lexer: Lexer = CSharpLexer(input)
    val stream: TokenStream = CommonTokenStream(lexer)
    val parser = CSharpParser(stream)
    val tree: ParseTree = parser.compilation_unit()
    val listener = CSharpListener()
    ParseTreeWalker.DEFAULT.walk(listener, tree)
    return listener.methods
}
