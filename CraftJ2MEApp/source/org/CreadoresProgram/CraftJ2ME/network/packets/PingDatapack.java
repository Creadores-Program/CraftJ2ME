package org.CreadoresProgram.CraftJ2ME.network.packets;
import org.json.me.JSONObject;
public class PingDatapack extends Datapack{
    public static String ID = "ping";
    public PingDatapack(String playerId){
        super(playerId);
    }
    public JSONObject toJson(){
        JSONObject datapack;
        try{
            datapack = new JSONObject();
        }catch(Exception er){
            return null;
        }
        datapack.put("ID", ID);
        datapack.put("identifier", playerId);
        datapack.put("ping", System.currentTimeMillis());
        return datapack;
    }
}