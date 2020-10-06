package jbr.intern.codesummary.antlr.csharpANTLR;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;

interface abs {
    void privet();
}

abstract class egik {
    void privet() {}
}

class CSharpParserImpl {

    class TestClass {
        void innerFunctionADFD() {}
    }
    void notmain() {

    }

    private static void hello(String s1, String s2) {

    }

    public static void main(String[] args) throws IOException {
        CharStream input = CharStreams.fromFileName("/home/vancho/Master_UBUNTU/НИР_20_21/code_summerization/code_summer/src/main/resources/CSHARP.cs");
        Lexer lexer = new CSharpLexer(input);
        TokenStream stream = new CommonTokenStream(lexer);
        CSharpParser parser = new CSharpParser(stream);
        ParseTree tree = parser.compilation_unit();

        CSharpListenerImpl listener = new CSharpListenerImpl();
        ParseTreeWalker.DEFAULT.walk(listener, tree);

//        System.out.println(listener.getMethods());
    }
}