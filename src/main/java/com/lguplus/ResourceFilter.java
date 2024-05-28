package com.lguplus;

import lombok.extern.slf4j.Slf4j;
import spoon.compiler.SpoonFolder;
import spoon.support.compiler.VirtualFolder;

import java.util.List;

@Slf4j
public class ResourceFilter extends VirtualFolder {
    private final String regex = ".*/repository|.*/entity";

    public ResourceFilter(SpoonFolder o) {
        this.addFolder(o);
    }

    @Override
    public void addFolder(SpoonFolder o) {
        List<SpoonFolder> spoonFolders = o.getSubFolders();

        if(spoonFolders.size() == 0) {
            //하위 디렉토리가 없으면, 이제 파일 추가!
            super.addFolder(o);
        } else {
            // 하위 디렉토리 있으면, 정규식에 해당하는 폴더는 제외하고 재귀 호출
            spoonFolders.stream().forEach(folder -> {
                if(!folder.getPath().matches(this.regex)) {
                    log.debug("SpoonFolder Path = {}", folder.getPath());
                    this.addFolder(folder);
                }
            });
        }

    }
}
