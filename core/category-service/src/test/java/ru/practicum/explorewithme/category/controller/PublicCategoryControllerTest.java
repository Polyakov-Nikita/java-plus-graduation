package ru.practicum.explorewithme.category.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.explorewithme.category.contoller.PublicCategoryController;
import ru.practicum.explorewithme.common.service.CategoryService;
import ru.practicum.test.utils.ControllerTest;

import static ru.practicum.explorewithme.common.controller.ControllerConstants.*;

public class PublicCategoryControllerTest extends ControllerTest {
    @InjectMocks
    private PublicCategoryController publicCategoryController;
    @Mock
    private CategoryService categoryService;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(publicCategoryController)
                .build();
    }

    @Test
    public void getCategories_StatusOK() {
        // Arrange
        int from = 0;
        int size = 10;

        // Act
        ResultActions result = performGet(createGetCategoriesUrl(from, size));

        // Assert
        expectStatusOk(result);
        assertMethodCall(categoryService, service ->
                service.getCategories(Mockito.eq(from), Mockito.eq(size)));
    }

    @Test
    public void getCategory_StatusOK() {
        // Arrange
        long categoryId = 1L;

        // Act
        ResultActions result = performGet(createIdUrl(URL_CATEGORIES, categoryId));

        // Assert
        expectStatusOk(result);
        assertMethodCall(categoryService, service ->
                service.getCategory(Mockito.eq(categoryId)));
    }

    private String createGetCategoriesUrl(int from, int size) {
        return String.format("%s?%s=%d&%s=%d",
                URL_CATEGORIES,
                PARAM_FROM, from,
                PARAM_SIZE, size);
    }
}
