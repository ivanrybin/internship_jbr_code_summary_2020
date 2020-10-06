package jbr.intern.codesummary.parsers.listeners

import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.Lexer
import org.antlr.v4.runtime.TokenStream
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.ParseTreeWalker

import jbr.intern.codesummary.antlr.python3ANTLR.Python3BaseListener
import jbr.intern.codesummary.antlr.python3ANTLR.Python3Lexer
import jbr.intern.codesummary.antlr.python3ANTLR.Python3Parser
import jbr.intern.codesummary.antlr.python3ANTLR.Python3Parser.ClassdefContext
import jbr.intern.codesummary.antlr.python3ANTLR.Python3Parser.FuncdefContext

class PythonListener : Python3BaseListener() {
    var currFunc = ""                       // inner functions possible
    var currClass = ""
    val methods = mutableListOf<String>()

    override fun enterClassdef(ctx: ClassdefContext?) {
        val className = ctx?.NAME()?.text
        className?.let {
            currClass += "$className."
        }
    }

    override fun exitClassdef(ctx: ClassdefContext?) {
        val className = ctx?.NAME()?.text
        className?.let {
            currClass = currClass.removeSuffix("$className.")
        }
    }

    override fun enterFuncdef(ctx: FuncdefContext?) {
        val funcName = ctx?.NAME()?.text
        funcName?.let {
            methods.add("$currClass$currFunc$funcName")
            currFunc += "$funcName."
        }
    }

    override fun exitFuncdef(ctx: FuncdefContext?) {
        val funcName = ctx?.NAME()?.text
        funcName?.let {
            currFunc = currFunc.removeSuffix("$funcName.")
        }
    }
}

fun getPythonMethods(input: CharStream): MutableList<String> {
    val lexer: Lexer = Python3Lexer(input)
    val stream: TokenStream = CommonTokenStream(lexer)
    val parser = Python3Parser(stream)
    val tree: ParseTree = parser.file_input()
    val listener = PythonListener()
    ParseTreeWalker.DEFAULT.walk(listener, tree)
    return listener.methods
}
