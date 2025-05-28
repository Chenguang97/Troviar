package com.troviar.protector.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;

import java.util.WeakHashMap;

/**
 * 自动追踪实体的 Chunk 并持续强制加载。
 * 会自动释放之前加载的 Chunk，避免资源泄漏。
 */
public class ChunkForceLoader {

    // 每个实体 -> 它当前被加载的 chunk
    private static final WeakHashMap<Entity, ChunkPos> trackedEntities = new WeakHashMap<>();

    /**
     * 在 tick 中调用。会追踪实体并保持其所在 chunk 加载。
     * 若实体移动，会自动释放旧的 chunk。
     */
    public static void tick(Entity entity) {
        if (entity.level().isClientSide || !(entity.level() instanceof ServerLevel serverLevel)) return;

        ChunkPos current = new ChunkPos(entity.blockPosition());
        ChunkPos last = trackedEntities.get(entity);

        if (last != null && !last.equals(current)) {
            serverLevel.setChunkForced(last.x, last.z, false); // 释放旧的
        }

        if (last == null || !last.equals(current)) {
            serverLevel.setChunkForced(current.x, current.z, true); // 加载新的
            trackedEntities.put(entity, current);
        }
    }

    /**
     * 手动移除某个实体的强加载状态。
     */
    public static void release(Entity entity) {
        if (!(entity.level() instanceof ServerLevel serverLevel)) return;

        ChunkPos last = trackedEntities.remove(entity);
        if (last != null) {
            serverLevel.setChunkForced(last.x, last.z, false);
        }
    }
}