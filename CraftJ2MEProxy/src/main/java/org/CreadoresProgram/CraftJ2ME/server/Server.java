package org.CreadoresProgram.CraftJ2ME.server;
import lombok.Getter;

import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.codec.v662.Bedrock_v662;
import org.cloudburstmc.nbt.NBTInputStream;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.nbt.NbtUtils;

import org.yaml.snakeyaml.Yaml;

import com.alibaba.fastjson2.JSONObject;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPInputStream;
import java.util.Objects;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.io.*;

import org.CreadoresProgram.CraftJ2ME.config.Config;
import org.CreadoresProgram.CraftJ2ME.player.Player;
import org.CreadoresProgram.CraftJ2ME.network.server.ServerCraftJ2ME;
import org.CreadoresProgram.CraftJ2ME.utils.TextFormat;
import org.CreadoresProgram.CraftJ2ME.utils.Logger;
import org.CreadoresProgram.CraftJ2ME.utils.FileManager;
import org.CreadoresProgram.CraftJ2ME.utils.NbtBlockDefinitionRegistry;
import org.CreadoresProgram.CraftJ2ME.renderVista.ItemsTexture;
public class Server{
    @Getter
    private static Server instance = null;
    @Getter
    private final Map<String, Player> players = new ConcurrentHashMap<>();
    @Getter
    private final BedrockCodec bedrockPacketCodec = Bedrock_v662.CODEC;
    @Getter
    private final Path dataPath;
    @Getter
    private String defaultSkinData;
    @Getter
    private String defaultSkinGeometry;
    @Getter
    private NbtBlockDefinitionRegistry blockDefinitions;
    @Getter
    private Logger logger;
    @Getter
    private ServerCraftJ2ME server;
    @Getter
    private Config config;
    @Getter
    private JSONObject itemsList;
    public Server(String dataPath){
        instance = this;
        this.logger = new Logger(TextFormat.GOLD.getAnsiCode()+"CraftJ2ME Proxy");
        this.getLogger().info("CraftJ2ME Proxy 1.0.0-beta Starting...");
        this.dataPath = Paths.get(dataPath);
        if (!initConfig()) {
            this.getLogger().emergency("Config file not found! Terminating...");
            System.exit(1);
        }
        loadBlockDefinitions();
        loadDefaultSkin();
        loadItemsTexture();
        startServer();
    }
    private boolean initConfig(){
        File configFile = new File(dataPath.toFile(), "config.yml");
        if (!configFile.exists()) {
            try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.yml")) {
                if (inputStream == null) {
                    return false;
                }
                Files.createDirectories(configFile.getParentFile().toPath());
                Files.copy(inputStream, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                return false;
            }
        }
        try {
            this.config = new Yaml().loadAs(Files.newBufferedReader(configFile.toPath()), Config.class);
        } catch (IOException e) {
            return false;
        }
        return true;
    }
    private void loadBlockDefinitions() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("block_palette.nbt")) {
            assert inputStream != null;
            try (NBTInputStream nbtInputStream = NbtUtils.createGZIPReader(inputStream)) {
                Object object = nbtInputStream.readTag();
                if (object instanceof NbtMap) {
                    NbtMap blocksTag = (NbtMap) object;
                    blockDefinitions = new NbtBlockDefinitionRegistry(blocksTag.getList("blocks", NbtType.COMPOUND));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load block definitions", e);
        }
    }
    private void loadDefaultSkin(){
        try {
            defaultSkinData = FileManager.getFileContents(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("skin/skin_data.txt")));
            defaultSkinGeometry = FileManager.getFileContents(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("skin/skin_geometry.json")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void loadItemsTexture(){
        JSONObject jsonItemsTextures = FileManager.getJsonObjectFromResource("texturesItems.json");
        itemsList = new JSONObject();
        Set<String> keys = jsonItemsTextures.keySet();
        for(String key : keys){
            String path = jsonItemsTextures.getString(key);
            String base = ItemsTexture.getTextures().get(path);
            itemsList.put(key, base);
        }
    }
    private void startServer(){
        server = new ServerCraftJ2ME(((int) config.getPort()), config.getImgPath());
        server.start();
        logger.info(TextFormat.GREEN.getAnsiCode()+"CraftJ2ME Proxy"+TextFormat.RESET.getAnsiCode()+" is running on [0.0.0.0::" + this.config.getPort() + "]");
        logger.info("Done!");
    }
}