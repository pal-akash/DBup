package com.sky.dbup.infrastructure.storage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class BackupCompressionService {
    public Path compressToZip(Path sourceFile) throws IOException{
        Path zipPath = Paths.get(sourceFile.toString() + ".zip");

        try(
                ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath));
            InputStream fis = Files.newInputStream(sourceFile)
        ){
            ZipEntry zipEntry = new ZipEntry(sourceFile.getFileName().toString());
            zos.putNextEntry(zipEntry);
            fis.transferTo(zos);
            zos.closeEntry();
        }

        Files.deleteIfExists(sourceFile);
        return zipPath;
    }
}
