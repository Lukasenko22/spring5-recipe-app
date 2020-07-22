package guru.springframework.services;

import guru.springframework.domain.Recipe;
import guru.springframework.repositories.RecipeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Service
public class ImageServiceImpl implements ImageService {

    RecipeRepository recipeRepository;

    @Autowired
    public ImageServiceImpl(RecipeRepository recipeRepository){
        this.recipeRepository = recipeRepository;
    }

    @Override
    @Transactional
    public void saveImageFile(Long recipeId, MultipartFile imageFile) {

        Optional<Recipe> recipeOpt = recipeRepository.findById(recipeId);
        if (!recipeOpt.isPresent()){
            throw new RuntimeException("Recipe with id="+recipeId+" not found");
        }

        Recipe recipe = recipeOpt.get();
        try {
            int i = 0;
            Byte[] byteObjects = new Byte[imageFile.getBytes().length];
            for (byte b : imageFile.getBytes()) {
                byteObjects[i] = b;
                i++;
            }
            recipe.setImage(byteObjects);
            recipeRepository.save(recipe);

        } catch (IOException e) {
            log.debug("Error uploading image file!");
            e.printStackTrace();
        }
        log.debug("Image file uploaded! Bytes length: "+recipe.getImage().length);
    }
}
