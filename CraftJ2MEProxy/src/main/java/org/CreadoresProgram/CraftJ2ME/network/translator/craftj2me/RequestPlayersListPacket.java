package org.CreadoresProgram.CraftJ2ME.network.translator.craftj2me;
import com.alibaba.fastjson2.JSONObject;

import org.CreadoresProgram.CraftJ2ME.network.translator.interfaces.CraftJ2MEPacketTranslator;
import org.CreadoresProgram.CraftJ2ME.network.server.packets.PlayersListDatapack;
import org.CreadoresProgram.CraftJ2ME.player.Player;
import org.CreadoresProgram.CraftJ2ME.server.Server;

public class RequestPlayersListPacket implements CraftJ2MEPacketTranslator{
    @Override
    public void translate(JSONObject pk, Player player){
        Server.getInstance().getServer().sendDataPacket(player.getIdentifier(), new PlayersListDatapack(player));
    }
}