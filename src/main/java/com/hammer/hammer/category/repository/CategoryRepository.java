package com.hammer.hammer.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hammer.hammer.category.entity.Category;

public interface CategoryRepository extends JpaRepository<Category,Long>{
	
}
