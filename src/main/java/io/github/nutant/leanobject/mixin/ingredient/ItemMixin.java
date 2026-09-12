package io.github.nutant.leanobject.mixin.ingredient;

import io.github.nutant.leanobject.FastStream;
import io.github.nutant.leanobject.ingredient.IIngredientObject;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * Caches the single-entry {@link Ingredient} for this item.
 *
 * <p>Constructed directly rather than through {@code Ingredient.of}, which is itself hooked by
 * {@link IngredientMixin} and would recurse back into this method.
 *
 * <p>Every recipe that uses the item alone then shares one {@code Ingredient}, so its lazily built
 * {@code itemStacks} array and {@code stackingIds} list are materialised once for the whole game
 * instead of once per recipe.
 */
@Mixin(Item.class)
public abstract class ItemMixin implements IIngredientObject {

    @Unique
    private Ingredient leanObject$ingredient;

    @Override
    public @NotNull Ingredient leanObject$getIngredient() {
        var cached = leanObject$ingredient;
        if (cached == null) {
            leanObject$ingredient = cached = new Ingredient(FastStream.create(new Ingredient.ItemValue(new ItemStack((Item) (Object) this))));
        }
        return cached;
    }
}
