package guru.springframework.domain;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data @ToString(exclude = {"ingredients","notes"})
@Entity
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
    private Integer prepTime;
    private Integer cookTime;
    private Integer servings;
    private String source;
    private String url;

    @Lob
    private String directions;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "recipe")
    private Set<Ingredient> ingredients;

    @Lob
    private Byte[] image;

    @Enumerated(value = EnumType.STRING)
    private Difficulty difficulty;

    @OneToOne(cascade = CascadeType.ALL)
    private Notes notes;

    @ManyToMany
    @JoinTable(name = "recipe_category",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories;

    public Recipe() {
    }

    public Recipe addIngredient(Ingredient ingredient){
        ingredient.setRecipe(this);
        if (ingredients == null){
            ingredients = new HashSet<>();
        }
        this.ingredients.add(ingredient);
        return this;
    }

    public Set<Ingredient> getIngredients() {
        if (ingredients == null){
            ingredients = new HashSet<>();
        }
        return ingredients;
    }

    public void setNotes(Notes notes) {
        if (notes != null){
            this.notes = notes;
            notes.setRecipe(this);
        }
    }

    public Set<Category> getCategories() {
        if (categories == null){
            categories = new HashSet<>();
        }
        return categories;
    }
}
