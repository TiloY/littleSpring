package org.littlespring.context.support;

import org.littlespring.core.io.ClassPathResource;
import org.littlespring.core.io.Resource;

public class ClassPathApplicationContext extends AbstractApplicationContext {

    public ClassPathApplicationContext(String configFile) {
        super(configFile);
    }

    @Override
    protected Resource getResourceByPatch(String patch) {
        return new ClassPathResource(patch,this.getBeanClassLoader());
    }

}
