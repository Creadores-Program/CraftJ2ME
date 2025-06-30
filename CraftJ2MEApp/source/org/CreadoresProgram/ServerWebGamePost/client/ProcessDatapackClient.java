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
            throw new RuntimeException("NullPointerException: server is null");
        }
        this.server = server;
    }
    public final void process(JSONObject datapacksLot){
        JSONArray datapacks;
        try{
            datapacks = datapacksLot.getJSONArray("datapacksLot");
        }catch(Exception e){
            e.printStackTrace();
            return;
        }
        for(int i = 0; i < datapacks.length(); i++){
            try{
                JSONObject datapack = datapacks.getJSONObject(i);
                this.processDatapack(datapack);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    public void processDatapack(JSONObject datapack){
        //code...
    }
}