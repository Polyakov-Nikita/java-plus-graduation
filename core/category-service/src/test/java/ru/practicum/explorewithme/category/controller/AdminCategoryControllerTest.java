package ru.practicum.explorewithme.category.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.explorewithme.category.contoller.AdminCategoryController;
import ru.practicum.explorewithme.common.dto.NewCategoryDto;
import ru.practicum.explorewithme.common.dto.UpdateCategoryDto;
import ru.practicum.explorewithme.common.service.CategoryService;
import ru.practicum.test.utils.ControllerTest;

import static ru.practicum.explorewithme.common.controller.ControllerConstants.ACCESS_ADMIN;
import static ru.practicum.explorewithme.common.controller.ControllerConstants.URL_CATEGORIES;

public class AdminCategoryControllerTest extends ControllerTest {
    private static final String URL_BASE = ACCESS_ADMIN + URL_CATEGORIES;

    @InjectMocks
    private AdminCategoryController adminCategoryController;
    @Mock
    private CategoryService categoryService;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminCategoryController)
                .build();
    }

    @Test
    public void createCategory_StatusCreated() {
        // Arrange
        NewCategoryDto body = NewCategoryDto.builder()
                .name("Category Name")
                .build();

        // Act
        ResultActions result = performPost(URL_BASE, body);

        // Assert
        expectStatusCreated(result);
        assertMethodCall(categoryService, service ->
                service.createCategory(Mockito.any(NewCategoryDto.class)));
    }

    @Test
    public void deleteCategory_StatusNoContent() {
        // Arrange
        long catId = 1L;

        // Act
        ResultActions result = performDelete(createIdUrl(URL_BASE, catId));

        // Assert
        expectStatusNoContent(result);
        assertMethodCall(categoryService, service ->
                service.deleteCategory(Mockito.eq(catId)));
    }

    @Test
    public void updateCategory_StatusOk() {
        // Arrange
        long catId = 1L;
        NewCategoryDto body = NewCategoryDto.builder()
                .name("Category Name")
                .build();

        // Act
        ResultActions result = performPatch(createIdUrl(URL_BASE, catId), body);

        // Assert
        expectStatusOk(result);
        assertMethodCall(categoryService, service -> service.updateCategory(
                Mockito.eq(catId),
                Mockito.any(UpdateCategoryDto.class)
        ));
    }
}
