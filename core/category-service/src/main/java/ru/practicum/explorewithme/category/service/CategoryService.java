package ru.practicum.explorewithme.category.service;

import ru.practicum.explorewithme.category.dto.CategoryDto;
import ru.practicum.explorewithme.category.dto.NewCategoryDto;
import ru.practicum.explorewithme.category.dto.UpdateCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(NewCategoryDto body);

    void deleteCategory(long catId);

    CategoryDto updateCategory(long catId, UpdateCategoryDto body);

    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategory(long catId);

    CategoryDto getCategoryInternal(long catId);
}
