package org.littespring.context.support;

import org.littlespring.core.io.FileSystemResource;
import org.littlespring.core.io.Resource;

public class FileSystemXmlApplicationContext extends AbstractApplicationContext {


    public FileSystemXmlApplicationContext(String configFile) {
        super(configFile);
    }

    @Override
    protected Resource getResourceByPatch(String patch) {
        return new FileSystemResource(patch);
    }
}
