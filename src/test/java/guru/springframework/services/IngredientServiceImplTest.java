package guru.springframework.services;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.commands.UnitOfMeasureCommand;
import guru.springframework.converters.IngredientCommandToIngredient;
import guru.springframework.converters.IngredientToIngredientCommand;
import guru.springframework.converters.UnitOfMeasureCommandToUnitOfMeasure;
import guru.springframework.converters.UnitOfMeasureToUnitOfMeasureCommand;
import guru.springframework.domain.Ingredient;
import guru.springframework.domain.Recipe;
import guru.springframework.domain.UnitOfMeasure;
import guru.springframework.repositories.RecipeRepository;
import guru.springframework.repositories.UnitOfMeasureRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class IngredientServiceImplTest {

    IngredientService ingredientService;

    @Mock
    RecipeRepository recipeRepository;

    @Mock
    UnitOfMeasureRepository unitOfMeasureRepository;

    IngredientToIngredientCommand ingredientToIngredientCommand;
    IngredientCommandToIngredient ingredientCommandToIngredient;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ingredientToIngredientCommand = new IngredientToIngredientCommand(new UnitOfMeasureToUnitOfMeasureCommand());
        ingredientCommandToIngredient = new IngredientCommandToIngredient(new UnitOfMeasureCommandToUnitOfMeasure());
        ingredientService = new IngredientServiceImpl(recipeRepository, unitOfMeasureRepository,
                ingredientToIngredientCommand, ingredientCommandToIngredient);
    }

    @Test
    public void findByIdAndRecipeId() throws Exception {
        //given
        Recipe recipe = new Recipe();
        recipe.setId(1L);

        Ingredient ingredient1 = new Ingredient();
        ingredient1.setId(1L);

        Ingredient ingredient2 = new Ingredient();
        ingredient2.setId(2L);

        recipe.addIngredient(ingredient1);
        recipe.addIngredient(ingredient2);

        Optional<Recipe> recipeOptional = Optional.of(recipe);

        //when
        when(recipeRepository.findById(anyLong())).thenReturn(recipeOptional);

        //then
        IngredientCommand ingredientCommand = ingredientService.findByIdAndRecipeId(2L,1L);

        verify(recipeRepository,times(1)).findById(anyLong());
        assertEquals(ingredientCommand.getId().longValue(),2L);
        assertEquals(ingredientCommand.getRecipeId().longValue(),1L);
    }

    @Test
    public void testSaveIngredientCommand(){
        //given
        IngredientCommand ingredientCommand = new IngredientCommand();
        ingredientCommand.setRecipeId(1L);

        Optional<Recipe> recipeOpt = Optional.of(new Recipe());

        Recipe savedRecipe = new Recipe();
        Ingredient ingredient = new Ingredient();
        ingredient.setId(2L);
        savedRecipe.addIngredient(ingredient);

        when(recipeRepository.findById(anyLong())).thenReturn(recipeOpt);
        when(recipeRepository.save(any())).thenReturn(savedRecipe);

        //when
        IngredientCommand savedIngredientCommand = ingredientService.saveOrUpdateIngredientCommand(ingredientCommand);

        //then
        assertEquals(2L,savedIngredientCommand.getId().longValue());
        verify(recipeRepository,times(1)).findById(anyLong());
        verify(recipeRepository,times(1)).save(any());

    }

    @Test
    public void testUpdateIngredientCommand(){
        //given
        IngredientCommand ingredientCommand = new IngredientCommand();
        ingredientCommand.setId(2L);
        ingredientCommand.setRecipeId(1L);
        ingredientCommand.setDescription("Test updated recipe");
        UnitOfMeasureCommand unitOfMeasureCommand = new UnitOfMeasureCommand();
        unitOfMeasureCommand.setId(1L);
        ingredientCommand.setUnitOfMeasureCommand(unitOfMeasureCommand);

        Optional<Recipe> recipeOpt = Optional.of(new Recipe());

        Ingredient ingredient1 = new Ingredient();
        ingredient1.setId(1L);
        ingredient1.setDescription("Default ingredient1 description");
        Ingredient ingredient2 = new Ingredient();
        ingredient2.setId(2L);
        ingredient2.setDescription("Default ingredient2 description");
        recipeOpt.get().addIngredient(ingredient1);
        recipeOpt.get().addIngredient(ingredient2);

        Recipe savedRecipe = new Recipe();
        Ingredient ingredient = new Ingredient();
        ingredient.setId(2L);
        ingredient.setDescription("Test updated recipe");
        savedRecipe.addIngredient(ingredient1);
        savedRecipe.addIngredient(ingredient);

        UnitOfMeasure uom = new UnitOfMeasure();
        uom.setId(1L);
        uom.setDescription("Each");
        Optional<UnitOfMeasure> uomOpt = Optional.of(uom);

        when(recipeRepository.findById(anyLong())).thenReturn(recipeOpt);
        when(recipeRepository.save(any())).thenReturn(savedRecipe);
        when(unitOfMeasureRepository.findById(anyLong())).thenReturn(uomOpt);

        //when
        IngredientCommand savedIngredientCommand = ingredientService.saveOrUpdateIngredientCommand(ingredientCommand);

        //then
        verify(recipeRepository,times(1)).findById(anyLong());
        verify(recipeRepository,times(1)).save(any());
        assertEquals(2L,savedIngredientCommand.getId().longValue());
        assertEquals("Test updated recipe",savedIngredientCommand.getDescription());
    }

}