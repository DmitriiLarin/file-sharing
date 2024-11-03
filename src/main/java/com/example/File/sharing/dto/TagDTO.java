package com.example.File.sharing.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
public class TagDTO {
    private List<Integer> tags;
    private int id;
    private String name;
}

