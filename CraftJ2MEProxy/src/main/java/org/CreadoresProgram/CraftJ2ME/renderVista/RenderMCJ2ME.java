package org.CreadoresProgram.CraftJ2ME.renderVista;
import com.microsoft.playwright.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;

import org.CreadoresProgram.CraftJ2ME.player.Player;
import org.CreadoresProgram.CraftJ2ME.Proxy;

import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.math.vector.Vector3f;
public class RenderMCJ2ME implements AutoCloseable{
    private static final Browser browser;
    private static final Playwright playwright;
    private final Page page;
    private final BrowserContext context;
    private final Player player;
    private static synchronized void initBrowser(){
        if(browser == null){
            playwright = Playwright.create();
            BrowserType.LaunchOptions options = new BrowserType.LaunchOptions().setHeadless(false).setArgs(Arrays.asList("--allow-file-access-from-files"));
            browser = playwright.chromium().launch(options);
        }
    }
    public RenderMCJ2ME(Player player){
        initBrowser();
        Browser.NewContextOptions contextOptions = new Browser.NewContextOptions();
        contextOptions.setViewportSize(player.getLoginDatapackCraftJ2ME().getIntValue("wScreen"), player.getLoginDatapackCraftJ2ME().getIntValue("hScreen"));
        this.context = browser.newContext(contextOptions);
        this.page = this.context.newPage();
        this.player = player;
        try{
            URL render3DUrl = Proxy.class.getResource("/renderCode/renderVista.html");
            if(render3DUrl == null){
                throw new Exception("Not Found renderVista!");
            }
            this.page.navigate(render3DUrl.toURI());
        }catch(Exception er){
            er.printStackTrace();
        }
    }
    public String renderFPS(){
        byte[] data = page.screenshot();
        return Base64.getEncoder().encodeToString(data);
    }
    public void moveEntity(Vector3f pos, Vector2f rota, long entityId){}
    //public void setWorld(Data){}
    //public void setAnimation(){}
    //public void updateSkinEntity(Data){}
    //public void updateBlock(){}
    //public void spawnEntity(Data){}
    public void updatePlayer(){
        //this.page.evaluate("code...");
    }
    
    public static void close(){
        if(playwright != null){
            playwright.close();
        }
    }
    @Override
    public void close(){
        if(page != null){
            page.close();
        }
        if(context != null){
            context.close();
        }
    }
}