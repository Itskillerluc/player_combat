package io.github.itskillerluc.player_combat.mixin;

import net.minecraftforge.event.ServerChatEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerChatEvent.class)
public interface ChatMessageMixin {
    @Mutable
    @Accessor
    void setUsername(String username);
}
