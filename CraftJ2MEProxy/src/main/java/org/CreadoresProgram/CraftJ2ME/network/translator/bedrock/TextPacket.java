package org.CreadoresProgram.CraftJ2ME.network.translator.bedrock;

import org.CreadoresProgram.CraftJ2ME.network.server.packets.ChatDatapack;
import net.kyori.adventure.text.Component;
import org.CreadoresProgram.CraftJ2ME.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftJ2ME.player.Player;
import org.CreadoresProgram.CraftJ2ME.server.Server;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;

public class TextPacket implements BedrockPacketTranslator {

    @Override
    public void translate(BedrockPacket pk, Player player) {
        org.cloudburstmc.protocol.bedrock.packet.TextPacket packet = (org.cloudburstmc.protocol.bedrock.packet.TextPacket) pk;

        switch (packet.getType()) {
            case TIP:
            case POPUP:
            case JUKEBOX_POPUP: {
                break;
            }
            case SYSTEM: {
                ChatDatapack subpk = new ChatDatapack();
                subpk.playerName = packet.getSourceName();
                subpk.message = packet.getMessage();
                Server.getInstance().getServer().sendDataPacket(player.getIdentifier(), subpk);
                break;
            }
            case TRANSLATION: {
                ChatDatapack subpk = new ChatDatapack();
                subpk.playerName = packet.getSourceName();
                subpk.message = packet.getMessage();
                Server.getInstance().getServer().sendDataPacket(player.getIdentifier(), subpk);
                if(player.getTraslateAd() != "true"){
                    player.setTraslateAd("true");
                    ChatDatapack subpkt = new ChatDatapack();
                    subpkt.playerName = packet.getSourceName();
                    subpkt.message = "§cChat translation not implemented! Force language on Minecraft bedrock server or use Translator for Geyser Reverse Barrel for Nukkit";
                    Server.getInstance().getServer().sendDataPacket(player.getIdentifier(), subpkt);
                }
            }
            default: {
                ChatDatapack subpk = new ChatDatapack();
                subpk.playerName = packet.getSourceName();
                subpk.message = packet.getMessage();
                Server.getInstance().getServer().sendDataPacket(player.getIdentifier(), subpk);
                break;
            }
        }
    }
}