package org.CreadoresProgram.CraftJ2ME.network.translator.craftj2me;
import com.alibaba.fastjson2.JSONObject;

import org.CreadoresProgram.CraftJ2ME.network.translator.interfaces.CraftJ2MEPacketTranslator;
import org.CreadoresProgram.CraftJ2ME.player.Player;
import org.CreadoresProgram.CraftJ2ME.server.Server;
import org.CreadoresProgram.CraftJ2ME.network.server.packets.PongDatapack;
public class PingPacket implements CraftJ2MEPacketTranslator{
    @Override
    public boolean immediate(){
        return true;
    }
    @Override
    public void translate(JSONObject pk, Player player){
        Server.getInstance().getServer().sendDataPacket(player.getIdentifier(), new PongDatapack());
    }
}