package io.github.itskillerluc.player_combat.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.itskillerluc.player_combat.blocks.entity.LeaderBoardBE;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.jetbrains.annotations.NotNull;

public class LeaderBoardRenderer implements BlockEntityRenderer<LeaderBoardBE> {
    private final BlockEntityRendererProvider.Context context;
    public LeaderBoardRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render(@NotNull LeaderBoardBE pBlockEntity, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        context.getFont().draw(pPoseStack, "test1234", 0, 0, 0xFFFFFF);
    }
}
