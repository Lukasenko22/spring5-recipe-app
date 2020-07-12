package guru.springframework.services;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.converters.IngredientToIngredientCommand;
import guru.springframework.domain.Ingredient;
import guru.springframework.domain.Recipe;
import guru.springframework.repositories.RecipeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class IngredientServiceImpl implements IngredientService {

    private RecipeRepository recipeRepository;
    private IngredientToIngredientCommand ingredientToIngredientCommand;

    @Autowired
    public IngredientServiceImpl(RecipeRepository recipeRepository,
                                 IngredientToIngredientCommand ingredientToIngredientCommand) {
        this.recipeRepository = recipeRepository;
        this.ingredientToIngredientCommand = ingredientToIngredientCommand;
    }

    @Override
    public IngredientCommand findByIdAndRecipeId(Long ingredientId, Long recipeId) {
        Optional<Recipe> recipeOptional = recipeRepository.findById(recipeId);
        if (!recipeOptional.isPresent()){
            throw new RuntimeException("Recipe with id="+recipeId+" not found");
        }

        Recipe recipe = recipeOptional.get();

        IngredientCommand ingredientCommand = null;
        for (Ingredient ingredient : recipe.getIngredients()) {
            if (ingredient.getId().equals(ingredientId)){
                ingredientCommand = ingredientToIngredientCommand.convert(ingredient);
                break;
            }
        }
        if (ingredientCommand == null){
            throw new RuntimeException("Ingredient with id="+ingredientId+" not found for recipe id="+recipeId);
        }

        return ingredientCommand;
    }
}
