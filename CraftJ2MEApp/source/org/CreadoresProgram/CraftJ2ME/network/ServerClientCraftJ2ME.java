package org.CreadoresProgram.CraftJ2ME.network;
import org.CreadoresProgram.ServerWebGamePost.client.ProcessDatapackClient;
import org.CreadoresProgram.ServerWebGamePost.client.ServerWebGamePostClient;

import org.CreadoresProgram.CraftJ2ME.Main;
import org.CreadoresProgram.CraftJ2ME.network.packets.*;
import org.CreadoresProgram.CraftJ2ME.ui.*;

import org.json.me.JSONObject;
public class ServerClientCraftJ2ME{
    private ServerWebGamePostClient serverRaw;
    private IntervalPing pingLoop;
    private IntervalMove moveLoop;
    private String domain;
    private int port;
    public ServerClientCraftJ2ME(String domain, int port){
        this.domain = domain;
        this.port = port;
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
    public void stop(){
        pingLoop = null;
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
}
