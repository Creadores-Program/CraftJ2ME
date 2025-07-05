package org.CreadoresProgram.CraftJ2ME.network.translator;

import com.alibaba.fastjson2.JSONObject;

import lombok.Getter;

import org.CreadoresProgram.CraftJ2ME.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftJ2ME.network.translator.interfaces.CraftJ2MEPacketTranslator;
import org.CreadoresProgram.CraftJ2ME.network.translator.bedrock.*;
import org.CreadoresProgram.CraftJ2ME.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
public class PacketTranslatorManager{
    private Player player;
    private final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(),
            Integer.MAX_VALUE,
            60,
            TimeUnit.SECONDS,
            new SynchronousQueue<>(),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );
    @Getter
    private final Map<Class<? extends BedrockPacket>, BedrockPacketTranslator> bedrockTranslators = new HashMap<>();
    @Getter
    private final Map<String, CraftJ2MEPacketTranslator> craftj2meTranslators = new HashMap<>();
    public PacketTranslatorManager(Player player){
        this.player = player;
        this.registerDefaultPackets();
    }
    public void translate(BedrockPacket packet){
        BedrockPacketTranslator translator = bedrockTranslators.get(pk.getClass());

        if (translator != null) {
            if (translator.immediate()) {
                translator.translate(pk, player);
            } else {
                threadPoolExecutor.execute(() -> translator.translate(pk, player));
            }
        }
    }
    public void translate(JSONObject packet){
        CraftJ2MEPacketTranslator translator = craftj2meTranslators.get(packet.getString("ID"));
        if(translator != null){
            if(translator.immediate()){
                translator.translate(pk, player);
            }else{
                threadPoolExecutor.execute(() -> translator.translate(pk, player));
            }
        }
    }
    private void registerDefaultPackets(){
        //CraftJ2ME
        //Bedrock
        bedrockTranslators.put(org.cloudburstmc.protocol.bedrock.packet.ResourcePackStackPacket.class, new ResourcePackStackPacket());
    }
}