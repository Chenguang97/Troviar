package com.troviar.protector.entity;

import com.troviar.protector.event.TroviarCombatWatcher;
import com.troviar.protector.util.ChunkForceLoader;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;


public class TroviarEntity extends PathfinderMob {
    private UUID ownerUuid = UUID.fromString("380df991-f603-344c-a090-369bad2a924a");// 主人 UUID（召唤/绑定的玩家）
    private boolean inCombat = false;   // 是否进入战斗模式
    private BlockPos homePosition;      // 家园坐标
    private ChunkPos lastChunk = null;


    public TroviarEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.homePosition = this.blockPosition(); // 初始家园设为出生点

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

    // 设置主人（可在召唤后绑定）
    public void setOwner(Player player) {
        this.ownerUuid = player.getUUID();
    }

    // 设置家园坐标
    public void setHome(BlockPos pos) {
        this.homePosition = pos;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        ChunkForceLoader.tick(this);

        if (this.level().isClientSide || this.inCombat) return;

        Player trigger = getCombatTrigger();
        if (trigger != null && TroviarCombatWatcher.wasRecentlyAttacked(trigger.getUUID())) {
            // 传送到触发战斗的玩家身边
            System.out.println("Troviar ticked");
            this.teleportTo(trigger.getX(), trigger.getY(), trigger.getZ());
            System.out.println("Troviar teleportTo");
            enterCombatMode();

//            LivingEntity preferred = findAttackerOfOwner();
//            setTarget(preferred != null ? preferred : findNearestHostile());

            // 优先寻找正在攻击主人的怪物
            LivingEntity preferred = findAttackerOfOwner();
            setTarget(preferred != null ? preferred : findNearestHostile());

        }

    }

    // 判断是否满足进入战斗模式的条件（根据优先级）
    private Player getCombatTrigger() {
        List<Player> players = this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(64));
        Player result = null;

        for (Player player : players) {
            double hp = player.getHealth();
            double max = player.getMaxHealth();

            if (hp < max) {
                if (player.getUUID().equals(this.ownerUuid)) return player; // 🥇 本人
                if (player.getName().getString().contains("光")) result = player; // 🥈 名含"光"
                else if (result == null) result = null; // 🥉 其他低血玩家!!!!!代改
            }
        }
        return result;
    }

    // 进入战斗模式（移除原有 AI，加入攻击）
    public void enterCombatMode() {
        this.inCombat = true;
        this.goalSelector.removeAllGoals(goal -> true);
        this.targetSelector.removeAllGoals(goal -> true);
        this.goalSelector.addGoal(0, new MeleeAttackGoal(this, 1.2, true));
        this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Monster.class, true));

        // 播放战斗音效 + 粒子提示
        this.level().broadcastEntityEvent(this, (byte) 20);
        this.playSound(SoundEvents.IRON_GOLEM_REPAIR, 1.0f, 1.0f);
    }

    // 查找当前攻击主人的怪物
    @Nullable
    private LivingEntity findAttackerOfOwner() {
        if (this.ownerUuid == null || this.level() == null) return null;

        Player owner = this.level().getPlayerByUUID(this.ownerUuid);
        if (owner == null) return null;

        List<Monster> monsters = this.level().getEntitiesOfClass(Monster.class, this.getBoundingBox().inflate(32));

        for (Monster monster : monsters) {
            LivingEntity target = monster.getTarget();
            if (target != null && target.getUUID().equals(ownerUuid)) {
                return monster; // 怪物正在攻击主人
            }
        }
        return null;
    }

    // 查找最近的怪物作为备选目标
    private LivingEntity findNearestHostile() {
        List<Monster> monsters = this.level().getEntitiesOfClass(Monster.class, this.getBoundingBox().inflate(16));
        return monsters.isEmpty() ? null : monsters.get(0);
    }

    // 玩家右键绑定主人
    @Override
    public InteractionResult interactAt(Player player, Vec3 vec, InteractionHand hand) {
        if (!this.level().isClientSide && this.ownerUuid == null) {
            this.setOwner(player);
            player.sendSystemMessage(Component.literal("Troviar: 主人绑定成功！"));
            return InteractionResult.SUCCESS;
        }
        return super.interactAt(player, vec, hand);
    }
}