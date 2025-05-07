package com.troviar.protector;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, TroviarMod.MODID);

    public static final RegistryObject<EntityType<TroviarEntity>> TROVIAR =
            ENTITIES.register("troviar", () ->
                    EntityType.Builder.of(TroviarEntity::new, MobCategory.CREATURE)
                            .sized(0.6f, 1.8f) // 宽度，高度（和玩家一样大）
                            .build(ResourceLocation.fromNamespaceAndPath(TroviarMod.MODID, "troviar").toString());
}
