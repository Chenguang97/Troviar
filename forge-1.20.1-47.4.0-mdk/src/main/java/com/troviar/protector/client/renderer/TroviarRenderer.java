package com.troviar.protector.client.renderer;

import com.troviar.protector.entity.TroviarEntity;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;


public class TroviarRenderer extends MobRenderer<TroviarEntity, HumanoidModel<TroviarEntity>> {
    public TroviarRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(TroviarEntity entity) {
        return ResourceLocation.fromNamespaceAndPath("troviar", "textures/entity/troviar1.png");
    }
}