package org.CreadoresProgram.CraftJ2ME.network;
import org.CreadoresProgram.CraftJ2ME.player.Player;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacketHandler;
import org.cloudburstmc.protocol.common.PacketSignal;
public class BedrockBatchHandler implements BedrockPacketHandler{
    private final Player player;
    public BedrockBatchHandler(Player player){
        this.player = player;
    }
    @Override
     public PacketSignal handlePacket(BedrockPacket packet) {
        player.getPacketTranslatorManager().translate(packet);
        return PacketSignal.HANDLED;
    }
}