package com.example.expensetrackingsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CategorySummaryDTO {

    private String type;
    private long count;

    private List<CategoryDTO> subcategories;

}
