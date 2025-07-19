package org.CreadoresProgram.CraftJ2ME.renderVista;
import java.util.Map;
import java.util.HashMap;
import java.util.Base64;
import java.util.stream.Stream;
import java.net.URI;
import java.net.URL;
import java.nio.file.*;

import org.CreadoresProgram.CraftJ2ME.Proxy;

import lombok.Getter;
public class ItemsTexture{
    @Getter
    private static final Map<String, String> textures = new HashMap<>();
    public static void init(){
        try{
            URI resourceUri = Proxy.class.getClassLoader().getResource("/textures/items").toURI();
            Path pathResour = Paths.get(resourceUri);
            recorProcess(pathResour);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    private static void recorProcess(Path startPath) throws Exception{
        try(Stream<Path> walk = Files.walk(startPath)){
            walk.filter(Files::isRegularFile)
                .forEach(filePath ->{
                    try{
                        byte[] fileBytes = Files.readAllBytes(filePath);
                        String base64Str = Base64.getEncoder().encodeToString(fileBytes);
                        String relaPath = startPath.relativize(filePath).toString();
                        String key = relaPath.replace('\\', '/');
                        textures.put(key, base64Str);
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }
            });
        }
    }
}