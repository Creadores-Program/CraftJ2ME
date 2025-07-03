package org.CreadoresProgram.CraftJ2ME.network.server.packets;
import com.alibaba.fastjson2.JSONObject;
public class VistaDatapack extends Datapack{
    public static String ID = "vista";
    public String vistaImg;
    public JSONObject toJson(){
        JSONObject datapack = new JSONObject();
        datapack.put("ID", ID);
        datapack.put("vistaImg", vistaImg);
        return datapack;
    }
}