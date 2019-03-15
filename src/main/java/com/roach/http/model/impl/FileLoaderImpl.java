package com.roach.http.model.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

public class FileLoaderImpl implements FileLoader{
    private FileWrapper file;

    public FileLoaderImpl(String string) {
        String rootFolder = string.split("/")[0];
        System.out.println(rootFolder);
        String fileName = string.split("/")[1];
        System.out.println(fileName);
        File target = null;
        Path root = Paths.get(rootFolder);
        try (Stream<Path> stream = Files.find(root, Integer.MAX_VALUE, (path, attr) ->
                path.getFileName().toString().equals(fileName))) {
            Optional<Path> path = stream.findFirst();
            if(path.isPresent()) {
                target = path.get().toFile();
                file.setFile(target);
                file.setRootDirectory(rootFolder);
            }
        }
        catch (IOException ignored) {}
    }

    @Override
    public FileWrapper getFile() {
        return file;
    }
}
