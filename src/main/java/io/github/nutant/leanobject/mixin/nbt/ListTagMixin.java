package io.github.nutant.leanobject.mixin.nbt;

import com.gto.fastcollection.fastutil.O2OOpenCacheHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagTypes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;

/**
 * Replaces the backing list of {@link ListTag} with a primitive-friendly {@code ObjectArrayList} on
 * every path that creates one - the empty constructor and NBT deserialization - and keeps
 * {@link #copy()} on the same container.
 *
 * <p>The vanilla {@code Lists} allocations are redirected to {@code null} so the list they would have
 * produced is never built; the replacement is supplied by the neighbouring {@code @ModifyArg} /
 * {@code @ModifyVariable}.
 */
@Mixin(value = ListTag.class, priority = 100000)
public abstract class ListTagMixin {

    @Shadow
    private byte type;

    @Shadow
    @Final
    private List<Tag> list;

    @ModifyArg(method = "<init>()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/ListTag;<init>(Ljava/util/List;B)V", ordinal = 0))
    private static List<Tag> leanObject$useCompactList(List<Tag> original) {
        return new ObjectArrayList<>();
    }

    @Redirect(method = "<init>()V", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Lists;newArrayList()Ljava/util/ArrayList;", remap = false))
    private static ArrayList<?> leanObject$skipVanillaListAlloc() {
        return null;
    }

    /**
     * @author nutant233
     * @reason Copy into the compact container, sharing the list when the element type is a value type
     */
    @Overwrite
    public ListTag copy() {
        if (TagTypes.getType(this.type).isValue()) {
            return new ListTag(new ObjectArrayList<>(this.list), this.type);
        } else {
            var copy = new ObjectArrayList<Tag>(this.list.size());
            this.list.forEach(t -> copy.add(t.copy()));
            return new ListTag(copy, this.type);
        }
    }

    @Mixin(targets = "net.minecraft.nbt.ListTag$1")
    static class Type {

        @ModifyVariable(method = "load(Ljava/io/DataInput;ILnet/minecraft/nbt/NbtAccounter;)Lnet/minecraft/nbt/ListTag;", at = @At(value = "INVOKE_ASSIGN", target = "Lcom/google/common/collect/Lists;newArrayListWithCapacity(I)Ljava/util/ArrayList;", remap = false))
        private List<Tag> leanObject$useCompactList(List<Tag> list) {
            return new ObjectArrayList<>();
        }

        @Redirect(method = "load(Ljava/io/DataInput;ILnet/minecraft/nbt/NbtAccounter;)Lnet/minecraft/nbt/ListTag;", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Lists;newArrayListWithCapacity(I)Ljava/util/ArrayList;", remap = false))
        private ArrayList<?> leanObject$skipVanillaListAlloc(int capacity) {
            return null;
        }
    }
}
