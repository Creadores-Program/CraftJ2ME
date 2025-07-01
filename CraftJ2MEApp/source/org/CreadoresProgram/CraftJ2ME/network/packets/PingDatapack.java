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
            datapack.put("ID", ID);
            datapack.put("identifier", playerId);
            datapack.put("ping", System.currentTimeMillis());
        }catch(Exception er){
            er.printStackTrace();
            return null;
        }
        return datapack;
    }
}