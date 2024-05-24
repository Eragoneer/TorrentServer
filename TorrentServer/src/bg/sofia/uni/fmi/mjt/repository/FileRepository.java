package bg.sofia.uni.fmi.mjt.repository;

import bg.sofia.uni.fmi.mjt.response.Response;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

public class FileRepository {
    private HashSet<Path> filePath;

    public FileRepository() {
        filePath = new HashSet<>();
    }

    public Response register(String username, Collection<String> paths) {

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username can't be null");
        }
        if (paths == null) {
            throw new IllegalArgumentException("Paths can't be null");
        }
        Collection<Path> validRegistrations = new LinkedList<>();
        for (String pathString : paths) {
            Path path = Paths.get(pathString);
            if (!filePath.contains(path)) {
                validRegistrations.add(path);
                filePath.add(path);
            }
        }
        return Response.register(username, validRegistrations);
    }

    public Response unregister(String username, Collection<String> paths) {

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username can't be null");
        }
        if (paths == null) {
            throw new IllegalArgumentException("Paths can't be null");
        }
        Collection<Path> validUnregistrations = new LinkedList<>();
        for (String pathString : paths) {
            Path path = Paths.get(pathString);
            if (filePath.contains(path)) {
                validUnregistrations.add(path);
                filePath.remove(path);
            }
        }
        return Response.unregister(username, validUnregistrations);
    }

    public Response listFiles(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username can't be null");
        }
        return Response.listFiles(username, filePath);
    }

    public Response exists(String username) {

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username can't be null");
        }

        return Response.exists(username);
    }

    public Response unauthorized(String username) {

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username can't be null");
        }

        return Response.unauthorized(username);
    }
}
