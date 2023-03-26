package io.github.itskillerluc.player_combat.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import io.github.itskillerluc.player_combat.blocks.entity.BountyBoardBE;
import io.github.itskillerluc.player_combat.blocks.entity.LeaderBoardBE;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class BountyBoardRenderer implements BlockEntityRenderer<BountyBoardBE> {
    private final BlockEntityRendererProvider.Context context;
    public BountyBoardRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render(BountyBoardBE pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        pPoseStack.translate(0, 0, .5);
        pPoseStack.scale(0.05f, 0.05f, 0);
        pPoseStack.mulPose(new Quaternion(Vector3f.ZP,180, true));
        var string = "test";
        context.getFont().draw(pPoseStack, Component.literal(string),-(context.getFont().width(string) * 0.975f), -30, 0x000000);

    }
}
