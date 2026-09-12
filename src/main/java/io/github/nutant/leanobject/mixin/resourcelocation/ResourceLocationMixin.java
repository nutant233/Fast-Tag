package io.github.nutant.leanobject.mixin.resourcelocation;

import io.github.nutant.leanobject.ResourceLocations;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.UnaryOperator;

/**
 * The {@code @Overwrite} form of ResourceLocation interning.
 *
 * <p>Each method here validates exactly as vanilla does and then interns the already-validated pair,
 * so the cache is only ever given a trusted lookup while the validation itself stays untouched. The
 * validating entry point ({@code fromNamespaceAndPath}) is not overwritten - it delegates to
 * {@code createUntrusted}, which is.
 */
@Mixin(value = ResourceLocation.class, priority = 100000)
public abstract class ResourceLocationMixin {

    @Shadow
    private static String assertValidNamespace(String namespace, String path) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    private static String assertValidPath(String namespace, String path) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public static boolean isValidNamespace(String namespace) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public static boolean isValidPath(String path) {
        throw new UnsupportedOperationException();
    }

    /**
     * @author nutant233
     * @reason Intern every trusted construction onto one instance per namespace:path
     */
    @Overwrite
    private static ResourceLocation createUntrusted(String namespace, String path) {
        return ResourceLocations.intern(assertValidNamespace(namespace, path), assertValidPath(namespace, path));
    }

    /**
     * @author nutant233
     * @reason Intern after validating
     */
    @Overwrite
    public static ResourceLocation withDefaultNamespace(String path) {
        return ResourceLocations.intern("minecraft", assertValidPath("minecraft", path));
    }

    /**
     * @author nutant233
     * @reason Intern the trusted build path
     */
    @Overwrite
    @Nullable
    public static ResourceLocation tryBuild(String namespace, String path) {
        return isValidNamespace(namespace) && isValidPath(path) ? ResourceLocations.intern(namespace, path) : null;
    }

    /**
     * @author nutant233
     * @reason Intern the separator form; parsing mirrors vanilla exactly
     */
    @Overwrite
    @Nullable
    public static ResourceLocation tryBySeparator(String location, char separator) {
        int i = location.indexOf(separator);
        if (i >= 0) {
            String path = location.substring(i + 1);
            if (!isValidPath(path)) {
                return null;
            } else if (i != 0) {
                String namespace = location.substring(0, i);
                return isValidNamespace(namespace) ? ResourceLocations.intern(namespace, path) : null;
            } else {
                return ResourceLocations.intern("minecraft", path);
            }
        } else {
            return isValidPath(location) ? ResourceLocations.intern("minecraft", location) : null;
        }
    }

    /**
     * @author nutant233
     * @reason Intern the derived location, keeping the existing namespace
     */
    @Overwrite
    public ResourceLocation withPath(String path) {
        var self = (ResourceLocation) (Object) this;
        return ResourceLocations.intern(self.getNamespace(), assertValidPath(self.getNamespace(), path));
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
