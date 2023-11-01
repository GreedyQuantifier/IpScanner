package org.example.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class SyncWriter {
    File file;

    public SyncWriter(File file) {
        this.file = file;
    }

    public synchronized void writeToFile(List<String> data) {
        try (Writer writer = new FileWriter(file, true)) {
            for (String line : data) {
                writer.write(line);
                writer.append("\n");
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
