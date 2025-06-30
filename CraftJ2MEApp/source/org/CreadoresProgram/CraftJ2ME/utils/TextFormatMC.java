package org.CreadoresProgram.CraftJ2ME.utils;
public class TextFormatMC{
    public static final int BLACK = RGBtoHex(0, 0, 0);
    public static final int DARK_BLUE = RGBtoHex(0, 0, 170);
    public static final int DARK_GREEN = RGBtoHex(0, 170, 0);
    public static final int DARK_AQUA = RGBtoHex(0, 170, 170);
    public static final int DARK_RED = RGBtoHex(170, 0, 0);
    public static final int DARK_PURPLE = RGBtoHex(170, 0, 170);
    public static final int GOLD = RGBtoHex(255, 170, 0);
    public static final int GRAY = RGBtoHex(170, 170, 170);
    public static final int DARK_GRAY = RGBtoHex(85, 85, 85);
    public static final int BLUE = RGBtoHex(85, 85, 255);
    public static final int GREEN = RGBtoHex(85, 255, 85);
    public static final int AQUA = RGBtoHex(85, 255, 255);
    public static final int RED = RGBtoHex(255, 85, 85);
    public static final int LIGHT_PURPLE = RGBtoHex(255, 85, 255);
    public static final int YELLOW = RGBtoHex(255, 255, 85);
    public static final int WHITE = RGBtoHex(255, 255, 255);
    public static final int MINECOIN_GOLD = RGBtoHex(221, 214, 5);
    public static final int RESET = WHITE;
    public static final int MATERIAL_QUARTZ = RGBtoHex(227, 212, 209);
    public static final int MATERIAL_IRON = RGBtoHex(206, 202, 202);
    public static final int MATERIAL_NETHERITE = RGBtoHex(68, 58, 59);
    public static final int MATERIAL_REDSTONE = RGBtoHex(151, 22, 7);
    public static final int MATERIAL_COPPER = RGBtoHex(180, 104, 77);
    public static final int MATERIAL_GOLD = RGBtoHex(222, 177, 45);
    public static final int MATERIAL_EMERALD = RGBtoHex(17, 160, 54);
    public static final int MATERIAL_DIAMOND = RGBtoHex(44, 186, 168);
    public static final int MATERIAL_LAPIS = RGBtoHex(33, 73, 123);
    public static final int MATERIAL_AMETHYST = RGBtoHex(154, 92, 168);
    public static final int MATERIAL_RESIN = RGBtoHex(234, 113, 19);
    private static final int[] colorsRGB = {
        BLACK,
        DARK_BLUE,
        DARK_GREEN,
        DARK_AQUA,
        DARK_RED,
        DARK_PURPLE,
        GOLD,
        GRAY,
        DARK_GRAY,
        BLUE,
        GREEN,
        AQUA,
        RED,
        LIGHT_PURPLE,
        YELLOW,
        WHITE,
        MINECOIN_GOLD,
        RESET,
        MATERIAL_QUARTZ,
        MATERIAL_IRON,
        MATERIAL_NETHERITE,
        MATERIAL_REDSTONE,
        MATERIAL_COPPER,
        MATERIAL_GOLD,
        MATERIAL_EMERALD,
        MATERIAL_DIAMOND,
        MATERIAL_LAPIS,
        MATERIAL_AMETHYST,
        MATERIAL_RESIN
    };
    private static final String[] colorsLetter = {
        "0",
        "1",
        "2",
        "3",
        "4",
        "5",
        "6",
        "7",
        "8",
        "9",
        "a",
        "b",
        "c",
        "d",
        "e",
        "f",
        "g",
        "r",
        "h",
        "i",
        "j",
        "m",
        "n",
        "p",
        "q",
        "s",
        "t",
        "u",
        "v"
    }
    private static int RGBtoHex(int r, int g, int b){
        return (r << 16) | (g << 8) | b;
    }
    public static String[] getLColorMC(String mensaje){
        char colorL = mensaje.charAt(0);
        return new String[]{
            String.valueOf(colorL),
            mensaje.substring(1);
        };
    }
    public static int getColorMC(String colorL){
        for(int i = 0; i < colorsLetter.length; i++){
            if(colorsLetter[i].equals(colorL)){
                return colorsRGB[i];
            }
        }
        return WHITE;
    }
}