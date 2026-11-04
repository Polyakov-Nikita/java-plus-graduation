package ru.practicum.explorewithme.category.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import ru.practicum.explorewithme.common.dto.NewCategoryDto;
import ru.practicum.explorewithme.common.dto.UpdateCategoryDto;
import ru.practicum.explorewithme.category.entity.Category;
import ru.practicum.explorewithme.category.exception.CategoryNotFoundException;
import ru.practicum.explorewithme.category.exception.DuplicatedNameException;
import ru.practicum.explorewithme.category.exception.NotEmptyCategoryException;
import ru.practicum.explorewithme.category.mapper.CategoryMapper;
import ru.practicum.explorewithme.category.repository.CategoryRepository;
import ru.practicum.explorewithme.common.dto.CategoryDto;
import ru.practicum.explorewithme.common.service.EventService;
import ru.practicum.test.utils.ServiceTest;

import java.util.List;

public class CategoryServiceImplTest extends ServiceTest {
    @InjectMocks
    private CategoryServiceImpl categoryService;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private EventService eventService;

    @Test
    public void createCategory_ReturnsObject() {
        // Arrange
        NewCategoryDto body = NewCategoryDto.builder()
                .name("Category Name")
                .build();
        long savedId = 1L;
        Category saved = buildCategory(savedId, body);
        whenCategoryNotExistBy(body.getName());
        whenSaveReturns(categoryRepository, saved);

        // Act
        CategoryDto actual = categoryService.createCategory(body);

        // Assert
        CategoryDto expected = buildCategoryDto(saved);
        assertEquals(actual, expected);
    }

    @Test
    public void createCategory_ExistingName_DuplicatedDataException() {
        // Arrange
        NewCategoryDto body = NewCategoryDto.builder()
                .name("Category Name")
                .build();
        whenCategoryExistBy(body.getName());

        // Act
        Throwable thrown = Assertions.catchThrowable(() -> categoryService.createCategory(body));

        // Assert
        assertException(thrown, DuplicatedNameException.class);
    }

    @Test
    public void deleteCategory() {
        // Arrange
        long id = 1L;
        whenEntityExistIn(categoryRepository);
        whenEventsNotExistIn(id);
        doNothingOnDeleteIn(categoryRepository);

        // Act
        categoryService.deleteCategory(id);

        // Assert
        assertMethodCall(categoryRepository, repository -> repository.deleteById(Mockito.eq(id)));
    }

    @Test
    public void deleteCategory_AbsentCategory_NotFoundException() {
        // Arrange
        long absentId = 1L;
        whenEntityAbsentIn(categoryRepository);

        // Act
        Throwable thrown = Assertions.catchThrowable(() -> categoryService.deleteCategory(absentId));

        // Assert
        assertException(thrown, CategoryNotFoundException.class);
    }

    @Test
    public void deleteCategory_CategoryWithEvents_NotEmptyCategoryException() {
        // Arrange
        long id = 1L;
        whenEntityExistIn(categoryRepository);
        whenEventsExistIn(id);

        // Act
        Throwable thrown = Assertions.catchThrowable(() -> categoryService.deleteCategory(id));

        // Assert
        assertException(thrown, NotEmptyCategoryException.class);
    }

    @Test
    public void updateCategory_ReturnsObject() {
        // Arrange
        long savedId = 1L;
        UpdateCategoryDto body = UpdateCategoryDto.builder()
                .name("Category Name Update")
                .build();
        Category saved = buildCategory(savedId);
        whenEntityFoundIn(categoryRepository, saved);
        whenCategoryNotExistBy(body.getName());
        whenSaveReturns(categoryRepository, saved);

        // Act
        CategoryDto actual = categoryService.updateCategory(savedId, body);

        // Assert
        CategoryDto expected = buildCategoryDto(saved);
        assertEquals(actual, expected);
    }

