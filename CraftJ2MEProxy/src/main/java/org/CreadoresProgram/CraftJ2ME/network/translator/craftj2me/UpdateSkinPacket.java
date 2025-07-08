package org.CreadoresProgram.CraftJ2ME.network.translator.craftj2me;
import com.alibaba.fastjson2.JSONObject;

import org.CreadoresProgram.CraftJ2ME.network.translator.interfaces.CraftJ2MEPacketTranslator;
import org.CreadoresProgram.CraftJ2ME.player.Player;
import org.CreadoresProgram.CraftJ2ME.server.Server;

import org.cloudburstmc.protocol.bedrock.packet.PlayerSkinPacket;
import org.cloudburstmc.protocol.bedrock.data.skin.SerializedSkin;
import org.cloudburstmc.protocol.bedrock.data.skin.ImageData;

import java.util.Base64;
import java.util.UUID;

import org.jsoup.Jsoup;
public class UpdateSkinPacket implements CraftJ2MEPacketTranslator{
    @Override
    public void translate(JSONObject pk, Player player){
        PlayerSkinPacket subpk = new PlayerSkinPacket();
        subpk.setUuid(UUID.fromString(player.getIdentifier()));
        subpk.setTrustedSkin(true);
        subpk.setNewSkinName(player.getIdentifier()+".Custom");
        subpk.setOldSkinName(player.getIdentifier()+".Custom");
        ImageData skinData;
        try{
            byte[] response = Jsoup.connect(pk.getString("skin"))
                .userAgent("Mozilla/5.0 (J2ME; U; MIDP-2.0; CLDC-1.1; CraftJ2MEProxy) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36")
                .ignoreContentType(true).execute().bodyAsBytes();
            skinData = ImageData.of(response);
        }catch(Exception er){
            skinData = ImageData.of(Base64.getDecoder().decode(Server.getInstance().getDefaultSkinData()));
        }
        SerializedSkin skin = SerializedSkin.of(player.getIdentifier()+".Custom", UUID.randomUUID().toString(), skinData, null, "{\"geometry\" : {\"default\" : \"geometry.humanoid.custom\"}}", Base64.getEncoder().encodeToString(Server.getInstance().getDefaultSkinGeometry().getBytes()), false);
        subpk.setSkin(skin);
        player.getBedrockClientSession().sendPacket(subpk);
    }
}