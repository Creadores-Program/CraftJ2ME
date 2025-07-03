package org.CreadoresProgram.CraftJ2ME.network.server.packets;
import com.alibaba.fastjson2.JSONObject;
public class ChatDatapack extends Datapack{
    public static String ID = "chat";
    public String message;
    public String playerName;
    public JSONObject toJson(){
        JSONObject datapack = new JSONObject();
        datapack.put("ID", ID);
        datapack.put("message", message);
        datapack.put("playerName", playerName);
        return datapack;
    }
}