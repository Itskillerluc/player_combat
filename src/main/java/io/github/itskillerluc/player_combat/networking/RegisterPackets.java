package io.github.itskillerluc.player_combat.networking;

import io.github.itskillerluc.player_combat.PlayerCombat;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class RegisterPackets {
    private static SimpleChannel INSTANCE;

    private static int packetId = 0;
    private static int id() {
        return packetId++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(PlayerCombat.MODID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        net.messageBuilder(SyncPointsPacket.class, id())
                .encoder(SyncPointsPacket::toBytes)
                .decoder(SyncPointsPacket::new)
                .consumerMainThread(SyncPointsPacket::handle)
                .add();

        net.messageBuilder(SyncBountyPacket.class, id())
                .encoder(SyncBountyPacket::toBytes)
                .decoder(SyncBountyPacket::new)
                .consumerMainThread(SyncBountyPacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
