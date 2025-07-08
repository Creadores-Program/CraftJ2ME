package org.CreadoresProgram.CraftJ2ME.network.server;
import org.CreadoresProgram.ServerWebGamePost.server.ServerWebGamePostServer;
import org.CreadoresProgram.ServerWebGamePost.server.ProcessDatapackServer;

import org.CreadoresProgram.CraftJ2ME.player.Player;
import org.CreadoresProgram.CraftJ2ME.network.server.packets.*;

import com.alibaba.fastjson2.JSONObject;
public class ServerCraftJ2ME extends Thread{
    private ServerWebGamePostServer serverRaw;
    private int port;
    private String imgPath;
    public ServerCraftJ2ME(int port, String imgPath){
        this.port = port;
        this.imgPath = imgPath;
    }
    public void run(){
        ProcessDatapackCraftJ2ME processDatapacks = new ProcessDatapackCraftJ2ME();
        serverRaw = new ServerWebGamePostServer(port, imgPath, processDatapacks);
        processDatapacks.server = serverRaw;
    }
    public void stopServ(){
        serverRaw.stop();
        serverRaw = null;
    }
    public void sendDataPacket(String identifier, Datapack datapack){
        serverRaw.sendDataPacket(identifier, datapack.toJson());
    }
    public class ProcessDatapackCraftJ2ME extends ProcessDatapackServer{
        public void processDatapack(JSONObject datapack){
            Player player = Server.getInstance().getPlayers().get(datapack.getString("identifier"));
            if(player == null && datapack.getString("ID") == "login"){
                new Player(datapack.getString("identifier"), datapack);
                return;
            }
            if(player == null){
                return;
            }
            player.getPacketTranslatorManager().translate(datapack);
        }
    }
}