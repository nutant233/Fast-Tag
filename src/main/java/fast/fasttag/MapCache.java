package fast.fasttag;

import com.google.common.base.Equivalence;
import com.google.common.collect.MapMaker;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public final class MapCache<K, V> {

    private final ConcurrentMap<K, V> map;
    private final Function<K, V> mapFunction;

    public MapCache(Function<K, V> mapFunction, ConcurrentMap<K, V> map) {
        this.mapFunction = mapFunction;
        this.map = map;
    }

    public V getCache(final K k) {
        return map.computeIfAbsent(k, mapFunction);
    }

    public static <K, V> Build<K, V> build(Function<K, V> mapFunction) {
        return new Build<>(mapFunction);
    }


    public static final class Build<K, V> {

        private static final Field keyEquivalence;

        static {
            try {
                keyEquivalence = MapMaker.class.getDeclaredField("keyEquivalence");
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }

        @Setter
        @Accessors(chain = true, fluent = true)
        private UnaryOperator<MapMaker> maker;
        @Setter
        @Accessors(chain = true, fluent = true)
        private Equivalence<K> equivalence;
        private final Function<K, V> mapFunction;

        public Build(Function<K, V> mapFunction) {
            this.mapFunction = mapFunction;
        }

        public Build<K, V> identity() {
            equivalence = (Equivalence<K>) Equivalence.identity();
            return this;
        }

        public Build<K, V> equals() {
            equivalence = (Equivalence<K>) Equivalence.equals();
            return this;
        }

        public MapCache<K, V> build() {
            if (maker == null && equivalence == null) {
                return new MapCache<>(mapFunction, new ConcurrentHashMap<>());
            }
            var mapMaker = new MapMaker();
            if (maker != null) {
                mapMaker = maker.apply(mapMaker);
            }
            if (equivalence != null) {
                try {
                    keyEquivalence.set(mapMaker, equivalence);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            return new MapCache<>(mapFunction, mapMaker.makeMap());
        }
    }

}
