package io.github.itskillerluc.player_combat.items;

import io.github.itskillerluc.player_combat.PlayerCombat;
import io.github.itskillerluc.player_combat.blocks.RegisterBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RegisterItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, PlayerCombat.MODID);

    public static final RegistryObject<BlockItem> BOUNTY_BOARD = ITEMS.register("bounty_board", () -> new BlockItem(RegisterBlocks.BOUNTY_BOARD.get(), new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS)));
    public static final RegistryObject<BlockItem> LEADERBOARD = ITEMS.register("leaderboard", () -> new BlockItem(RegisterBlocks.LEADERBOARD.get(), new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS)));
}
