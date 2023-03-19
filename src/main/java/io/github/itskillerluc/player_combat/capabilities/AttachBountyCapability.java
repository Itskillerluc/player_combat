package io.github.itskillerluc.player_combat.capabilities;

import io.github.itskillerluc.player_combat.PlayerCombat;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AttachBountyCapability {
    public static final Capability<IBountyCapability> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});
    private static class BountyCapabilityProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        public static final ResourceLocation IDENTIFIER = new ResourceLocation(PlayerCombat.MODID, "bounty");

        private final IBountyCapability backend = new BountyCapaiblity();
        private final LazyOptional<IBountyCapability> optionalData = LazyOptional.of(() -> backend);

        @NotNull
        @Override
        public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return INSTANCE.orEmpty(cap, this.optionalData);
        }

        @Override
        public CompoundTag serializeNBT() {
            return this.backend.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            this.backend.deserializeNBT(nbt);
        }
    }

    public static void attach(final AttachCapabilitiesEvent<Level> event) {
        event.addCapability(BountyCapabilityProvider.IDENTIFIER, new BountyCapabilityProvider());
    }
}
