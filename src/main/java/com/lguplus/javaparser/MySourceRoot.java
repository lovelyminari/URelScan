package com.lguplus.javaparser;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.printer.XmlPrinter;
import com.github.javaparser.utils.SourceRoot;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

@Slf4j
public class MySourceRoot extends SourceRoot {

    public MySourceRoot(Path root) {
        super(root);
    }

    public MySourceRoot(Path root, ParserConfiguration parserConfiguration) {
        super(root, parserConfiguration);
    }

    @Override
    public ParseResult<CompilationUnit> tryToParse(String startPackage, String filename, ParserConfiguration configuration) throws IOException {
        log.info("Overrided Method is Called! (MySourceRoot.tryToParse)");
        ParseResult<CompilationUnit> result = super.tryToParse(startPackage, filename, configuration);

        //result를 파일로 저장...
        result.getResult().ifPresent(cu -> saveXMLFile(cu));

        return result;
    }

    private boolean saveXMLFile(CompilationUnit cu) {
        log.info("'saveXMLFile' Method is Called.");
        String strFolderPath = super.getRoot().toString()+"/ast/";
        XmlPrinter printer = new XmlPrinter(true);
        log.info("strFolderPath = {}", strFolderPath);

        File folder = new File(strFolderPath);
        if(!folder.exists()) {
            folder.mkdir();
        }

        cu.findAll(ClassOrInterfaceDeclaration.class).stream().forEach(e -> {
            String strFullFilePath = strFolderPath + e.getNameAsString() + ".xml";
            log.info("strFullFilePath = {}", strFullFilePath);
            File file = new File(strFullFilePath);
            try {
                FileOutputStream fos = new FileOutputStream(file, false);
                fos.write(printer.output(cu).getBytes());
            } catch(IOException ex) {
                log.error("Generating file failed. {}", ex.getMessage());
            }
        });

        return true;
    }
}
