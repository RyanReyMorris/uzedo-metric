package ru.blogic.uzedometric.storage;

import java.util.Objects;

public class LastFileLineInfo {

    private String file;
    private Long filePointer;

    public LastFileLineInfo(String file, Long filePointer) {
        this.file = file;
        this.filePointer = filePointer;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Long getFilePointer() {
        return filePointer;
    }

    public void setFilePointer(Long filePointer) {
        this.filePointer = filePointer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LastFileLineInfo that = (LastFileLineInfo) o;
        return Objects.equals(file, that.file) && Objects.equals(filePointer, that.filePointer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file, filePointer);
    }
}
