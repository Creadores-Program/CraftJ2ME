package org.CreadoresProgram.CraftJ2ME.player;
import org.CreadoresProgram.CraftJ2ME.network.translator.PacketTranslatorManager;
import org.CreadoresProgram.CraftJ2ME.network.BedrockBatchHandler;
import org.CreadoresProgram.CraftJ2ME.server.Server;
import org.CreadoresProgram.CraftJ2ME.utils.Utils;
import org.CreadoresProgram.CraftJ2ME.config.Config;
import org.CreadoresProgram.CraftJ2ME.network.server.packets.ExitDatapack;
import org.CreadoresProgram.CraftJ2ME.renderVista.RenderMCJ2ME;

import lombok.Getter;
import lombok.Setter;

import io.netty.channel.Channel;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONArray;

import org.cloudburstmc.protocol.bedrock.BedrockClientSession;
import org.cloudburstmc.protocol.bedrock.packet.LoginPacket;
import org.cloudburstmc.protocol.bedrock.util.EncryptionUtils;
import org.cloudburstmc.netty.channel.raknet.RakChannelFactory;
import org.cloudburstmc.netty.channel.raknet.config.RakChannelOption;
import org.cloudburstmc.protocol.bedrock.netty.initializer.BedrockClientInitializer;
import org.cloudburstmc.protocol.bedrock.packet.RequestNetworkSettingsPacket;
import org.cloudburstmc.protocol.bedrock.packet.MovePlayerPacket;
import org.cloudburstmc.protocol.bedrock.packet.StartGamePacket;
import org.cloudburstmc.protocol.bedrock.packet.PlayerAuthInputPacket;
import org.cloudburstmc.protocol.bedrock.packet.PlayerListPacket;
import org.cloudburstmc.protocol.bedrock.data.AuthoritativeMovementMode;
import org.cloudburstmc.protocol.bedrock.data.InputInteractionModel;
import org.cloudburstmc.protocol.bedrock.data.InputMode;
import org.cloudburstmc.protocol.bedrock.data.ClientPlayMode;
import org.cloudburstmc.protocol.bedrock.data.PlayerAuthInputData;
import org.cloudburstmc.protocol.bedrock.data.inventory.transaction.ItemUseTransaction;
import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.math.vector.Vector3i;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.KeyPair;
import java.security.Signature;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.Base64;
import java.util.Random;
import java.util.List;

