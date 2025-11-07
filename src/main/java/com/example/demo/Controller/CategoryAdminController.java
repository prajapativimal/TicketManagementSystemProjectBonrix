package com.example.demo.Controller;



import com.example.demo.Service.CategoryAdminService;
import com.example.demo.entity.AdminCategory;
import com.example.demo.entity.Issue;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/addcategories")
@SecurityRequirement(name = "Bearer Authentication")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class CategoryAdminController {

    private final CategoryAdminService categoryService;

    // Create a new category (e.g., "payment")
    @PostMapping
    public ResponseEntity<AdminCategory> createCategory(@RequestBody AdminCategory category) {
        return ResponseEntity.ok(categoryService.createCategory(category));
    }

    // Add an issue to an existing category (e.g., add "ont working" to "payment")
    @PostMapping("/{categoryId}/issues")
    public ResponseEntity<Issue> createIssue(@PathVariable Long categoryId, @RequestBody Issue issue) {
        return ResponseEntity.ok(categoryService.addIssueToCategory(categoryId, issue));
    }

    // Get all categories and their issues
    @GetMapping
    public ResponseEntity<List<AdminCategory>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    // Delete a category and all its issues
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("Category and its issues deleted successfully.");
    }

    // Delete a single issue
    @DeleteMapping("/issues/{issueId}")
    public ResponseEntity<String> deleteIssue(@PathVariable Long issueId) {
        categoryService.deleteIssue(issueId);
        return ResponseEntity.ok("Issue deleted successfully.");
    }
}