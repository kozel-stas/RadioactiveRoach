package com.roach.http.model.impl;
// своя обёртка

import java.io.File;

public class FileWrapper {
    private File file;
    private String rootDirectory;

    public void setFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public String getRootDirectory() {
        return rootDirectory;
    }

    public void setRootDirectory(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }
}
