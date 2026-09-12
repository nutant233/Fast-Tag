package io.github.nutant.leanobject;

import com.gto.fastcollection.cache.HashCache;
import com.gto.fastcollection.cache.WeakValueHashCache;
import net.minecraft.resources.ResourceLocation;

/**
 * Interning cache for {@link ResourceLocation}, keyed by namespace then path.
 *
 * <p>{@code ResourceLocation} is {@code final} in 1.21 and every public factory routes through one
 * private constructor, so interning the factory results guarantees a single instance per logical
 * location and identity comparison becomes safe.
 */
public final class ResourceLocations {

    private static final HashCache<String, WeakValueHashCache<String, ResourceLocation>> PATHS = new HashCache<>(ns -> new WeakValueHashCache<>(path -> new ResourceLocation(ns, path)));

    /** Returns the canonical instance for {@code namespace:path}. */
    public static ResourceLocation intern(String namespace, String path) {
        return PATHS.getCache(namespace).getCache(path);
    }

    /** Returns the canonical instance if one is currently alive, without creating one. */
    public static ResourceLocation peek(String namespace, String path) {
        var inner = PATHS.getIfPresent(namespace);
        return inner == null ? null : inner.getIfPresent(path);
    }
}
