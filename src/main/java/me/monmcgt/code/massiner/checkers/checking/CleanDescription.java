package me.monmcgt.code.massiner.checkers.checking;

public class CleanDescription {
    public static String clean(String line) {
        if (line == null) {
            return "";
        } else {
            line = line.replace("§0", "");
            line = line.replace("§1", "");
            line = line.replace("§2", "");
            line = line.replace("§3", "");
            line = line.replace("§4", "");
            line = line.replace("§5", "");
            line = line.replace("§6", "");
            line = line.replace("§7", "");
            line = line.replace("§8", "");
            line = line.replace("§9", "");
            line = line.replace("§a", "");
            line = line.replace("§b", "");
            line = line.replace("§c", "");
            line = line.replace("§d", "");
            line = line.replace("§e", "");
            line = line.replace("§f", "");
            line = line.replace("§k", "");
            line = line.replace("§l", "");
            line = line.replace("§m", "");
            line = line.replace("§n", "");
            line = line.replace("§r", "");
            line = line.replace("§o", "");
            line = line.replace("§A", "");
            line = line.replace("§B", "");
            line = line.replace("§C", "");
            line = line.replace("§D", "");
            line = line.replace("§E", "");
            line = line.replace("§F", "");
            line = line.replace("§K", "");
            line = line.replace("§L", "");
            line = line.replace("§M", "");
            line = line.replace("§N", "");
            line = line.replace("§R", "");
            line = line.replace("§O", "");
            line = line.replace("\n", "");
            line = line.trim().replaceAll(" +", " ");
            return line;
        }
    }
}
