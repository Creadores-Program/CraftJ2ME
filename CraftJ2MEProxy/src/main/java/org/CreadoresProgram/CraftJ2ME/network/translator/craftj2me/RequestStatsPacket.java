package org.CreadoresProgram.CraftJ2ME.network.translator.craftj2me;
import com.alibaba.fastjson2.JSONObject;

import org.CreadoresProgram.CraftJ2ME.network.translator.interfaces.CraftJ2MEPacketTranslator;
import org.CreadoresProgram.CraftJ2ME.network.server.packets.ResponseStatsDatapack;
import org.CreadoresProgram.CraftJ2ME.player.Player;
import org.CreadoresProgram.CraftJ2ME.server.Server;

public class RequestStatsPacket implements CraftJ2MEPacketTranslator{
    @Override
    public void translate(JSONObject pk, Player player){
        ResponseStatsDatapack response = new ResponseStatsDatapack();
        response.vida = player.vida;
        response.hambre = player.hambre;
        response.armadura = player.armadura;
        response.xp = player.xp;
        Server.getInstance().getServer().sendDataPacket(player.getIdentifier(), response);
    }
}