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
        }catch(Exception er){
            return null;
        }
        datapack.put("ID", ID);
        datapack.put("identifier", playerId);
        return datapack;
    }
}