package com.example.File.sharing.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table
public class File {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    @NotEmpty(message = "name should not be empty")
    private String name;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "file")
    private List<Comment> comments;

    @ManyToMany(mappedBy = "files")
    private Set<Tag> tags;

    public File() {}

    public File(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public void addTag(Tag tag) {
        this.tags.add(tag);
        tag.getFiles().add(this);
    }

    public void removeTag(Tag tag) {
        this.tags.remove(tag);
        tag.getFiles().remove(this);
    }

}
