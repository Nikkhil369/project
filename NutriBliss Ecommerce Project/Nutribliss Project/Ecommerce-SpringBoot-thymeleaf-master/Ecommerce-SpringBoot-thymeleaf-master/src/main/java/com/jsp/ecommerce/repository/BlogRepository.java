package com.jsp.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jsp.ecommerce.dto.Blog;

public interface BlogRepository extends JpaRepository<Blog, Integer> {
}
