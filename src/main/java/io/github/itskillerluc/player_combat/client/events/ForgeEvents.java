package io.github.itskillerluc.player_combat.client.events;

import io.github.itskillerluc.player_combat.PlayerCombat;
import io.github.itskillerluc.player_combat.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(bus= Mod.EventBusSubscriber.Bus.FORGE, modid = PlayerCombat.MODID, value = Dist.CLIENT)
public class ForgeEvents {


    @SubscribeEvent
    static void chatEvent(final ClientChatReceivedEvent event) {
        String[] message = event.getMessage().getString().replaceFirst("<", "").split(">");
        var list = Utils.SYNCHED_POINTS.entrySet().stream().sorted(Comparator.comparingInt(entry -> ((Map.Entry<UUID, Integer>) entry).getValue()).reversed()).toList();
        var points = String.valueOf(Utils.SYNCHED_POINTS.get(event.getMessageSigner().profileId()));
        var component = Component.empty()
                .append(getComponent(event, list))
                .append(Component.literal(points.equals("null") ? "0" : points))
                        .withStyle(ChatFormatting.UNDERLINE)
                .append(" | ")
                .append(message[0]);

        if (Utils.SYNCHED_BOUNTY.containsKey(event.getMessageSigner().profileId()) && Utils.SYNCHED_BOUNTY.get(event.getMessageSigner().profileId()) != 0) {
            component.append(Component.literal("\uEff2")
                    .withStyle(Style.EMPTY.withFont(new ResourceLocation(PlayerCombat.MODID, "icons"))
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(String.valueOf(Utils.SYNCHED_BOUNTY.get(event.getMessageSigner().profileId())))))));
        }

        component.append(":");
        component.append(message[1]);
        event.setMessage(component);
    }

    private static Component getComponent(ClientChatReceivedEvent event, List<Map.Entry<UUID, Integer>> list){
        return switch (list.stream().map(Map.Entry::getKey).toList().indexOf(event.getMessageSigner().profileId())) {
            case 0 -> Component.literal("\uEff3").setStyle(Style.EMPTY.withFont(new ResourceLocation(PlayerCombat.MODID,"icons")));
            case 1 -> Component.literal("\uEff4").setStyle(Style.EMPTY.withFont(new ResourceLocation(PlayerCombat.MODID,"icons")));
            case 2 -> Component.literal("\uEff5").setStyle(Style.EMPTY.withFont(new ResourceLocation(PlayerCombat.MODID,"icons")));
            default -> Component.literal("\uEff1").setStyle(Style.EMPTY.withFont(new ResourceLocation(PlayerCombat.MODID,"icons")));
        };
    }
}
