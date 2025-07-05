package org.CreadoresProgram.CraftJ2ME.network.translator.craftj2me;
import com.alibaba.fastjson2.JSONObject;

import org.CreadoresProgram.CraftJ2ME.network.translator.interfaces.CraftJ2MEPacketTranslator;
import org.CreadoresProgram.CraftJ2ME.player.Player;
import org.CreadoresProgram.CraftJ2ME.server.Server;

import org.cloudburstmc.protocol.bedrock.packet.CommandRequestPacket;
import org.cloudburstmc.protocol.bedrock.data.command.CommandOriginType;
import org.cloudburstmc.protocol.bedrock.data.command.CommandOriginData;
import org.cloudburstmc.protocol.bedrock.packet.TextPacket;
public class ChatPacket implements CraftJ2MEPacketTranslator{
    @Override
    public void translate(JSONObject pk, Player player){
        if(!pk.getString("message").startsWith("/")){
            TextPacket subpk = new TextPacket();
            subpk.setType(TextPacket.Type.CHAT);
            subpk.setNeedsTranslation(false);
            subpk.setSourceName(player.getLoginDatapackCraftJ2ME().getString("name"));
            subpk.setMessage(pk.getString("message"));
            subpk.setXuid("");
            subpk.setPlatformChatId("");
            player.getBedrockClientSession().sendPacket(subpk);
        }else{
            CommandRequestPacket Crp = new CommandRequestPacket();
            Crp.setVersion(Server.getInstance().getBedrockPacketCodec().getProtocolVersion());
            Crp.setCommand("?"+pk.getString("message").substring(1));
            CommandOriginData Cod = new CommandOriginData(CommandOriginType.PLAYER, UUID.fromString(player.getIdentifier()), player.getIdentifier(), 0);
            Crp.setCommandOriginData(Cod);
            player.getBedrockClientSession().sendPacket(Crp);
        }
    }
}