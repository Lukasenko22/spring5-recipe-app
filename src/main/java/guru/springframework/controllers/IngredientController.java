package guru.springframework.controllers;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.services.RecipeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
public class IngredientController {
    private RecipeService recipeService;

    @Autowired
    public IngredientController(RecipeService recipeService){
        this.recipeService = recipeService;
    }

    @RequestMapping("/recipe/{id}/ingredients")
    public String getIngredientList(@PathVariable String id, Model model){
        RecipeCommand recipeCommand = recipeService.findRecipeCommandById(Long.valueOf(id));
        model.addAttribute("recipe",recipeCommand);
        return "recipe/ingredient/list";
    }
}
