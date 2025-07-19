package org.CreadoresProgram.CraftJ2ME.network;
import org.CreadoresProgram.ServerWebGamePost.client.ProcessDatapackClient;
import org.CreadoresProgram.ServerWebGamePost.client.ServerWebGamePostClient;

import org.CreadoresProgram.CraftJ2ME.Main;
import org.CreadoresProgram.CraftJ2ME.network.packets.*;
import org.CreadoresProgram.CraftJ2ME.ui.*;

import org.json.me.JSONObject;
import org.json.me.JSONArray;

import java.util.Vector;
public class ServerClientCraftJ2ME extends Thread{
    private ServerWebGamePostClient serverRaw;
    private IntervalPing pingLoop;
    public IntervalQueue queueLoop;
    private String domain;
    private int port;
    public ServerClientCraftJ2ME(String domain, int port){
        this.domain = domain;
        this.port = port;
    }
    public void run(){
        serverRaw = new ServerWebGamePostClient(domain, port, false);
        serverRaw.setProcessDatapacks(new ProcessDatapackCraftJ2ME(serverRaw));
        LoginDatapack initPacket = new LoginDatapack(Main.instance.getIdPlayer());
        initPacket.name = Main.instance.getPlayerName();
        initPacket.wScreen = Main.instance.getVistaCanvasMC().getWidth();
        initPacket.hScreen = Main.instance.getVistaCanvasMC().getHeight();
        initPacket.skin = Main.instance.getSkin();
        sendDataPacket(initPacket);
        pingLoop = new IntervalPing();
        pingLoop.start();
    }
    public void stopServ(){
        pingLoop = null;
        queueLoop = null;
        serverRaw = null;
    }
    public void sendDataPacket(Datapack datapack){
        serverRaw.sendDataPacket(datapack.toJson());
    }
    public class ProcessDatapackCraftJ2ME extends ProcessDatapackClient{
        public ProcessDatapackCraftJ2ME(ServerWebGamePostClient serv){
            super(serv);
        }
        public void processDatapack(JSONObject datapack){
            String id;
            try{
                id = datapack.getString("ID");
            }catch(Exception e){
                e.printStackTrace();
                return;
            }
            if(id == "pong"){
                pingLoop.lastResponse = System.currentTimeMillis();
            }else if(id == "chat"){
                try{
                    Main.instance.sendChat(new StringMCItem(datapack.getString("message"), datapack.getString("playerName")));
                }catch(Exception er){
                    er.printStackTrace();
                }
            }else if(id == "vista"){
                if(queueLoop == null){
                    queueLoop = new IntervalQueue();
                    queueLoop.start();
                }
                try{
                    Main.instance.getVistaCanvasMC().updateVistaMC(datapack.getString("vistaImg"));
                }catch(Exception er){
                    er.printStackTrace();
                }
            }else if(id == "inventary"){
                //actualizar lista de inventario
            }else if(id == "exit"){
                if(queueLoop != null){
                    queueLoop.running = false;
                }
                pingLoop.running = false;
                try{
                    Main.instance.exitServer(datapack.getString("reason"));
                }catch(Exception er){
                    er.printStackTrace();
                }
            }else if(id == "form"){
                //establecer vista de form
            }else if(id == "settingsRForm"){
                //añadir a lista de config
            }else if(id == "playersList"){
                JSONArray playersListd;
                try{
                    playersListd = datapack.getJSONArray("playersList");
                }catch(Exception ex){
                    ex.printStackTrace();
                    return;
                }
                for(int a = 0; a < playersListd.length(); a++){
                    String playerName;
                    try{
                        playerName = playersListd.getString(a);
                    }catch(Exception ex){
                        ex.printStackTrace();
                        continue;
                    }
                    boolean isRepeat = false;
                    for(int i = 0; i < Main.instance.getPlayersList().size(); i++){
                        if(Main.instance.getPlayersList().getString(i).equals(playerName)){
                            isRepeat = true;
                            break;
                        }
                    }
                    if(isRepeat){
                        continue;
                    }
                    Main.instance.getPlayersList().append(playerName, null);
                }
                for(int b = 0; b < Main.instance.getPlayersList().size(); b++){
                    String playerName = Main.instance.getPlayersList().getString(b);
                    boolean encontred = false;
                    for(int c = 0; c < playersListd.length(); c++){
                        String coinciden;
                        try{
                            coinciden = playersListd.getString(c);
                        }catch(Exception ex){
                            ex.printStackTrace();
                            continue;
                        }
                        if(playerName.equals(coinciden)){
                            encontred = true;
                        }
                    }
                    if(!encontred){
                        Main.instance.getPlayersList().delete(b);
                    }
                }
            }
        }
    }
    public class IntervalPing extends Thread{
        public boolean running = true;
        private int timeoutMs = 5000;
        public long lastResponse = System.currentTimeMillis();
        public void run(){
            while(running){
                try{
                    long now = System.currentTimeMillis();
                    if(now - lastResponse > timeoutMs){
                        running = false;
                        if(queueLoop != null){
                            queueLoop.running = false;
                        }
                        Main.instance.noRespondingServer();
                        return;
                    }
                    PingDatapack packet = new PingDatapack(Main.instance.getIdPlayer());
                    sendDataPacket(packet);
                    Thread.sleep(1000);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
    public class IntervalQueue extends Thread{
        public Vector datapacks;
        private boolean running = true;
        public void run(){
            datapacks = new Vector();
            while(running){
                if(datapacks.size() == 0){
                    try{
                        Thread.sleep(1000);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    continue;
                }
                for(int i = 0; i < datapacks.size(); i++){
                    try{
                        Datapack datapack = (Datapack) datapacks.elementAt(i);
                        sendDataPacket(datapack);
                    }catch(Exception er){
                        er.printStackTrace();
                    }
                    datapacks.removeElementAt(i);
                }
                try{
                    Thread.sleep(1000);
                }catch(Exception er){
                    er.printStackTrace();
                }
            }
        }
    }
}
