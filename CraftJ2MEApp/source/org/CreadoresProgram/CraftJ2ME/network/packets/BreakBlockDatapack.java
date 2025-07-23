package org.CreadoresProgram.CraftJ2ME.network.packets;
import org.json.me.JSONObject;
public class BreakBlockDatapack extends Datapack{
    public static String ID = "breakblock";
    public int idInt;//0 no romper, 1 romper
    public BreakBlockDatapack(String playerId){
        super(playerId);
    }
    public JSONObject toJson(){
        JSONObject datapack;
        try{
            datapack = new JSONObject();
            datapack.put("ID", ID);
            datapack.put("identifier", playerId);
            datapack.put("idInt", idInt);
        }catch(Exception er){
            er.printStackTrace();
            return null;
        }
        return datapack;
    }
}