package org.CreadoresProgram.CraftJ2ME.network.server.packets;
import com.alibaba.fastjson2.JSONObject;
public class PongDatapack extends Datapack{
    public static String ID = "pong";
    public JSONObject toJson(){
        JSONObject datapack = new JSONObject();
        datapack.put("ID", ID);
        return datapack;
    }
}