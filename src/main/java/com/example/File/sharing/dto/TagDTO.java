package com.example.File.sharing.dto;

import java.util.List;

public class TagDTO {
    private List<Integer> tags;

    public List<Integer> getTags() {
        return tags;
    }

    public void setTags(List<Integer> tags) {
        this.tags = tags;
    }
    private int id; // Добавьте идентификатор
    private String name; // И имя

    // Конструкторы
    public TagDTO() {
    }

    public TagDTO(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

