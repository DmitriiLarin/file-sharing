package com.example.File.sharing.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    @OneToOne(mappedBy = "file")
    private LifeTime lifeTime;

    public void addTag(Tag tag) {
        this.tags.add(tag);
        tag.getFiles().add(this);
    }

    public void removeTag(Tag tag) {
        this.tags.remove(tag);
        tag.getFiles().remove(this);
    }

}
