package org.CreadoresProgram.CraftJ2ME.network;
import org.CreadoresProgram.ServerWebGamePost.client.ProcessDatapackClient;
import org.CreadoresProgram.ServerWebGamePost.client.ServerWebGamePostClient;

import org.CreadoresProgram.CraftJ2ME.Main;
import org.CreadoresProgram.CraftJ2ME.network.packets.*;
import org.CreadoresProgram.CraftJ2ME.ui.*;

import org.json.me.JSONObject;
import org.json.me.JSONArray;
public class ServerClientCraftJ2ME extends Thread{
    private ServerWebGamePostClient serverRaw;
    private IntervalPing pingLoop;
    private IntervalMove moveLoop;
    private IntervalChat chatLoop:
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
        moveLoop = null;
        chatLoop = null;
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
                if(moveLoop == null){
                    moveLoop = new IntervalMove();
                    moveLoop.start();
                }
                if(chatLoop == null){
                    chatLoop = new IntervalChat();
                    chatLoop.start();
                }
                try{
                    Main.instance.getVistaCanvasMC().updateVistaMC(datapack.getString("vistaImg"));
                }catch(Exception er){
                    er.printStackTrace();
                }
            }else if(id == "inventary"){
                //actualizar lista de inventario
            }else if(id == "exit"){
                if(moveLoop != null){
                    moveLoop.running = false;
                }
                if(chatLoop != null){
                    chatLoop.running = false;
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
                        if(moveLoop != null){
                            moveLoop.running = false;
                        }
                        if(chatLoop != null){
                            chatLoop.running = false;
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
    public class IntervalMove extends Thread{
        private int x = 0;
        private int y = 0;
        private int z = 0;
        private int yaw = 0;
        private int pitch = 0;
        public boolean running = true;
        public void run(){
            while(running){
                if(x == Main.instance.getVistaCanvasMC().x && y == Main.instance.getVistaCanvasMC().y && z == Main.instance.getVistaCanvasMC().z && yaw == Main.instance.getVistaCanvasMC().yaw && pitch == Main.instance.getVistaCanvasMC().pitch){
                    try{
                        Thread.sleep(1000);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    continue;
                }
                x = Main.instance.getVistaCanvasMC().x;
                y = Main.instance.getVistaCanvasMC().y;
                z = Main.instance.getVistaCanvasMC().z;
                yaw = Main.instance.getVistaCanvasMC().yaw;
                pitch = Main.instance.getVistaCanvasMC().pitch;
                MoveDatapack datapack = new MoveDatapack(Main.instance.getIdPlayer());
                datapack.x = x;
                datapack.y = y;
                datapack.z = z;
                datapack.yaw = yaw;
                datapack.pitch = pitch;
                sendDataPacket(datapack);
                try{
                    Thread.sleep(1000);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
    public class IntervalChat extends Thread{
        public JSONArray chat;
        private boolean running = true;
        public void run(){
            try{
                chat = new JSONArray();
            }catch(Exception er){
                er.printStackTrace();
            }
            while(running){
                if(chat.length() == 0){
                    try{
                        Thread.sleep(1000);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    continue;
                }
                for(int i = 0; i < chat.length(); i++){
                    ChatDatapack datapack = new ChatDatapack(Main.instance.getIdPlayer());
                    try{
                        datapack.message = chat.getString(i);
                        sendDataPacket(datapack);
                    }catch(Exception er){
                        er.printStackTrace();
                    }
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
