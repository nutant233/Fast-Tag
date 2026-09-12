package io.github.nutant.leanobject.mixin.ingredient;

import io.github.nutant.leanobject.FastStream;
import io.github.nutant.leanobject.ingredient.IIngredientObject;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * Caches the single-entry {@link Ingredient} for this tag.
 *
 * <p>Constructed directly rather than through {@code Ingredient.of}, which is itself hooked by
 * {@link IngredientMixin} and would recurse back into this method. Every recipe gated on the tag then
 * shares one instance and one pair of lazily built lookup arrays.
 */
@Mixin(TagKey.class)
public abstract class TagKeyMixin implements IIngredientObject {

    @Unique
    private Ingredient leanObject$ingredient;

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull Ingredient leanObject$getIngredient() {
        var cached = leanObject$ingredient;
        if (cached == null) {
            leanObject$ingredient = cached = new Ingredient(FastStream.create(new Ingredient.TagValue((TagKey<Item>) (Object) this)));
        }
        return cached;
    }
}
