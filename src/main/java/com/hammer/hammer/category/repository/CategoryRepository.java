package com.hammer.hammer.category.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hammer.hammer.category.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long>{
	@Query("SELECT c FROM Category c")
	List<Category> findAllCategories();

}
