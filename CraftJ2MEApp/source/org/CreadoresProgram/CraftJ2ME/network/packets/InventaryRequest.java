package org.CreadoresProgram.CraftJ2ME.network.packets;
import org.json.me.JSONObject;
public class InventaryRequest extends Datapack{
    public static String ID = "inventaryrequest";
    public int pageRequest;
    public InventaryRequest(String playerId){
        super(playerId);
    }
    public JSONObject toJson(){
        JSONObject datapack;
        try{
            datapack = new JSONObject();
            datapack.put("ID", ID);
            datapack.put("identifier", playerId);
            datapack.put("pageRequest", pageRequest);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
        return datapack;
    }
}