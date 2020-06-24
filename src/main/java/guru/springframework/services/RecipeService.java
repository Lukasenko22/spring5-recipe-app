package guru.springframework.services;

import guru.springframework.domain.Recipe;
import guru.springframework.repositories.RecipeRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface RecipeService {

    Set<Recipe> getAllRecipes();
    Recipe getRecipe(Long recipeId);
}
