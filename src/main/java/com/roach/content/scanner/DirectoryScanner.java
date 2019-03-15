package com.roach.content.scanner;


import com.roach.http.model.MappingHandler;
import com.roach.http.model.impl.StaticContentMappingHandler;
import com.roach.http.service.HttpMultiProcessor;
import com.roach.http.service.HttpMultiProcessorImpl;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class DirectoryScanner {

    private String rootDirectory;
    private Collection files;
    private MappingHandler mappingHandler;

    public DirectoryScanner (String rootDirectory, ScheduledThreadPoolExecutor executor, HttpMultiProcessor httpMultiProcessor) {
        this.mappingHandler = new StaticContentMappingHandler("/lalka/la.txt");
        httpMultiProcessor.addHandler(mappingHandler);
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