    @Test
    public void updateCategory_NameNotChange_ReturnsObject() {
        // Arrange
        long savedId = 1L;
        UpdateCategoryDto body = UpdateCategoryDto.builder()
                .name("Category Name Update")
                .build();
        String existingName = body.getName();
        Category saved = buildCategory(savedId, existingName);
        whenEntityFoundIn(categoryRepository, saved);

        // Act
        CategoryDto actual = categoryService.updateCategory(savedId, body);

        // Assert
        CategoryDto expected = buildCategoryDto(saved);
        assertEquals(actual, expected);
        assertMethodNotCall(categoryRepository, repository ->
                repository.save(Mockito.any(Category.class)));
    }

    @Test
    public void updateCategory_AbsentCategory_NotFoundException() {
        // Arrange
        long absentId = 1L;
        UpdateCategoryDto body = UpdateCategoryDto.builder()
                .name("Category Name Update")
                .build();
        whenEntityNotFoundIn(categoryRepository);

        // Act
        Throwable thrown = Assertions.catchThrowable(() -> categoryService.updateCategory(absentId, body));

        // Assert
        assertException(thrown, CategoryNotFoundException.class);
    }

    @Test
    public void updateCategory_ExistingName_DuplicatedDataException() {
        // Arrange
        long savedId = 1L;
        UpdateCategoryDto body = UpdateCategoryDto.builder()
                .name("Category Name Update")
                .build();
        Category saved = buildCategory(savedId);
        whenEntityFoundIn(categoryRepository, saved);
        whenCategoryExistBy(body.getName());

        // Act
        Throwable thrown = Assertions.catchThrowable(() -> categoryService.updateCategory(savedId, body));

        // Assert
        assertException(thrown, DuplicatedNameException.class);
    }

    @Test
    public void getCategories_ReturnsArray() {
        // Arrange
        int from = 0;
        int size = 10;
        List<Category> categories = List.of(
                buildCategory(1),
                buildCategory(2)
        );
        whenFindWithOffsetReturns(categories);

        // Act
        List<CategoryDto> actual = categoryService.getCategories(from, size);

        // Assert
        List<CategoryDto> expected = categories.stream()
                .map(CategoryMapper::toCategoryDto)
                .toList();
        assertEquals(actual, expected);
    }

    @Test
    public void getCategory_ReturnsObject() {
        // Arrange
        long categoryId = 1L;
        Category category = buildCategory(categoryId);
        whenEntityFoundIn(categoryRepository, category);

        // Act
        CategoryDto actual = categoryService.getCategory(categoryId);

        // Assert
        CategoryDto expected = buildCategoryDto(category);
        assertEquals(actual, expected);
    }

    @Test
    public void getCategory_AbsentCategory_NotFoundException() {
        // Arrange
        long absentId = 1L;
        whenEntityNotFoundIn(categoryRepository);

        // Act
        Throwable thrown = Assertions.catchThrowable(() -> categoryService.getCategory(absentId));

        // Assert
        assertException(thrown, CategoryNotFoundException.class);
    }

    private void whenCategoryExistBy(String name) {
        Mockito.when(categoryRepository.existsByName(Mockito.eq(name)))
                .thenReturn(true);
    }

    private void whenCategoryNotExistBy(String name) {
        Mockito.when(categoryRepository.existsByName(Mockito.eq(name)))
                .thenReturn(false);
    }

    private void whenEventsExistIn(long categoryId) {
        Mockito.when(eventService.existsByCategoryId(Mockito.eq(categoryId)))
                .thenReturn(true);
    }

    private void whenEventsNotExistIn(long categoryId) {
        Mockito.when(eventService.existsByCategoryId(Mockito.eq(categoryId)))
                .thenReturn(false);
    }

    private void whenFindWithOffsetReturns(List<Category> categories) {
        Mockito.when(categoryRepository.findWithOffset(Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(categories);
    }

    private CategoryDto buildCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    private Category buildCategory(long id, String name) {
        return Category.builder()
                .id(id)
                .name(name)
                .build();
    }

    private Category buildCategory(long id) {
        return buildCategory(id, "Category Name");
    }

    private Category buildCategory(long id, NewCategoryDto newCategoryDto) {
        return buildCategory(id, newCategoryDto.getName());
    }
}
