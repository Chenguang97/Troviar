package com.troviar.protector;

import com.troviar.protector.core.ModEntities;
import com.troviar.protector.entity.TroviarEntity;
import com.troviar.protector.core.ModItems;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;


import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;


@Mod(TroviarMod.MODID)
public class TroviarMod {
    public static final String MODID = "troviar";

    public TroviarMod(FMLJavaModLoadingContext context) {
        IEventBus bus = context.getModEventBus();

        ModEntities.ENTITIES.register(bus);
        ModItems.ITEMS.register(bus);
        bus.addListener(this::onEntityAttributes);
    }

    private void onEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.TROVIAR.get(), TroviarEntity.createAttributes().build());
    }
}