package org.CreadoresProgram.CraftJ2ME.network.translator.craftj2me;
import com.alibaba.fastjson2.JSONObject;

import org.CreadoresProgram.CraftJ2ME.network.translator.interfaces.CraftJ2MEPacketTranslator;
import org.CreadoresProgram.CraftJ2ME.player.Player;

import org.cloudburstmc.protocol.bedrock.packet.ServerSettingsRequestPacket;
public class SettingsEnterPacket implements CraftJ2MEPacketTranslator{
    @Override
    public void translate(JSONObject pk, Player player){
        player.getBedrockClientSession().sendPacket(new ServerSettingsRequestPacket());
    }
}