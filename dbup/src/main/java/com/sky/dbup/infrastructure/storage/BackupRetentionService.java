package com.sky.dbup.infrastructure.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class BackupRetentionService {

    private static final int MAX_BACKUPS = 5;

    public void enforceRetention(Path backupDirectory) throws IOException {
        if(!Files.exists(backupDirectory)){
            return;
        }
        try(Stream<Path> files = Files.list(backupDirectory)){
            List<Path> sorted = files.filter(Files::isRegularFile)
                    .sorted(Comparator.comparingLong(
                            this::getLastModifiedTime
                    ).reversed())
                    .toList();
            if(sorted.size() <= MAX_BACKUPS){
                return;
            }
            List<Path> toDelete = sorted.subList(MAX_BACKUPS, sorted.size());
            for(Path file : toDelete){
                Files.deleteIfExists(file);
            }
        }
    }

    private long getLastModifiedTime(Path path){
        try{
            return Files.getLastModifiedTime(path).toMillis();
        } catch (IOException e){
            return 0;
        }
    }
}
