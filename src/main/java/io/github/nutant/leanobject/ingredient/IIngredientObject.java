package io.github.nutant.leanobject.ingredient;

import net.minecraft.world.item.crafting.Ingredient;

import org.jetbrains.annotations.NotNull;

/**
 * Implemented by the mixins on {@code Item} and {@code TagKey} so that the single-entry
 * {@link Ingredient} for that object can be built once and shared.
 */
public interface IIngredientObject {

    @NotNull
    Ingredient leanObject$getIngredient();
}
