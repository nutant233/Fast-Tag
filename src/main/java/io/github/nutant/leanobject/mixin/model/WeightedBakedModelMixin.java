package io.github.nutant.leanobject.mixin.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.WeightedBakedModel;
import net.minecraft.util.random.WeightedEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * Freezes the weighted model list into an {@link ImmutableList}, which is smaller than the list the
 * bakery hands over.
 */
@Mixin(value = WeightedBakedModel.class, priority = 100000)
public abstract class WeightedBakedModelMixin {

    @Mutable
    @Shadow
    @Final
    private List<WeightedEntry.Wrapper<BakedModel>> list;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void leanObject$freezeList(List<WeightedEntry.Wrapper<BakedModel>> list, CallbackInfo ci) {
        if (this.list != null) this.list = ImmutableList.copyOf(this.list);
    }
}
