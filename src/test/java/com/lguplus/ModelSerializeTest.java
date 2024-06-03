package com.lguplus;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import spoon.Launcher;
import spoon.JarLauncher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.CompressionType;
import spoon.support.SerializationModelStreamer;

import java.io.*;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ExtendWith(TimingExtension.class)
public class ModelSerializeTest {
    @Test
    public void test_모델저장() throws Exception {
        String filePath = "/Users/seonmiji/IdeaProjects/Sample/src/main/java";
        Launcher launcher = new Launcher();
        launcher.addInputResource(filePath);
        log.info("파일 로드까지 실행 완료.");

        launcher.getEnvironment().setNoClasspath(true);
        launcher.getEnvironment().setComplianceLevel(8);
        CtModel model = launcher.buildModel();
        log.info("모델 빌드까지 실행 완료.");

        Factory factory = launcher.getFactory();
        factory.getEnvironment().setCompressionType(CompressionType.GZIP);

        File modelFile = new File("/Users/seonmiji/IdeaProjects/Sample/SpoonModelFileEx.gz");
        OutputStream fos = new FileOutputStream(modelFile);
        SerializationModelStreamer serializationModelStreamer = new SerializationModelStreamer();

        serializationModelStreamer.save(factory, fos);
        log.info("모델 저장까지 실행 완료.");

        File loadedFile = new File("/Users/seonmiji/IdeaProjects/Sample/SpoonModelFileEx.gz");
        serializationModelStreamer.load(new FileInputStream(loadedFile));
        log.info("저장된 모델 로드까지 실행 완료.");
    }

    @Test
    public void test_모델저장ByJar() throws Exception {
        String filePath = "/Users/seonmiji/IdeaProjects/Sample/target/Sample-1.0-SNAPSHOT.jar";
        Launcher launcher = new JarLauncher(filePath);
        //launcher.addInputResource(filePath);
        log.info("파일 로드까지 실행 완료.");

        launcher.getEnvironment().setNoClasspath(true);
        launcher.getEnvironment().setComplianceLevel(8);
        CtModel model = launcher.buildModel();
        log.info("모델 빌드까지 실행 완료.");

        Factory factory = launcher.getFactory();
        factory.getEnvironment().setCompressionType(CompressionType.GZIP);

        File modelFile = new File("/Users/seonmiji/IdeaProjects/Sample/SpoonModelFileEx2.gz");
        OutputStream fos = new FileOutputStream(modelFile);
        SerializationModelStreamer serializationModelStreamer = new SerializationModelStreamer();

        serializationModelStreamer.save(factory, fos);
        log.info("모델 저장까지 실행 완료.");

        File loadedFile = new File("/Users/seonmiji/IdeaProjects/Sample/SpoonModelFileEx2.gz");
        serializationModelStreamer.load(new FileInputStream(loadedFile));
        log.info("저장된 모델 로드까지 실행 완료.");
    }

    @Test
    public void test_모델저장_NUCM() throws Exception {
        String filePath = "/Users/seonmiji/IdeaProjects/nucm-svc-master/src/main/java/com/lguplus/ncube/nucm/online";
        Launcher launcher = new Launcher();
        launcher.addInputResource(filePath);
        launcher.getEnvironment().setNoClasspath(true);
        log.info("파일 로드까지 실행 완료.");
        launcher.getEnvironment().setComplianceLevel(8);
        launcher.buildModel();
        log.info("모델 빌드까지 실행 완료.");

        Factory factory = launcher.getFactory();

        factory.getEnvironment().setCompressionType(CompressionType.GZIP);

        File modelFile = new File("/Users/seonmiji/IdeaProjects/Sample/SpoonModelFileEx2.gz");
        OutputStream fos = new FileOutputStream(modelFile);
        SerializationModelStreamer serializationModelStreamer = new SerializationModelStreamer();

        serializationModelStreamer.save(factory, fos);
        log.info("모델 저장까지 실행 완료.");
    }

    @Test
    public void test_모델로드_NUCM() throws Exception {
        File loadedFile = new File("/Users/seonmiji/IdeaProjects/Sample/SpoonModelFileEx2.gz");

        SerializationModelStreamer serializationModelStreamer = new SerializationModelStreamer();
        Factory factory = serializationModelStreamer.load(new FileInputStream(loadedFile));
        CtModel model = factory.getModel();

        log.info("저장된 모델 로드까지 실행 완료.");

        CtClass<?> aClass = factory.Class().get("com.lguplus.ncube.nucm.online.rvspvs.service.OcmpTrmDvchLikgMService");
        CtMethod<?> ctMethod = aClass.getMethodsByName("receiveOcmpDevUsimChng").get(0);

        log.info("aClass = {}", aClass.getSimpleName());
        log.info("ctMethod = {}", ctMethod.getSimpleName());

        List<CtMethod> callers = model.getElements(new TypeFilter<CtInvocation>(CtInvocation.class) {
            @Override
            public boolean matches(CtInvocation element) {
                CtExecutableReference executable = element.getExecutable();
                if (executable.getSimpleName().equals(ctMethod.getSimpleName())
                        && executable.isOverriding(ctMethod.getReference())) {
                    log.info("ctMethod : {}", ctMethod.getSimpleName());
                    log.info("executable : {}", executable.getSimpleName());
                    return true;
                }
                return false;
            }
        }).stream().map(i -> {
            CtMethod parent = i.getParent(CtMethod.class);
            parent.putMetadata("OcmpTrmDvchLikgMService", i.getArguments().get(0).toString());
            log.info(parent.prettyprint());
            return parent;
        }).collect(Collectors.toList());

        CtTypeReference<? extends Annotation> requestAnnotation = factory.Type().createReference("org.springframework.web.bind.annotation.RequestMapping");
        CtTypeReference<? extends Annotation> getAnnotation = factory.Type().createReference("org.springframework.web.bind.annotation.GetMapping");
        CtTypeReference<? extends Annotation> postAnnotation = factory.Type().createReference("org.springframework.web.bind.annotation.PostMapping");


        model.getRootPackage().accept(new CtScanner() {
            @Override
            public <T> void visitCtInvocation(CtInvocation<T> invocation) {
                CtExecutableReference<T> executable = invocation.getExecutable();
                for (int i = 0; i < callers.size(); i++) {
                    CtMethod method = callers.get(i);
                    if (method.getSignature().equals(executable.getSignature())) {
                        CtMethod parent = invocation.getParent(CtMethod.class);
                        CtClass parentClss = invocation.getParent(CtClass.class);
                        log.info("Controller = {}", parentClss.getSimpleName());
                        CtAnnotation<? extends Annotation> requestCtAnnotation = parentClss.getAnnotation(requestAnnotation);
                        String subDomain = requestCtAnnotation.getValue("value").toString();
                        CtAnnotation<? extends Annotation> getCtAnnotation = parent.getAnnotation(getAnnotation);
                        CtAnnotation<? extends Annotation> postCtAnnotation = parent.getAnnotation(postAnnotation);
                        if (getCtAnnotation != null) {
                            String uri = subDomain + getCtAnnotation.getValue("value").toString();
                            uri = uri.replace("\"", "");
                            System.out.println(method.getMetadata("OcmpTrmDvchLikgMService") + " -> " + uri);
                        } else if (postCtAnnotation != null) {
                            String uri = subDomain + postCtAnnotation.getValue("value").toString();
                            uri = uri.replace("\"", "");
                            System.out.println(method.getMetadata("OcmpTrmDvchLikgMService") + " -> " + uri);
                        }
                    }
                }
                super.visitCtInvocation(invocation);
            }
        });
    }
}
