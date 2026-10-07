package com.possystem.catalog.service;

import com.possystem.catalog.dto.CategoryDto;
import com.possystem.catalog.entity.Category;
import com.possystem.catalog.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryDto> getAllCategories(){
        return categoryRepository.findAll().stream()
                .map(cat -> new CategoryDto(cat.getId(), cat.getName(), cat.getDescription()))
                .toList();
    }


    public CategoryDto createCategory(CategoryDto dto){
        Category category = Category.builder()
                .name(dto.name())
                .description(dto.description())
                .build();

        Category saved = categoryRepository.save(category);
        return new CategoryDto(saved.getId(), saved.getName(), saved.getDescription());
    }

}
