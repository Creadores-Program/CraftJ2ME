package org.CreadoresProgram.CraftJ2ME;
import javax.microedition.midlet.MIDlet;
import javax.microedition.lcdui.*;
import javax.microedition.rms.*;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import java.util.Random;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;

import StackOverflow.Base64;

import org.json.me.JSONObject;
import org.json.me.JSONArray;

import org.CreadoresProgram.CraftJ2ME.ui.*;
import org.CreadoresProgram.CraftJ2ME.network.packets.ExitDatapack;
import org.CreadoresProgram.CraftJ2ME.network.packets.ChatDatapack;
import org.CreadoresProgram.CraftJ2ME.network.packets.RequestPlayersListDatapack;
import org.CreadoresProgram.CraftJ2ME.network.packets.UpdateSkinDatapack;
import org.CreadoresProgram.CraftJ2ME.network.ServerClientCraftJ2ME;
public class Main extends MIDlet implements CommandListener{
    private static final int LIMIT_MAX_BYTES_IMG = 100 * 1024;
    private List preservers;
    private Command preServerAddServ;
    private Command preServerDelServ;
    private Command preServerQuit;
    private Command preServerConfig;
    
    private VistaMCcanvas mcVista;
    private Command mcVistaChat;
    private Command mcVistaPause;

    private List pause;
    private Command pauseResume;

    private List config;
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

    private TextBox cambiarSkin;
    private Command cambiarSkinOK;
    private Command cambiarSkinCancel;

    private List playersList;
    private Command playersListQuit;

    private Form chatMC;
    private Command chatMCQuit;
    private Command chatMCSendMsg;

    private TextBox sendMsgChat;
    private Command sendMsgChatSend;
    private Command sendMsgChatCancel;

