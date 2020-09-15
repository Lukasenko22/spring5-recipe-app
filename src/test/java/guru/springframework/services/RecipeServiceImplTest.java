package guru.springframework.services;

import guru.springframework.converters.RecipeCommandToRecipe;
import guru.springframework.converters.RecipeToRecipeCommand;
import guru.springframework.domain.Recipe;
import guru.springframework.exceptions.NotFoundException;
import guru.springframework.repositories.RecipeRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RecipeServiceImplTest {

    RecipeService recipeService;

    @Mock
    RecipeRepository recipeRepository;

    @Mock
    RecipeToRecipeCommand recipeToRecipeCommand;

    @Mock
    RecipeCommandToRecipe recipeCommandToRecipe;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        recipeService = new RecipeServiceImpl(recipeRepository, recipeToRecipeCommand, recipeCommandToRecipe);
    }

    @Test
    public void getAllRecipes() throws Exception {
        Recipe recipe = new Recipe();
        Set<Recipe> recipesData = new HashSet<>();
        recipesData.add(recipe);

        when(recipeRepository.findAll()).thenReturn(recipesData);

        Set<Recipe> recipes = recipeService.getAllRecipes();
        assertEquals(1, recipes.size());
        verify(recipeRepository,times(1)).findAll();
    }

    @Test
    public void getRecipe() throws Exception {
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        Optional<Recipe> recipeOpt = Optional.of(recipe);

        when(recipeRepository.findById(anyLong())).thenReturn(recipeOpt);

        Recipe returnedRecipe = recipeService.getRecipe(1L);

        assertNotNull(returnedRecipe);
        verify(recipeRepository,times(1)).findById(anyLong());
    }

    @Test
    public void deleteById(){
        recipeService.deleteRecipeById(2L);
        // no 'when' because the method has void return type
        verify(recipeRepository,times(1)).deleteById(anyLong());
    }

    @Test(expected = NotFoundException.class)
    public void getRecipeByIdTestNotFound(){
        //given
        Optional<Recipe> recipeOptional = Optional.empty();

        //when
        when(recipeRepository.findById(anyLong())).thenReturn(recipeOptional);

        //then
        Recipe returnedRecipe = recipeService.getRecipe(1L);
     }
}