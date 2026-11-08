package de.skycracker;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.util.Identifier;

public class SkyCrackerRenderer extends MobEntityRenderer<SkyCrackerEntity, PlayerEntityModel<SkyCrackerEntity>> {

    /** Minecraft-Name, dessen Skin der SkyCracker bekommt. */
    private static final String SKIN_OWNER = "_SkyQuafta_";

    /** Wird benutzt, solange der Skin noch lädt oder nicht gefunden wurde. */
    private static final Identifier FALLBACK_TEXTURE =
            Identifier.of(SkyCrackerMod.MOD_ID, "textures/entity/skycracker.png");

    private static volatile GameProfile skinProfile;
    private static boolean requested = false;

    public SkyCrackerRenderer(EntityRendererFactory.Context context) {
        super(context, new PlayerEntityModel<>(context.getPart(EntityModelLayers.PLAYER), false), 0.5f);
    }

    @Override
    public Identifier getTexture(SkyCrackerEntity entity) {
        if (!requested) {
            requested = true;
            SkullBlockEntity.fetchProfileByName(SKIN_OWNER)
                    .thenAccept(result -> result.ifPresent(found -> skinProfile = found));
        }
        GameProfile profile = skinProfile;
        if (profile == null) {
            return FALLBACK_TEXTURE;
        }
        return MinecraftClient.getInstance().getSkinProvider().getSkinTextures(profile).texture();
    }
}
