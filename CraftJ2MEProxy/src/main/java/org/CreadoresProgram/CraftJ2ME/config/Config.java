package org.CreadoresProgram.CraftJ2ME.config;
import lombok.Getter;
public class Config{
    @Getter
    public Integer port;
    @Getter
    public String imgPath;
    @Getter
    public Integer maxPlayers;
    @Getter
    public String shutdownMessage;
    @Getter
    public String bedrockAddress;
    @Getter
    public Integer bedrockPort;
}