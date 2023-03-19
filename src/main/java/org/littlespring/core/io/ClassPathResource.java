package org.littlespring.core.io;

import org.littlespring.utils.ClassUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ClassPathResource implements Resource{

    private String patch ;
    private ClassLoader classLoader ;

    public ClassPathResource(String patch) {
      this(patch,(ClassLoader)null);
    }

    public ClassPathResource(String patch, ClassLoader classLoader) {
        this.patch = patch;
        this.classLoader = (classLoader != null ? classLoader: ClassUtils.getDefaultClassLoader());
    }

    @Override
    public InputStream getInputStream() throws IOException {
        InputStream is = this.classLoader.getResourceAsStream(this.patch);
        if(is  ==null){
            throw new FileNotFoundException(patch+ "cannot be opened");
        }
        return is;
    }

    @Override
    public String getDescription() {
        return this.patch;
    }
}
