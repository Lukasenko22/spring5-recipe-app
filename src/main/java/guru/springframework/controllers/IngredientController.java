package guru.springframework.controllers;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.commands.RecipeCommand;
import guru.springframework.commands.UnitOfMeasureCommand;
import guru.springframework.domain.UnitOfMeasure;
import guru.springframework.services.IngredientService;
import guru.springframework.services.RecipeService;
import guru.springframework.services.UnitOfMeasureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Slf4j
@Controller
public class IngredientController {
    private RecipeService recipeService;
    private IngredientService ingredientService;
    private UnitOfMeasureService unitOfMeasureService;

    @Autowired
    public IngredientController(RecipeService recipeService,
                                IngredientService ingredientService,
                                UnitOfMeasureService unitOfMeasureService){
        this.recipeService = recipeService;
        this.ingredientService = ingredientService;
        this.unitOfMeasureService = unitOfMeasureService;
    }

    @GetMapping("/recipe/{id}/ingredients")
    public String getIngredientList(@PathVariable String id, Model model){
        RecipeCommand recipeCommand = recipeService.findRecipeCommandById(Long.valueOf(id));
        model.addAttribute("recipe",recipeCommand);
        return "recipe/ingredient/list";
    }

    @GetMapping("/recipe/{recipeId}/ingredient/{ingredientId}/show")
    public String showRecipeIngredient(@PathVariable String recipeId,
                                       @PathVariable String ingredientId,
                                       Model model){
        model.addAttribute("ingredient",
                ingredientService.findByIdAndRecipeId(Long.valueOf(ingredientId), Long.valueOf(recipeId)));
        return "recipe/ingredient/show";
    }

    @GetMapping("/recipe/{recipeId}/ingredient/{ingredientId}/update")
    public String showUpdateIngredientForm(@PathVariable String recipeId,
                                           @PathVariable String ingredientId,
                                           Model model){
        IngredientCommand ingredientCommand = ingredientService
                .findByIdAndRecipeId(Long.valueOf(ingredientId), Long.valueOf(recipeId));
        model.addAttribute("ingredient",ingredientCommand);

        List<UnitOfMeasureCommand> unitOfMeasureList = unitOfMeasureService.findAllUnitOfMeasures();
        model.addAttribute("uomList",unitOfMeasureList);

        return "recipe/ingredient/ingredient-form";
    }
}
