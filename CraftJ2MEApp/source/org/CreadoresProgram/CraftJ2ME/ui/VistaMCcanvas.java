package org.CreadoresProgram.CraftJ2ME.ui;
import javax.microedition.lcdui.*;
import StackOverflow.Base64;
public class VistaMCcanvas extends Canvas{
    private Image vistaMC;
    public int x = 0;
    public int y = 0;
    public int z = 0;
    public int yaw = 0;
    public int pitch = 0;
    protected void keyPressed(int keyCode){
        int action = getGameAction(keyCode);
        switch(action){
            //cuerpo
            case Canvas.UP:
                break;
            case Canvas.DOWN:
                break;
            case Canvas.LEFT:
                break;
            case Canvas.RIGHT:
                break;
            case Canvas.FIRE://Shift
                break;
            default:
                //cara
                switch(keyCode){
                    case KEY_NUM2://up
                        break;
                    case KEY_NUM8://down
                        break;
                    case KEY_NUM4://left
                        break;
                    case KEY_NUM6://right
                        break;
                    case KEY_NUM5://salto
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
}
