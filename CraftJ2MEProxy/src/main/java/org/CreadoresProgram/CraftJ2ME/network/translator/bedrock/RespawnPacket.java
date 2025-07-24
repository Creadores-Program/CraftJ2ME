package org.CreadoresProgram.CraftJ2ME.network.translator.bedrock;

import org.CreadoresProgram.CraftJ2ME.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftJ2ME.player.Player;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;

public class RespawnPacket implements BedrockPacketTranslator {

    @Override
    public void translate(BedrockPacket pk, Player player) {
        org.cloudburstmc.protocol.bedrock.packet.RespawnPacket packet = (org.cloudburstmc.protocol.bedrock.packet.RespawnPacket) pk;
        if(packet.getRuntimeEntityId() != player.getRuntimeEntityId()) {
            return;
        }
        player.setVector3f(packet.getPosition());
        player.setRotation(player.getLastServerRotation());
        //actualizar vista
    }
}