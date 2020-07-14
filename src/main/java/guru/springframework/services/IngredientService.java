package guru.springframework.services;

import guru.springframework.commands.IngredientCommand;

public interface IngredientService {
    IngredientCommand findByIdAndRecipeId(Long ingredientId, Long recipeId);

    IngredientCommand saveOrUpdateIngredientCommand(IngredientCommand ingredientCommand);
}
