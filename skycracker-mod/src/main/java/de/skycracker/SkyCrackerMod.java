package de.skycracker;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SkyCrackerMod implements ModInitializer {
    public static final String MOD_ID = "skycracker";

    public static final EntityType<SkyCrackerEntity> SKYCRACKER = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(MOD_ID, "skycracker"),
            EntityType.Builder.create(SkyCrackerEntity::new, SpawnGroup.CREATURE)
                    .dimensions(0.6f, 1.95f)
                    .maxTrackingRange(10)
                    .build("skycracker")
    );

    public static final Item SKYCRACKER_SPAWN_EGG = Registry.register(
            Registries.ITEM,
            Identifier.of(MOD_ID, "skycracker_spawn_egg"),
            new SpawnEggItem(SKYCRACKER, 0xE02020, 0xFFD400, new Item.Settings())
    );

    @Override
    public void onInitialize() {
        FabricDefaultAttributeRegistry.register(SKYCRACKER, SkyCrackerEntity.createSkyCrackerAttributes());

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS)
                .register(entries -> entries.add(SKYCRACKER_SPAWN_EGG));

        // /skycracker          -> holt einen SkyCracker zu dir (ohne Cheats, auch im Survival)
        // /skycracker remove   -> entfernt alle SkyCracker in deiner Dimension
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(CommandManager.literal("skycracker")
                        .executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                            ServerWorld world = player.getServerWorld();
                            SkyCrackerEntity entity = SKYCRACKER.spawn(world, player.getBlockPos(), SpawnReason.COMMAND);
                            if (entity == null) {
                                context.getSource().sendError(Text.literal("Der SkyCracker konnte nicht gespawnt werden."));
                                return 0;
                            }
                            context.getSource().sendFeedback(() -> Text.literal("Ein SkyCracker ist jetzt bei dir!"), false);
                            return 1;
                        })
                        .then(CommandManager.literal("remove")
                                .executes(context -> {
                                    ServerWorld world = context.getSource().getWorld();
                                    int count = 0;
                                    for (SkyCrackerEntity skyCracker : world.getEntitiesByType(SKYCRACKER, e -> true)) {
                                        skyCracker.discard();
                                        count++;
                                    }
                                    final int removed = count;
                                    context.getSource().sendFeedback(() -> Text.literal(removed + " SkyCracker entfernt."), false);
                                    return removed;
                                }))));
    }
}
