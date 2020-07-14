package guru.springframework.controllers;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.commands.RecipeCommand;
import guru.springframework.converters.IngredientToIngredientCommand;
import guru.springframework.converters.UnitOfMeasureToUnitOfMeasureCommand;
import guru.springframework.domain.Ingredient;
import guru.springframework.domain.Recipe;
import guru.springframework.services.IngredientService;
import guru.springframework.services.RecipeService;
import guru.springframework.services.UnitOfMeasureService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class IngredientControllerTest {

    @Mock
    RecipeService recipeService;

    @Mock
    IngredientService ingredientService;

    @Mock
    UnitOfMeasureService unitOfMeasureService;

    @Mock
    Model model;

    IngredientController ingredientController;

    MockMvc mockmvc;
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ingredientController = new IngredientController(recipeService, ingredientService, unitOfMeasureService);
        mockmvc = MockMvcBuilders.standaloneSetup(ingredientController).build();
    }

    @Test
    public void getIngredientList() throws Exception {
        RecipeCommand recipeCommand = new RecipeCommand();
        Ingredient ingredient1 = new Ingredient();
        ingredient1.setDescription("ING1");
        Ingredient ingredient2 = new Ingredient();
        ingredient1.setDescription("ING2");
        IngredientToIngredientCommand ingredientToIngredientCommand = new IngredientToIngredientCommand(new UnitOfMeasureToUnitOfMeasureCommand());

        recipeCommand.getIngredients().add(ingredientToIngredientCommand.convert(ingredient1));
        recipeCommand.getIngredients().add(ingredientToIngredientCommand.convert(ingredient2));

        when(recipeService.findRecipeCommandById(anyLong())).thenReturn(recipeCommand);

        mockmvc.perform(get("/recipe/1/ingredients"))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe/ingredient/list"))
                .andExpect(model().attributeExists("recipe"));

        verify(recipeService,times(1)).findRecipeCommandById(anyLong());
    }

    @Test
    public void showIngredientTest() throws Exception {
        //given
        IngredientCommand ingredientCommand = new IngredientCommand();

        //when
        when(ingredientService.findByIdAndRecipeId(anyLong(),anyLong())).thenReturn(ingredientCommand);

        //then
        mockmvc.perform(get("/recipe/1/ingredient/1/show"))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe/ingredient/show"))
                .andExpect(model().attributeExists("ingredient"));

        verify(ingredientService,times(1)).findByIdAndRecipeId(anyLong(),anyLong());
    }

    @Test
    public void saveOrUpdateIngredientTest() throws Exception {
        //given
        IngredientCommand ingredientCommand = new IngredientCommand();
        ingredientCommand.setId(2L);
        ingredientCommand.setRecipeId(1L);

        //when
        when(ingredientService.saveOrUpdateIngredientCommand(any())).thenReturn(ingredientCommand);

        //then
        mockmvc.perform(post("/recipe/1/ingredient")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id","")
                .param("description","Test some string"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/recipe/1/ingredient/2/show"));

        verify(ingredientService,times(1)).saveOrUpdateIngredientCommand(any());
    }

}