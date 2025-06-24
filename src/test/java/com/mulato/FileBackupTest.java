package com.mulato;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FileBackupTest {

    @Test
    void testCountFilesEmptyFolder() throws IOException {
        File tempDir = new File("testDirEmpty");
        tempDir.mkdir();
        try {
            int count = invokeCountFiles(tempDir);
            assertEquals(0, count);
        } finally {
            tempDir.delete();
        }
    }

    @Test
    void testCountFilesWithFiles() throws IOException {
        File tempDir = new File("testDirFiles");
        tempDir.mkdir();
        File file1 = new File(tempDir, "a.txt");
        File file2 = new File(tempDir, "b.txt");
        file1.createNewFile();
        file2.createNewFile();
        try {
            int count = invokeCountFiles(tempDir);
            assertEquals(2, count);
        } finally {
            file1.delete();
            file2.delete();
            tempDir.delete();
        }
    }

    @Test
    void testCopyDirectory() throws IOException {
        File sourceDir = new File("sourceDir");
        File destDir = new File("destDir");
        sourceDir.mkdir();
        destDir.mkdir();
        File file = new File(sourceDir, "file.txt");
        try (FileWriter fw = new FileWriter(file)) {
            fw.write("test");
        }
        AtomicInteger filesProcessed = new AtomicInteger(0);

        try {
            FileBackup.copyDirectory(sourceDir, destDir, filesProcessed);
            File copiedFile = new File(destDir, "file.txt");
            assertTrue(copiedFile.exists());
            assertEquals(1, filesProcessed.get());
        } finally {
            file.delete();
            sourceDir.delete();
            for (File f : destDir.listFiles()) f.delete();
            destDir.delete();
        }
    }

    // Métodos auxiliares para acessar métodos privados/estáticos
    private int invokeCountFiles(File folder) {
        return FileBackup.countFiles(folder);
    }
}