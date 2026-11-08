package de.skycracker;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class SkyCrackerClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(SkyCrackerMod.SKYCRACKER, SkyCrackerRenderer::new);
    }
}
