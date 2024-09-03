package com.lguplus.javaparser;

import com.github.javaparser.ParseResult;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.utils.SourceRoot;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SampleJpaTest {

    @Test
    public void parseJavaWithSymbolSolverForJava() throws Exception {
        String JAR_PATH = "/Users/seonmiji/IdeaProjects/sample-jpa/sample-jpa/target/sample-jpa-0.0.1-RELEASE.jar";
        String JAVA_PATH = "/Users/seonmiji/IdeaProjects/sample-jpa/sample-jpa/src/main/java";
        String FILE_PATH = "/Users/seonmiji/IdeaProjects/sample-jpa/sample-jpa/src/main/java/com/lguplus/ucube/samplejpa/web/basic/cust/service/BasicCustService.java";
        String SRC_PATH = "/Users/seonmiji/IdeaProjects/sample-jpa/sample-jpa/src/main/java/com/lguplus/ucube/samplejpa/web";

        //TypeSolver jarSolver = new JarTypeSolver(JAR_PATH);
        TypeSolver javaSolver = new JavaParserTypeSolver(JAVA_PATH);
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(javaSolver);

        /* 1. 모든 소스 파일 parsing */
        /*SourceRoot sourceRoot = new SourceRoot(Paths.get(SRC_PATH));
        sourceRoot.getParserConfiguration().setSymbolResolver(symbolSolver);

        List<ParseResult<CompilationUnit>> parseResultList = sourceRoot.tryToParse();
//        List<CompilationUnit> cuList = sourceRoot.getCompilationUnits();*/


        /* 2. 찾고자 하는 메소드의 소스 파일 parsing */
        StaticJavaParser.getParserConfiguration().setSymbolResolver(symbolSolver);
        CompilationUnit trgtCu = StaticJavaParser.parse(new File(FILE_PATH));


        /* 3. Visitor를 이용해 MethodDeclaration만 수집 */
        VoidVisitorAdapter visitor = new VoidVisitorAdapter<List<MethodDeclaration>>() {
            @Override
            public void visit(MethodDeclaration md, List<MethodDeclaration> collector) {
                super.visit(md, collector);
                collector.add(md);
            }
        };

        List<MethodDeclaration> mdList = new ArrayList<>();

        visitor.visit(trgtCu, mdList);

        String trgtMthdDecl = mdList.get(0).getDeclarationAsString();

        log.debug("target MethodDeclaration : {}", trgtMthdDecl);

        String trgtMthdSig = mdList.get(0).resolve().getQualifiedSignature();

        log.debug("target Method Signature : {}", trgtMthdSig);


        /*mdList.stream().forEach(md -> {
            log.debug("MethodDeclaration : {}", md.resolve().getQualifiedSignature());
        });*/
    }

    @Test
    public void parseJavaWithSymbolSolverForJar() throws Exception {
        String SRC_PATH = "/Users/seonmiji/IdeaProjects/sample-jpa/sample-jpa/target/jar/BOOT-INF/classes";
        String JAR_PATH = "/Users/seonmiji/IdeaProjects/sample-jpa/sample-jpa/target/app2.jar"; //SRC_PATH에서 "jar -cvf app2.jar ." 명령어 실행한 jar 파일
        String FILE_PATH = "/Users/seonmiji/IdeaProjects/sample-jpa/sample-jpa/src/main/java/com/lguplus/ucube/samplejpa/web/basic/cust/service/BasicCustService.java";

        TypeSolver typeSolver = new JarTypeSolver(JAR_PATH);
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);

        /* 1. 찾고자 하는 메소드의 소스 파일 parsing */
        StaticJavaParser.getParserConfiguration().setSymbolResolver(symbolSolver);
        CompilationUnit trgtCu = StaticJavaParser.parse(new File(FILE_PATH));


        /* 2. Visitor를 이용해 MethodDeclaration만 수집 */
        VoidVisitorAdapter visitor = new VoidVisitorAdapter<List<MethodDeclaration>>() {
            @Override
            public void visit(MethodDeclaration md, List<MethodDeclaration> collector) {
                super.visit(md, collector);
                collector.add(md);
            }
        };

        List<MethodDeclaration> mdList = new ArrayList<>();

        visitor.visit(trgtCu, mdList);

        String trgtMthdDecl = mdList.get(0).getDeclarationAsString();

        log.debug("target MethodDeclaration : {}", trgtMthdDecl);

        String trgtMthdSig = mdList.get(0).resolve().getQualifiedSignature();

        log.debug("target Method Signature : {}", trgtMthdSig);

    }

    @Test
    public void parseJavaToStructuredData() throws Exception {
        String FILE_PATH = "/Users/seonmiji/IdeaProjects/sample-jpa/sample-jpa/src/main/java/com/lguplus/ucube/samplejpa/web/basic/cust/service/BasicCustService.java";

        /* 1. 찾고자 하는 메소드의 소스 파일 parsing */
        CompilationUnit trgtCu = StaticJavaParser.parse(new File(FILE_PATH));

        System.out.println(trgtCu);

    }


}
