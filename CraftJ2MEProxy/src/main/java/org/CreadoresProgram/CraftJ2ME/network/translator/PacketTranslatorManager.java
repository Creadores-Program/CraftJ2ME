package org.CreadoresProgram.CraftJ2ME.network.translator;

import com.alibaba.fastjson2.JSONObject;

import lombok.Getter;

import org.CreadoresProgram.CraftJ2ME.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftJ2ME.network.translator.interfaces.CraftJ2MEPacketTranslator;
import org.CreadoresProgram.CraftJ2ME.network.translator.bedrock.*;
import org.CreadoresProgram.CraftJ2ME.player.Player;

import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;

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
        BedrockPacketTranslator translator = bedrockTranslators.get(packet.getClass());

        if (translator != null) {
            if (translator.immediate()) {
                translator.translate(packet, player);
            } else {
                threadPoolExecutor.execute(() -> translator.translate(packet, player));
            }
        }
    }
    public void translate(JSONObject packet){
        CraftJ2MEPacketTranslator translator = craftj2meTranslators.get(packet.getString("ID"));
        if(translator != null){
            if(translator.immediate()){
                translator.translate(packet, player);
            }else{
                threadPoolExecutor.execute(() -> translator.translate(packet, player));
            }
        }
    }
    private void registerDefaultPackets(){
        //CraftJ2ME
        craftj2meTranslators.put("exit", new org.CreadoresProgram.CraftJ2ME.network.translator.craftj2me.ExitPacket());
        craftj2meTranslators.put("ping", new org.CreadoresProgram.CraftJ2ME.network.translator.craftj2me.PingPacket());
        craftj2meTranslators.put("chat", new org.CreadoresProgram.CraftJ2ME.network.translator.craftj2me.ChatPacket());
        craftj2meTranslators.put("move", new org.CreadoresProgram.CraftJ2ME.network.translator.craftj2me.MovePacket());
        craftj2meTranslators.put("settingsenter", new org.CreadoresProgram.CraftJ2ME.network.translator.craftj2me.SettingsEnterPacket());
        craftj2meTranslators.put("requestplayerslist", new org.CreadoresProgram.CraftJ2ME.network.translator.craftj2me.RequestPlayersListPacket());
        //Bedrock
        bedrockTranslators.put(org.cloudburstmc.protocol.bedrock.packet.ResourcePackStackPacket.class, new ResourcePackStackPacket());
        bedrockTranslators.put(org.cloudburstmc.protocol.bedrock.packet.ResourcePacksInfoPacket.class, new ResourcePacksInfoPacket());
        bedrockTranslators.put(org.cloudburstmc.protocol.bedrock.packet.ServerToClientHandshakePacket.class, new ServerToClientHandshakePacket());
        bedrockTranslators.put(org.cloudburstmc.protocol.bedrock.packet.NetworkSettingsPacket.class, new NetworkSettingsPacket());
        bedrockTranslators.put(org.cloudburstmc.protocol.bedrock.packet.PlayStatusPacket.class, new PlayStatusPacket());
        bedrockTranslators.put(org.cloudburstmc.protocol.bedrock.packet.DisconnectPacket.class, new DisconnectPacket());
        bedrockTranslators.put(org.cloudburstmc.protocol.bedrock.packet.TextPacket.class, new TextPacket());
        bedrockTranslators.put(org.cloudburstmc.protocol.bedrock.packet.LevelChunkPacket.class, new LevelChunkPacket());
        bedrockTranslators.put(org.cloudburstmc.protocol.bedrock.packet.SetTimePacket.class, new SetTimePacket());
        bedrockTranslators.put(org.cloudburstmc.protocol.bedrock.packet.StartGamePacket.class, new StartGamePacket());
        bedrockTranslators.put(org.cloudburstmc.protocol.bedrock.packet.PlayerListPacket.class, new PlayerListPacket());
    }
}