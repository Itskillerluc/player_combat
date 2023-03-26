package io.github.itskillerluc.player_combat.blocks;

import io.github.itskillerluc.player_combat.PlayerCombat;
import io.github.itskillerluc.player_combat.blocks.entity.BountyBoardBE;
import io.github.itskillerluc.player_combat.blocks.entity.LeaderBoardBE;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RegisterBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, PlayerCombat.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, PlayerCombat.MODID);

    public static final RegistryObject<BountyBoard> BOUNTY_BOARD = BLOCKS.register("bounty_board", () -> new BountyBoard(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<LeaderBoard> LEADERBOARD = BLOCKS.register("leaderboard", () -> new LeaderBoard(BlockBehaviour.Properties.copy(Blocks.STONE)));

    public static final RegistryObject<BlockEntityType<BountyBoardBE>> BOUNTY_BOARD_BE = BLOCK_ENTITIES.register("bounty_board_be", () -> BlockEntityType.Builder.of(BountyBoardBE::new, BOUNTY_BOARD.get()).build(null));
    public static final RegistryObject<BlockEntityType<LeaderBoardBE>> LEADERBOARD_BE = BLOCK_ENTITIES.register("leaderboard_be", () -> BlockEntityType.Builder.of(LeaderBoardBE::new, LEADERBOARD.get()).build(null));
}
