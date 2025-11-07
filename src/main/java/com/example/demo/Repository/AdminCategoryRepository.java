package com.example.demo.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.AdminCategory;

public interface AdminCategoryRepository extends JpaRepository<AdminCategory, Long> 
{
    Optional<AdminCategory> findByCategoryName(String categoryName);

	
}
