package org.CreadoresProgram.CraftJ2ME;
import javax.microedition.midlet.MIDlet;
import javax.microedition.lcdui.*;
import javax.microedition.rms.*;

import java.util.Random;

import org.json.me.JSONObject;
import org.json.me.JSONArray;

import org.CreadoresProgram.CraftJ2ME.ui.*;
import org.CreadoresProgram.CraftJ2ME.network.packets.ExitDatapack;
import org.CreadoresProgram.CraftJ2ME.network.ServerClientCraftJ2ME;
public class Main extends MIDlet implements CommandListener{
    private List preservers;
    private Command preServerSelectServ;
    private Command preServerAddServ;
    private Command preServerDelServ;
    private Command preServerQuit;
    
    private VistaMCcanvas mcVista;
    private Command mcVistaChat;
    private Command mcVistaPause;

    private List pause;
    private Command pauseSelec;
    private Command pauseResume;

    private List config;
    private Command configSelec;
    private Command configQuit;

    private Form anidirServer;
    private TextField anidirServerIP;
    private TextField anidirServerPort;
    private TextField anidirServerName;
    private Command anidirServerCancel;
    private Command anidirServerAdd;

    private TextBox cambiarNombre;
    private Command cambiarNombreOK;
    private Command cambiarNombreCancel;

    private List playersList;
    private Command playersListQuit;

    private Form chatMC;
    private Command chatMCQuit;
    private Command chatMCSendMsg;

    private TextBox sendMsgChat;
    private Command sendMsgChatSend;
    private Command sendMsgChatCancel;

    private List inventary;
    private Command inventarySelec;
    private Command inventaryCancel;

