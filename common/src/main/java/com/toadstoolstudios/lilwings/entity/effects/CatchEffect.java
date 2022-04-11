package com.toadstoolstudios.lilwings.entity.effects;

import dev.willowworks.lilwings.entity.ButterflyEntity;
import net.minecraft.world.entity.player.Player;

public interface CatchEffect {

    void onCatch(Player player, ButterflyEntity butterfly, int catchAmount);
}