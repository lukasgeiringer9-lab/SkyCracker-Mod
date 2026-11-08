package de.skycracker;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.util.Identifier;

public class SkyCrackerRenderer extends MobEntityRenderer<SkyCrackerEntity, BipedEntityModel<SkyCrackerEntity>> {

    private static final Identifier TEXTURE =
            Identifier.of(SkyCrackerMod.MOD_ID, "textures/entity/skycracker.png");

    public SkyCrackerRenderer(EntityRendererFactory.Context context) {
        super(context, new BipedEntityModel<>(context.getPart(EntityModelLayers.ZOMBIE)), 0.5f);
    }

    @Override
    public Identifier getTexture(SkyCrackerEntity entity) {
        return TEXTURE;
    }
}
