package io.github.nutant.leanobject;

import com.gto.fastcollection.cache.HashCache;
import com.gto.fastcollection.cache.WeakValueHashCache;
import net.minecraft.resources.ResourceLocation;

/**
 * Interning cache for {@link ResourceLocation}, keyed by namespace then path.
 *
 * <p>Validation is split across the two levels, each performed by that level's own factory so it runs
 * once per key rather than once per creation: the outer factory validates the namespace when a
 * namespace is first seen, and the inner factory validates the path when an entry is first created.
 * Callers therefore only ever ask for a {@code namespace:path} - they never supply a factory.
 *
 * <p>{@link #DEFAULT_NAMESPACE} is the {@code minecraft} sub-cache hoisted into a field, so the most
 * common namespace never needs a lookup of its own.
 *
 * <p>{@code ResourceLocation} is {@code final} in 1.21 and every public factory routes through one
 * private constructor, so one instance per {@code namespace:path} is enough for identity comparison to
 * be safe.
 */
public final class ResourceLocations {

    private static final HashCache<String, WeakValueHashCache<String, ResourceLocation>> CACHES = new HashCache<>(n -> {
        var ns = ResourceLocation.assertValidNamespace(n, n);
        return new WeakValueHashCache<>(path -> new ResourceLocation(ns, ResourceLocation.assertValidPath(ns, path)));
    });

    /** The {@code minecraft} sub-cache, hoisted - the overwhelmingly common namespace. */
    public static final WeakValueHashCache<String, ResourceLocation> DEFAULT_NAMESPACE = CACHES.getCache("minecraft");

    private ResourceLocations() {
    }

    /** The sub-cache for {@code namespace}; the namespace is validated when the sub-cache is created. */
    public static WeakValueHashCache<String, ResourceLocation> namespace(String namespace) {
        return CACHES.getCache(namespace);
    }

    /** Returns the canonical instance for {@code namespace:path}, validating each part once. */
    public static ResourceLocation intern(String namespace, String path) {
        var cache = CACHES.getCache(namespace);
        return cache.getCache(path, cache.createFunction());
    }
}
