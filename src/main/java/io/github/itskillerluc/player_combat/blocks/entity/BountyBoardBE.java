package io.github.itskillerluc.player_combat.blocks.entity;

import io.github.itskillerluc.player_combat.blocks.RegisterBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BountyBoardBE extends BlockEntity {
    public BountyBoardBE(BlockPos pPos, BlockState pBlockState) {
        super(RegisterBlocks.BOUNTY_BOARD_BE.get(), pPos, pBlockState);
    }
}
