package com.troviar.protector;

import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;


public class TroviarEntity extends PathfinderMob {
    public TroviarEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        // 战斗目标（攻击最近的怪物）
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Monster.class, true));

        // 攻击行为（近战攻击）
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2D, true));

        // 普通行为：漂浮（避免溺水）
        this.goalSelector.addGoal(2, new FloatGoal(this));

        // 看向玩家
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0f));

        // 在附近随机走动
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1.0D));

        // 随机看一圈（不是很重要但看起来更像“活的”）
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D);
    }
}