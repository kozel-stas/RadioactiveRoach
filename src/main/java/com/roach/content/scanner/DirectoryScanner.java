package com.roach.content.scanner;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

public class DirectoryScanner {

    private String rootDirectory;
    private Collection files;


    public DirectoryScanner (String rootDirectory) {
        this.rootDirectory = rootDirectory;
        this.files = FileUtils.listFiles(
                new File(rootDirectory),
                new RegexFileFilter("^(.*?)"),
                DirectoryFileFilter.DIRECTORY
        );
    }

    public void show () {
        System.out.println(Arrays.toString(files.toArray()));
    }

}
