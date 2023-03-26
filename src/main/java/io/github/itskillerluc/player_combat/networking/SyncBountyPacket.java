package io.github.itskillerluc.player_combat.networking;

import io.github.itskillerluc.player_combat.util.Utils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class SyncBountyPacket {
    private Map<UUID, Integer> bounty;
    private static final String MESSAGE = "bounty_packet";

    public SyncBountyPacket(Map<UUID, Integer> map) {
        this.bounty = map;
    }

    public SyncBountyPacket(FriendlyByteBuf buf) {
        bounty = buf.readMap(FriendlyByteBuf::readUUID, FriendlyByteBuf::readInt);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeMap(bounty, FriendlyByteBuf::writeUUID, FriendlyByteBuf::writeInt);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            Utils.SYNCHED_BOUNTY = bounty;
        });
        return true;
    }
}
