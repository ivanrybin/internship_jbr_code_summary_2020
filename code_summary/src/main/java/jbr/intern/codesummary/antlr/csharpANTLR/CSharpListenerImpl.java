package jbr.intern.codesummary.antlr.csharpANTLR;

import java.util.LinkedList;
import java.util.List;

public class CSharpListenerImpl extends CSharpParserBaseListener {
    private List<String> methods = new LinkedList<>();

    @Override public void enterClass_definition(CSharpParser.Class_definitionContext ctx) {
        String className = ctx.identifier().getText();
        System.out.println("Entered class " + className);
    }
    @Override public void enterMethod_declaration(CSharpParser.Method_declarationContext ctx) {
        String funcName = ctx.method_member_name().getText();
        methods.add(funcName);
    }
    public List<String> getMethods() {
        return methods;
    }
}
