package bg.sofia.uni.fmi.mjt.response;

public enum Status {
    REGISTER("Registered"),
    UNREGISTER("Unregister"),
    LIST_FILES("List files"),
    EXISTS("Exists"),
    UNAUTHORIZED("Unauthorized");

    private String name;

    Status(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}