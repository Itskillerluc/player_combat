package io.github.itskillerluc.player_combat.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import io.github.itskillerluc.player_combat.blocks.BountyBoard;
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
        pPoseStack.translate(-0.5, 0, 0.5);

        float rotation = ((-22.5f) * pBlockEntity.getBlockState().getValue(BountyBoard.ROTATION) + 180) % 360;
        var string = "testtest";

        float length = context.getFont().width(string) * 0.975f;
//
//        float z = (float) Math.cos(Math.toRadians(rotation)) * (length/40);
//        float x = (float) Math.cos(Math.toRadians(rotation)) * (length/40);
//
//        float zOffset = 0.5f - z;
//        float xOffset = .5f - x;
//        pPoseStack.translate(x, 0, z);



        pPoseStack.mulPose(new Quaternion(Vector3f.YP, rotation, true));

        pPoseStack.scale(0.05f, 0.05f, 0);


        pPoseStack.mulPose(new Quaternion(Vector3f.ZP,180, true));
        context.getFont().draw(pPoseStack, Component.literal(string),-length, -30, 0x000000);
        context.getFont().draw(pPoseStack, Component.literal(string),-length, -(30 + 1 * 10), 0x000000);

    }
}
