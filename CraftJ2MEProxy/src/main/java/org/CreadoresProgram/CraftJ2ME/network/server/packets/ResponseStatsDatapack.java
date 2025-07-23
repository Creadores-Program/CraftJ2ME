package org.CreadoresProgram.CraftJ2ME.network.server.packets;
import com.alibaba.fastjson2.JSONObject;
public class ResponseStatsDatapack extends Datapack{
    public static String ID = "responsestats";
    public int vida;
    public int hambre;
    public int armadura;
    public JSONObject toJson(){
        JSONObject datapack = new JSONObject();
        datapack.put("ID", ID);
        datapack.put("vida", vida);
        datapack.put("hambre", hambre);
        datapack.put("armadura", armadura);
        return datapack;
    }
}