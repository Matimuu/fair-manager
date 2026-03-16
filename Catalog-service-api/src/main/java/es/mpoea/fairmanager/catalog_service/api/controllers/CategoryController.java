package es.mpoea.fairmanager.catalog_service.api.controllers;

import es.mpoea.fairmanager.catalog_service.api.commands.category.CreateCategoryCommand;
import es.mpoea.fairmanager.catalog_service.api.commands.category.UpdateCategoryCommand;
import es.mpoea.fairmanager.catalog_service.api.mappers.CategoryMapper;
import es.mpoea.fairmanager.catalog_service.api.services.CategoryService;
import es.mpoea.fairmanager.catalog_service.persistence.models.Category;
import es.mpoea.fairmanager.commondata.DTO.requests.category.CreateCategoryRequest;
import es.mpoea.fairmanager.commondata.DTO.requests.category.UpdateCategoryRequest;
import es.mpoea.fairmanager.commondata.DTO.responses.category.CategoryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody @Valid CreateCategoryRequest request, UriComponentsBuilder uriBuilder) {
        CreateCategoryCommand command = categoryMapper.toCreateCommand(request);

        Category category = categoryService.createCategory(command);

        URI location = uriBuilder
                .path("/api/v1/categories/{id}")
                .buildAndExpand(category.getId())
                .toUri();

        CategoryResponse response = categoryMapper.toResponse(category);

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable("id") long id) {
        Category category = categoryService.getCategory(id);

        CategoryResponse response = categoryMapper.toResponse(category);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping(params = "name")
    public ResponseEntity<CategoryResponse> getCategoryByName(@RequestParam("name") String name) {
        Category category = categoryService.getCategory(name);

        CategoryResponse response = categoryMapper.toResponse(category);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping(params = "!name")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();

        List<CategoryResponse> responses = categories.stream()
                .map(categoryMapper::toResponse)
                .toList();

        return ResponseEntity.ok().body(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable("id") long id, @RequestBody @Valid UpdateCategoryRequest request) {
        UpdateCategoryCommand command = categoryMapper.toUpdateCommand(request);

        Category category = categoryService.updateCategory(id, command);

        CategoryResponse response = categoryMapper.toResponse(category);

        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategoryById(@PathVariable("id") long id) {
        categoryService.deleteCategory(id);

        return ResponseEntity.noContent().build();
    }
}
