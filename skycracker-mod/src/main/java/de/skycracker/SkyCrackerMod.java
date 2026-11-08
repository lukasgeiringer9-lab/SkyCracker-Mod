package de.skycracker;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
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
    }
}
