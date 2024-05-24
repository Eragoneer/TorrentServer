package bg.sofia.uni.fmi.mjt.response;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;

public record Response(Status status, String username, Collection<Path> filePaths) {

    public static Response register(String username, Collection<Path> filePaths) {
        return new Response(Status.REGISTER, username, filePaths);
    }

    public static Response unregister(String username, Collection<Path> filePaths) {
        return new Response(Status.UNREGISTER, username, filePaths);
    }

    public static Response listFiles(String username, Collection<Path> filePaths) {
        return new Response(Status.LIST_FILES, username, filePaths);
    }

    public static Response exists(String username) {
        return new Response(Status.EXISTS, username, null);
    }

    public static Response unauthorized(String username) {
        return new Response(Status.UNAUTHORIZED, username, null);
    }

    private String iteratePathCollection() {
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<Path> it = filePaths.iterator(); // check for iterator
        if (it.hasNext()) {
            stringBuilder.append(it.next());
        }
        while (it.hasNext()) {
            stringBuilder.append(", ").append(it.next());
        }
        return stringBuilder.toString();
    }

    private String formatRegister() {
        return String.format("%s from %s : %s", status.getName(), username, iteratePathCollection());
    }

    private String formatUnregister() {
        return String.format("%s from %s : %s", status.getName(), username, iteratePathCollection());
    }

    private String formatListFiles() {
        return String.format("%s from %s are: %s", status.getName(), username, iteratePathCollection());
    }

    private String formatExists() {
        return String.format("Username %s", status.getName());
    }

    private String formatUnauthorized() {
        return String.format("%s to modify %s", status.getName(), username);
    }

    @Override
    public String toString() {
        return switch (status) {
            case REGISTER -> formatRegister();
            case UNREGISTER -> formatUnregister();
            case LIST_FILES -> formatListFiles();
            case EXISTS -> formatExists();
            case UNAUTHORIZED -> formatUnauthorized();
        };
    }

}
