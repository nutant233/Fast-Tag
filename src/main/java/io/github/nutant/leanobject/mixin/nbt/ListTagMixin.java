package io.github.nutant.leanobject.mixin.nbt;

import com.gto.fastcollection.fastutil.O2OOpenCacheHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;

/**
 * Replaces the backing list of {@link ListTag} with a primitive-friendly {@code ObjectArrayList},
 * rewritten at the allocation site so the vanilla {@code Lists.newArrayList()} is never created.
 */
@Mixin(value = ListTag.class, priority = 100000)
public abstract class ListTagMixin {

    @Shadow
    @Final
    private List<Tag> list;

    @ModifyArg(method = "<init>()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/ListTag;<init>(Ljava/util/List;B)V", ordinal = 0))
    private static List<Tag> leanObject$useCompactList(List<Tag> original) {
        return new ObjectArrayList<>();
    }
}
