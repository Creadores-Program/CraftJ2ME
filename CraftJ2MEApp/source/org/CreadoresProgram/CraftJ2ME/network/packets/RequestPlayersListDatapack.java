package org.CreadoresProgram.CraftJ2ME.network.packets;
import org.json.me.JSONObject;
public class RequestPlayersListDatapack extends Datapack{
    public static String ID = "requestplayerslist";
    public RequestPlayersListDatapack(String playerId){
        super(playerId);
    }
    public JSONObject toJson(){
        JSONObject datapack;
        try{
            datapack = new JSONObject();
            datapack.put("ID", ID);
            datapack.put("identifier", playerId);
        }catch(Exception er){
            er.printStackTrace();
            return null;
        }
        return datapack;
    }
}