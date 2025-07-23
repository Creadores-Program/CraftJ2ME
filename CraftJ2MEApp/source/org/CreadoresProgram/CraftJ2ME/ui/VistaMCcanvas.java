package org.CreadoresProgram.CraftJ2ME.ui;
import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.GameCanvas;

import StackOverflow.Base64;

import org.CreadoresProgram.CraftJ2ME.Main;
import org.CreadoresProgram.CraftJ2ME.network.packets.MoveDatapack;
import org.CreadoresProgram.CraftJ2ME.network.packets.InventaryRequest;
import org.CreadoresProgram.CraftJ2ME.network.packets.RequestStatsDatapack;
import org.CreadoresProgram.CraftJ2ME.network.packets.InteractDatapack;
import org.CreadoresProgram.CraftJ2ME.network.packets.BreakBlockDatapack;

import java.util.Vector;
public class VistaMCcanvas extends GameCanvas implements Runnable{
    private Vector vistaMCFPS;
    private boolean running = true;
    private Thread gameThread;
    private int x = 0;
    private int y = 0;
    private int z = 0;
    private int yaw = 0;
    private int pitch = 0;
    public VistaMCcanvas(){
        super(true);
        vistaMCFPS = new Vector();
    }
    protected void keyPressed(int keyCode){
        if(!running || Main.instance.getServerMC() == null || Main.instance.getServerMC().queueLoop == null){
            return;
        }
        int action = getGameAction(keyCode);
        switch(action){
            //cuerpo
            case Canvas.UP:
                z = 1;
                updateMoveData();
                break;
            case Canvas.DOWN:
                z = -1;
                updateMoveData();
                break;
            case Canvas.LEFT:
                x = -1;
                updateMoveData();
                break;
            case Canvas.RIGHT:
                x = 1;
                updateMoveData();
                break;
            case Canvas.FIRE://Shift
                y = -1;
                updateMoveData();
                break;
            default:
                //cara
                switch(keyCode){
                    case KEY_NUM2://up
                        pitch = -1;
                        updateMoveData();
                        break;
                    case KEY_NUM8://down
                        pitch = 1;
                        updateMoveData();
                        break;
                    case KEY_NUM4://left
                        yaw = -1;
                        updateMoveData();
                        break;
                    case KEY_NUM6://right
                        yaw = 1;
                        updateMoveData();
                        break;
                    case KEY_NUM5://salto
                        y = 1;
                        updateMoveData();
                        break;
                    case KEY_NUM0://inventario datapack
                        InventaryRequest datapack = new InventaryRequest(Main.instance.getIdPlayer());
                        datapack.pageRequest = 0;
                        Main.instance.getServerMC().queueLoop.datapacks.addElement(datapack);
                        break;
                    case KEY_NUM7://romper datapack
                        BreakBlockDatapack breakBlockDatapack = new BreakBlockDatapack(Main.instance.getIdPlayer());
                        breakBlockDatapack.idInt = 1;
                        Main.instance.getServerMC().queueLoop.datapacks.addElement(breakBlockDatapack);
                        break;
                    case KEY_POUND://stats datapack
                        Main.instance.getServerMC().queueLoop.datapacks.addElement(new RequestStatsDatapack(Main.instance.getIdPlayer()));
                        break;
                    case KEY_STAR://interactuar datapack
                        Main.instance.getServerMC().queueLoop.datapacks.addElement(new InteractDatapack(Main.instance.getIdPlayer()));
                        break;
                }
        }
    }
    protected void keyReleased(int keyCode){
        if(!running || Main.instance.getServerMC() == null || Main.instance.getServerMC().queueLoop == null){
            return;
        }
        int action = getGameAction(keyCode);
        switch(action){
            case Canvas.UP:
            case Canvas.DOWN:
                z = 0;
                updateMoveData();
                break;
            case Canvas.LEFT:
            case Canvas.RIGHT:
                x = 0;
                updateMoveData();
                break;
            case Canvas.FIRE:
                y = 0;
                updateMoveData();
                break;
            default:
                switch(keyCode){
                    case KEY_NUM2:
                    case KEY_NUM8:
                        pitch = 0;
                        updateMoveData();
                        break;
                    case KEY_NUM4:
                    case KEY_NUM6:
                        yaw = 0;
                        updateMoveData();
                        break;
                    case KEY_NUM5:
                        y = 0;
                        updateMoveData();
                        break;
                    case KEY_NUM0:
                        Main.instance.setInventary();
                        break;
                    case KEY_NUM7:
                        BreakBlockDatapack breakBlockDatapack = new BreakBlockDatapack(Main.instance.getIdPlayer());
                        breakBlockDatapack.idInt = 0;
                        Main.instance.getServerMC().queueLoop.datapacks.addElement(breakBlockDatapack);
                        break;
                    case KEY_POUND:
                        Main.instance.showStats();
                        break;
                }
        }
    }
    public void start(){
       gameThread = new Thread(this);
       gameThread.start();
    }
    public void run(){
        Graphics g = getGraphics();
        while(running){
            if(vistaMCFPS.size() == 0){
                try{
                    Thread.sleep(50);
                }catch(Exception e){
                    e.printStackTrace();
                }
                continue;
            }
            g.setColor(0x000000);
            g.fillRect(0, 0, getWidth(), getHeight());
            int w = getWidth();
            int h = getHeight();
            int imgW = ((Image) vistaMCFPS.elementAt(0)).getWidth();
            int imgH = ((Image) vistaMCFPS.elementAt(0)).getHeight();
            int x = 0;
            int y = 0;
            if(w != imgW){
                x = (w - imgW) / 2;
            }
            if(h != imgH){
                y = (h - imgH) / 2;
            }
            g.drawImage((Image) vistaMCFPS.elementAt(0), x, y, Graphics.TOP | Graphics.LEFT);
            flushGraphics();
            vistaMCFPS.removeElementAt(0);
            try{
                Thread.sleep(50);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    public void stop(){
        running = false;
        if(gameThread != null){
            try{
                gameThread.join();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        gameThread = null;
    }
    public void updateVistaMC(String baseImg){
        byte[] imageBytes = Base64.decode(baseImg);
        this.vistaMCFPS.addElement(Image.createImage(imageBytes, 0, imageBytes.length));
    }
    public void updateVistaMC(Image img){
        this.vistaMCFPS.addElement(img);
    }
    public void updateVistaMC(byte[] imgBytes){
        try{
            this.vistaMCFPS.addElement(Image.createImage(imgBytes, 0, imgBytes.length));
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    private void updateMoveData(){
        MoveDatapack datapack = new MoveDatapack(Main.instance.getIdPlayer());
        datapack.x = x;
        datapack.y = y;
        datapack.z = z;
        datapack.yaw = yaw;
        datapack.pitch = pitch;
        Main.instance.getServerMC().queueLoop.datapacks.addElement(datapack);
    }
}
