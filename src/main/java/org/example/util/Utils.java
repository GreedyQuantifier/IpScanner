package org.example.util;

import java.io.File;
import java.util.UUID;

public class Utils {
    public static int toInt(String value) {
        int number;
        try {
            number = Integer.parseInt(value);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Isn't a number");
        }
        return number;
    }

    public static File getName() {
        File file;
        do {
            file = new File(UUID.randomUUID() + ".txt");
        } while (file.exists());
        return file;

    }
}
