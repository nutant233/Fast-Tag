package io.github.nutant.leanobject.mixin.nbt;

import com.gto.fastcollection.fastutil.O2OOpenCacheHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Map;

/**
 * Replaces the backing map of {@link CompoundTag} with a leaner open-addressing map.
 *
 * <p>Rather than allocating the vanilla {@code Maps.newHashMap()} and then swapping it out, this
 * rewrites the map at the allocation site, so the vanilla map is never created at all.
 */
@Mixin(value = CompoundTag.class, priority = 100000)
public abstract class CompoundTagMixin {

    @Shadow
    public Map<String, Tag> tags;

    @ModifyArg(method = "<init>()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;<init>(Ljava/util/Map;)V"))
    private static Map<String, Tag> leanObject$useCompactMap(Map<String, Tag> original) {
        return new O2OOpenCacheHashMap<>(0);
    }
}
