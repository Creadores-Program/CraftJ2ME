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
    private String domain;
    private int port;
    public ServerClientCraftJ2ME(String domain, int port){
        this.domain = domain;
        this.port = port;
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
            pingLoop.lastResponse = System.currentTimeMillis();
            String id;
            try{
                id = datapack.getString("ID");
            }catch(Exception e){
                e.printStackTrace();
                return;
            }
            /*switch(id){
                case "pong":
                    break;
                case "chat":
                    Main.instance.sendChat(new StringMCItem(datapack.getString("message"), datapack.getString("playerName")));
                    break;
                case "vista":
                    //actualizar
                    break;
                case "inventary":
                    //subir
                    break;
                case "move":
                    //mover
                    break;
                case "exit":
                    pingLoop.running = false;
                    Main.instance.exitServer(datapack.getString("reason"));
                    break;
                case "form":
                    break;
                case "settingsRform":
                    break;
            }*/
        }
    }
    public class IntervalPing extends Thread{
        public boolean running = true;
        private int timeoutMs = 5000;
        public long lastResponse = System.currentTimeMillis();
        public void run(){
            serverRaw = new ServerWebGamePostClient(domain, port, false);
            serverRaw.setProcessDatapacks(new ProcessDatapackCraftJ2ME(serverRaw));
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
        public void stopInterval(){
            running = false;
        }
    }
}