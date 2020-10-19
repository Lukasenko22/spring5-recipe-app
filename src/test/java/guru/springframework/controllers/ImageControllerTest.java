package guru.springframework.controllers;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.services.ImageService;
import guru.springframework.services.RecipeService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class ImageControllerTest {

    @Mock
    RecipeService recipeService;

    @Mock
    ImageService imageService;

    MockMvc mockMvc;

    ImageController imageController;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        imageController = new ImageController(recipeService, imageService);
        mockMvc = MockMvcBuilders.standaloneSetup(imageController)
                .setControllerAdvice(new ExceptionHandlerController())
                .build();
    }

    @Test
    public void showUploadImageFormTest() throws Exception {
        //given
        RecipeCommand recipeCommand = new RecipeCommand();
        recipeCommand.setId(1L);

        //when
        when(recipeService.findRecipeCommandById(anyLong())).thenReturn(recipeCommand);

        //then
        mockMvc.perform(get("/recipe/1/upload-image"))
                .andExpect(model().attributeExists("recipe"))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe/image-upload-form"));

        verify(recipeService,times(1)).findRecipeCommandById(anyLong());
    }

    @Test
    public void uploadImageTest() throws Exception {
        //given
        Long recipeId = 1L;
        MockMultipartFile mockMultipartFile = new MockMultipartFile("imagefile","testing.txt",
                "text/plain", "Test string as bytes".getBytes());

        //when

        //then
        mockMvc.perform(multipart("/recipe/"+recipeId+"/image").file(mockMultipartFile))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/recipe/"+recipeId+"/show"));

        verify(imageService,times(1)).saveImageFile(anyLong(),any());
    }

    @Test
    public void displayImageFromDbTest() throws Exception {
        //given
        RecipeCommand recipeCommand = new RecipeCommand();
        recipeCommand.setId(1L);

        String fakeImage = "Fake image as text";
        Byte[] bytes = new Byte[fakeImage.getBytes().length];

        int i = 0;
        for (byte b : fakeImage.getBytes()) {
            bytes[i] = b;
            i++;
        }

        recipeCommand.setImage(bytes);

        //when
        when(recipeService.findRecipeCommandById(anyLong())).thenReturn(recipeCommand);

        //then
        MockHttpServletResponse response = mockMvc.perform(get("/recipe/1/recipe-image"))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        byte[] responseBytes = response.getContentAsByteArray();
        assertEquals(fakeImage.getBytes().length,responseBytes.length);
    }
}