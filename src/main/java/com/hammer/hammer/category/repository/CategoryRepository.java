package com.hammer.hammer.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hammer.hammer.category.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long>{
	
}
