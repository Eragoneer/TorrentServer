package bg.sofia.uni.fmi.mjt.client.exceptions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;

public abstract class ClientLogger {
    private static final String LOGGER_PATH_FILE = "C:\\Users\\pc1\\Desktop\\Java Rewind\\TorrentServer" +
            "\\src\\bg\\sofia\\uni\\fmi\\mjt\\client\\exceptions\\logger.txt";

    private static final Date DATE = new Date();

    public static void createLogger() {
        File logger = Path.of(LOGGER_PATH_FILE).toFile();
        try {
            logger.createNewFile();
        } catch (IOException e) {
            System.out.printf("Attempting to create a logger file with path %s has failed%s",
                    LOGGER_PATH_FILE, System.lineSeparator());
        }
    }

    public static void deleteLogger() {
        File logger = Path.of(LOGGER_PATH_FILE).toFile();
        try {
            logger.delete();
        } catch (SecurityException e) {
            System.out.printf("Attempting to clear the logger file with path %s has failed%s",
                    LOGGER_PATH_FILE, System.lineSeparator());
        }
    }

    public static void appendLogger(String message) {
        File logger = Path.of(LOGGER_PATH_FILE).toFile();
        try (FileWriter out = new FileWriter(logger, true)) {
            out.write("***************************");
            out.write(System.lineSeparator());
            out.write(System.lineSeparator());
            out.write(DATE.toString());
            out.write(" : ");
            out.write(message);
            out.write(System.lineSeparator());
            out.write(System.lineSeparator());
            out.write("***************************");
            out.flush();
        } catch (IOException e) {
            System.out.printf("Attempting to append to the logger file with path %s has failed%s",
                    LOGGER_PATH_FILE, System.lineSeparator());
        }
    }

    public static void appendLogger(StackTraceElement[] elements) {
        File logger = Path.of(LOGGER_PATH_FILE).toFile();
        try (FileWriter out = new FileWriter(logger, true)) {
            out.write("***************************");
            out.write(System.lineSeparator());
            out.write(System.lineSeparator());
            for (StackTraceElement element : elements) {
                out.write(DATE.toString());
                out.write(" : ");
                out.write(element.toString());
                out.write(System.lineSeparator());
                out.flush();
            }
            out.write(System.lineSeparator());
            out.write("***************************");
            out.flush();
        } catch (IOException e) {
            System.out.printf("Attempting to append to the logger file with path %s has failed%s",
                    LOGGER_PATH_FILE, System.lineSeparator());
        }
    }
}
