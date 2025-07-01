package org.CreadoresProgram.CraftJ2ME.network.packets;
import org.json.me.JSONObject;
public class LoginDatapack extends Datapack{
    public static String ID = "login";
    public String name;
    public String skin;
    public int wScreen;
    public int hScreen;
    public LoginDatapack(String playerId){
        super(playerId);
    }
    public JSONObject toJson(){
        JSONObject datapack;
        try{
            datapack = new JSONObject();
            datapack.put("ID", ID);
            datapack.put("identifier", playerId);
            datapack.put("name", name);
            datapack.put("skin", skin);
            datapack.put("wScreen", wScreen);
            datapack.put("hScreen", hScreen);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
        return datapack;
    }
}