    private String idPlayer;
    private String namePlayer;
    private JSONArray servers;
    private ServerClientCraftJ2ME serverMC;
    private Random random = new Random();
    public static Main instance;
    public void startApp(){
        instance = this;
        preservers = new List("Servidores", List.IMPLICIT);
        preServerSelectServ = new Command("Seleccionar Servidor", Command.OK, 1);
        preServerAddServ = new Command("Añadir Servidor", Command.SCREEN, 2);
        preServerDelServ = new Command("Eliminar Servidor", Command.SCREEN, 3);
        preServerQuit = new Command("Salir", Command.EXIT, 4);
        preservers.addCommand(preServerSelectServ);
        preservers.addCommand(preServerAddServ);
        preservers.addCommand(preServerDelServ);
        preservers.addCommand(preServerQuit);
        preservers.setCommandListener(this);
        if(getItem("servers") != null){
            try{
                servers = new JSONArray(getItem("servers"));
            }catch(Exception er){
                er.printStackTrace();
            }
            for(int i = 0; i < servers.length(); i++){
                JSONObject server = servers.getJSONObject(i);
                preservers.append(server.getString("name"), null);
            }
        }else{
            setItem("servers", "[]");
            try{
                servers = new JSONArray();
            }catch(Exception er){
                er.printStackTrace();
            }
        }
        mcVistaChat = new Command("Chat", Command.OK, 1);
        mcVistaPause = new Command("Pause", Command.BACK, 2);
        pause = new List("Pause", List.IMPLICIT);
        pauseSelec = new Command("Seleccionar", Command.OK, 1);
        pauseResume = new Command("Resumir", Command.BACK, 2);
        pause.addCommand(pauseSelec);
        pause.addCommand(pauseResume);
        pause.setCommandListener(this);
        pause.append("Config", null);
        pause.append("Lista de Jugadores", null);
        config = new List("Config", List.IMPLICIT);
        configSelec = new Command("Seleccionar", Command.OK, 1);
        configQuit = new Command("Volver", Command.BACK, 2);
        config.addCommand(configSelec);
        config.addCommand(configQuit);
        config.setCommandListener(this);
        config.append("Cambiar Skin(por URL)", null);
        if(getItem("name") == null){
            setItem("name", "Crafti");
        }
        namePlayer = getItem("name");
        config.append("Cambiar Nombre", null);
        anidirServer = new Form("Añadir Server");
        anidirServerAdd = new Command("Añadir", Command.OK, 1);
        anidirServerCancel = new Command("Cancelar", Command.BACK, 2);
        anidirServer.addCommand(anidirServerAdd);
        anidirServer.addCommand(anidirServerCancel);
        anidirServer.setCommandListener(this);
        anidirServerIP = new TextField("IP", "", 35, TextField.ANY);
        anidirServerName = new TextField("Nombre", "", 16, TextField.ANY);
        anidirServerPort = new TextField("Puerto", "", 5, TextField.NUMERIC);
        anidirServer.append(anidirServerName);
        anidirServer.append(anidirServerIP);
        anidirServer.append(anidirServerPort);
        cambiarNombre = new TextBox("Escribe tu Nombre", namePlayer, 16, TextField.ANY);
        cambiarNombreCancel = new Command("Cancelar", Command.BACK, 1);
        cambiarNombreOK = new Command("OK", Command.OK, 2);
        cambiarNombre.addCommand(cambiarNombreOK);
        cambiarNombre.addCommand(cambiarNombreCancel);
        cambiarNombre.setCommandListener(this);
        Display.getDisplay(this).setCurrent(preservers);
        if(getItem("present") == null){
            Image img = Image.createImage("/textures/creadoresPro.png");
            Alert present = new Alert("CraftJ2ME", "Bienvenido a CraftJ2ME!\nUn Juego para Jugar Servidores MC desde tu Celular Clasico!\nPara Empezar ya viste el README de nuestro repo de Github?\nTe recomiendo verlo para saber como hacer tu servidor compatible con CraftJ2ME!\nGracias por juegar nuestro juego!\n\nNo estamos afiliados a Monjag ni a Microsoft.\nEste Juego tiene licencia GPL GNU v3.\n\nCreadores Program © 2025\n\"La Revolución del Codigo\"", img, AlertType.INFO);
            present.setTimeout(Alert.FOREVER);
            present.setCommandListener(new CommandListener(){
                public void commandAction(Command c, Displayable d){
                    setItem("present", "true");
                    Display.getDisplay(Main.this).setCurrent(preservers);
                }
            });
            Display.getDisplay(this).setCurrent(present);
        }
    }
    public void commandAction(Command c, Displayable d) {}
    public void pauseApp() {
        if(serverMC != null){
            Display.getDisplay(this).setCurrent(pause);
        }
    }
    public String getIdPlayer(){
        return idPlayer;
    }
    public ServerClientCraftJ2ME getServerMC(){
        return serverMC;
    }
    public void noRespondingServer(){
        serverMC.stop();
        serverMC = null;
        chatMC = null;
        chatMCQuit = null;
        chatMCSendMsg = null;
        playersList = null;
        playersListQuit = null;
        sendMsgChat = null;
        sendMsgChatSend = null;
        sendMsgChatCancel = null;
        inventary = null;
        inventarySelec = null;
        inventaryCancel = null;
        mcVista = null;
        mcVistaChat = null;
        mcVistaPause = null;
        Alert disconec = new Alert("Desconectado", "Desconexión del Servidor!", null, AlertType.INFO);
        disconec.setTimeout(Alert.FOREVER);
        disconec.setCommandListener(new CommandListener(){
            public void commandAction(Command c, Displayable d){
                Display.getDisplay(Main.this).setCurrent(preservers);
            }
        });
        Display.getDisplay(this).setCurrent(disconec);
    }
    public void exitServer(String reason){
        serverMC.stop();
        serverMC = null;
        chatMC = null;
        chatMCQuit = null;
        chatMCSendMsg = null;
        playersList = null;
        playersListQuit = null;
        sendMsgChat = null;
        sendMsgChatSend = null;
        sendMsgChatCancel = null;
        inventary = null;
        inventarySelec = null;
        inventaryCancel = null;
        mcVista = null;
        mcVistaChat = null;
        mcVistaPause = null;
        Alert disconec = new Alert("Desconectado", reason, null, AlertType.INFO);
        disconec.setTimeout(Alert.FOREVER);
        disconec.setCommandListener(new CommandListener(){
            public void commandAction(Command c, Displayable d){
                Display.getDisplay(Main.this).setCurrent(preservers);
            }
        });
        Display.getDisplay(this).setCurrent(disconec);
    }
    public void sendChat(StringMCItem str){
        chatMC.append(str);
    }
    public void destroyApp(boolean unconditional) {
        if(serverMC != null){
            serverMC.sendDataPacket(new ExitDatapack(idPlayer));
            serverMC.stop();
            serverMC = null;
            chatMC = null;
            chatMCQuit = null;
            chatMCSendMsg = null;
            playersList = null;
            playersListQuit = null;
            sendMsgChat = null;
            sendMsgChatSend = null;
            sendMsgChatCancel = null;
            inventary = null;
            inventarySelec = null;
            inventaryCancel = null;
            mcVista = null;
            mcVistaChat = null;
            mcVistaPause = null;
        }
    }
    private void setItem(String idkey, String value){
        try {
            RecordStore rs = RecordStore.openRecordStore(idkey, true);
            RecordEnumeration re = rs.enumerateRecords(null, null, false);
            if (re.hasNextElement()) {
                int recordId = re.nextRecordId();
                rs.setRecord(recordId, value.getBytes(), 0, value.getBytes().length);
            } else {
                rs.addRecord(value.getBytes(), 0, value.getBytes().length);
            }
            rs.closeRecordStore();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private String getItem(String idkey){
        try{
            RecordStore rs = RecordStore.openRecordStore(idkey, false);
            RecordEnumeration re = rs.enumerateRecords(null, null, false);
            byte[] data = null;
            if (re.hasNextElement()) {
                int recordId = re.nextRecordId();
                data = rs.getRecord(recordId);
            }
            rs.closeRecordStore();
            return data != null ? new String(data) : null;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private void joinServer(){}
    private String generateUUID() {
        StringBuffer uuid = new StringBuffer();
        String template = "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx";
        for(int i = 0; i < template.length(); i++){
            char c = template.charAt(i);
            if(c == 'x' || c == 'y'){
                int r = random.nextInt(16);
                int v = (c == 'x') ? r : ((r & 0x3) | 0x8);
                uuid.append(Integer.toHexString(v));
            }else{
                uuid.append(c);
            }
        }
        return uuid.toString();
    }
}