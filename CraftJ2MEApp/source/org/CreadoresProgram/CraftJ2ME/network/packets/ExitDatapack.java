package org.CreadoresProgram.CraftJ2ME.network.packets;
import org.json.me.JSONObject;
public class ExitDatapack extends Datapack{
    public static String ID = "exit";
    public ExitDatapack(String playerId){
        super(playerId);
    }
    public JSONObject toJson(){
        JSONObject datapack;
        try{
            datapack = new JSONObject();
            datapack.put("ID", ID);
            datapack.put("identifier", playerId);
        }catch(Exception er){
            return null;
        }
        return datapack;
    }
}