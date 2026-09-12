package io.github.nutant.leanobject.mixin.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.client.RenderTypeGroup;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

/**
 * Freezes the face lists of a baked model into {@link ImmutableList}s, which are smaller than the
 * lists the bakery builds.
 *
 * <p>1.21 has two constructors; this targets the one NeoForge adds, which carries a
 * {@link RenderTypeGroup}. {@code renderTypesFast} from 1.20 no longer exists.
 */
@Mixin(value = SimpleBakedModel.class, priority = 100000)
public abstract class SimpleBakedModelMixin {

    @Mutable
    @Shadow
    @Final
    protected List<BakedQuad> unculledFaces;

    @Shadow
    @Final
    protected Map<Direction, List<BakedQuad>> culledFaces;

    @Inject(
            method = "<init>(Ljava/util/List;Ljava/util/Map;ZZZLnet/minecraft/client/renderer/texture/TextureAtlasSprite;Lnet/minecraft/client/renderer/block/model/ItemTransforms;Lnet/minecraft/client/renderer/block/model/ItemOverrides;Lnet/neoforged/neoforge/client/RenderTypeGroup;)V",
            at = @At("RETURN")
    )
    private void leanObject$freezeFaces(List<BakedQuad> unculledFaces, Map<Direction, List<BakedQuad>> culledFaces,
                                        boolean hasAmbientOcclusion, boolean usesBlockLight, boolean isGui3d,
                                        TextureAtlasSprite particleIcon, ItemTransforms transforms, ItemOverrides overrides,
                                        RenderTypeGroup renderTypes, CallbackInfo ci) {
        if (this.unculledFaces != null) this.unculledFaces = ImmutableList.copyOf(this.unculledFaces);
        if (this.culledFaces != null) {
            for (var entry : this.culledFaces.entrySet()) {
                entry.setValue(ImmutableList.copyOf(entry.getValue()));
            }
        }
    }
}
