package io.github.nutant.leanobject.mixin.resourcelocation.safe;

import io.github.nutant.leanobject.ResourceLocations;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.UnaryOperator;

/**
 * The compatible form of ResourceLocation interning.
 *
 * <p>Where the {@code @Overwrite} variant replaces the vanilla bodies, this one wraps them: vanilla
 * runs untouched on a cache miss, and only the returned instance is replaced with the interned one.
 * That is slower on a miss but keeps every line of vanilla's parsing and validation in play.
 */
@Mixin(value = ResourceLocation.class, priority = 100000)
public abstract class ResourceLocationMixin {

    @Inject(method = "createUntrusted", at = @At("HEAD"), cancellable = true)
    private static void leanObject$createUntrusted(String namespace, String path, CallbackInfoReturnable<ResourceLocation> cir) {
        var cached = ResourceLocations.peek(namespace, path);
        if (cached != null) {
            cir.setReturnValue(cached);
        }
    }

    @Inject(method = "withDefaultNamespace", at = @At("HEAD"), cancellable = true)
    private static void leanObject$withDefaultNamespace(String path, CallbackInfoReturnable<ResourceLocation> cir) {
        var cached = ResourceLocations.peek("minecraft", path);
        if (cached != null) {
            cir.setReturnValue(cached);
        }
    }

    @Inject(method = "tryBuild", at = @At("RETURN"), cancellable = true)
    private static void leanObject$tryBuild(String namespace, String path, CallbackInfoReturnable<ResourceLocation> cir) {
        var result = cir.getReturnValue();
        if (result != null) {
            cir.setReturnValue(ResourceLocations.intern(result.getNamespace(), result.getPath()));
        }
    }

    @Inject(method = "tryBySeparator", at = @At("RETURN"), cancellable = true)
    private static void leanObject$tryBySeparator(String location, char separator, CallbackInfoReturnable<ResourceLocation> cir) {
        var result = cir.getReturnValue();
        if (result != null) {
            cir.setReturnValue(ResourceLocations.intern(result.getNamespace(), result.getPath()));
        }
    }

    @Inject(method = "withPath(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;", at = @At("RETURN"), cancellable = true)
    private void leanObject$withPath(String path, CallbackInfoReturnable<ResourceLocation> cir) {
        var result = cir.getReturnValue();
        if (result != null) {
            cir.setReturnValue(ResourceLocations.intern(result.getNamespace(), result.getPath()));
        }
    }

    @Inject(method = "withPath(Ljava/util/function/UnaryOperator;)Lnet/minecraft/resources/ResourceLocation;", at = @At("RETURN"), cancellable = true)
    private void leanObject$withPathOperator(UnaryOperator<String> operator, CallbackInfoReturnable<ResourceLocation> cir) {
        var result = cir.getReturnValue();
        if (result != null) {
            cir.setReturnValue(ResourceLocations.intern(result.getNamespace(), result.getPath()));
        }
    }
}
