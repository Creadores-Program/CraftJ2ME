package org.CreadoresProgram.CraftJ2ME.network.packets;
import org.json.me.JSONObject;
public class UpdateSkinDatapack extends Datapack{
    public static String ID = "updateskin";
    public String skin;
    public UpdateSkinDatapack(String playerId){
        super(playerId);
    }
    public JSONObject toJson(){
        JSONObject datapack;
        try{
            datapack = new JSONObject();
            datapack.put("ID", ID);
            datapack.put("identifier", playerId);
            datapack.put("skin", skin);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
        return datapack;
    }
}