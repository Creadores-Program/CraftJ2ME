package org.CreadoresProgram.CraftJ2ME.ui;
import javax.microedition.lcdui.*;

import org.CreadoresProgram.CraftJ2ME.utils.StringUtils;
import org.CreadoresProgram.CraftJ2ME.utils.TextFormatMC;
public class StringMCItem extends CustomItem{
    private String content;
    private static final Font font = Font.getDefaultFont();
    public StringMCItem(String content, String label){
        super(label);
        this.content = content;
    }
    protected int getMinContentWidth(){
        return font.stringWidth(content);
    }
    protected int getMinContentHeight(){
        return font.getHeight();
    }
    protected int getPrefContentWidth(int height){
        return font.stringWidth(content);
    }
    protected int getPrefContentHeight(int width){
        return font.getHeight();
    }
    protected void paint(Graphics g, int w, int h){
        g.setFont(font);
        String[] coloredMC = StringUtils.split(this.content, "§");
        int x = 0;
        if(coloredMC.length == 1){
            g.setColor(TextFormatMC.WHITE);
            g.drawString(content, x, 0, Graphics.TOP | Graphics.LEFT);
            return;
        }
        for(int i = 0; i < coloredMC.length; i++){
            if(coloredMC[i].length() == 0){
                continue;
            }
            String[] codexColor = TextFormatMC.getLColorMC(coloredMC[i]);
            g.setColor(TextFormatMC.getColorMC(codexColor[0]));
            g.drawString(codexColor[1], x, 0, Graphics.TOP | Graphics.LEFT);
            x += font.stringWidth(codexColor[1]);
        }
    }
}