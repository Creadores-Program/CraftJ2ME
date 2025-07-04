package org.CreadoresProgram.CraftJ2ME.network.packets;
import org.json.me.JSONObject;
public class LoginDatapack extends Datapack{
    public static String ID = "login";
    public String name;
    public String skin;
    public int wScreen;
    public int hScreen;
    private static final String deviceModel = (System.getProperty("microedition.platform") == null) ? "unknown" : System.getProperty("microedition.platform");
    private static final String language = (System.getProperty("microedition.locale") == null) ? "es-MX" : System.getProperty("microedition.locale");
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
            datapack.put("deviceModel", deviceModel);
            datapack.put("language", language);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
        return datapack;
    }
}