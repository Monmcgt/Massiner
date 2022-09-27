package me.monmcgt.code.massiner.managers;

import me.monmcgt.code.massiner.Main;
import me.monmcgt.code.massiner.checkers.checking.CleanDescription;
import me.monmcgt.code.massiner.checkers.checking.Results;
import me.monmcgt.code.massiner.checkers.checking.rawData.Player;
import me.monmcgt.code.massiner.utilities.FileUtil;
import me.monmcgt.code.massiner.utilities.Time;
import me.monmcgt.code.massiner.utilities.Utilities;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ResultManager {
    public static ResultManager INSTANCE;

    private final List<Results> resultsList;

    private final FileUtil fileUtil;

    private final Random random;

    public ResultManager() {
        INSTANCE = this;

        this.resultsList = new ArrayList<>();

        this.fileUtil = new FileUtil();

        this.random = new Random();
    }

    public synchronized void receiveResult(Results result) {
//        System.out.println("Received result: " + result.getIp() + ":" + result.getPort());
        this.addResult(result);

        if (Main.showReceivedData) {
            System.out.println("Received result: " + result.getIp() + ":" + result.getPort() + " | Working? " + result.isWorking());
        }

        synchronized (this) {
//            System.out.println("Size: " + this.resultsList.size());

            if (this.resultsList.size() >= Integer.parseInt(Main.configs.get(ConfigManager.ConfigData.MODE_UPDATE_LAST_SERVER_SERVER_PER_UPDATE.getName()))) {
                List[] lists = this.convertResults();
                List<StandaloneServerTable> onlineServers = (List<StandaloneServerTable>) lists[0];
                List<MixedServerTable> mixedServers = (List<MixedServerTable>) lists[2];

                this.addResultToDatabase$Standalone(onlineServers);
                this.addResultToDatabase$Mixed(mixedServers);

                if (Main.configs.get(ConfigManager.ConfigData.MODE_UPDATE_LAST_SERVER.getName()).equals("2")) {
                    this.updateLastServer$2();
                }

                this.clearResults();
            }
        }

        if (Main.configs.get(ConfigManager.ConfigData.MODE_UPDATE_LAST_SERVER.getName()).equals("1")) {
            this.updateLastServer$1(result);
        }
//        String statement = "UPDATE `last_server` SET `address` = '" + result.getAddress() + "' WHERE `id` = 1;";
        if (!(Main.configs.get(ConfigManager.ConfigData.MODE_UPDATE_LAST_SERVER.getName()).equals("1") || Main.configs.get(ConfigManager.ConfigData.MODE_UPDATE_LAST_SERVER.getName()).equals("2"))) {
            System.err.println("Invalid value for MODE_UPDATE_LAST_SERVER: " + Main.configs.get(ConfigManager.ConfigData.MODE_UPDATE_LAST_SERVER.getName()));
        }
    }

    public synchronized void updateLastServer$1(Results results) {
        String statement = String.format("UPDATE `%s` SET `address` = '%s:%s' WHERE `id` = 1;", Main.configs.get(ConfigManager.ConfigData.TABLE_NAME_LAST_UPDATE.getName()), results.getIp(), results.getPort());
        try {
            DatabaseManager.instance.execute(statement);

            if (Main.showUpdateLastServer) {
                System.out.println("Updated last server: " + results.getIp() + ":" + results.getPort());
            }
        } catch (SQLException e) {
            System.err.println("Failed to update last server: " + e.getMessage());
            System.err.println("Query: " + statement);
            System.err.println("-----------------------------------------------------");
        }
    }

    public synchronized void updateLastServer$2() {
        synchronized (this) {
            String lastServer = null;
            if (Main.configs.get(ConfigManager.ConfigData.UPDATE_LAST_SERVER_BY_RANDOM.getName()).equals("true")) {
//                String randomAddress = this.resultsList.get(this.random.nextInt(this.resultsList.size()));

                lastServer = this.resultsList.get(0).getAddress();
            } else {
                List<Results> resultsList = this.resultsList;

                List<String> results = new ArrayList<>();

                for (Results result : resultsList) {
                    results.add(result.getAddress());
                }

                Utilities.sortList(results);

                lastServer = results.get(results.size() - 1);
            }

            String statement = String.format("UPDATE `%s` SET `address` = '%s:%s' WHERE `id` = 1;", Main.configs.get(ConfigManager.ConfigData.TABLE_NAME_LAST_UPDATE.getName()), lastServer.split(":")[0], lastServer.split(":")[1]);
            try {
                DatabaseManager.instance.execute(statement);

                if (Main.showUpdateLastServer) {
                    System.out.println("Updated last server: " + lastServer);
                }
            } catch (SQLException e) {
                System.err.println("Failed to update last server: " + e.getMessage());
                System.err.println("Query: " + statement);
                System.err.println("-----------------------------------------------------");
            }
        }
    }

    public void addResultToDatabase$Standalone(List<StandaloneServerTable> onlineServers) {
        for (StandaloneServerTable server : onlineServers) {
            String query = String.format("INSERT INTO `%s` (`second`, `minute`, `hour`, `day`, `month`, `year`, `ip`, `port`) VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s');", Main.configs.get(ConfigManager.ConfigData.TABLE_NAME_ONLINE_SERVERS.getName()), server.time.getSecond(), server.time.getMinute(), server.time.getHour(), server.time.getDay(), server.time.getMonth(), server.time.getYear(), server.ip, server.port);
            try {
                DatabaseManager.instance.execute(query);
                System.out.println("Added result to database (Address: " + server.ip + ":" + server.port + ")");
//                System.out.println("Query: " + query);
                System.out.println("-----------------------------------------------------");
            } catch (SQLException e) {
                System.err.println("Failed to add result to database (Address: " + server.ip + ":" + server.port + "): " + e.getMessage());
                System.err.println("Query: " + query);
                System.err.println("-----------------------------------------------------");

                try {
                    this.fileUtil.writeFile(new File(server.ip + ":" + server.port + ".standalone.txt"), new String[] {query});
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    public void addResultToDatabase$Mixed(List<MixedServerTable> mixedServers) {
        for (MixedServerTable server : mixedServers) {
            String query = String.format("INSERT INTO `%s` (`second`, `minute`, `hour`, `day`, `month`, `year`, `ip`, `port`, `working`, `version`, `motd`, `players`, `exception`) VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s');", Main.configs.get(ConfigManager.ConfigData.TABLE_NAME_MIXED_SERVERS.getName()), server.standaloneServerTable.time.getSecond(), server.standaloneServerTable.time.getMinute(), server.standaloneServerTable.time.getHour(), server.standaloneServerTable.time.getDay(), server.standaloneServerTable.time.getMonth(), server.standaloneServerTable.time.getYear(), server.standaloneServerTable.ip, server.standaloneServerTable.port, server.working, server.version, server.motd, server.players, server.exception);
            try {
                DatabaseManager.instance.execute(query);
                /*System.out.println("Added result to database (Address: " + server.standaloneServerTable.ip + ":" + server.standaloneServerTable.port + ")");
                System.out.println("Query: " + query);
                System.out.println("-----------------------------------------------------");*/
            } catch (SQLException e) {
                System.err.println("Failed to add result to database (Address: " + server.standaloneServerTable.ip + ":" + server.standaloneServerTable.port + "): " + e.getMessage());
                System.err.println("Query: " + query);
                System.err.println("-----------------------------------------------------");

                try {
                    this.fileUtil.writeFile(new File(server.standaloneServerTable.ip + ":" + server.standaloneServerTable.port + ".mixed.txt"), new String[] {query});
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    public synchronized List[] convertResults() {
        List<StandaloneServerTable> workingList = new ArrayList<>();
        List<StandaloneServerTable> offlineList = new ArrayList<>();
        List<MixedServerTable> mixedList = new ArrayList<>();

        final List<Results> finalResults = new ArrayList<>(this.resultsList);

        for (Results result : finalResults) {
            try {
                if (result.isWorking()) {
                    workingList.add(new StandaloneServerTable(result.getTime(), result.getIp(), result.getPort()));

                    StringBuilder players = new StringBuilder();

                    try {
                        for (int i = 0; i < result.getPlayers().getSample().size(); i++) {
                            Player player = result.getPlayers().getSample().get(i);

                            players.append(player.getName());

                            if (i != result.getPlayers().getSample().size() - 1) {
                                players.append(", ");
                            }
                        }
                    } catch (NullPointerException ignored) {
                    }

                    /*if (players.length() > 0) {
//                        mixedList.add(String.format("%s | %s | Version: %s | Motd: %s  | Players: %s", result.isWorking() ? "✅" : "❌", result.getAddress(), result.getVersion(), CleanDescription.clean(result.getMotd()), players.toString()));
                        mixedList.add(String.format("%s | %s | Version: %s | Motd: %s  | Players: %s", result.isWorking() ? "✅" : "❌", result.getAddress(), result.getVersion(), CleanDescription.clean(result.getMotd()), players.toString()));
                    } else {
//                        mixedList.add(String.format("%s | %s | Version: %s | Motd: %s", result.isWorking() ? "✅" : "❌", result.getAddress(), result.getVersion(), CleanDescription.clean(result.getMotd())));
                        mixedList.add(String.format("%s | %s | Version: %s | Motd: %s", result.isWorking() ? "✅" : "❌", result.getAddress(), result.getVersion(), CleanDescription.clean(result.getMotd())));
                    }*/

                    mixedList.add(new MixedServerTable(new StandaloneServerTable(result.getTime(), result.getIp(), result.getPort()), result.isWorking() ? "t" : "f", result.getVersion(), CleanDescription.clean(result.getMotd()), players.toString(), null));
                } else {
                    offlineList.add(new StandaloneServerTable(result.getTime(), result.getIp(), result.getPort()));

//                    mixedList.add(String.format("%s | %s | Exception: %s", result.isWorking() ? "✅" : "❌", result.getAddress(), result.getException().getMessage() == null ? "Unknown" : result.getException().getMessage()));
//                    mixedList.add(String.format("%s | %s | Exception: %s", result.isWorking() ? "✅" : "❌", result.getAddress(), result.getException().getMessage() == null ? "Unknown" : result.getException().getMessage()));
                }
            } catch (Exception e) {
                if (!(e instanceof NullPointerException)) {
                    System.err.println("[Error] - " + e.getMessage());
//                    this.logger.error("[Error] - " + e.getMessage());
                }
            }
        }

        return new List[] {workingList, offlineList, mixedList};
    }

    public synchronized void addResult(Results result) {
        this.resultsList.add(result);
    }

    public synchronized void clearResults() {
        this.resultsList.clear();
    }

    public synchronized List<Results> getResults() {
        return this.resultsList;
    }

    public static class StandaloneServerTable {
        public Time time;
        public String ip;
        public int port;

        public StandaloneServerTable(Time time, String ip, int port) {
            this.time = time;
            this.ip = ip;
            this.port = port;
        }
    }

    public static class MixedServerTable {
        public StandaloneServerTable standaloneServerTable;
        public String working;
        public String version;
        public String motd;
        public String players;
        public String exception;

        public MixedServerTable(StandaloneServerTable standaloneServerTable, String working, String version, String motd, String players, String exception) {
            this.standaloneServerTable = standaloneServerTable;
            this.working = working;
            this.version = version;
            this.motd = motd;
            this.players = players;
            this.exception = exception;

            this.ValidateData();
        }

        private void ValidateData() {
            if (this.version == null) {
                this.version = "Unknown";
            }
            if (this.motd == null) {
                this.motd = "Unknown";
            }
            if (this.players == null) {
                this.players = "Unknown";
            }
            if (this.exception == null) {
                this.exception = "";
            }

            this.version = this.getPreventSQLInjection(this.version);
            this.motd = this.getPreventSQLInjection(this.motd);
            this.players = this.getPreventSQLInjection(this.players);
            this.exception = this.getPreventSQLInjection(this.exception);

            if (this.version.length() > 75) {
                this.version = this.version.substring(0, 70) + "...";
            }
            if (this.motd.length() > 75) {
                this.motd = this.motd.substring(0, 70) + "...";
            }
            if (this.exception.length() > 75) {
                this.exception = this.exception.substring(0, 70) + "...";
            }
        }

        private String getPreventSQLInjection(String input) {
            return input.replace("'", "&#39;").replace("\"", "&#34;").replace("\\", "&#92;").replace("/", "&#47;").replace("<", "&lt;").replace(">", "&gt;");
        }
    }
}
