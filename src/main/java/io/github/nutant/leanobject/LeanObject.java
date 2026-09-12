package io.github.nutant.leanobject;

import com.gto.fastcollection.cache.WeakHashInterner;
import com.gto.fastcollection.cache.WeakValueHashCache;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

import java.util.Locale;
import java.util.function.UnaryOperator;

@Mod(Config.MODID)
public final class LeanObject {

    public static final UnaryOperator<CompoundTag> NBT_COPY = CompoundTag::copy;
    public static final WeakHashInterner<String> NAMESPACE_INTERNER = new WeakHashInterner<>();
    public static final WeakHashInterner<String> PATH_INTERNER = new WeakHashInterner<>();
    public static final WeakValueHashCache<String, String> VARIANT_CACHE = new WeakValueHashCache<>(s -> s.toLowerCase(Locale.ROOT));

    public LeanObject() {
        if (FMLEnvironment.dist.isClient()) {
            Client.init();
        }
    }
}
