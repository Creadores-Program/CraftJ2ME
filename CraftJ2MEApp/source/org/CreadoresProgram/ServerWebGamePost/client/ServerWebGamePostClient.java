package org.CreadoresProgram.ServerWebGamePost.client;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import org.json.me.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
public class ServerWebGamePostClient{
    private String domain;
    private int port;
    private boolean isHttps;

    public ProcessDatapackClient processDatapacks;
    public ProcessDatapackClient getProcessDatapacks(){
        return this.processDatapacks;
    }
    public void setProcessDatapacks(ProcessDatapackClient processDatapacks){
        this.processDatapacks = processDatapacks;
    }
    public String userAgent = "Mozilla/5.0 (J2ME; U; MIDP-2.0; CLDC-1.1; ServerWebGamePost) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36";
    public String getUserAgent(){
        return userAgent;
    }
    public void setUserAgent(String userA){
        this.userAgent = userA;
    }
    public ServerWebGamePostClient(String domain, int port, boolean isHttps){
        if(domain == null){
            throw new RuntimeException("NullPointerException: domain is null");
        }
        this.domain = domain;
        this.port = port;
        this.isHttps = isHttps;
        this.processDatapacks = new ProcessDatapackClient(this);
    }
    public void sendDataPacket(JSONObject datapack) {
        HttpConnection conn = null;
        InputStream is = null;
        OutputStream os = null;
        StringBuffer sb = new StringBuffer();
        try {
            String prefix = this.isHttps ? "https://" : "http://";
            String datapackstr = datapack.toString();
            String url = prefix + this.domain + ":" + this.port + "/ServerWebGamePost";
            conn = (HttpConnection) Connector.open(url);
            conn.setRequestMethod(HttpConnection.POST);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("User-Agent", userAgent);
            os = conn.openOutputStream();
            os.write(datapackstr.getBytes("UTF-8"));
            os.flush();
            int responseCode = conn.getResponseCode();
            if (responseCode != HttpConnection.HTTP_OK && responseCode != 201) {
                throw new Exception("HTTP error code: " + responseCode);
            }
            is = conn.openInputStream();
            InputStreamReader reader = new InputStreamReader(is, "UTF-8");
            int ch;
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            reader.close();
            this.processDatapacks.process(new JSONObject(sb.toString()));
        } catch(Exception erd) {
            System.err.println(erd);
        }
    }
}