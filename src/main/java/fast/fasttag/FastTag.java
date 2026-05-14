package fast.fasttag;

import com.google.common.collect.MapMaker;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.neoforged.fml.common.Mod;

import java.util.function.Function;

@Mod("fasttag")
@SuppressWarnings("rawtypes")
public class FastTag {

    private static final Function<ResourceKey, MapCache<Identifier, TagKey>> TAG_MAP_FUNCTION = k -> MapCache.build(tagFunction(k)).maker(MapMaker::weakValues).build();
    private static final MapCache<ResourceKey, MapCache<Identifier, TagKey>> TAG_INTERNING_MAP = MapCache.build(TAG_MAP_FUNCTION).build();

    private static Function<Identifier, TagKey> tagFunction(ResourceKey key) {
        return id -> new TagKey(key, id);
    }

    public static MapCache<Identifier, TagKey> getTagCache(ResourceKey<?> resourceKey) {
        return TAG_INTERNING_MAP.getCache(resourceKey);
    }

    private static final Function<ResourceKey, MapCache<Identifier, ResourceKey>> RESOURCEKEY_MAP_FUNCTION = k -> MapCache.build(resourcekeyFunction(k)).maker(MapMaker::weakValues).build();
    private static final MapCache<ResourceKey, MapCache<Identifier, ResourceKey>> RESOURCEKEY_INTERNING_MAP = MapCache.build(RESOURCEKEY_MAP_FUNCTION).build();
    private static final ResourceKey ROOT = new ResourceKey(Registries.ROOT_REGISTRY_NAME, Registries.ROOT_REGISTRY_NAME);
    private static final MapCache<Identifier, ResourceKey> ROOT_REGISTRY_MAP = RESOURCEKEY_INTERNING_MAP.getCache(ROOT);

    private static Function<Identifier, ResourceKey> resourcekeyFunction(ResourceKey key) {
        return id -> new ResourceKey(key.identifier(), id);
    }

    public static MapCache<Identifier, ResourceKey> getResourceKeyCache(ResourceKey<?> resourceKey) {
        return RESOURCEKEY_INTERNING_MAP.getCache(resourceKey);
    }

    public static ResourceKey getRootResourceKey(Identifier identifier) {
        return ROOT_REGISTRY_MAP.getCache(identifier);
    }

}
