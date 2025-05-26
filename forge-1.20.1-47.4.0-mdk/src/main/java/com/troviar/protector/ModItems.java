package com.troviar.protector;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.common.ForgeSpawnEggItem;


public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, TroviarMod.MODID);

    public static final RegistryObject<Item> TROVIAR_SPAWN_EGG =
            ITEMS.register("troviar_spawn_egg", () ->
                    new ForgeSpawnEggItem(
                            ModEntities.TROVIAR, // 你要生成的实体类型
                            0xaaaaff, // 主色
                            0x5555aa, // 点缀色
                            new Item.Properties() // 不需要 tab，测试用
                    ));
}