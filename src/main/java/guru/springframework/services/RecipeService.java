package guru.springframework.services;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.domain.Recipe;
import guru.springframework.repositories.RecipeRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public interface RecipeService {

    Set<Recipe> getAllRecipes();
    Recipe getRecipe(Long recipeId);
    RecipeCommand saveRecipeCommand(RecipeCommand command);
    RecipeCommand findRecipeCommandById(Long commandId);
    void deleteRecipeById(Long id);
}
