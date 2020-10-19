package guru.springframework.controllers;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.domain.Recipe;
import guru.springframework.exceptions.NotFoundException;
import guru.springframework.services.RecipeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Slf4j
@Controller
public class RecipeController {

    private RecipeService recipeService;

    @Autowired
    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @RequestMapping({"recipe/{id}/show"})
    public String getRecipeDetailById(@PathVariable String id,  Model model){
        Recipe recipe = recipeService.getRecipe(Long.valueOf(id));
        model.addAttribute("recipe", recipe);
        return "recipe/show";
    }

    @RequestMapping({"recipe/{id}/update"})
    public String showUpdateRecipeForm(@PathVariable String id,  Model model){
        RecipeCommand recipeCommand = recipeService.findRecipeCommandById(Long.valueOf(id));
        model.addAttribute("recipe", recipeCommand);
        return "recipe/recipe-form";
    }

    @RequestMapping("recipe/new")
    public String showNewRecipeForm(Model model){
        model.addAttribute("recipe",new RecipeCommand());
        return "recipe/recipe-form";
    }

    @PostMapping("recipe")
    public String saveOrUpdateRecipe(@Valid @ModelAttribute("recipe") RecipeCommand recipeCommand, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            bindingResult.getAllErrors().forEach(objectError -> {
                log.debug(objectError.toString());
            });
            return "recipe/recipe-form";
        }

        RecipeCommand savedCommand = recipeService.saveRecipeCommand(recipeCommand);
        return "redirect:/recipe/"+savedCommand.getId()+"/show";
    }

    @RequestMapping({"recipe/{id}/delete"})
    public String deleteRecipeById(@PathVariable String id){
        recipeService.deleteRecipeById(Long.valueOf(id));
        return "redirect:/index";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ModelAndView handleNotFound(Exception e){
        log.error("Handling 404 Not Found Exception");
        log.error(e.getMessage());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("recipe/404error");
        modelAndView.addObject("exception",e);
        return modelAndView;
    }
}
