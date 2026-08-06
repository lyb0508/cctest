package com.example.foodai.recipe.repository;

import com.example.foodai.recipe.entity.RecipeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepository extends JpaRepository<RecipeEntity, Long> {
}