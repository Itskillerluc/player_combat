package io.github.itskillerluc.player_combat.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Map;
import java.util.UUID;

public interface IBountyCapability extends INBTSerializable<CompoundTag> {
    boolean isBounty(UUID uuid);
    int getBounty(UUID uuid);
    int setBounty(UUID uuid, int bounty);
    Map<UUID, Integer> getBountyList();
    int removeBounty(UUID uuid);

}
