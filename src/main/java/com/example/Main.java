package com.example;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.io.File;


public class Main {
    public static void main(String args[])throws Exception{
        String path=args.length>0?args[0]: "C:/janani/cdprjt/sqlinjectiondetector/testfiles/safe.java";
        CompilationUnit cu=StaticJavaParser.parse(new File(path));
        cu.accept(new SqlInjectionVisitor(),null);
        System.out.println("scan complete: "+path);
    }
}
class SqlInjectionVisitor extends VoidVisitorAdapter<Void> {
    @Override
    public void visit(MethodCallExpr call, Void arg) {
        super.visit(call, arg);
        String name = call.getNameAsString();
        if (name.equals("executeQuery") || name.equals("executeUpdate")) {
            if (!call.getArguments().isEmpty()) {
                Expression first = call.getArgument(0);
                if (first.isBinaryExpr()
                        && first.asBinaryExpr().getOperator() == BinaryExpr.Operator.PLUS) {
                    int line = call.getBegin().map(p -> p.line).orElse(-1);
                    System.out.println("[HIGH] SQL Injection at line " + line + ": " + first);
                }
            }
        }
    }
}
