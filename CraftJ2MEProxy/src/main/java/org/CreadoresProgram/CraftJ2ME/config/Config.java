package org.CreadoresProgram.CraftJ2ME.config;
import lombok.Getter;
public class Config{
    @Getter
    public String bindAddress;
    @Getter
    public Integer port;
    @Getter
    public Integer maxPlayers;
    @Getter
    public String shutdownMessage;
    @Getter
    public String bedrockAddress;
    @Getter
    public Integer bedrockPort;
}