package me.monmcgt.code.massiner.utilities;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    public String[] getLines(File file) throws IOException {
        /*try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {*/
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8))) {
            String line;
            List<String> lines = new ArrayList<>();

            while ((line = bufferedReader.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }

                lines.add(line);
            }

            return lines.toArray(new String[0]);
        }
    }

    public void writeFile(File file, String[] lines) throws IOException {
        if (!file.exists()) {
            if (!file.createNewFile()) {
                System.err.println("Failed to create file: " + file.getAbsolutePath());
//                this.logger.error("Failed to create file: " + file.getAbsolutePath());
            }
        }

        StringBuilder singleLine = new StringBuilder();

        for (String line : lines) {
            singleLine.append(line).append(System.lineSeparator());
        }

        try {
            try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8))) {
                bufferedWriter.write(singleLine.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
