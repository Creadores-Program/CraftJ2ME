package org.CreadoresProgram.CraftJ2ME.ui;
import javax.microedition.lcdui.*;

import StackOverflow.Base64;

import org.CreadoresProgram.CraftJ2ME.Main;
import org.CreadoresProgram.CraftJ2ME.network.packets.MoveDatapack;
import org.CreadoresProgram.CraftJ2ME.network.packets.InventaryRequest;
public class VistaMCcanvas extends Canvas{
    private Image vistaMC;
    private int x = 0;
    private int y = 0;
    private int z = 0;
    private int yaw = 0;
    private int pitch = 0;
    protected void keyPressed(int keyCode){
        if(Main.instance.getServerMC() == null || Main.instance.getServerMC().queueLoop == null){
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
                }
        }
    }
    protected void keyReleased(int keyCode){
        if(Main.instance.getServerMC() == null || Main.instance.getServerMC().queueLoop == null){
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
                }
        }
    }
    protected void paint(Graphics g){
        if(vistaMC == null) return;
        int w = getWidth();
        int h = getHeight();
        int imgW = vistaMC.getWidth();
        int imgH = vistaMC.getHeight();
        int x = (w - imgW) / 2;
        int y = (h - imgH) / 2;
        g.drawImage(vistaMC, x, y, Graphics.TOP | Graphics.LEFT);
    }
    public void updateVistaMC(String baseImg){
        byte[] imageBytes = Base64.decode(baseImg);
        this.vistaMC = Image.createImage(imageBytes, 0, imageBytes.length);
        repaint();
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
