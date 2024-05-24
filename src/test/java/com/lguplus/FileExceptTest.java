package com.lguplus;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import spoon.Launcher;
import spoon.SpoonModelBuilder;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.factory.Factory;
import spoon.support.compiler.FileSystemFolder;
import spoon.support.compiler.FilteringFolder;
import spoon.support.compiler.jdt.CompilationUnitFilter;

@Slf4j
@ExtendWith(TimingExtension.class)
public class FileExceptTest {

    @Test
    public void test_전체모델빌드() {
        String filePath = "/Users/seonmiji/IdeaProjects/nucm-svc-master/src/main/java/com/lguplus/ncube/nucm/online";
        Launcher launcher = new Launcher();
        launcher.addInputResource(filePath);

        log.info("파일 로드까지 실행 완료.");
        launcher.getEnvironment().setNoClasspath(true);
        launcher.getEnvironment().setComplianceLevel(8);
        CtModel model = launcher.buildModel();
        log.info("모델 빌드까지 실행 완료.");

        Factory factory = launcher.getFactory();
        CtClass<?> aClass = factory.Class().get("com.lguplus.ncube.nucm.online.rvspvs.entity.OcmpTrmDvchLikgMEntity");

        Assertions.assertEquals("OcmpTrmDvchLikgMEntity", aClass.getSimpleName());
    }

    @Test
    public void test_서비스상위모델빌드() {
        String filePath = "/Users/seonmiji/IdeaProjects/nucm-svc-master/src/main/java/com/lguplus/ncube/nucm/online";
        Launcher launcher = new Launcher();
        // launcher.addInputResource(filePath);

        // Service 하위 layer는 무시하기
        SpoonModelBuilder smb = launcher.getModelBuilder();
        FilteringFolder resource = new FilteringFolder();
        resource.addFolder(new FileSystemFolder(filePath));
        resource.removeAllThatMatch(".*/repository/.*|.*/entity/.*");
        smb.addInputSource(resource);
        log.info("파일 로드까지 실행 완료.");

        launcher.getEnvironment().setNoClasspath(true);
        launcher.getEnvironment().setComplianceLevel(8);
        CtModel model = launcher.buildModel();
        log.info("모델 빌드까지 실행 완료.");

        Factory factory = launcher.getFactory();
        CtClass<?> aClass = factory.Class().get("com.lguplus.ncube.nucm.online.rvspvs.entity.OcmpTrmDvchLikgMEntity");

        Assertions.assertThrows(NullPointerException.class, () -> {
            aClass.getSimpleName();
        });

    }

    @Test
    public void test_정규식() {
        String regex = ".*/repository/.*|.*/entity/.*";
        String a = "aaa/repository/bbb.java";
        String b = "bbb/entity/aaa.java";
        String c = "aaarepositorybbb.java";
        String d = "bbb/entityaaa.java";

        Assertions.assertTrue(a.matches(regex));
        Assertions.assertTrue(b.matches(regex));
        Assertions.assertFalse(c.matches(regex));
        Assertions.assertFalse(d.matches(regex));
    }

}
