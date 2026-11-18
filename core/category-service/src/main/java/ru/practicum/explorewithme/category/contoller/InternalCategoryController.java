package ru.practicum.explorewithme.category.contoller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explorewithme.category.dto.CategoryDto;
import ru.practicum.explorewithme.category.service.CategoryService;

import static ru.practicum.explorewithme.common.controller.ControllerConstants.*;

@RestController
@RequestMapping(path = ACCESS_INTERNAL + URL_CATEGORIES)
@RequiredArgsConstructor
@Validated
@SuppressWarnings("unused")
public class InternalCategoryController {
    private final CategoryService categoryService;

    @GetMapping("/{" + ID_CATEGORY + "}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable(name = ID_CATEGORY) long catId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(categoryService.getCategoryInternal(catId));
    }
}
