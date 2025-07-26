package org.CreadoresProgram.CraftJ2ME.network.translator.bedrock;

import org.CreadoresProgram.CraftJ2ME.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftJ2ME.player.Player;

import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.math.vector.Vector3i;

public class UpdateBlockPacket implements BedrockPacketTranslator {

    @Override
    public void translate(BedrockPacket pk, Player player) {
        org.cloudburstmc.protocol.bedrock.packet.UpdateBlockPacket packet = (org.cloudburstmc.protocol.bedrock.packet.UpdateBlockPacket) pk;
        Vector3i position = packet.getBlockPosition();
        int blockId = packet.getDefinition().getRuntimeId();
        player.getRenderMCJ2ME().setBlock(position.getX(), position.getY(), position.getZ(), blockId);
    }
}