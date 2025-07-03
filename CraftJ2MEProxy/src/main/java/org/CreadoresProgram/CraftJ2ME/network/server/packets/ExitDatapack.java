package org.CreadoresProgram.CraftJ2ME.network.server.packets;
import com.alibaba.fastjson2.JSONObject;
public class ExitDatapack extends Datapack{
    public static String ID = "exit";
    public String reason;
    public JSONObject toJson(){
        JSONObject datapack = new JSONObject();
        datapack.put("ID", ID);
        datapack.put("reason", reason);
        return datapack;
    }
}