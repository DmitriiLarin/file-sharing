package com.example.File.sharing.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;


@Entity
@Table
public class Comment {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "text")
    @NotEmpty(message = "comment should not be empty")
    private String text;

    @ManyToOne
    @JoinColumn(name = "file_id", referencedColumnName = "id")
    @JsonIgnore
    private File file;

    public Comment() {}

    public Comment(int id, String text, File file) {
        this.id = id;
        this.text = text;
        this.file = file;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public @NotEmpty(message = "comment should not be empty") String getText() {
        return text;
    }

    public void setText(@NotEmpty(message = "comment should not be empty") String text) {
        this.text = text;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
