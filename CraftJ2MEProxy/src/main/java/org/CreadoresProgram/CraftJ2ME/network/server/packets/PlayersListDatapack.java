package org.CreadoresProgram.CraftJ2ME.network.server.packets;
import org.CreadoresProgram.CraftJ2ME.player.Player;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONArray;

import org.cloudburstmc.protocol.bedrock.packet.PlayerListPacket;

import java.util.List;
public class PlayersListDatapack extends Datapack{
    public static String ID = "playersList";
    public Player player;
    public PlayersListDatapack(Player player){
        this.player = player;
    }
    public JSONObject toJson(){
        JSONObject datapack = new JSONObject();
        datapack.put("ID", ID);
        JSONArray playersList = new JSONArray();
        for(PlayerListPacket.Entry entry : player.getPlayersList()){
            playersList.add(entry.getName());
        }
        datapack.put("playersList", playersList);
        return datapack;
    }
}