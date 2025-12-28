package com.ecom.repository;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ecom.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer>{

	public boolean existsByName(String name);
	
	@Query("SELECT COUNT(c) > 0 FROM Category c WHERE LOWER(c.name)=LOWER(:name) AND c.id <> :id")
	boolean existsByNameAndNotId(@Param("name") String name, @Param("id") int id);

	public List<Category> findByIsActiveTrue();

}
