package guru.springframework.services;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.converters.IngredientCommandToIngredient;
import guru.springframework.converters.IngredientToIngredientCommand;
import guru.springframework.domain.Ingredient;
import guru.springframework.domain.Recipe;
import guru.springframework.domain.UnitOfMeasure;
import guru.springframework.repositories.RecipeRepository;
import guru.springframework.repositories.UnitOfMeasureRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.Optional;

@Slf4j
@Service
public class IngredientServiceImpl implements IngredientService {

    private RecipeRepository recipeRepository;
    private UnitOfMeasureRepository unitOfMeasureRepository;
    private IngredientToIngredientCommand ingredientToIngredientCommand;
    private IngredientCommandToIngredient ingredientCommandToIngredient;

    @Autowired
    public IngredientServiceImpl(RecipeRepository recipeRepository,
                                 UnitOfMeasureRepository unitOfMeasureRepository,
                                 IngredientToIngredientCommand ingredientToIngredientCommand,
                                 IngredientCommandToIngredient ingredientCommandToIngredient) {
        this.recipeRepository = recipeRepository;
        this.unitOfMeasureRepository = unitOfMeasureRepository;
        this.ingredientToIngredientCommand = ingredientToIngredientCommand;
        this.ingredientCommandToIngredient = ingredientCommandToIngredient;
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

    @Override
    @Transactional
    public IngredientCommand saveOrUpdateIngredientCommand(IngredientCommand ingredientCommand) {
        Optional<Recipe> recipeOpt = recipeRepository.findById(ingredientCommand.getRecipeId());
        if (!recipeOpt.isPresent()){
            throw new RuntimeException("Recipe with id="+ingredientCommand.getRecipeId()+" not found!");
        }

        Recipe recipe = recipeOpt.get();

        boolean foundIngredient = false;
        for (Ingredient ingredient : recipe.getIngredients()) {
            if (ingredient.getId().equals(ingredientCommand.getId())){
                // Update existing ingredient
                ingredient.setDescription(ingredientCommand.getDescription());
                ingredient.setAmount(ingredientCommand.getAmount());

                Optional<UnitOfMeasure> uomOpt = unitOfMeasureRepository
                        .findById(ingredientCommand.getUnitOfMeasureCommand().getId());

                if (!uomOpt.isPresent()){
                    throw new RuntimeException("Unit of measure with " +
                            "id="+ingredientCommand.getUnitOfMeasureCommand().getId()+" not found!");
                }

                ingredient.setUnitOfMeasure(uomOpt.get());
                foundIngredient = true;
            }
        }

        if (recipe.getIngredients().isEmpty() || !foundIngredient){
            // Add new Ingredient
            ingredientCommand.setRecipeId(recipe.getId());
            recipe.addIngredient(ingredientCommandToIngredient.convert(ingredientCommand));
        }

        Recipe savedRecipe = recipeRepository.save(recipe);

        for (Ingredient ingredient : savedRecipe.getIngredients()) {
            if (ingredient.getId().equals(ingredientCommand.getId())){
                return ingredientToIngredientCommand.convert(ingredient);
            }
        }
        return ingredientToIngredientCommand.convert(
                savedRecipe.getIngredients().get(savedRecipe.getIngredients().size()-1));
    }

    @Override
    @Transactional
    public void deleteIngredientByIdAndRecipeId(Long ingredientId, Long recipeId) {
        Optional<Recipe> recipeOpt = recipeRepository.findById(recipeId);
        if (!recipeOpt.isPresent()){
            throw new RuntimeException("Recipe with id="+recipeId+" not found!");
        }

        Recipe recipe = recipeOpt.get();
        Ingredient foundIngredient = null;
        for (Ingredient ingredient : recipe.getIngredients()) {
            if (ingredient.getId().equals(ingredientId)){
                foundIngredient = ingredient;
            }
        }

        if (foundIngredient == null){
            throw new RuntimeException("Ingredient id="+ingredientId+" for recipe id="+recipeId+" not found!");
        }

        foundIngredient.setRecipe(null);
        recipe.getIngredients().remove(foundIngredient);
        recipeRepository.save(recipe);

        log.debug("Ingredient with id="+ingredientId+" has been deleted!");
    }
}
