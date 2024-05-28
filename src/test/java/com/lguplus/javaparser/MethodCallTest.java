package com.lguplus.javaparser;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.utils.SourceRoot;
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
    public void test_MethodCallerFetch() throws Exception {
        Path projPath = Paths.get("/Users/seonmiji/IdeaProjects/nucm-svc-master/src/main/java/com/lguplus/ncube/nucm/online/rvspvs");
        String methdDclr = "public void receiveOcmpDevUsimChng(OcmpUsimDvchInfoLikgDTO data) throws Exception";

        SourceRoot sourceRoot = new SourceRoot(projPath);
        sourceRoot.tryToParse();
        List<CompilationUnit> cuList = sourceRoot.getCompilationUnits();

        MethodDeclaration mdFound = new MethodDeclaration();

        VoidVisitorAdapter visitor = new VoidVisitorAdapter<MethodDeclaration>() {
            @Override
            public void visit(MethodDeclaration md, MethodDeclaration collector) {
                super.visit(md, collector);
                if(md.getDeclarationAsString().equals(methdDclr)) {
                    collector = md;
                }
            }
        };

        cuList.forEach(cu -> {
            cu.getTypes().stream().forEach(type -> {
                System.out.println(type.getNameAsString());
            });
            visitor.visit(cu, mdFound);
        });


        //Node node = mdFound.getParentNode().get();
        //System.out.println(node.getComment().get().asString());


    }
}
