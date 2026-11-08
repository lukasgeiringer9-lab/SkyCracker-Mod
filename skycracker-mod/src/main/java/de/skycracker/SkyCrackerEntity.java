package de.skycracker;

import java.util.EnumSet;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

/**
 * Der SkyCracker: läuft dem nächsten Spieler hinterher und schreit ihn
 * in zufälligen Abständen an. Er ist völlig harmlos.
 */
public class SkyCrackerEntity extends PathAwareEntity {

    private static final String[] YELLS = {
            "HEY! WARTE AUF MICH!",
            "BOOOOM! DAS WAR ICH!",
            "LAUF NICHT SO SCHNELL, ICH HAB KURZE BEINE!",
            "ZÜNDE MICH AN! ICH HALTE ES NICHT MEHR AUS!",
            "WIE LANGE NOCH?! MIR WIRD LANGWEILIG!",
            "HÖRST DU MIR ÜBERHAUPT ZU?!",
            "DU TRÖDELST SCHON WIEDER!",
            "ICH HAB HUNGER! GIB MIR SCHIESSPULVER!",
            "SCHAU MICH AN, WENN ICH MIT DIR REDE!",
            "AAAAAAAAAAAAAH! ... NUR SO ZUM SPASS.",
            "IST DAS ALLES, WAS DU KANNST?!",
            "FEUER FREI! ... ACH NEIN, DOCH NICHT.",
            "ICH BIN DEIN SKYCRACKER UND ICH BLEIBE BEI DIR!",
            "PSSST... PSSSSST... BOOOOOM!",
            "MACH ENDLICH WAS ZUM BAUEN, ICH LANGWEILE MICH!"
    };

    private int yellCooldown;

    public SkyCrackerEntity(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
        this.yellCooldown = 100 + this.random.nextInt(200);
        this.setPersistent();
    }

    public static DefaultAttributeContainer.Builder createSkyCrackerAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.32)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 64.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new FollowPlayerGoal(this, 1.15));
        this.goalSelector.add(4, new WanderAroundFarGoal(this, 0.8));
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 16.0f));
        this.goalSelector.add(6, new LookAroundGoal(this));
    }

    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient) {
            return;
        }
        if (this.yellCooldown > 0) {
            this.yellCooldown--;
            return;
        }
        PlayerEntity nearest = this.getWorld().getClosestPlayer(this, 40.0);
        if (nearest == null) {
            this.yellCooldown = 100; // später nochmal versuchen
            return;
        }
        this.yell();
        this.yellCooldown = 200 + this.random.nextInt(400); // alle 10 bis 30 Sekunden
    }

    private void yell() {
        String message = YELLS[this.random.nextInt(YELLS.length)];
        Text text = Text.literal("<SkyCracker> ").formatted(Formatting.GOLD)
                .append(Text.literal(message).formatted(Formatting.RED, Formatting.BOLD));

        for (PlayerEntity player : this.getWorld().getPlayers()) {
            if (this.squaredDistanceTo(player) < 40.0 * 40.0) {
                player.sendMessage(text, false);
            }
        }

        this.playSound(SoundEvents.ENTITY_RAVAGER_ROAR, 2.0f, 0.9f + this.random.nextFloat() * 0.4f);

        if (this.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.FIREWORK,
                    this.getX(), this.getEyeY() + 0.3, this.getZ(),
                    25, 0.3, 0.3, 0.3, 0.08);
            serverWorld.spawnParticles(ParticleTypes.ANGRY_VILLAGER,
                    this.getX(), this.getEyeY() + 0.5, this.getZ(),
                    3, 0.2, 0.1, 0.2, 0.0);
        }
    }

    /** Läuft dem nächsten Spieler hinterher und teleportiert sich bei zu großer Entfernung heran. */
    static class FollowPlayerGoal extends Goal {
        private final SkyCrackerEntity mob;
        private final double speed;
        private PlayerEntity target;
        private int recalcDelay;

        FollowPlayerGoal(SkyCrackerEntity mob, double speed) {
            this.mob = mob;
            this.speed = speed;
            this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        @Override
        public boolean canStart() {
            this.target = this.mob.getWorld().getClosestPlayer(this.mob, 64.0);
            return this.target != null
                    && !this.target.isSpectator()
                    && this.mob.squaredDistanceTo(this.target) > 16.0;
        }

        @Override
        public boolean shouldContinue() {
            return this.target != null
                    && this.target.isAlive()
                    && !this.target.isSpectator()
                    && this.mob.squaredDistanceTo(this.target) > 9.0;
        }

        @Override
        public void start() {
            this.recalcDelay = 0;
        }

        @Override
        public void stop() {
            this.target = null;
            this.mob.getNavigation().stop();
        }

        @Override
        public void tick() {
            this.mob.getLookControl().lookAt(this.target, 30.0f, 30.0f);
            if (--this.recalcDelay > 0) {
                return;
            }
            this.recalcDelay = 10;
            if (this.mob.squaredDistanceTo(this.target) > 24.0 * 24.0) {
                this.tryTeleportToTarget();
            } else {
                this.mob.getNavigation().startMovingTo(this.target, this.speed);
            }
        }

        private void tryTeleportToTarget() {
            for (int i = 0; i < 12; i++) {
                double angle = this.mob.getRandom().nextDouble() * Math.PI * 2.0;
                double distance = 3.0 + this.mob.getRandom().nextDouble() * 3.0;
                double x = this.target.getX() + Math.cos(angle) * distance;
                double y = this.target.getY();
                double z = this.target.getZ() + Math.sin(angle) * distance;

                Box box = this.mob.getBoundingBox()
                        .offset(x - this.mob.getX(), y - this.mob.getY(), z - this.mob.getZ());
                if (this.mob.getWorld().isSpaceEmpty(this.mob, box)
                        && !this.mob.getWorld().containsFluid(box)) {
                    this.mob.refreshPositionAndAngles(x, y, z, this.mob.getYaw(), this.mob.getPitch());
                    this.mob.getNavigation().stop();
                    return;
                }
            }
        }
    }
}
