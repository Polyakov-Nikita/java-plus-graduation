package ru.practicum.explorewithme.category.mapper;

import ru.practicum.explorewithme.common.dto.CategoryDto;
import ru.practicum.explorewithme.common.dto.NewCategoryDto;
import ru.practicum.explorewithme.common.dto.UpdateCategoryDto;
import ru.practicum.explorewithme.category.entity.Category;

public class CategoryMapper {
    private CategoryMapper() {

    }

    public static Category toCategory(NewCategoryDto newCategoryDto) {
        return Category.builder()
                .name(newCategoryDto.getName())
                .build();
    }

    public static Category toCategory(Category category, UpdateCategoryDto updateCategoryDto) {
        category.setName(updateCategoryDto.getName());
        return category;
    }


    public static CategoryDto toCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
