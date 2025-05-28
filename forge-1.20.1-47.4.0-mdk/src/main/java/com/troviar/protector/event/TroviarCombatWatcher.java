package com.troviar.protector.event;

import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = "troviar")
public class TroviarCombatWatcher {
    private static UUID lastAttackedPlayer;
    private static long lastAttackTime;

    @SubscribeEvent
    public static void onPlayerAttacked(LivingAttackEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!(event.getSource().getEntity() instanceof LivingEntity)) return;

        lastAttackedPlayer = player.getUUID();
        lastAttackTime = System.currentTimeMillis();
    }

    public static boolean wasRecentlyAttacked(UUID uuid) {
        return lastAttackedPlayer != null
                && lastAttackedPlayer.equals(uuid)
                && (System.currentTimeMillis() - lastAttackTime) < 3000;
    }
}