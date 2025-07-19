package org.CreadoresProgram.CraftJ2ME.network.translator.bedrock;
import org.CreadoresProgram.CraftJ2ME.network.server.packets.PlayersListDatapack;
import org.CreadoresProgram.CraftJ2ME.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftJ2ME.player.Player;

import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
public class PlayerListPacket implements BedrockPacketTranslator {

    @Override
    public void translate(BedrockPacket pk, Player player) {
        org.cloudburstmc.protocol.bedrock.packet.PlayerListPacket packet = (org.cloudburstmc.protocol.bedrock.packet.PlayerListPacket) pk;
        if(packet.getAction() == org.cloudburstmc.protocol.bedrock.packet.PlayerListPacket.Action.ADD){
            player.getPlayersList().addAll(packet.getEntries());
        }else{
            player.getPlayersList().removeAll(packet.getEntries());
        }
    }
}