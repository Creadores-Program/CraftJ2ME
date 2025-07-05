package org.CreadoresProgram.CraftJ2ME.network.translator.interfaces;

import org.CreadoresProgram.CraftJ2ME.player.Player;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;

public interface BedrockPacketTranslator {

    default boolean immediate() {
        return false;
    }

    void translate(BedrockPacket pk, Player player);
}