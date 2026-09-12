package io.github.nutant.leanobject.mixin.resourcelocation;

import io.github.nutant.leanobject.ResourceLocations;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

/**
 * The {@code @Overwrite} form of ResourceLocation interning.
 *
 * <p>No factory is supplied from here: both validations belong to the cache levels themselves, so a hit
 * returns the canonical instance without validating anything and a miss runs the level's own factory.
 * The validating entry point ({@code fromNamespaceAndPath}) is not overwritten; it delegates to
 * {@code createUntrusted}.
 */
@Mixin(value = ResourceLocation.class, priority = 100000)
public abstract class ResourceLocationMixin {

    @Shadow
    @Final
    private String namespace;

    /**
     * @author nutant233
     * @reason Intern every trusted construction onto one instance per namespace:path
     */
    @Overwrite
    private static ResourceLocation createUntrusted(String namespace, String path) {
        return ResourceLocations.intern(namespace, path);
    }

    /**
     * @author nutant233
     * @reason Intern under the default namespace, validation handled by the cache level
     */
    @Overwrite
    public static ResourceLocation withDefaultNamespace(String path) {
        return ResourceLocations.DEFAULT_NAMESPACE.getCache(path, ResourceLocations.DEFAULT_NAMESPACE.createFunction());
    }

    /**
     * @author nutant233
     * @reason Intern the trusted build path; the cache's validation decides validity, so an invalid
     * pair simply throws and becomes the {@code null} vanilla returns
     */
    @Overwrite
    @Nullable
    public static ResourceLocation tryBuild(String namespace, String path) {
        try {
            return ResourceLocations.intern(namespace, path);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @author nutant233
     * @reason Intern the separator form. Parsing mirrors vanilla, but validity is decided by the
     * cache's validation throwing rather than by pre-checking, so nothing is validated on a hit
     */
    @Overwrite
    @Nullable
    public static ResourceLocation tryBySeparator(String location, char separator) {
        try {
            int i = location.indexOf(separator);
            if (i > 0) {
                return ResourceLocations.intern(location.substring(0, i), location.substring(i + 1));
            } else if (i == 0) {
                return ResourceLocations.DEFAULT_NAMESPACE.getCache(location.substring(1), ResourceLocations.DEFAULT_NAMESPACE.createFunction());
            } else {
                return ResourceLocations.DEFAULT_NAMESPACE.getCache(location, ResourceLocations.DEFAULT_NAMESPACE.createFunction());
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @author nutant233
     * @reason Intern the derived location, keeping the existing namespace
     */
    @Overwrite
    public ResourceLocation withPath(String path) {
        return ResourceLocations.intern(this.namespace, path);
    }

    /**
     * @author nutant233
     * @reason Interned locations are unique by reference
     */
    @Overwrite(remap = false)
    public boolean equals(Object other) {
        return other == this;
    }

    /**
     * @author nutant233
     * @reason Identity hash, consistent with the identity equals above
     */
    @Overwrite(remap = false)
    public int hashCode() {
        return System.identityHashCode(this);
    }
}
