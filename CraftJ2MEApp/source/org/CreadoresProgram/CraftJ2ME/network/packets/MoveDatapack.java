package org.CreadoresProgram.CraftJ2ME.network.packets;
import org.json.me.JSONObject;
public class MoveDatapack extends Datapack{
    public static String ID = "move";
    public int x;
    public int y;
    public int z;
    public int yaw;
    public int pitch;
    public MoveDatapack(String playerId){
        super(playerId);
    }
    public JSONObject toJson(){
        JSONObject datapack;
        try{
            datapack = new JSONObject();
            datapack.put("ID", ID);
            datapack.put("identifier", playerId);
            datapack.put("x", x);
            datapack.put("y", y);
            datapack.put("z", z);
            datapack.put("yaw", yaw);
            datapack.put("pitch", pitch);
        }catch(Exception er){
            er.printStackTrace();
            return null;
        }
        return datapack;
    }
}