package me.monmcgt.code.massiner;

import com.google.inject.internal.util.Lists;
import me.monmcgt.code.massiner.managers.ConfigManager;
import me.monmcgt.code.massiner.managers.DatabaseManager;
import me.monmcgt.code.massiner.managers.LoopManager;
import me.monmcgt.code.massiner.managers.ResultManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static Map<String, String> configs;

    public static boolean showReceivedData = false;
    public static boolean showUpdateLastServer = false;

    public static void main(String[] args) throws SQLException, InterruptedException {
        for (String arg : args) {
            if (arg.equals("--show-received-data")) {
                showReceivedData = true;
                System.out.println("Show received data: true");
            }
            if (arg.equals("--show-update-last-server")) {
                showUpdateLastServer = true;
                System.out.println("Show update last server: true");
            }
        }


        Main main = new Main();

        main.initConfig();

//        new DatabaseManager("localhost", "3306", "test1", "root", "").connect();
        main.initDatabase();
        new ResultManager();

        main.startLooping();
    }

    private void startLooping() throws InterruptedException {
        System.out.println("Starting looping...");

        Thread.sleep(5000);

        List<String> exceptions = Lists.newArrayList(
                "0.0.0.0", "127.0.0.1"
        );

        List<String> superExceptions = Lists.newArrayList(
                "192.168."
        );

        LoopManager loopManager = new LoopManager(Integer.parseInt(configs.get(ConfigManager.ConfigData.THREADS.getName())), exceptions, superExceptions);
        loopManager.init();
//        loopManager.setStartAddress(127, 0, 0, 0, 65500);
//        loopManager.setStartAddress(192, 168, 0, 0, 65500);
//        loopManager.setStartAddress(192, 167, 255, 255, 65500);
//        loopManager.setStartAddress(139, 99, 9, 156, 65500);

        try {
            int[] lastServer = this.getLastServer();
            if (lastServer != null) {
                loopManager.setStartAddress(lastServer[0], lastServer[1], lastServer[2], lastServer[3], lastServer[4]);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        loopManager.initAddNewThread();
    }

    private int[] getLastServer() throws SQLException {
        String statement = String.format("SELECT * FROM `%s` WHERE `id` = '1'", configs.get(ConfigManager.ConfigData.TABLE_NAME_LAST_UPDATE.getName()));
        DatabaseManager.ExecuteQueryClass executeQueryClass = DatabaseManager.instance.createExecuteQueryClass(statement);
        ResultSet resultSet = executeQueryClass.resultSet;
        if (resultSet.next()) {
            String address = resultSet.getString("address");

            String[] split = address.split(":");
            String ip = split[0];
            int port = Integer.parseInt(split[1]);

            String[] ipSplit = ip.split("\\.");
            int ip1 = Integer.parseInt(ipSplit[0]);
            int ip2 = Integer.parseInt(ipSplit[1]);
            int ip3 = Integer.parseInt(ipSplit[2]);
            int ip4 = Integer.parseInt(ipSplit[3]);

            System.out.println("Last server: " + ip + ":" + port);

            return new int[] {ip1, ip2, ip3, ip4, port};
        }

        return null;
    }

    private void initConfig() {
        Map<String, String> config = new ConfigManager().getConfig();

        try {
            configs = new HashMap<>();

            configs.put(ConfigManager.ConfigData.THREADS.getName(), config.get(ConfigManager.ConfigData.THREADS.getName()));
            configs.put(ConfigManager.ConfigData.TIMEOUT.getName(), config.get(ConfigManager.ConfigData.TIMEOUT.getName()));
            configs.put(ConfigManager.ConfigData.DATABASE_HOST.getName(), config.get(ConfigManager.ConfigData.DATABASE_HOST.getName()));
            configs.put(ConfigManager.ConfigData.DATABASE_PORT.getName(), config.get(ConfigManager.ConfigData.DATABASE_PORT.getName()));
            configs.put(ConfigManager.ConfigData.DATABASE_USERNAME.getName(), config.get(ConfigManager.ConfigData.DATABASE_USERNAME.getName()));
            configs.put(ConfigManager.ConfigData.DATABASE_PASSWORD.getName(), config.get(ConfigManager.ConfigData.DATABASE_PASSWORD.getName()));
            configs.put(ConfigManager.ConfigData.DATABASE_NAME.getName(), config.get(ConfigManager.ConfigData.DATABASE_NAME.getName()));
            configs.put(ConfigManager.ConfigData.TABLE_NAME_ONLINE_SERVERS.getName(), config.get(ConfigManager.ConfigData.TABLE_NAME_ONLINE_SERVERS.getName()));
            configs.put(ConfigManager.ConfigData.TABLE_NAME_MIXED_SERVERS.getName(), config.get(ConfigManager.ConfigData.TABLE_NAME_MIXED_SERVERS.getName()));
            configs.put(ConfigManager.ConfigData.TABLE_NAME_LAST_UPDATE.getName(), config.get(ConfigManager.ConfigData.TABLE_NAME_LAST_UPDATE.getName()));
            configs.put(ConfigManager.ConfigData.UPDATE_LAST_SERVER_BY_RANDOM.getName(), config.get(ConfigManager.ConfigData.UPDATE_LAST_SERVER_BY_RANDOM.getName()));
            configs.put(ConfigManager.ConfigData.MODE_UPDATE_LAST_SERVER.getName(), config.get(ConfigManager.ConfigData.MODE_UPDATE_LAST_SERVER.getName()));
            configs.put(ConfigManager.ConfigData.MODE_UPDATE_LAST_SERVER_SERVER_PER_UPDATE.getName(), config.get(ConfigManager.ConfigData.MODE_UPDATE_LAST_SERVER_SERVER_PER_UPDATE.getName()));
            configs.put(ConfigManager.ConfigData.ADDRESS_ITERATOR_PORT_RANGE.getName(), config.get(ConfigManager.ConfigData.ADDRESS_ITERATOR_PORT_RANGE.getName()));

            /*configs.put(ConfigManager.ConfigData.ASCIIART.getName(), Boolean.parseBoolean(config.get(ConfigManager.ConfigData.ASCIIART.getName())) ? "true" : "false");
            configs.put(ConfigManager.ConfigData.CREDIT.getName(), Boolean.parseBoolean(config.get(ConfigManager.ConfigData.CREDIT.getName())) ? "true" : "false");
            configs.put(ConfigManager.ConfigData.SERVER_ONLINE.getName(), Boolean.parseBoolean(config.get(ConfigManager.ConfigData.SERVER_ONLINE.getName())) ? "true" : "false");
            configs.put(ConfigManager.ConfigData.SERVER_OFFLINE.getName(), Boolean.parseBoolean(config.get(ConfigManager.ConfigData.SERVER_OFFLINE.getName())) ? "true" : "false");
            configs.put(ConfigManager.ConfigData.SERVER_MIXED.getName(), Boolean.parseBoolean(config.get(ConfigManager.ConfigData.SERVER_MIXED.getName())) ? "true" : "false");*/
        } catch (Exception e) {
            System.err.println("Failed to load configs (config might be corrupted or invalid)");
//            this.logger.fatal("Failed to load configs (config might be corrupted or invalid)");
        }

        System.out.println("Configs loaded: ");
        for (Map.Entry<String, String> entry : configs.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    private void initDatabase() throws SQLException {
        System.out.println("Connecting to database...");

        String host = configs.get(ConfigManager.ConfigData.DATABASE_HOST.getName());
        String port = configs.get(ConfigManager.ConfigData.DATABASE_PORT.getName());
        String username = configs.get(ConfigManager.ConfigData.DATABASE_USERNAME.getName());
        String password = configs.get(ConfigManager.ConfigData.DATABASE_PASSWORD.getName());
        String database = configs.get(ConfigManager.ConfigData.DATABASE_NAME.getName());

        new DatabaseManager(host, port, database, username, password).connect();
    }
}
