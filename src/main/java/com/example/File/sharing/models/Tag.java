package com.example.File.sharing.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.Set;

@Entity
@Table
public class Tag {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    @NotEmpty(message = "name should not be empty")
    private String name;

    @ManyToMany
    @JoinTable(
            name = "File_Tag",
            joinColumns = @JoinColumn(name = "tag_id"),
            inverseJoinColumns = @JoinColumn(name = "file_id"))
    @JsonIgnore
    private Set<File> files;

    public Set<File> getFiles() {
        return files;
    }

    public void setFiles(Set<File> files) {
        this.files = files;
    }

    public Tag() {
    }

    public Tag(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public @NotEmpty(message = "name should not be empty") String getName() {
        return name;
    }

    public void setName(@NotEmpty(message = "name should not be empty") String name) {
        this.name = name;
    }

    public void addFile(File file) {
        this.files.add(file);
        file.getTags().add(this);
    }

    public void removeFile(File file) {
        this.files.remove(file);
        file.getTags().remove(this);
    }
}
