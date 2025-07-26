package org.CreadoresProgram.CraftJ2ME.network.translator.bedrock;

import org.CreadoresProgram.CraftJ2ME.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftJ2ME.player.Player;

import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.math.vector.Vector2f;

public class MoveEntityAbsolutePacket implements BedrockPacketTranslator {

    @Override
    public void translate(BedrockPacket pk, Player player) {
        org.cloudburstmc.protocol.bedrock.packet.MoveEntityAbsolutePacket packet = (org.cloudburstmc.protocol.bedrock.packet.MoveEntityAbsolutePacket) pk;
        Vector3f position = packet.getPosition();
        Vector2f rotation = packet.getRotation().toVector2();
        if(packet.getRuntimeEntityId() == player.getRuntimeEntityId()) {
            player.setVector3f(position);
            player.setRotation(rotation);
            player.getRenderMCJ2ME().setPlayerPos(player.getVector3f(), player.getRotation());
        }else{
            // Proximamente
        }
    }
}