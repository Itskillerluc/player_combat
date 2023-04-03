package io.github.itskillerluc.player_combat.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class BountyCapaiblity implements IBountyCapability{
    Map<UUID, Integer> bountyMap = new HashMap<>();

    @Override
    public boolean isBounty(UUID uuid) {
        return bountyMap.containsKey(uuid);
    }

    @Override
    public int getBounty(UUID uuid) {
        return bountyMap.get(uuid) == null ? 0 : bountyMap.get(uuid);
    }

    @Override
    public int setBounty(UUID uuid, int bounty) {
        if (isBounty(uuid)){
            Integer integer = bountyMap.replace(uuid, getBounty(uuid) + bounty);
            return integer == null ? 0 : integer;
        }
        bountyMap.put(uuid, bounty);
        return 1;
    }

    @Override
    public Map<UUID, Integer> getBountyList() {
        return bountyMap;
    }

    @Override
    public int removeBounty(UUID uuid) {
        return bountyMap.remove(uuid) == null ? 0 : 1;
    }


    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag listTag = new ListTag();
        for (Map.Entry<UUID, Integer> uuidIntegerEntry : bountyMap.entrySet()) {
            CompoundTag entry = new CompoundTag();
            entry.putUUID("target", uuidIntegerEntry.getKey());
            entry.putFloat("bounty", uuidIntegerEntry.getValue());
            listTag.add(entry);
        }
        tag.put("bountyMap", listTag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        LinkedHashMap<UUID, Integer> map = new LinkedHashMap<>();
        ListTag list = nbt.getList("bountyMap", 10);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag damageTrackMap = list.getCompound(i);
            map.put(damageTrackMap.getUUID("target"), damageTrackMap.getInt("bounty"));
        }
        bountyMap = map;
    }
}
