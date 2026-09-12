package io.github.nutant.leanobject.mixin.resourcelocation.safe;

import io.github.nutant.leanobject.ResourceLocations;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * The compatible form of ResourceLocation interning.
 *
 * <p>Vanilla's body is never replaced. Each method's {@code new ResourceLocation(...)} is intercepted and
 * replaced by a cache lookup: a miss runs the cache level's own factory - which validates exactly as
 * vanilla does - and the result is stored, so the first call populates the entry and later calls take
 * the fast path without allocating.
 */
@Mixin(value = ResourceLocation.class, priority = 100000)
public abstract class ResourceLocationMixin {

    @Shadow
    @Final
    private String namespace;

    @Inject(
            method = "createUntrusted",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void leanObject$createUntrusted(String namespace, String path, CallbackInfoReturnable<ResourceLocation> cir) {
        cir.setReturnValue(ResourceLocations.intern(namespace, path));
    }

    @Inject(
            method = "withDefaultNamespace",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void leanObject$withDefaultNamespace(String path, CallbackInfoReturnable<ResourceLocation> cir) {
        cir.setReturnValue(ResourceLocations.DEFAULT_NAMESPACE.getCache(path, ResourceLocations.DEFAULT_NAMESPACE.createFunction()));
    }

    @Inject(
            method = "tryBuild",
            at = @At(value = "NEW", target = "(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"),
            cancellable = true
    )
    private static void leanObject$tryBuild(String namespace, String path, CallbackInfoReturnable<ResourceLocation> cir) {
        cir.setReturnValue(ResourceLocations.intern(namespace, path));
    }

    @Inject(
            method = "tryBySeparator",
            at = @At(value = "NEW", target = "(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"),
            cancellable = true
    )
    private static void leanObject$tryBySeparator(String location, char separator, CallbackInfoReturnable<ResourceLocation> cir) {
        int i = location.indexOf(separator);
        if (i > 0) {
            cir.setReturnValue(ResourceLocations.intern(location.substring(0, i), location.substring(i + 1)));
        } else if (i == 0) {
            var path = location.substring(1);
            cir.setReturnValue(ResourceLocations.DEFAULT_NAMESPACE.getCache(path, ResourceLocations.DEFAULT_NAMESPACE.createFunction()));
        } else {
            cir.setReturnValue(ResourceLocations.DEFAULT_NAMESPACE.getCache(location, ResourceLocations.DEFAULT_NAMESPACE.createFunction()));
        }
    }

    @Inject(
            method = "withPath(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void leanObject$withPath(String path, CallbackInfoReturnable<ResourceLocation> cir) {
        cir.setReturnValue(ResourceLocations.intern(this.namespace, path));
    }
}
