package org.CreadoresProgram.CraftJ2ME.network.translator.bedrock;
import org.CreadoresProgram.CraftJ2ME.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftJ2ME.player.Player;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
public class SetTimePacket implements BedrockPacketTranslator {

    @Override
    public void translate(BedrockPacket pk, Player player) {
        org.cloudburstmc.protocol.bedrock.packet.SetTimePacket packet = (org.cloudburstmc.protocol.bedrock.packet.SetTimePacket) pk;
        player.getRenderMCJ2ME().setTime(packet.getTime());
    }
}