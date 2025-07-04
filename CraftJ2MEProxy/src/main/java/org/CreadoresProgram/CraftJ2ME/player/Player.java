package org.CreadoresProgram.CraftJ2ME.player;
import org.CreadoresProgram.CraftJ2ME.network.translator.PacketTranslatorManager;

import lombok.Getter;
import lombok.Setter;

import io.netty.channel.Channel;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONArray;

import org.cloudburstmc.protocol.bedrock.BedrockClientSession;
import org.cloudburstmc.protocol.bedrock.packet.LoginPacket;
import org.cloudburstmc.protocol.bedrock.util.EncryptionUtils;

import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.KeyPair;
import java.security.Signature;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.Base64;
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
    public Player(String identifier, JSONObject loginDatapack){
        this.packetTranslatorManager = new PacketTranslatorManager(this);
        this.identifier = identifier;
        loginServer(loginDatapack);
    }
    private void loginServer(JSONObject loginDatapack){}
    private LoginPacket getLoginDatapack(JSONObject loginDatapack){
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
        extraData.put("displayName", loginDatapack.getString("name"));
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
        datapack.setExtra(this.getSkinData(loginDatapack));
        datapack.setProtocolVersion(Server.getInstance().getBedrockPacketCodec().getProtocolVersion());
        return datapack;
    }
    private String getSkinData(JSONObject loginDatapack) {
        String publicKeyBase64 = Base64.getEncoder().encodeToString(this.publicKey.getEncoded());
        JSONObject jwtHeader = new JSONObject();
        jwtHeader.put("alg", "ES384");
        jwtHeader.put("x5u", publicKeyBase64);
        JSONObject skinData = new JSONObject();
        skinData.put("AnimatedImageData", new JSONArray());
        skinData.put("ArmSize", "");
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
        skinData.put("DeviceModel", loginDatapack.getString("deviceModel"));
        skinData.put("DeviceOS", 14);
        skinData.put("GameVersion", Server.getInstance().getBedrockPacketCodec().getMinecraftVersion());
        skinData.put("GuiScale", 0);
        skinData.put("LanguageCode", loginDatapack.getString("language"));
        skinData.put("PersonaPieces", new JSONArray());
        skinData.put("PersonaSkin", false);
        skinData.put("PieceTintColors", new JSONArray());
        skinData.put("PlatformOfflineId", "");
        skinData.put("PlatformOnlineId", "");
        skinData.put("PremiumSkin", false);
        skinData.put("SelfSignedId", this.identifier);
        skinData.put("ServerAddress", Server.getInstance().getConfig().getBedrockAddress() + ":" + ProxyServer.getInstance().getConfig().getBedrockPort());
        skinData.put("SkinAnimationData", "");
        skinData.put("SkinColor", "#0");
        skinData.put("SkinData", Server.getInstance().getDefaultSkinData());
        skinData.put("SkinGeometryData", Base64.getEncoder().encodeToString(Server.getInstance().getDefaultSkinGeometry().getBytes()));
        skinData.put("SkinId", this.identifier + ".Custom");
        skinData.put("SkinImageHeight", 64);
        skinData.put("SkinImageWidth", 64);
        skinData.put("SkinResourcePatch", "ewogICAiZ2VvbWV0cnkiIDogewogICAgICAiZGVmYXVsdCIgOiAiZ2VvbWV0cnkuaHVtYW5vaWQuY3VzdG9tIgogICB9Cn0K");
        skinData.put("ThirdPartyName", loginDatapack.getString("name"));
        skinData.put("ThirdPartyNameOnly", false);
        skinData.put("UIProfile", 0);
        skinData.put("IsEditorMode", 0);
        skinData.put("TrustedSkin", 1);
        //skinData.put("SkinGeometryDataEngineVersion", Base64.getEncoder().encodeToString(ProxyServer.getInstance().getBedrockPacketCodec().getMinecraftVersion().getBytes()));
        skinData.put("OverrideSkin", false);
        return generateJwt(jwtHeader, skinData);
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
}