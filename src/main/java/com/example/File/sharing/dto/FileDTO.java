package com.example.File.sharing.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
public class FileDTO {
    private int id;
    private String name;
    private List<TagDTO> tags;
}
