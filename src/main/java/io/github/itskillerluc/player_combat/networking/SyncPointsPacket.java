package io.github.itskillerluc.player_combat.networking;

import io.github.itskillerluc.player_combat.util.Utils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class SyncPointsPacket {
    private Map<UUID, Integer> points;

    public SyncPointsPacket(Map<UUID, Integer> map) {
        this.points = map;
    }

    public SyncPointsPacket(FriendlyByteBuf buf) {
        points = buf.readMap(FriendlyByteBuf::readUUID, FriendlyByteBuf::readInt);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeMap(points, FriendlyByteBuf::writeUUID, FriendlyByteBuf::writeInt);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            Utils.SYNCHED_POINTS = points;
        });
        return true;
    }
}
