package com.mymanager.mfm;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Formatter;

public class FileInfo {
    enum FileType {
        DIRECTORY("D"), FILE("F");

        private String name;

        public String getName() {
            return name;
        }

        FileType(String name) {
            this.name = name;
        }
    }

    private String fileName;
    private FileType type;
    private long size;
    private LocalDateTime lastModified;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public FileType getType() {
        return type;
    }

    public void setType(FileType type) {
        this.type = type;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getLastModified() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return lastModified.format(dtf);
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public FileInfo (Path path) {

        try {
            setFileName(path.getFileName().toString());
            setSize(Files.size(path));
            setType(Files.isDirectory(path) ? FileType.DIRECTORY : FileType.FILE);
            if (getType() == FileType.DIRECTORY) {
                setSize(-1L);
            }
            setLastModified(LocalDateTime.ofInstant(Files.getLastModifiedTime(path).toInstant(), ZoneOffset.systemDefault()));
        } catch (IOException e) {
            throw new RuntimeException("Невозможно определить информацию по файлу");
        }
    }
}
