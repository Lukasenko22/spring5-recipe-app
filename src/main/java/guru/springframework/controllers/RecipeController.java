package guru.springframework.controllers;


import guru.springframework.domain.Recipe;
import guru.springframework.services.RecipeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
public class RecipeController {

    private RecipeService recipeService;

    @Autowired
    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }
    @RequestMapping({"recipe/show/{id}"})
    public String getRecipeDetailById(@PathVariable String id,  Model model){
        Recipe recipe = recipeService.getRecipe(Long.valueOf(id));
        model.addAttribute("recipe", recipe);
        return "recipe/show";
    }
}
