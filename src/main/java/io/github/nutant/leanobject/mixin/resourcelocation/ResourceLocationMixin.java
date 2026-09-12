package io.github.nutant.leanobject.mixin.resourcelocation;

import io.github.nutant.leanobject.ResourceLocations;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.UnaryOperator;

@Mixin(value = ResourceLocation.class, priority = 100000)
public abstract class ResourceLocationMixin {

    /**
     * @author nutant233
     * @reason Intern every location, so one instance exists per namespace:path
     */
    @Overwrite
    private static ResourceLocation createUntrusted(String namespace, String path) {
        return ResourceLocations.intern(namespace, path);
    }

    /**
     * @author nutant233
     * @reason Intern by whole string, reusing the split enforced by the vanilla parser
     */
    @Inject(method = "parse", at = @At("RETURN"), cancellable = true)
    private static void parse(String location, CallbackInfoReturnable<ResourceLocation> cir) {
        var parsed = cir.getReturnValue();
        if (parsed != null) {
            cir.setReturnValue(ResourceLocations.intern(parsed.getNamespace(), parsed.getPath()));
        }
    }

    @Inject(method = "tryParse", at = @At("RETURN"), cancellable = true)
    private static void tryParse(String location, CallbackInfoReturnable<ResourceLocation> cir) {
        var parsed = cir.getReturnValue();
        if (parsed != null) {
            cir.setReturnValue(ResourceLocations.intern(parsed.getNamespace(), parsed.getPath()));
        }
    }

    @Inject(method = "fromNamespaceAndPath", at = @At("RETURN"), cancellable = true)
    private static void fromNamespaceAndPath(String namespace, String path, CallbackInfoReturnable<ResourceLocation> cir) {
        cir.setReturnValue(ResourceLocations.intern(namespace, path));
    }

    @Inject(method = "withDefaultNamespace", at = @At("RETURN"), cancellable = true)
    private static void withDefaultNamespace(String path, CallbackInfoReturnable<ResourceLocation> cir) {
        cir.setReturnValue(ResourceLocations.intern("minecraft", path));
    }

    @Inject(method = "tryBuild", at = @At("RETURN"), cancellable = true)
    private static void tryBuild(String namespace, String path, CallbackInfoReturnable<ResourceLocation> cir) {
        var built = cir.getReturnValue();
        if (built != null) {
            cir.setReturnValue(ResourceLocations.intern(built.getNamespace(), built.getPath()));
        }
    }

    @Inject(method = "bySeparator", at = @At("RETURN"), cancellable = true)
    private static void bySeparator(String location, char separator, CallbackInfoReturnable<ResourceLocation> cir) {
        var parsed = cir.getReturnValue();
        if (parsed != null) {
            cir.setReturnValue(ResourceLocations.intern(parsed.getNamespace(), parsed.getPath()));
        }
    }

    @Inject(method = "tryBySeparator", at = @At("RETURN"), cancellable = true)
    private static void tryBySeparator(String location, char separator, CallbackInfoReturnable<ResourceLocation> cir) {
        var parsed = cir.getReturnValue();
        if (parsed != null) {
            cir.setReturnValue(ResourceLocations.intern(parsed.getNamespace(), parsed.getPath()));
        }
    }

    @Inject(method = "withPath(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;", at = @At("RETURN"), cancellable = true)
    private void withPath(String path, CallbackInfoReturnable<ResourceLocation> cir) {
        var result = cir.getReturnValue();
        if (result != null) {
            cir.setReturnValue(ResourceLocations.intern(result.getNamespace(), result.getPath()));
        }
    }

    @Inject(method = "withPath(Ljava/util/function/UnaryOperator;)Lnet/minecraft/resources/ResourceLocation;", at = @At("RETURN"), cancellable = true)
    private void withPathOperator(UnaryOperator<String> operator, CallbackInfoReturnable<ResourceLocation> cir) {
        var result = cir.getReturnValue();
        if (result != null) {
            cir.setReturnValue(ResourceLocations.intern(result.getNamespace(), result.getPath()));
        }
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
