package ru.nikkitavr.tfs.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import org.springframework.stereotype.Component;

@Component
public class FileSystemUtils {

  /** Ensure that the parent directories for the given file exist. */
  public static void ensureParentExists(Path file) throws IOException {
    Path parent = file.getParent();
    if (parent != null) {
      Files.createDirectories(parent);
    }
  }

  /**
   * Append text to the file or create a new file if it does not exist.
   */
  public static void appendToFile(Path file, String content) throws IOException {
    ensureParentExists(file);
    if (Files.exists(file)) {
      Files.writeString(file, content, StandardOpenOption.APPEND);
    } else {
      Files.writeString(file, content);
    }
  }

  /**
   * Returns a unique file path by appending increasing numbers if needed.
   */
  public static Path getUniqueFilePath(Path file) {
    if (!Files.exists(file)) {
      return file;
    }
    String fileName = file.getFileName().toString();
    String baseName = fileName;
    String ext = "";
    int dot = fileName.lastIndexOf('.');
    if (dot != -1) {
      baseName = fileName.substring(0, dot);
      ext = fileName.substring(dot);
    }
    int index = 1;
    Path parent = file.getParent();
    Path candidate;
    do {
      String newName = baseName + "-" + index + ext;
      candidate = parent == null ? Path.of(newName) : parent.resolve(newName);
      index++;
    } while (Files.exists(candidate));
    return candidate;
  }
}
