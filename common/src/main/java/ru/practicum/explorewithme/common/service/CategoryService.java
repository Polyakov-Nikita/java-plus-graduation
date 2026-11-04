package ru.practicum.explorewithme.common.service;

import ru.practicum.explorewithme.common.dto.CategoryDto;
import ru.practicum.explorewithme.common.dto.NewCategoryDto;
import ru.practicum.explorewithme.common.dto.UpdateCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(NewCategoryDto body);

    void deleteCategory(long catId);

    CategoryDto updateCategory(long catId, UpdateCategoryDto body);

    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategory(long catId);
}
