package guru.springframework.controllers;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.services.ImageService;
import guru.springframework.services.RecipeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Controller
public class ImageController {

    private RecipeService recipeService;
    private ImageService imageService;

    @Autowired
    public ImageController(RecipeService recipeService, ImageService imageService) {
        this.recipeService = recipeService;
        this.imageService = imageService;
    }

    @GetMapping("recipe/{recipeId}/upload-image")
    public String showUploadImageForm(@PathVariable String recipeId, Model model){
        RecipeCommand recipeCommand = recipeService.findRecipeCommandById(Long.valueOf(recipeId));
        model.addAttribute("recipe", recipeCommand);
        return "recipe/image-upload-form";
    }

    @PostMapping("recipe/{recipeId}/image")
    public String uploadImage(@PathVariable String recipeId, @RequestParam("imagefile") MultipartFile file){
        imageService.saveImageFile(Long.valueOf(recipeId),file);
        return "redirect:/recipe/"+recipeId+"/show";
    }

    @GetMapping("recipe/{recipeId}/recipe-image")
    public void renderImageFromDb(@PathVariable String recipeId, HttpServletResponse response) throws IOException {
        RecipeCommand recipeCommand = recipeService.findRecipeCommandById(Long.valueOf(recipeId));

        byte[] primitiveImageBytes = new byte[recipeCommand.getImage().length];
        int i = 0;
        for (Byte b : recipeCommand.getImage()){
            primitiveImageBytes[i] = b;
            i++;
        }

        response.setContentType("image/jpeg");
        InputStream inputStream = new ByteArrayInputStream(primitiveImageBytes);
        IOUtils.copy(inputStream,response.getOutputStream());
    }
}
