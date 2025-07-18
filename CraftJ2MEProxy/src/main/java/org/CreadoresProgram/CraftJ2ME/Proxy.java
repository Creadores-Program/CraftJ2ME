package org.CreadoresProgram.CraftJ2ME;
import org.CreadoresProgram.CraftJ2ME.server.Server;
import org.CreadoresProgram.CraftJ2ME.renderVista.BlocksTexture;
import org.CreadoresProgram.CraftJ2ME.renderVista.ItemsTexture;
public class Proxy{
    public static String DATA_PATH = System.getProperty("user.dir") + "/";
    public static void main(String[] args){
        System.out.println("Starting CraftJ2ME Proxy Software");
        System.setProperty("java.compiler", "javac");
        System.getProperties().putIfAbsent("io.netty.allocator.type", "unpooled");
        System.out.println("CraftJ2ME Proxy is distributed under the GPL GNU v3 License");
        ItemsTexture.init();
        BlocksTexture.init();
        new Server(DATA_PATH);
    }
}