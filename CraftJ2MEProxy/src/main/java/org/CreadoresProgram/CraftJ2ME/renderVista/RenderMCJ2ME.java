package org.CreadoresProgram.CraftJ2ME.renderVista;

import java.io.IOException;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.awt.image.BufferedImage;

import javafx.scene.Scene;
import javafx.scene.image.WritableImage;
import javafx.application.Platform;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import javafx.concurrent.Worker;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingFXUtils;
import netscape.javascript.JSObject;

import javax.imageio.ImageIO;

import org.CreadoresProgram.CraftJ2ME.player.Player;
import org.CreadoresProgram.CraftJ2ME.Proxy;

import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.math.vector.Vector3f;
public class RenderMCJ2ME{
    private final WebView webView;
    private final WebEngine webEngine;
    private final Player player;

    static {
        new JFXPanel();
    }
    public RenderMCJ2ME(final Player player){
        this.player = player;
        CompletableFuture<Void> initFuture = new CompletableFuture<>();
        Platform.runLater(()-> {
            this.webView = new WebView();
            this.webEngine = webView.getEngine();
            StackPane root = new StackPane(webView);
            new Scene(root, player.getLoginDatapackCraftJ2ME().getIntValue("wScreen"), player.getLoginDatapackCraftJ2ME().getIntValue("hScreen"));
            initFuture.complete(null);
        });
        initFuture.join();
    }
    public static void offRender(){
        Platform.exit();
    }
    private CompletableFuture<Void> setJsCallback(String name, Object callbackObject) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember(name, callbackObject);
                future.complete(null);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }
    private CompletableFuture<Object> execJS(String code){
        CompletableFuture<Object> future = new CompletableFuture<>();
        Platform.runLater(()->{
            try{
                String encodedCode = URLEncoder.encode(code, StandardCharsets.UTF_8.toString());
                String dataUrl = "data:text/javascript;charset=utf-8,"+ encodedCode;
                String script = String.format("import('%s')", dataUrl);
                JSObject promise = (JSObject) webEngine.executeScript(script);
                java.util.function.Consumer<Object> thenJS = (Object result)-> future.complete(result);
                java.util.function.Consumer<Object> catchJS = (Object errorJS)-> {
                    future.completeExceptionally(new RuntimeException(String.valueOf(errorJS)));
                };
                promise.call("then", thenJS);
                promise.call("catch", catchJS);
            }catch(Exception ex){
                future.completeExceptionally(ex);
            }
        });
        return future;
    }
    public class JSComunique{}
    /*private static final Browser browser;
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
    */
}
