package io.github.itskillerluc.player_combat.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class DamageTrackCapability implements IDamageTrackCapability{
    private LinkedHashMap<UUID, Float> damageMap = new LinkedHashMap<>();

    @Override
    public LinkedHashMap<UUID, Float> getDamageMap() {
        return damageMap;
    }

    @Override
    public Map.Entry<UUID, Float> getDamageMapEntry(int entry) {
        return new ArrayList<>(damageMap.entrySet()).get(entry);
    }

    @Override
    public void setDamageMap(LinkedHashMap<UUID, Float> map) {
        damageMap = map;
    }

    @Override
    public void addDamageMapEntry(Map.Entry<UUID, Float> entry) {
        damageMap.put(entry.getKey(), entry.getValue());
    }

    @Override
    public void removeDamageMapEntry(int entry) {
        damageMap.remove(new ArrayList<>(damageMap.entrySet()).get(entry).getKey());
    }

    @Override
    public float getDamage(UUID id) {
        return damageMap.get(id);
    }

    @Override
    public void addDamage(UUID id, float damage) {
        Float result = damageMap.putIfAbsent(id, damage);
        if (result != null){
            damageMap.replace(id, damage + result);
        }
    }

    @Override
    public void removeDamage(UUID id, float damage) {
        damageMap.replace(id, getDamage(id) - damage);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag listTag = new ListTag();
        for (Map.Entry<UUID, Float> uuidIntegerEntry : damageMap.entrySet()) {
            CompoundTag entry = new CompoundTag();
            entry.putUUID("attacker", uuidIntegerEntry.getKey());
            entry.putFloat("damage", uuidIntegerEntry.getValue());
            listTag.add(entry);
        }
        tag.put("damageTrackMap", listTag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        LinkedHashMap<UUID, Float> map = new LinkedHashMap<>();
        ListTag list = nbt.getList("damageTrackMap", 10);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag damageTrackMap = list.getCompound(i);
            map.put(damageTrackMap.getUUID("attacker"), damageTrackMap.getFloat("damage"));
        }
        setDamageMap(map);
    }
}
