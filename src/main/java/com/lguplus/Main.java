package com.lguplus;

import lombok.extern.slf4j.Slf4j;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.compiler.jdt.CompilationUnitFilter;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        //0. nucm-svc 참조할 수 있는가? (1개의 java 파일로만 테스트)
        //number_zero();

        //1. 어떤 서비스 메소드가 있을 때, 이 서비스 메소드를 참조하는 다른 서비스/컨트롤러들을 찾아서, 결국 어떤 API에 영향이 있는지 찾아서 출력하기
        number_one();

        //2. 특정 파일을 제외하고 AST 모델 만들기

    }

    public static void number_zero() {
        String filePath = "/Users/seonmiji/IdeaProjects/nucm-svc-sample/src/main/java/com/lguplus/ncube/nucm/online/rvspvs/service/OcmpTrmDvchLikgMService.java";
        Launcher launcher = new Launcher();
        launcher.addInputResource(filePath);
        launcher.getEnvironment().setNoClasspath(true);
        log.info("파일 로드까지 실행 완료.");
        // optional
        // launcher.getEnvironment().setSourceClasspath(
        //        "lib1.jar:lib2.jar".split(":"));
        launcher.getEnvironment().setComplianceLevel(7);
        CtModel model = launcher.buildModel();

        List<CtClass> list = model.getElements(new TypeFilter<>(CtClass.class));
        list.stream().forEach(ctClass -> {
            Set<CtMethod> methodSet = ctClass.getMethods();
            methodSet.stream().forEach(ctMethod -> {
                log.info(ctMethod.getSimpleName());
            });
        });
    }

    public static void number_one() {
        String filePath = "/Users/seonmiji/IdeaProjects/nucm-svc-master/src/main/java/com/lguplus/ncube/nucm/online/rvspvs";
        Launcher launcher = new Launcher();
        launcher.addInputResource(filePath);
        launcher.getEnvironment().setNoClasspath(true);
        log.info("파일 로드까지 실행 완료.");
        launcher.getEnvironment().setComplianceLevel(8);
        CtModel model = launcher.buildModel();

        Factory factory = launcher.getFactory();
        CtClass<?> aClass = factory.Class().get("com.lguplus.ncube.nucm.online.rvspvs.service.OcmpTrmDvchLikgMService");
        CtMethod<?> ctMethod = aClass.getMethodsByName("receiveOcmpDevUsimChng").get(0);

        log.info("aClass = {}", aClass.getSimpleName());
        log.info("ctMethod = {}", ctMethod.getSimpleName());
        //log.info("rootPackage = {}", model.getRootPackage().isUnnamedPackage());

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