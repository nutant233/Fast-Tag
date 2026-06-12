package fast.fasttag;

import com.google.common.collect.MapMaker;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Function;

@Mod("fasttag")
@SuppressWarnings("rawtypes")
public class FastTag {

    private static final Function<ResourceKey, MapCache<ResourceLocation, TagKey>> TAG_MAP_FUNCTION = k -> MapCache.build(tagFunction(k)).maker(MapMaker::weakValues).build();
    private static final MapCache<ResourceKey, MapCache<ResourceLocation, TagKey>> TAG_INTERNING_MAP = MapCache.build(TAG_MAP_FUNCTION).build();

    private static Function<ResourceLocation, TagKey> tagFunction(ResourceKey key) {
        return id -> new TagKey(key, id);
    }

    public static MapCache<ResourceLocation, TagKey> getTagCache(ResourceKey<?> resourceKey) {
        return TAG_INTERNING_MAP.getCache(resourceKey);
    }

    private static final Function<ResourceLocation, MapCache<ResourceLocation, ResourceKey>> RESOURCE_LOCATION_TO_RESOURCEKEY_MAP_FUNCTION = k -> MapCache.build(resourcekeyFunction(k)).maker(MapMaker::weakValues).build();
    private static final MapCache<ResourceLocation, MapCache<ResourceLocation, ResourceKey>> RESOURCE_LOCATION_TO_RESOURCEKEY_INTERNING_MAP = MapCache.build(RESOURCE_LOCATION_TO_RESOURCEKEY_MAP_FUNCTION).build();
    private static final Function<ResourceKey, MapCache<ResourceLocation, ResourceKey>> RESOURCEKEY_MAP_FUNCTION = k ->RESOURCE_LOCATION_TO_RESOURCEKEY_INTERNING_MAP.getCache(k.location());
    private static final MapCache<ResourceKey, MapCache<ResourceLocation, ResourceKey>> RESOURCEKEY_INTERNING_MAP = MapCache.build(RESOURCEKEY_MAP_FUNCTION).build();
    private static final ResourceKey ROOT = new ResourceKey(BuiltInRegistries.ROOT_REGISTRY_NAME, BuiltInRegistries.ROOT_REGISTRY_NAME);
    private static final MapCache<ResourceLocation, ResourceKey> ROOT_REGISTRY_MAP = RESOURCEKEY_INTERNING_MAP.getCache(ROOT);

    private static Function<ResourceLocation, ResourceKey> resourcekeyFunction(ResourceLocation key) {
        return id -> new ResourceKey(key, id);
    }

    public static MapCache<ResourceLocation, ResourceKey> getResourceKeyCache(ResourceLocation location) {
        return RESOURCE_LOCATION_TO_RESOURCEKEY_INTERNING_MAP.getCache(location);
    }

    public static MapCache<ResourceLocation, ResourceKey> getResourceKeyCache(ResourceKey<?> resourceKey) {
        return RESOURCEKEY_INTERNING_MAP.getCache(resourceKey);
    }

    public static ResourceKey getRootResourceKey(ResourceLocation identifier) {
        return ROOT_REGISTRY_MAP.getCache(identifier);
    }

}
