package com.lguplus.javaparser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.CallableDeclaration.Signature;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.utils.SourceRoot;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import com.github.javaparser.ast.CompilationUnit;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MethodCallTest {

    @Test
    public void test_JavaParser() throws Exception {
        File projDir = new File("/Users/seonmiji/IdeaProjects/nucm-svc-sample/src/main/java/com/lguplus/ncube/nucm/online/rvspvs/service/OcmpTrmDvchLikgMService.java");

        CompilationUnit cu = StaticJavaParser.parse(projDir);
        List<Comment> commentList = cu.getAllComments();

        commentList.forEach(comment -> {
            System.out.println(comment.asString());
        });
    }

    @Test
    public void test_MethodDeclaration() throws Exception {
        File projDir = new File("/Users/seonmiji/IdeaProjects/nucm-svc-sample/src/main/java/com/lguplus/ncube/nucm/online/rvspvs/service/OcmpTrmDvchLikgMService.java");

        CompilationUnit cu = StaticJavaParser.parse(projDir);

        VoidVisitorAdapter visitor = new VoidVisitorAdapter<List<MethodDeclaration>>() {
            @Override
            public void visit(MethodDeclaration md, List<MethodDeclaration> collector) {
                super.visit(md, collector);
                collector.add(md);
            }
        };

        List<MethodDeclaration> mdList = new ArrayList<>();

        visitor.visit(cu, mdList);

        mdList.forEach(md -> {
            System.out.println(md.getDeclarationAsString());
        });

    }

    @Test
    public void test_MethodFetchByDeclare() throws Exception {
        Path projPath = Paths.get("/Users/seonmiji/IdeaProjects/nucm-svc-master/src/main/java/com/lguplus/ncube/nucm/online/rvspvs");
        String methdDclr = "public void receiveOcmpDevUsimChng(OcmpUsimDvchInfoLikgDTO data) throws Exception";

        SourceRoot sourceRoot = new SourceRoot(projPath);
        sourceRoot.tryToParse();
        List<CompilationUnit> cuList = sourceRoot.getCompilationUnits();

        List<MethodDeclaration> mdList = new ArrayList<>();

        VoidVisitorAdapter visitor = new VoidVisitorAdapter<List<MethodDeclaration>>() {
            @Override
            public void visit(MethodDeclaration md, List<MethodDeclaration> collector) {
                //System.out.println(md.getName().asString());
                super.visit(md, collector);
                if(md.getDeclarationAsString().equals(methdDclr)) {
                    collector.add(md);
                }
            }
        };

        cuList.forEach(cu -> {visitor.visit(cu, mdList);});

        mdList.forEach(md -> {
            System.out.println(md.getDeclarationAsString());
        });

    }

    @Test
    public void test_MethodFetchByName() throws Exception {
        Path projPath = Paths.get("/Users/seonmiji/IdeaProjects/nucm-svc-master/src/main/java/com/lguplus/ncube/nucm/online/rvspvs");
        String methdNm = "receiveOcmpDevUsimChng";

        SourceRoot sourceRoot = new SourceRoot(projPath);
        sourceRoot.tryToParse();
        List<CompilationUnit> cuList = sourceRoot.getCompilationUnits();

        List<MethodDeclaration> mdList = new ArrayList<>();

        VoidVisitorAdapter visitor = new VoidVisitorAdapter<List<MethodDeclaration>>() {
            @Override
            public void visit(MethodDeclaration md, List<MethodDeclaration> collector) {
                super.visit(md, collector);
                if(md.getName().asString().equals(methdNm)) {
                    collector.add(md);
                }
            }
        };

        cuList.forEach(cu -> {visitor.visit(cu, mdList);});

        mdList.forEach(md -> {
            System.out.println(md.getDeclarationAsString());
        });

    }

    @Test
    public void test_MethodClassFetch() throws Exception {
        Path projPath = Paths.get("/Users/seonmiji/IdeaProjects/nucm-svc-master/src/main/java");
        String methdDclr = "public void receiveOcmpDevUsimChng(OcmpUsimDvchInfoLikgDTO data) throws Exception";

        SourceRoot sourceRoot = new SourceRoot(projPath);
        sourceRoot.tryToParse();
        List<CompilationUnit> cuList = sourceRoot.getCompilationUnits();

        List<MethodDeclaration> mdList = new ArrayList<>();

        VoidVisitorAdapter visitor = new VoidVisitorAdapter<List<MethodDeclaration>>() {
            @Override
            public void visit(MethodDeclaration md, List<MethodDeclaration>  collector) {
                super.visit(md, collector);
                if(md.getDeclarationAsString().equals(methdDclr)) {
                    collector.add(md);
                }
            }
        };

        cuList.forEach(cu -> {
            /*System.out.println("=======================");
            cu.getTypes().stream().forEach(type -> {
                System.out.println(type.getNameAsString());
            });*/
            visitor.visit(cu, mdList);
        });

        log.info("mdList.size() = {}", mdList.size());
        log.info("mdList.get(0).getNameAsString() = {}", mdList.size() > 0 ? mdList.get(0).getNameAsString() : "null");

        MethodDeclaration md = mdList.get(0);
        String className = md.getParentNode().map(x -> (ClassOrInterfaceDeclaration) x).map(NodeWithSimpleName::getNameAsString).orElse("");

        log.info("Enclosing Class = {}", className);
    }

    @Test
    public void test_JavaParserTypeResolver() throws Exception {
        String SRC_PATH = "/Users/seonmiji/IdeaProjects/nucm-svc-master/src/main/java";
        String FILE_PATH = "/Users/seonmiji/IdeaProjects/nucm-svc-master/src/main/java/com/lguplus/ncube/nucm/online/rvspvs/service/OcmpTrmDvchLikgMService.java";

        String methdDclr = "public void receiveOcmpDevUsimChng(OcmpUsimDvchInfoLikgDTO data) throws Exception";

        //TypeSolver jreSolver = new ReflectionTypeSolver();
        TypeSolver javaSolver = new JavaParserTypeSolver(SRC_PATH);
        //CombinedTypeSolver combinedSolver = new CombinedTypeSolver();
        //combinedSolver.add(jreSolver);
        //combinedSolver.add(javaSolver);
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(javaSolver);

        StaticJavaParser.getParserConfiguration().setSymbolResolver(symbolSolver);

        CompilationUnit cu = StaticJavaParser.parse(new File(FILE_PATH));

        List<MethodDeclaration> mdList = new ArrayList<>();

        VoidVisitorAdapter visitor = new VoidVisitorAdapter<List<MethodDeclaration>>() {
            @Override
            public void visit(MethodDeclaration md, List<MethodDeclaration>  collector) {
                super.visit(md, collector);
                if(md.getDeclarationAsString().equals(methdDclr)) {
                    collector.add(md);
                }
            }
        };

        visitor.visit(cu, mdList);

        MethodDeclaration md = mdList.get(0);

        System.out.println(md.resolve().getQualifiedSignature());
    }


    @Test
    public void test_CallerClassFetch() throws Exception {
        String SRC_PATH = "/Users/seonmiji/IdeaProjects/nucm-svc-master/src/main/java";
        String FILE_PATH = "/Users/seonmiji/IdeaProjects/nucm-svc-master/src/main/java/com/lguplus/ncube/nucm/online/rvspvs/service/OcmpTrmDvchLikgMService.java";
        String methdDclr = "public void receiveOcmpDevUsimChng(OcmpUsimDvchInfoLikgDTO data) throws Exception";

        TypeSolver javaSolver = new JavaParserTypeSolver(SRC_PATH);
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(javaSolver);

        log.info("================pre=================");

        /* 0. 모든 소스 파일 CompilationUnit으로 parsing */
        SourceRoot sourceRoot = new SourceRoot(Paths.get(SRC_PATH));
        sourceRoot.getParserConfiguration().setSymbolResolver(symbolSolver);
        sourceRoot.tryToParse();
        List<CompilationUnit> cuList = sourceRoot.getCompilationUnits();

        //log.info("================save=================");
        //sourceRoot.saveAll();

        log.info("================0=================");

        /* 1. 대상 메소드의 메소드 시그니처 추출 */
        StaticJavaParser.getParserConfiguration().setSymbolResolver(symbolSolver);
        CompilationUnit trgtCu = StaticJavaParser.parse(new File(FILE_PATH));
        List<MethodDeclaration> trgtList = new ArrayList<>();

        VoidVisitorAdapter targetFinder = new VoidVisitorAdapter<List<MethodDeclaration>>() {
            @Override
            public void visit(MethodDeclaration md, List<MethodDeclaration>  collector) {
                super.visit(md, collector);
                if(md.getDeclarationAsString().equals(methdDclr)) {
                    collector.add(md);
                }
            }
        };

        targetFinder.visit(trgtCu, trgtList);

        MethodDeclaration trgt = trgtList.get(0);
        System.out.println(trgt.resolve().getQualifiedSignature());

        log.info("================1=================");

        /* cuList.forEach(cu -> {
            cu.accept(new VoidVisitorAdapter<Object>() {
                @Override
                public void visit(MethodCallExpr mce, Object arg) {
                    super.visit(mce, arg);

                    if(mce.resolve().getQualifiedSignature().equals(trgt.resolve().getQualifiedSignature())) {
                        String className = mce.getParentNode().map(x -> (ClassOrInterfaceDeclaration) x).map(NodeWithSimpleName::getNameAsString).orElse("");
                        log.info("Caller Class = {}", className);
                    }
                }
            }, null);

            List<MethodCallExpr> list = cu.findAll(MethodCallExpr.class);
            list.stream().forEach(e -> {
                if(e.resolve().getQualifiedSignature().equals(trgt.resolve().getQualifiedSignature())) {
                    String className = e.getParentNode().map(x -> (ClassOrInterfaceDeclaration) x).map(NodeWithSimpleName::getNameAsString).orElse("");
                    log.info("Caller Class = {}", className);
                }
            });
        }); */


    }
}
