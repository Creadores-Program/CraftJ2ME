package org.CreadoresProgram.CraftJ2ME.network.translator.bedrock;

import org.CreadoresProgram.CraftJ2ME.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftJ2ME.player.Player;
import org.CreadoresProgram.CraftJ2ME.server.Server;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;

public class NetworkSettingsPacket implements BedrockPacketTranslator {
    @Override
    public void translate(BedrockPacket pk, Player player) {
        org.cloudburstmc.protocol.bedrock.packet.NetworkSettingsPacket packet = (org.cloudburstmc.protocol.bedrock.packet.NetworkSettingsPacket) pk;
        player.getBedrockClientSession().setCompression(packet.getCompressionAlgorithm());
        player.getBedrockClientSession().sendPacketImmediately(player.getLoginDatapack());
    }
}