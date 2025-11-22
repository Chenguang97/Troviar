package com.troviar.protector.entity;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MyScheduler {

    private static final Map<Runnable, Integer> TASKS = new HashMap<>();

    // 外部调用这个方法：delayTicks 之后执行 task
    public static void runLater(Runnable task, int delayTicks) {
        TASKS.put(task, delayTicks);
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Iterator<Map.Entry<Runnable, Integer>> it = TASKS.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Runnable, Integer> entry = it.next();
            int remain = entry.getValue() - 1;
            if (remain <= 0) {
                entry.getKey().run();
                it.remove();
            } else {
                entry.setValue(remain);
            }
        }
    }
}