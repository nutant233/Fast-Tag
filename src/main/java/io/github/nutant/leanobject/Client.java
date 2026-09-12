package io.github.nutant.leanobject;

import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.bus.api.EventPriority;

final class Client {

    private Client() {
    }

    static void init() {
        if (Config.loggingInGC) NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, false, PlayerEvent.PlayerLoggedInEvent.class, Client::loggingIn);
    }

    private static void loggingIn(PlayerEvent.PlayerLoggedInEvent event) {
        Runtime runtime = Runtime.getRuntime();
        System.gc();
        Config.LOGGER.info("Post-GC heap in use: {} MB (max {} MB)",
                String.format("%.2f", (runtime.totalMemory() - runtime.freeMemory()) / (1024.0 * 1024.0)),
                String.format("%.2f", runtime.maxMemory() / (1024.0 * 1024.0)));
    }
}
