package ru.blogic.uzedometric.data;

public enum Project {
    GPN("gpn"),
    VTBL("vtbl"),
    RN("rn"),
    VANKOR("vankor");

    private final String folder;

    Project(String folder) {
        this.folder = folder;
    }

    public String getFolder() {
        return this.folder;
    }
}
