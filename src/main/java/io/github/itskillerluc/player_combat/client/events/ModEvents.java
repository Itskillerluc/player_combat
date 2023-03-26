package io.github.itskillerluc.player_combat.client.events;

import io.github.itskillerluc.player_combat.PlayerCombat;
import io.github.itskillerluc.player_combat.blocks.RegisterBlocks;
import io.github.itskillerluc.player_combat.client.renderer.BountyBoardRenderer;
import io.github.itskillerluc.player_combat.client.renderer.LeaderBoardRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus= Mod.EventBusSubscriber.Bus.MOD, modid = PlayerCombat.MODID, value = Dist.CLIENT)
public class ModEvents {
    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(RegisterBlocks.BOUNTY_BOARD_BE.get(), BountyBoardRenderer::new);
        event.registerBlockEntityRenderer(RegisterBlocks.LEADERBOARD_BE.get(), LeaderBoardRenderer::new);
    }
}
