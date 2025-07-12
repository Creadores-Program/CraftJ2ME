package org.CreadoresProgram.CraftJ2ME.renderVista;

import java.io.IOException;
import java.io.File;
import java.io.ByteArrayOutputStream;
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
import javafx.animation.AnimationTimer;
import netscape.javascript.JSObject;

import javax.imageio.ImageIO;

import org.CreadoresProgram.CraftJ2ME.player.Player;
import org.CreadoresProgram.CraftJ2ME.server.Server;
import org.CreadoresProgram.CraftJ2ME.Proxy;
import org.CreadoresProgram.CraftJ2ME.network.server.packets.VistaDatapack;

import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.math.vector.Vector3f;

import lombok.Setter;
public class RenderMCJ2ME{
    private WebView webView = null;
    private WebEngine webEngine = null;
    private final Player player;
    private FPSRender fpsRender;

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
            this.setJsCallback("JavaComunique", new JSComunique());
            webEngine.load(Proxy.class.getResource("/renderCode/renderVista.html").toExternalForm());
            initFuture.complete(null);
        });
        initFuture.join();
    }
    public void offRender(){
        fpsRender.setRunning(false);
        Platform.exit();
    }
    public void onRender(){
        if(fpsRender != null){
            fpsRender.start();
        }else{
            new Thread(){
                @Override
                public void run(){
                    while(fpsRender == null){
                        //null
                    }
                    fpsRender.start();
                }
            }.start();
        }
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
    /*
    public void moveEntity(Vector3f pos, Vector2f rota, long entityId){}
    //public void setWorld(Data){}
    //public void setAnimation(){}
    //public void updateSkinEntity(Data){}
    //public void updateBlock(){}
    //public void spawnEntity(Data){}
    public void updatePlayer(){
        //this.page.evaluate("code...");
    }
    */
    public class JSComunique{
        public void doneThree(){
            fpsRender = new FPSRender();
        }
    }
    public class FPSRender extends Thread{
        @Setter
        private boolean running = true;
        @Override
        public void run(){
            while(running){
                try{
                    CompletableFuture<Void> future = new CompletableFuture<>();
                    Platform.runLater(()->{
                        try{
                            WritableImage image = webView.snapshot(null, null);
                            BufferedImage buferredImage = SwingFXUtils.fromFXImage(image, null);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            ImageIO.write(buferredImage, "png", baos);
                            VistaDatapack pk = new VistaDatapack();
                            pk.vistaImg = Base64.getEncoder().encodeToString(baos.toByteArray());
                            Server.getInstance().getServer().sendDataPacket(player.getIdentifier(), pk);
                            future.complete(null);
                        }catch(Exception ex){
                            future.completeExceptionally(ex);
                        }
                    });
                    future.join();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
                try{
                    Thread.sleep(50);
                }catch(Exception ex){
                    //IGNORE
                }
            }
        }
    }
}
