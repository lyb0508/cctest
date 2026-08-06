package com.example.foodai.recipe.entity;

import com.example.foodai.recipe.vo.RecipeData;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "recipes")
public class RecipeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "generation_type")
    private String generationType;

    private String title;

    private String summary;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "recipe_data", nullable = false, columnDefinition = "jsonb")
    private RecipeData recipeData;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGenerationType() {
        return generationType;
    }

    public void setGenerationType(String generationType) {
        this.generationType = generationType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public RecipeData getRecipeData() {
        return recipeData;
    }

    public void setRecipeData(RecipeData recipeData) {
        this.recipeData = recipeData;
    }
}