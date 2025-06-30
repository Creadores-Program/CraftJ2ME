package org.CreadoresProgram.CraftJ2ME.network.packets;
import org.json.me.JSONObject;
public class Datapack{
    public static String ID = "unknown";
    public String playerId = "unknown";
    public JSONObject toJson(){}
    public Datapack(String playerId){
        this.playerId = playerId;
    }
}