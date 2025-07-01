package org.CreadoresProgram.CraftJ2ME.network.packets;
import org.json.me.JSONObject;
public class ChatDatapack extends Datapack{
    public static String ID = "chat";
    public String message;
    public ChatDatapack(String playerId){
        super(playerId);
    }
    public JSONObject toJson(){
        JSONObject datapack;
        try{
            datapack = new JSONObject();
            datapack.put("ID", ID);
            datapack.put("identifier", playerId);
            datapack.put("message", message);
        }catch(Exception er){
            er.printStackTrace();
            return null;
        }
        return datapack;
    }
}