package com.example;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.io.File;


public class Main {
    public static void main(String args[])throws Exception{
        File folder = new File(args.length > 0 ? args[0] : "testfiles");
        File[] files = folder.listFiles((d, n) -> n.endsWith(".java"));

        if (files == null) {
            System.out.println("Folder not found: " + folder.getAbsolutePath());
            return;
        }

        for (File f : files) {
            System.out.println("Scanning: " + f.getName());
            CompilationUnit cu = StaticJavaParser.parse(f);
            cu.accept(new SqlInjectionVisitor(), null);
        }
        System.out.println("Scan complete");
    }
}
class SqlInjectionVisitor extends VoidVisitorAdapter<Void> {
    @Override
    public void visit(MethodCallExpr call, Void arg) {
        super.visit(call, arg);
        String nm = call.getNameAsString();
        if (nm.equals("executeQuery") || nm.equals("executeUpdate")) {
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