    private List inventary;
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
        preServerAddServ = new Command("Añadir Servidor", Command.SCREEN, 1);
        preServerDelServ = new Command("Eliminar Servidor", Command.ITEM, 2);
        preServerQuit = new Command("Salir", Command.EXIT, 3);
        preServerConfig = new Command("Config", Command.SCREEN, 4);
        preservers.addCommand(preServerAddServ);
        preservers.addCommand(preServerDelServ);
        preservers.addCommand(preServerQuit);
        preservers.addCommand(preServerConfig);
        preservers.setCommandListener(this);
        if(getItem("servers") != null){
            try{
                servers = new JSONArray(getItem("servers"));
            }catch(Exception er){
                er.printStackTrace();
            }
            new Thread(){
                public void run(){
                    for(int i = 0; i < servers.length(); i++){
                        try{
                            JSONObject server = servers.getJSONObject(i);
                            Image imgServ = getImageServer(server.getString("ip"), server.getInt("port"));
                            preservers.append(server.getString("name"), imgServ);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
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
        pauseResume = new Command("Resumir", Command.BACK, 1);
        pause.addCommand(pauseResume);
        pause.setCommandListener(this);
        pause.append("Config", null);
        pause.append("Lista de Jugadores", null);
        pause.append("Salir del Servidor", null);
        config = new List("Config", List.IMPLICIT);
        configQuit = new Command("Atras", Command.BACK, 1);
        config.addCommand(configQuit);
        config.setCommandListener(this);
        config.append("Cambiar Skin(por URL)", null);
        if(getItem("name") == null){
            setItem("name", "Crafti");
        }
        namePlayer = getItem("name");
        if(getItem("playerId") == null){
            setItem("playerId", generateUUID());
        }
        idPlayer = getItem("playerId");
        config.append("Cambiar Nombre", null);
        anidirServer = new Form("Añadir Server");
        anidirServerAdd = new Command("Añadir", Command.OK, 1);
        anidirServerCancel = new Command("Cancelar", Command.CANCEL, 2);
        anidirServer.addCommand(anidirServerAdd);
        anidirServer.addCommand(anidirServerCancel);
        anidirServer.setCommandListener(this);
        anidirServerIP = new TextField("IP", "", 35, TextField.URL);
        anidirServerName = new TextField("Nombre", "", 16, TextField.ANY);
        anidirServerPort = new TextField("Puerto", "", 5, TextField.NUMERIC);
        anidirServer.append(anidirServerName);
        anidirServer.append(anidirServerIP);
        anidirServer.append(anidirServerPort);
        cambiarNombre = new TextBox("Escribe tu Nombre", namePlayer, 16, TextField.ANY);
        cambiarNombreCancel = new Command("Cancelar", Command.CANCEL, 1);
        cambiarNombreOK = new Command("OK", Command.OK, 2);
        cambiarNombre.addCommand(cambiarNombreOK);
        cambiarNombre.addCommand(cambiarNombreCancel);
        cambiarNombre.setCommandListener(this);
        cambiarSkin = new TextBox("Escribe la URL de tu skin", getSkin(), 10000, TextField.URL);
        cambiarSkinCancel = new Command("Cancelar", Command.CANCEL, 1);
        cambiarSkinOK = new Command("OK", Command.OK, 2);
        cambiarSkin.addCommand(cambiarSkinOK);
        cambiarSkin.addCommand(cambiarSkinCancel);
        cambiarSkin.setCommandListener(this);
        Display.getDisplay(this).setCurrent(preservers);
        if(getItem("present") == null){
            Image img;
            try{
                img = Image.createImage("/textures/creadoresPro.png");
            }catch(Exception e){
                e.printStackTrace();
                img = null;
            }
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
    public void commandAction(Command c, Displayable d) {
        if(c == List.SELECT_COMMAND){
            if(d == preservers){
                int indexSelec = preservers.getSelectedIndex();
                try{
                    JSONObject server = servers.getJSONObject(indexSelec);
                    joinServer(server.getString("ip"), server.getInt("port"));
                }catch(Exception e){
                    e.printStackTrace();
                }
            }else if(d == config){
                int indexSelec = config.getSelectedIndex();
                switch(indexSelec){
                    case 0://skin
                        Display.getDisplay(this).setCurrent(cambiarSkin);
                        break;
                    case 1://cambiar Nombre
                        Display.getDisplay(this).setCurrent(cambiarNombre);
                        break;
                }
            }
        }else if(c == preServerAddServ){
            Display.getDisplay(this).setCurrent(anidirServer);
        }else if(c == preServerDelServ){
            JSONArray nuevoArr;
            try{
                nuevoArr = new JSONArray();
            }catch(Exception err){
                err.printStackTrace();
                return;
            }
            int indexList = preservers.getSelectedIndex();
            if(indexList == -1){
                Alert noElem = new Alert("Error", "No seleccionaste ningun servidor!", null, AlertType.ERROR);
                noElem.setTimeout(Alert.FOREVER);
                noElem.setCommandListener(new CommandListener(){
                    public void commandAction(Command c, Displayable d){
                        Display.getDisplay(Main.this).setCurrent(preservers);
                    }
                });
                return;
            }
            for(int i = 0; i < servers.length(); i++){
                if(i == indexList){
                    continue;
                }
                try{
                    nuevoArr.put(servers.get(i));
                }catch(Exception er){
                    er.printStackTrace();
                    continue;
                }
            }
            servers = nuevoArr;
            setItem("servers", servers.toString());
        }else if(c == configQuit){
            if(mcVista != null){
                Display.getDisplay(this).setCurrent(pause);
            }else{
                Display.getDisplay(this).setCurrent(preservers);
            }
        }else if(c == preServerQuit){
            notifyDestroyed();
        }else if(c == preServerConfig){
            Display.getDisplay(this).setCurrent(config);
        }else if(c == anidirServerCancel){
            Display.getDisplay(this).setCurrent(preservers);
        }else if(c == anidirServerAdd){
            final String ip = anidirServerIP.getString();
            String port = anidirServerPort.getString();
            final String name = anidirServerName.getString();
            if(ip.length() < 3){
                Alert adNoLeng = new Alert("Añadir Servidor", "Error!\nIP invalida!", null, AlertType.ERROR);
                adNoLeng.setTimeout(Alert.FOREVER);
                adNoLeng.setCommandListener(new CommandListener(){
                    public void commandAction(Command c, Displayable d){
                        Display.getDisplay(Main.this).setCurrent(preservers);
                    }
                });
                Display.getDisplay(this).setCurrent(adNoLeng);
                return;
            }
            if(name.length() < 3){
                Alert adNoLeng = new Alert("Añadir Servidor", "Error!\nel Nombre es muy corto!", null, AlertType.ERROR);
                adNoLeng.setTimeout(Alert.FOREVER);
                adNoLeng.setCommandListener(new CommandListener(){
                    public void commandAction(Command c, Displayable d){
                        Display.getDisplay(Main.this).setCurrent(preservers);
                    }
                });
                Display.getDisplay(this).setCurrent(adNoLeng);
                return;
            }
            if(port.length() < 1){
                Alert adNoLeng = new Alert("Añadir Servidor", "Error!\nPuerto Invalido!", null, AlertType.ERROR);
                adNoLeng.setTimeout(Alert.FOREVER);
                adNoLeng.setCommandListener(new CommandListener(){
                    public void commandAction(Command c, Displayable d){
                        Display.getDisplay(Main.this).setCurrent(preservers);
                    }
                });
                Display.getDisplay(this).setCurrent(adNoLeng);
                return;
            }
            int finalPort = 0;
            try{
                finalPort = Integer.parseInt(port);
                if(finalPort < 1){
                    throw new RuntimeException("Puerto invalido");
                }
            }catch(Exception ex){
                Alert adNoLeng = new Alert("Añadir Servidor", "Error!\nPuerto Invalido!", null, AlertType.ERROR);
                adNoLeng.setTimeout(Alert.FOREVER);
                adNoLeng.setCommandListener(new CommandListener(){
                    public void commandAction(Command c, Displayable d){
                        Display.getDisplay(Main.this).setCurrent(preservers);
                    }
                });
                Display.getDisplay(this).setCurrent(adNoLeng);
                return;
            }
            for(int i = 0; i < servers.length(); i++){
                try{
                    JSONObject server = servers.getJSONObject(i);
                    if(server.getString("ip").equals(ip) && server.getInt("port") == finalPort){
                        Alert adNoLeng = new Alert("Añadir Servidor", "Error!\nEl servidor ya existe!", null, AlertType.ERROR);
                        adNoLeng.setTimeout(Alert.FOREVER);
                        adNoLeng.setCommandListener(new CommandListener(){
                            public void commandAction(Command c, Displayable d){
                                Display.getDisplay(Main.this).setCurrent(preservers);
                            }
                        });
                        Display.getDisplay(this).setCurrent(adNoLeng);
                        return;
                    }
                }catch(Exception e){
                    e.printStackTrace();
                    Alert adNoLeng = new Alert("Añadir Servidor", "Error!\nHubo un error al verificar el servidor!", null, AlertType.ERROR);
                    adNoLeng.setTimeout(Alert.FOREVER);
                    adNoLeng.setCommandListener(new CommandListener(){
                        public void commandAction(Command c, Displayable d){
                            Display.getDisplay(Main.this).setCurrent(preservers);
                        }
                    });
                    Display.getDisplay(this).setCurrent(adNoLeng);
                }
            }
            try{
                JSONObject serc = new JSONObject();
                serc.put("ip", ip);
                serc.put("port", finalPort);
                serc.put("name", name);
                servers.put(serc);
                setItem("servers", servers.toString());
                final int ffinalPort = finalPort;
                Thread loadCod = new Thread(){
                    public void run(){
                        Image imgServ = getImageServer(ip, ffinalPort);
                        preservers.append(name, imgServ);
                        Display.getDisplay(Main.this).setCurrent(preservers);
                    }
                };
                Form loadi = new Form("Añadiendo Servidor...");
                loadi.append("Añadiendo el servidor " + name + " (" + ip + ":" + finalPort + ")...");
                Display.getDisplay(this).setCurrent(loadi);
                loadCod.start();
            }catch(Exception exc){
                Alert errordeck = new Alert("Añadir Servidor", "Error!\nHubo un error desconocido al añadir el servidor!", null, AlertType.ERROR);
                errordeck.setTimeout(Alert.FOREVER);
                errordeck.setCommandListener(new CommandListener(){
                    public void commandAction(Command c, Displayable d){
                        Display.getDisplay(Main.this).setCurrent(preservers);
                    }
                });
                Display.getDisplay(this).setCurrent(errordeck);
                exc.printStackTrace();
            }
        }else if(c == cambiarNombreOK){
            String name = cambiarNombre.getString();
            if(name == null || name.length() < 3){
                Alert adNoLeng = new Alert("Cambiar Nombre", "Error!\nTu nombre no puede tener menos de 3 letras!", null, AlertType.ERROR);
                adNoLeng.setTimeout(Alert.FOREVER);
                adNoLeng.setCommandListener(new CommandListener(){
                    public void commandAction(Command c, Displayable d){
                        Display.getDisplay(Main.this).setCurrent(config);
                    }
                });
                Display.getDisplay(this).setCurrent(adNoLeng);
                return;
            }
            setItem("name", name);
            namePlayer = name;
            Alert susses = new Alert("Cambiar Nombre", "Tu nombre fue cambiado con exito!", null, AlertType.CONFIRMATION);
            susses.setTimeout(Alert.FOREVER);
            susses.setCommandListener(new CommandListener(){
                public void commandAction(Command c, Displayable d){
                    Display.getDisplay(Main.this).setCurrent(config);
                }
            });
            Display.getDisplay(this).setCurrent(susses);
        }else if(c == cambiarNombreCancel){
            Display.getDisplay(this).setCurrent(config);
        }else if(c == cambiarSkinOK){
            String urlSkin = cambiarSkin.getString();
            if(urlSkin.length() < 10){
                Alert adNoLeng = new Alert("Cambiar Skin", "Error!\nUrl Invalida!", null, AlertType.ERROR);
                adNoLeng.setTimeout(Alert.FOREVER);
                adNoLeng.setCommandListener(new CommandListener(){
                    public void commandAction(Command c, Displayable d){
                        Display.getDisplay(Main.this).setCurrent(config);
                    }
                });
                Display.getDisplay(this).setCurrent(adNoLeng);
                return;
            }
            setItem("skinPlayer", urlSkin);
            Alert succes = new Alert("Cambiar Skin", "Tu skin fue cambiado con exito!", null, AlertType.CONFIRMATION);
            succes.setTimeout(Alert.FOREVER);
            succes.setCommandListener(new CommandListener(){
                public void commandAction(Command c, Displayable d){
                    Display.getDisplay(Main.this).setCurrent(config);
                }
            });
            if(serverMC != null){
                UpdateSkinDatapack pkSk = new UpdateSkinDatapack(idPlayer);
                pkSk.skin = urlSkin;
                serverMC.queueLoop.datapacks.addElement(pkSk);
            }
            Display.getDisplay(this).setCurrent(succes);
        }else if(c == cambiarSkinCancel){
            Display.getDisplay(this).setCurrent(config);
        }
        if(serverMC == null || mcVista == null){
            return;
        }
        if(c == mcVistaChat){
            Display.getDisplay(this).setCurrent(chatMC);
        }else if(c == mcVistaPause){
            Display.getDisplay(this).setCurrent(pause);
        }else if(c == pauseResume){
            Display.getDisplay(this).setCurrent(mcVista);
        }else if(c == playersListQuit){
            Display.getDisplay(this).setCurrent(pause);
        }else if(c == chatMCQuit){
            Display.getDisplay(this).setCurrent(mcVista);
        }else if(c == chatMCSendMsg){
            Display.getDisplay(this).setCurrent(sendMsgChat);
        }else if(c == sendMsgChatSend){
            String msg = sendMsgChat.getString();
            if(msg == null || msg.length() == 0){
                Alert adNoLeng = new Alert("Enviar Chat", "Error!\nEl mensaje esta vacio!", null, AlertType.ERROR);
                adNoLeng.setTimeout(Alert.FOREVER);
                adNoLeng.setCommandListener(new CommandListener(){
                    public void commandAction(Command c, Displayable d){
                        Display.getDisplay(Main.this).setCurrent(chatMC);
                    }
                });
                Display.getDisplay(this).setCurrent(adNoLeng);
                return;
            }
            ChatDatapack datapack = new ChatDatapack(idPlayer);
            datapack.message = msg;
            serverMC.queueLoop.datapacks.addElement(datapack);
            sendMsgChat.setString("");
            Display.getDisplay(this).setCurrent(chatMC);
        }else if(c == sendMsgChatCancel){
            Display.getDisplay(this).setCurrent(chatMC);
        }else if(c == List.SELECT_COMMAND){
            if(d == pause){
                int indexList = pause.getSelectedIndex();
                switch(indexList){
                    case 0://config
                        Display.getDisplay(this).setCurrent(config);
                        break;
                    case 1://playersList
                        serverMC.queueLoop.datapacks.addElement(new RequestPlayersListDatapack(idPlayer));
                        Display.getDisplay(this).setCurrent(playersList);
                        break;
                    case 2://salir
                        Display.getDisplay(this).setCurrent(preservers);
                        new Thread(){
                            public void run(){
                                serverMC.sendDataPacket(new ExitDatapack(idPlayer));
                                serverMC.stopServ();
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
                                inventaryCancel = null;
                                mcVista = null;
                                mcVistaChat = null;
                                mcVistaPause = null;
                            }
                        }.start();
                        break;
                }
            }else if(d == inventary){
                //code...
            }
        }else if(c == inventaryCancel){
            Display.getDisplay(this).setCurrent(mcVista);
        }
    }
    public void pauseApp() {
        if(serverMC != null){
            Display.getDisplay(this).setCurrent(pause);
        }
    }
    public String getIdPlayer(){
        return idPlayer;
    }
    public String getPlayerName(){
        return namePlayer;
    }
    public String getSkin(){
        if(getItem("skinPlayer") == null){
            return "";
        }else{
            return getItem("skinPlayer");
        }
    }
    public List getPlayersList(){
        return playersList;
    }
    public VistaMCcanvas getVistaCanvasMC(){
        return mcVista;
    }
    public ServerClientCraftJ2ME getServerMC(){
        return serverMC;
    }
    public void noRespondingServer(){
        serverMC.stopServ();
        Alert disconec = new Alert("Desconectado", "Desconexión del Servidor!", null, AlertType.INFO);
        disconec.setTimeout(Alert.FOREVER);
        disconec.setCommandListener(new CommandListener(){
            public void commandAction(Command c, Displayable d){
                Display.getDisplay(Main.this).setCurrent(preservers);
            }
        });
        Display.getDisplay(this).setCurrent(disconec);
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
        inventaryCancel = null;
        mcVista = null;
        mcVistaChat = null;
        mcVistaPause = null;
    }
    public void exitServer(String reason){
        Alert disconec = new Alert("Desconectado", reason, null, AlertType.INFO);
        disconec.setTimeout(Alert.FOREVER);
        disconec.setCommandListener(new CommandListener(){
            public void commandAction(Command c, Displayable d){
                Display.getDisplay(Main.this).setCurrent(preservers);
            }
        });
        Display.getDisplay(this).setCurrent(disconec);
        serverMC.stopServ();
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
        inventaryCancel = null;
        mcVista = null;
        mcVistaChat = null;
        mcVistaPause = null;
    }
    public void sendChat(StringMCItem str){
        chatMC.append(str);
    }
    public void setInventary(){
        if(inventary == null){
            inventary = new List("Inventario", List.IMPLICIT);
            inventaryCancel = new Command("Atras", Command.BACK, 1);
            inventary.setCommandListener(this);
        }
        inventary.append("Mostrar más", null);
        inventary.addCommand(inventaryCancel);
        Display.getDisplay(this).setCurrent(inventary);
    }
    public void destroyApp(boolean unconditional) {
        if(serverMC != null){
            serverMC.sendDataPacket(new ExitDatapack(idPlayer));
            serverMC.stopServ();
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
    private void joinServer(String domain, int port){
        mcVista = new VistaMCcanvas();
        mcVista.addCommand(mcVistaPause);
        mcVista.addCommand(mcVistaChat);
        mcVista.setCommandListener(this);
        Display.getDisplay(this).setCurrent(mcVista);
        mcVista.start();
        try{
            mcVista.updateVistaMC(Image.createImage("/textures/loading.png"));
        }catch(Exception er){
            er.printStackTrace();
        }
        chatMC = new Form("Chat");
        chatMCQuit = new Command("Salir", Command.BACK, 1);
        chatMCSendMsg = new Command("Enviar Mensaje", Command.OK, 2);
        chatMC.addCommand(chatMCQuit);
        chatMC.addCommand(chatMCSendMsg);
        chatMC.setCommandListener(this);
        sendMsgChat = new TextBox("Enviar Chat Escribe tu mensaje:", "", 256, TextField.ANY);
        sendMsgChatSend = new Command("Enviar", Command.OK, 2);
        sendMsgChatCancel = new Command("Cancelar", Command.CANCEL, 1);
        sendMsgChat.addCommand(sendMsgChatCancel);
        sendMsgChat.addCommand(sendMsgChatSend);
        sendMsgChat.setCommandListener(this);
        playersList = new List("Jugadores", List.IMPLICIT);
        playersListQuit = new Command("Salir", Command.BACK, 1);
        playersList.addCommand(playersListQuit);
        playersList.setCommandListener(this);
        serverMC = new ServerClientCraftJ2ME(domain, port);
        serverMC.start();
    }
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
    private Image getImageServer(String domain, int port){
        HttpConnection conexion = null;
        InputStream is = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Image imagen = null;
        try{
            conexion = (HttpConnection) Connector.open("http://"+domain+":"+port+"/favicon.ico");
            int codigoResp = conexion.getResponseCode();
            if(codigoResp != HttpConnection.HTTP_OK){
                throw new Exception("Error en la solicitud del Icono");
            }
            long longi = conexion.getLength();
            if(longi != -1 && longi > LIMIT_MAX_BYTES_IMG){
                throw new Exception("El icono supera el limite permitido!");
            }
            is = conexion.openInputStream();
            int ch;
            while ((ch = is.read()) != -1){
                baos.write(ch);
            }
            byte[] dataImg = baos.toByteArray();
            if(dataImg.length > 0){
                imagen = Image.createImage(dataImg, 0, dataImg.length);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            try{
                if(is != null){
                    is.close();
                }
                if(baos != null){
                    baos.close();
                }
                if(conexion != null){
                    conexion.close();
                }
            }catch(Exception er){
                er.printStackTrace();
            }
        }
        return imagen;
    }
}
