package org.CreadoresProgram.CraftJ2ME.network.packets;
import org.json.me.JSONObject;
public class InventarySetItem extends Datapack{
    public static String ID = "inventarysetitem";
    public int indexItem;
    public InventaryRequest(String playerId){
        super(playerId);
    }
    public JSONObject toJson(){
        JSONObject datapack;
        try{
            datapack = new JSONObject();
            datapack.put("ID", ID);
            datapack.put("identifier", playerId);
            datapack.put("indexItem", indexItem);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
        return datapack;
    }
}