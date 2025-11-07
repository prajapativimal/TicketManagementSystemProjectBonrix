package com.example.demo.Service;




import com.example.demo.Repository.AdminCategoryRepository;
import com.example.demo.Repository.IssueRepository;
import com.example.demo.entity.AdminCategory;
import com.example.demo.entity.Issue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryAdminService {

    private final AdminCategoryRepository categoryRepository;
    private final IssueRepository issueRepository;

    // --- Category CRUD ---
    public AdminCategory createCategory(AdminCategory category) { return categoryRepository.save(category); }
    public List<AdminCategory> getAllCategories() { return categoryRepository.findAll(); }
    public void deleteCategory(Long id) { categoryRepository.deleteById(id); }

    // --- Issue CRUD ---
    public Issue addIssueToCategory(Long categoryId, Issue issueDetails) {
        AdminCategory category = categoryRepository.findById(categoryId).orElseThrow();
        issueDetails.setCategory(category);
        return issueRepository.save(issueDetails);
    }
    public void deleteIssue(Long issueId) { issueRepository.deleteById(issueId); }
}
