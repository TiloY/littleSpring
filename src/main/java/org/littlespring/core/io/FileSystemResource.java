package org.littlespring.core.io;

import org.littlespring.utils.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileSystemResource implements Resource {

    private String patch;
    private File file;

    public FileSystemResource(String fileSystemConfig) {
        Assert.notNull(fileSystemConfig, "fileSystemConfig must not be null ");
        this.file = new File(fileSystemConfig);
        this.patch = fileSystemConfig;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(this.file);
    }

    @Override
    public String getDescription() {
        return "file [" + this.file.getAbsolutePath() + "]";
    }
}
