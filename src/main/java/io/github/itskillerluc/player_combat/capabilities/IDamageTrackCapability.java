package io.github.itskillerluc.player_combat.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public interface IDamageTrackCapability extends INBTSerializable<CompoundTag> {
    LinkedHashMap<UUID, Float> getDamageMap();
    Map.Entry<UUID, Float> getDamageMapEntry(int entry);
    void setDamageMap(LinkedHashMap<UUID, Float> map);
    default Map.Entry<UUID, Float> popDamageMapEntry() {
        var toReturn = getDamageMapEntry(0);
        removeDamageMapEntry(0);
        return toReturn;
    }
    void addDamageMapEntry(Map.Entry<UUID, Float> entry);
    void removeDamageMapEntry(int entry);
    float getDamage(UUID id);

    void addDamage(UUID id, float damage);

    void removeDamage(UUID id, float damage);
}