import org.jsoup.Jsoup;
public class Player{
    @Getter
    private final PacketTranslatorManager packetTranslatorManager;
    @Getter
    private final String identifier;
    @Getter
    private BedrockClientSession bedrockClientSession;
    @Getter
    private ECPublicKey publicKey;
    @Getter
    private ECPrivateKey privateKey;
    @Getter
    private Channel channel;
    @Getter
    private JSONObject loginDatapackCraftJ2ME;
    @Getter
    private MoveLoop moveLoop;
    @Setter
    @Getter
    private Vector3f vector3f;
    @Getter
    @Setter
    private Vector2f rotation;
    @Setter
    @Getter
    private Vector3f oldPosition;
    @Setter
    @Getter
    private Vector3f lastServerPosition;
    @Setter
    @Getter
    private Vector2f lastServerRotation;
    @Setter
    @Getter
    private long runtimeEntityId;
    @Getter
    @Setter
    private StartGamePacket startGamePacketCache;
    private boolean tickPlayerInputStarted = false;
    @Setter
    @Getter
    private boolean sneaking = false;
    @Setter
    @Getter
    private boolean immobile = false;
    private final ScheduledExecutorService playerInputExecutor = Executors.newScheduledThreadPool(1);
    @Setter
    @Getter
    private ItemUseTransaction playerAuthInputItemUseTransaction = null;
    @Getter
    @Setter
    private int hotbarSlot = 0;
    @Getter
    @Setter
    private String traslateAd = "false";
    @Getter
    @Setter
    private RenderMCJ2ME renderMCJ2ME;
    @Getter
    private PlayerPingLoop pingLoop;
    @Setter
    @Getter
    private int dimention = 0;
    @Getter
    private final List<PlayerListPacket.Entry> playersList = new ObjectArrayList<>();
    public int vida = 20;
    public int hambre = 20;
    public int armadura = 0;
    public int xp = 0;
    public Player(String identifier, JSONObject loginDatapack){
        this.packetTranslatorManager = new PacketTranslatorManager(this);
        this.identifier = identifier;
        this.loginDatapackCraftJ2ME = loginDatapack;
        this.pingLoop = new PlayerPingLoop(this);
        this.pingLoop.start();
        loginServer();
    }
    private void loginServer(){
        Config config = Server.getInstance().getConfig();
        InetSocketAddress bedrockAddress = new InetSocketAddress(config.getBedrockAddress(), config.getBedrockPort());
        try {
            channel = new Bootstrap().channelFactory(RakChannelFactory.client(NioDatagramChannel.class))
                .group(new NioEventLoopGroup())
                .option(RakChannelOption.RAK_PROTOCOL_VERSION, Server.getInstance().getBedrockPacketCodec().getRaknetProtocolVersion())
                .handler(new BedrockClientInitializer() {
                    @Override
                    protected void initSession(BedrockClientSession session) {
                        bedrockClientSession = session;
                        session.setCodec(Server.getInstance().getBedrockPacketCodec());
                        session.setPacketHandler(new BedrockBatchHandler(Player.this));

                        RequestNetworkSettingsPacket requestNetworkSettingsPacket = new RequestNetworkSettingsPacket();
                        requestNetworkSettingsPacket.setProtocolVersion(Server.getInstance().getBedrockPacketCodec().getProtocolVersion());
                        session.sendPacketImmediately(requestNetworkSettingsPacket);
                    }
                })
                .connect(bedrockAddress)
                .awaitUninterruptibly().channel();
                Server.getInstance().getPlayers().put(this.identifier, this);
        }catch(Exception ex){
            disconnect("Failed to connect: " + ex);
        }
    }
    public LoginPacket getLoginDatapack(){
        LoginPacket datapack = new LoginPacket();
        KeyPair ecdsa384KeyPair = EncryptionUtils.createKeyPair();
        this.publicKey = (ECPublicKey) ecdsa384KeyPair.getPublic();
        this.privateKey = (ECPrivateKey) ecdsa384KeyPair.getPrivate();
        String publicKeyBase64 = Base64.getEncoder().encodeToString(this.publicKey.getEncoded());
        JSONObject chain = new JSONObject();
        chain.put("exp", Instant.now().getEpochSecond() + TimeUnit.HOURS.toSeconds(6));
        chain.put("identityPublicKey", publicKeyBase64);
        chain.put("nbf", Instant.now().getEpochSecond() - TimeUnit.HOURS.toSeconds(6));
        JSONObject extraData = new JSONObject();
        extraData.put("identity", this.identifier);
        extraData.put("XUID", "");
        extraData.put("displayName", loginDatapackCraftJ2ME.getString("name"));
        chain.put("extraData", extraData);
        JSONObject jwtHeader = new JSONObject();
        jwtHeader.put("alg", "ES384");
        jwtHeader.put("x5u", publicKeyBase64);
        String jwt = generateJwt(jwtHeader, chain);
        JSONArray chainDataJsonArray = new JSONArray();
        chainDataJsonArray.add(jwt);
        for (Object o : chainDataJsonArray) {
            datapack.getChain().add((String) o);
        }
        datapack.setExtra(this.getSkinData());
        datapack.setProtocolVersion(Server.getInstance().getBedrockPacketCodec().getProtocolVersion());
        return datapack;
    }
    private String getSkinData() {
        String publicKeyBase64 = Base64.getEncoder().encodeToString(this.publicKey.getEncoded());
        JSONObject jwtHeader = new JSONObject();
        jwtHeader.put("alg", "ES384");
        jwtHeader.put("x5u", publicKeyBase64);
        JSONObject skinData = new JSONObject();
        skinData.put("AnimatedImageData", new JSONArray());
        skinData.put("ArmSize", "wide");
        skinData.put("CapeData", "");
        skinData.put("CapeId", "");
        skinData.put("PlayFabId", java.util.UUID.randomUUID().toString());
        skinData.put("CapeImageHeight", 0);
        skinData.put("CapeImageWidth", 0);
        skinData.put("CapeOnClassicSkin", false);
        skinData.put("ClientRandomId", new Random().nextLong());
        skinData.put("CompatibleWithClientSideChunkGen", false);
        skinData.put("CurrentInputMode", 1);
        skinData.put("DefaultInputMode", 1);
        skinData.put("DeviceId", java.util.UUID.randomUUID().toString());
        skinData.put("DeviceModel", loginDatapackCraftJ2ME.getString("deviceModel"));
        skinData.put("DeviceOS", 14);
        skinData.put("GameVersion", Server.getInstance().getBedrockPacketCodec().getMinecraftVersion());
        skinData.put("GuiScale", 0);
        skinData.put("LanguageCode", loginDatapackCraftJ2ME.getString("language"));
        skinData.put("PersonaPieces", new JSONArray());
        skinData.put("PersonaSkin", false);
        skinData.put("PieceTintColors", new JSONArray());
        skinData.put("PlatformOfflineId", "");
        skinData.put("PlatformOnlineId", "");
        skinData.put("PremiumSkin", false);
        skinData.put("SelfSignedId", this.identifier);
        skinData.put("ServerAddress", Server.getInstance().getConfig().getBedrockAddress() + ":" + Server.getInstance().getConfig().getBedrockPort());
        skinData.put("SkinAnimationData", "");
        skinData.put("SkinColor", "#0");
        if(loginDatapackCraftJ2ME.getString("skin") != ""){
            try{
                byte[] response = Jsoup.connect(loginDatapackCraftJ2ME.getString("skin"))
                    .userAgent("Mozilla/5.0 (J2ME; U; MIDP-2.0; CLDC-1.1; CraftJ2MEProxy) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36")
                    .ignoreContentType(true).execute().bodyAsBytes();
                String skinDataB = Base64.getEncoder().encodeToString(response);
                skinData.put("SkinData", skinDataB);
            }catch(Exception er){
                skinData.put("SkinData", Server.getInstance().getDefaultSkinData());
            }
        }else{
            skinData.put("SkinData", Server.getInstance().getDefaultSkinData());
        }
        skinData.put("SkinGeometryData", Base64.getEncoder().encodeToString(Server.getInstance().getDefaultSkinGeometry().getBytes()));
        skinData.put("SkinGeometryName", "{\"geometry\" : {\"default\" : \"geometry.humanoid.custom\"}}");
        skinData.put("SkinId", this.identifier + ".Custom");
        skinData.put("SkinImageHeight", 64);
        skinData.put("SkinImageWidth", 64);
        skinData.put("SkinResourcePatch", "ewogICAiZ2VvbWV0cnkiIDogewogICAgICAiZGVmYXVsdCIgOiAiZ2VvbWV0cnkuaHVtYW5vaWQuY3VzdG9tIgogICB9Cn0K");
        skinData.put("ThirdPartyName", loginDatapackCraftJ2ME.getString("name"));
        skinData.put("ThirdPartyNameOnly", false);
        skinData.put("UIProfile", 0);
        skinData.put("IsEditorMode", 0);
        skinData.put("TrustedSkin", 1);
        skinData.put("SkinGeometryDataEngineVersion", Base64.getEncoder().encodeToString(Server.getInstance().getBedrockPacketCodec().getMinecraftVersion().getBytes()));
        skinData.put("OverrideSkin", false);
        return generateJwt(jwtHeader, skinData);
    }
    public void startSendingPlayerInput() {
        if (!tickPlayerInputStarted) {
            tickPlayerInputStarted = true;

            PlayerAuthInputThread playerAuthInputThread = new PlayerAuthInputThread();
            playerAuthInputThread.player = this;
            playerAuthInputThread.tick = getStartGamePacketCache().getCurrentTick();

            playerInputExecutor.scheduleAtFixedRate(playerAuthInputThread, 0, 50, TimeUnit.MILLISECONDS);
        }
    }
    public void startMoveLoop(){
        moveLoop = new MoveLoop(this);
        moveLoop.start();
    }
    public void disconnect(String reason) {
        playerInputExecutor.shutdown();
        if(moveLoop != null){
            moveLoop.setRunning(false);
        }
        if(pingLoop != null){
            pingLoop.setRunning(false);
        }
        try {
            this.bedrockClientSession.disconnect();
        } catch (Throwable ignored) {
        }
        if (this.channel != null && this.channel.isOpen()) {
            this.channel.disconnect();
            this.channel.parent().disconnect();
        }
        ExitDatapack datap = new ExitDatapack();
        datap.reason = reason;
        Server.getInstance().getServer().sendDataPacket(this.identifier, datap);
        Server.getInstance().getPlayers().remove(this.identifier);
        Server.getInstance().getLogger().info(loginDatapackCraftJ2ME.getString("name") + " disconnected: " + reason);
    }
    private String generateJwt(JSONObject jwtHeader, JSONObject chain) {
        String header = Base64.getUrlEncoder().withoutPadding().encodeToString(jwtHeader.toJSONString().getBytes());
        String payload = Base64.getUrlEncoder().withoutPadding().encodeToString(chain.toJSONString().getBytes());

        byte[] dataToSign = (header + "." + payload).getBytes();
        byte[] signatureBytes = null;
        try {
            Signature signature = Signature.getInstance("SHA384withECDSA");
            signature.initSign(this.privateKey);
            signature.update(dataToSign);
            signatureBytes = Utils.DERToJOSE(signature.sign(), Utils.AlgorithmType.ECDSA384);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException ignored) {
        }
        String signatureString = Base64.getUrlEncoder().withoutPadding().encodeToString(signatureBytes);

        return header + "." + payload + "." + signatureString;
    }
    public static class MoveLoop extends Thread{
        @Setter
        @Getter
        public Vector3f velocityCraftJ2ME = Vector3f.from(0, 0, 0);
        @Setter
        @Getter
        private Vector2f velocityFaceCraftJ2ME = Vector2f.from(0, 0);
        @Setter
        @Getter
        private Vector3f velocity = Vector3f.from(0.216, 0.216, 0.216);
        private static final Vector2f velocityFace = Vector2f.from(7.5, 7.5);
        @Setter
        private boolean running = true;
        private Player player;
        public MoveLoop(Player player){
            this.player = player;
        }
        public void run(){
            while(running){
                if((velocityCraftJ2ME.getX() == 0 && velocityCraftJ2ME.getY() == 0 && velocityCraftJ2ME.getZ() == 0 && velocityFaceCraftJ2ME.getX() == 0 && velocityFaceCraftJ2ME.getY() == 0) || player.isImmobile()){
                    try{
                        Thread.sleep(50);
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }
                    continue;
                }
                MovePlayerPacket pk = new MovePlayerPacket();
                pk.setRuntimeEntityId(player.getRuntimeEntityId());
                if(velocityCraftJ2ME.getX() != 0 || velocityCraftJ2ME.getY() != 0 || velocityCraftJ2ME.getZ() != 0){
                    player.setOldPosition(player.getVector3f());
                    double y = 0;
                    double x = 0;
                    double z = 0;
                    if(velocityCraftJ2ME.getY() == -1){
                        velocity = Vector3f.from(0.065, 0.065, 0.065);
                        player.setSneaking(true);
                    }else if(velocityCraftJ2ME.getY() == 1){
                        velocity = Vector3f.from(0.216, 0.216, 0.216);
                        player.setSneaking(false);
                        y = player.getVector3f().getY() + velocity.getY();
                    }else{
                        velocity = Vector3f.from(0.216, 0.216, 0.216);
                        player.setSneaking(false);
                        y = player.getVector3f().getY();
                    }
                    if(velocityCraftJ2ME.getX() == 1){
                        x = player.getVector3f().getX() + velocity.getX();
                    }else if(velocityCraftJ2ME.getX() == -1){
                        x = player.getVector3f().getX() - velocity.getX();
                    }else{
                        x = player.getVector3f().getX();
                    }
                    if(velocityCraftJ2ME.getZ() == 1){
                        z = player.getVector3f().getZ() + velocity.getZ();
                    }else if(velocityCraftJ2ME.getZ() == -1){
                        z = player.getVector3f().getZ() - velocity.getZ();
                    }else{
                        z = player.getVector3f().getZ();
                    }
                    player.setVector3f(Vector3f.from(x, y, z));
                }
                pk.setPosition(player.getVector3f());
                if(velocityFaceCraftJ2ME.getX() != 0 || velocityFaceCraftJ2ME.getY() != 0){
                    double x = 0;
                    double y = 0;
                    if(velocityFaceCraftJ2ME.getX() == 1){
                        x = player.getRotation().getX() + velocityFace.getX();
                    }else if(velocityCraftJ2ME.getX() == -1){
                        x = player.getRotation().getX() - velocityFace.getX();
                    }else{
                        x = player.getRotation().getX();
                    }
                    if(velocityFaceCraftJ2ME.getY() == 1){
                        y = player.getRotation().getY() + velocityFace.getY();
                    }else if(velocityFaceCraftJ2ME.getY() == -1){
                        y = player.getRotation().getY() - velocityFace.getY();
                    }else{
                        y = player.getRotation().getY();
                    }
                    player.setRotation(Vector2f.from(x, y));
                }
                pk.setRotation(Vector3f.from(player.getRotation().getX(), player.getRotation().getY(), player.getRotation().getY()));
                pk.setMode(MovePlayerPacket.Mode.NORMAL);
                pk.setRidingRuntimeEntityId(0);
                pk.setTeleportationCause(MovePlayerPacket.TeleportationCause.UNKNOWN);
                pk.setEntityType(0);
                pk.setOnGround(true);
                if(player.getRenderMCJ2ME() != null){
                    player.getRenderMCJ2ME().setPlayerPos(player.getVector3f(), player.getRotation());
                }
                if(player.getStartGamePacketCache().getAuthoritativeMovementMode() == AuthoritativeMovementMode.CLIENT){
                    player.getBedrockClientSession().sendPacket(pk);
                }
            }
        }
    }
    public static class PlayerPingLoop extends Thread{
        @Setter
        private boolean running = true;
        private Player player;
        @Setter
        private long lastResponse = System.currentTimeMillis();
        private int timeoutMs = 5000;
        public PlayerPingLoop(Player player){
            this.player = player;
        }
        public void run(){
            while(running){
                try{
                    long now = System.currentTimeMillis();
                    if(now - lastResponse > timeoutMs){
                        player.disconnect("Ping Excessed");
                    }
                    Thread.sleep(1000);
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }
    static class PlayerAuthInputThread implements Runnable {
        public Player player;
        public long tick;
        public void run(){
            try{
                if(player.getBedrockClientSession().isConnected()){
                    tick++;
                    PlayerAuthInputPacket pk = new PlayerAuthInputPacket();
                    pk.setPosition(player.getVector3f());
                    pk.setRotation(Vector3f.from(player.getRotation().getX(), player.getRotation().getY(), player.getRotation().getY()));
                    pk.setMotion(Vector2f.ZERO);
                    pk.setInputInteractionModel(InputInteractionModel.CROSSHAIR);
                    pk.setInputMode(InputMode.MOUSE);
                    pk.setPlayMode(ClientPlayMode.SCREEN);
                    pk.setVrGazeDirection(null);
                    pk.setTick(tick);
                    pk.setDelta(Vector3f.from(player.getVector3f().getX() - player.getOldPosition().getX(), player.getVector3f().getY() - player.getOldPosition().getY(), player.getVector3f().getZ() - player.getOldPosition().getZ()));
                    pk.setItemStackRequest(null);
                    pk.setItemUseTransaction(player.getPlayerAuthInputItemUseTransaction());
                    pk.setAnalogMoveVector(Vector2f.ZERO);
                    if (player.isSneaking()) {
                        pk.getInputData().add(PlayerAuthInputData.SNEAKING);
                    }
                    player.getBedrockClientSession().sendPacketImmediately(pk);
                    player.setPlayerAuthInputItemUseTransaction(null);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}