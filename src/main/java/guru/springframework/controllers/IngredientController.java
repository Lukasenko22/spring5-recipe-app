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
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping("/recipe/{id}/ingredients")
    public String getIngredientList(@PathVariable String id, Model model){
        RecipeCommand recipeCommand = recipeService.findRecipeCommandById(Long.valueOf(id));
        model.addAttribute("recipe",recipeCommand);
        return "recipe/ingredient/list";
    }

    @RequestMapping("/recipe/{recipeId}/ingredient/{ingredientId}/show")
    public String showRecipeIngredient(@PathVariable String recipeId,
                                       @PathVariable String ingredientId,
                                       Model model){
        model.addAttribute("ingredient",
                ingredientService.findByIdAndRecipeId(Long.valueOf(ingredientId), Long.valueOf(recipeId)));
        return "recipe/ingredient/show";
    }

    @RequestMapping("/recipe/{recipeId}/ingredient/{ingredientId}/update")
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

    @PostMapping("/recipe/{recipeId}/ingredient")
    public String saveOrUpdateIngredient(@ModelAttribute IngredientCommand ingredientCommand){
        IngredientCommand savedIngCommand = ingredientService.saveOrUpdateIngredientCommand(ingredientCommand);
        return "redirect:/recipe/"+savedIngCommand.getRecipeId()+"/ingredient/"+savedIngCommand.getId()+"/show";
    }

    @RequestMapping("/recipe/{recipeId}/ingredient/new")
    public String showNewIngredientForm(@PathVariable String recipeId, Model model){
        RecipeCommand recipeCommand = recipeService.findRecipeCommandById(Long.valueOf(recipeId));

        IngredientCommand ingredientCommand = new IngredientCommand();
        ingredientCommand.setRecipeId(recipeCommand.getId());

        List<UnitOfMeasureCommand> unitOfMeasureCommands = unitOfMeasureService.findAllUnitOfMeasures();
        model.addAttribute("uomList", unitOfMeasureCommands);

        model.addAttribute("ingredient", ingredientCommand);
        return "recipe/ingredient/ingredient-form";
    }

    @RequestMapping("/recipe/{recipeId}/ingredient/{ingredientId}/delete")
    public String deleteIngredient(@PathVariable String recipeId,
                                   @PathVariable String ingredientId){
        ingredientService.deleteIngredientByIdAndRecipeId(Long.valueOf(ingredientId),Long.valueOf(recipeId));
        return "redirect:/recipe/"+recipeId+"/ingredients";
    }
}
