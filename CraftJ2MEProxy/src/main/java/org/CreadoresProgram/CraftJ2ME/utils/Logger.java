package org.CreadoresProgram.CraftJ2ME.utils;
public class Logger{
  public String prefix;
  public Logger(String prefix){
    this.prefix = prefix;
  }
  public Logger getLogger(){
    return this;
  }
  public String getPrefix(){
    return this.prefix;
  }
  public void emergency(String message){
    System.err.println("["+TextFormat.RED.getAnsiCode()+"EMERGENCY"+TextFormat.RESET.getAnsiCode()+"] ["+ this.prefix +TextFormat.RESET.getAnsiCode()+"] "+message);
  }
  public void emergency(String message, Throwable er){
    System.err.println("["+TextFormat.RED.getAnsiCode()+"EMERGENCY"+TextFormat.RESET.getAnsiCode()+"] ["+ this.prefix +TextFormat.RESET.getAnsiCode()+"] "+message);
    er.printStackTrace();
  }
  public void info(String message){
    System.out.println("["+TextFormat.GREEN.getAnsiCode()+"INFO"+TextFormat.RESET.getAnsiCode()+"] ["+ this.prefix +TextFormat.RESET.getAnsiCode()+"] "+message);
  }
  public void info(String message, Throwable er){
    System.out.println("["+TextFormat.GREEN.getAnsiCode()+"INFO"+TextFormat.RESET.getAnsiCode()+"] ["+ this.prefix +TextFormat.RESET.getAnsiCode()+"] "+message);
    er.printStackTrace();
  }
  public void alert(String message){
    System.out.println("["+TextFormat.YELLOW.getAnsiCode()+"WARN"+TextFormat.RESET.getAnsiCode()+"] ["+ this.prefix +TextFormat.RESET.getAnsiCode()+"] "+message);
  }
  public void alert(String message, Throwable er){
    System.out.println("["+TextFormat.YELLOW.getAnsiCode()+"WARN"+TextFormat.RESET.getAnsiCode()+"] ["+ this.prefix +TextFormat.RESET.getAnsiCode()+"] "+message);
    er.printStackTrace();
  }
  public void critical(String message){
    System.err.println("["+TextFormat.DARK_RED.getAnsiCode()+"FATAL"+TextFormat.RESET.getAnsiCode()+"] ["+ this.prefix +TextFormat.RESET.getAnsiCode()+"] "+message);
  }
  public void critical(String message, Throwable er){
    System.err.println("["+TextFormat.DARK_RED.getAnsiCode()+"FATAL"+TextFormat.RESET.getAnsiCode()+"] ["+ this.prefix +TextFormat.RESET.getAnsiCode()+"] "+message);
    er.printStackTrace();
  }
  public void error(String message){
    System.err.println("["+TextFormat.RED.getAnsiCode()+"ERROR"+TextFormat.RESET.getAnsiCode()+"] ["+ this.prefix +TextFormat.RESET.getAnsiCode()+"] "+message);
  }
  public void error(String message, Throwable er){
    System.err.println("["+TextFormat.RED.getAnsiCode()+"ERROR"+TextFormat.RESET.getAnsiCode()+"] ["+ this.prefix +TextFormat.RESET.getAnsiCode()+"] "+message);
    er.printStackTrace();
  }
  public void warning(String message){
    System.out.println("["+TextFormat.YELLOW.getAnsiCode()+"WARN"+TextFormat.RESET.getAnsiCode()+"] ["+ this.prefix +TextFormat.RESET.getAnsiCode()+"] "+message);
  }
  public void warning(String message, Throwable er){
    System.out.println("["+TextFormat.YELLOW.getAnsiCode()+"WARN"+TextFormat.RESET.getAnsiCode()+"] ["+ this.prefix +TextFormat.RESET.getAnsiCode()+"] "+message);
    er.printStackTrace();
  }
  public void notice(String message){
    System.out.println("["+TextFormat.YELLOW.getAnsiCode()+"WARN"+TextFormat.RESET.getAnsiCode()+"] ["+ this.prefix +TextFormat.RESET.getAnsiCode()+"] "+message);
  }
  public void notice(String message, Throwable er){
    System.out.println("["+TextFormat.YELLOW.getAnsiCode()+"WARN"+TextFormat.RESET.getAnsiCode()+"] ["+ this.prefix +TextFormat.RESET.getAnsiCode()+"] "+message);
    er.printStackTrace();
  }
  public void debug(String message){
    System.out.println("[DEBUG] ["+ this.prefix +TextFormat.RESET.getAnsiCode()+"] "+message);
  }
  public void debug(String message, Throwable er){
    System.out.println("[DEBUG] ["+ this.prefix +TextFormat.RESET.getAnsiCode()+"] "+message);
    er.printStackTrace();
  }
  public void logException(Throwable er){
    er.printStackTrace();
  }
  public void log(String message){
    System.out.println(message);
  }
  public void log(String message, Throwable er){
    System.out.println(message);
    er.printStackTrace();
  }
  public void warn(String message){
    System.out.println("["+TextFormat.YELLOW.getAnsiCode()+"WARN"+TextFormat.RESET.getAnsiCode()+"] ["+ this.prefix +TextFormat.RESET.getAnsiCode()+"] "+message);
  }
  public void warn(String message, Throwable er){
    System.out.println("["+TextFormat.YELLOW.getAnsiCode()+"WARN"+TextFormat.RESET.getAnsiCode()+"] ["+ this.prefix +TextFormat.RESET.getAnsiCode()+"] "+message);
    er.printStackTrace();
  }
}