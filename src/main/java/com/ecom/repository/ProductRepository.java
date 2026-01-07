package com.ecom.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ecom.model.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

	public List<Product> findByIsActiveTrue();

	public List<Product> findAllByCategory(String category);
	
	public List<Product> findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(String ch,String ch2);

	public Page<Product> findByIsActiveTrue(Pageable pageable);

	public Page<Product> findAllByCategory(Pageable pageable,String category);

}
