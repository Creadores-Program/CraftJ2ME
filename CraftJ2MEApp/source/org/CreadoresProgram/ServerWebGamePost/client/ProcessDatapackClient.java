package org.CreadoresProgram.ServerWebGamePost.client;
import org.json.me.JSONObject;
import org.json.me.JSONArray;
public class ProcessDatapackClient{
    public ServerWebGamePostClient server;
    public ServerWebGamePostClient getServer(){
        return server;
    }
    public ProcessDatapackClient(ServerWebGamePostClient server){
        if(server == null){
            throw RuntimeException("NullPointerException: server is null");
        }
        this.server = server;
    }
    public final void process(@NonNull JSONObject datapacksLot){
        JSONArray datapacks = datapacksLot.getJSONArray("datapacksLot");
        for(int i = 0; i < datapacks.length(); i++){
            JSONObject datapack = datapacks.getJSONObject(i);
            this.processDatapack(datapack);
        }
    }
    public void processDatapack(JSONObject datapack){
        //code...
    }
}