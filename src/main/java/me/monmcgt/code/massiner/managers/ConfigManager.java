package me.monmcgt.code.massiner.managers;

import com.google.inject.internal.util.Lists;
import me.monmcgt.code.massiner.utilities.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {
    private final String FILE_NAME = "config.txt";

    private final File file;

    private final FileUtil fileUtil;

    public ConfigManager() {
        this.file = new File(FILE_NAME);

        this.fileUtil = new FileUtil();

        this.checkConfigFile();
    }

    public void checkConfigFile() {
        try {
            if (!this.file.exists()) {
//                Utilities.printLine();

                System.out.println("Config file not found, creating new one ...");
//                this.logger.info("Config file not found, creating new one ...");
                /*if (!this.file.getParentFile().mkdirs()) {
                    this.logger.fatal("Failed to create config directory!");

                    Main.noOutput = true;

                    System.exit(1);
                }*/
                if (!this.file.createNewFile()) {
                    System.err.println("Failed to create config file!");
//                    this.logger.fatal("Failed to create config file!");

//                    Utilities.printLine();

//                    Main.noOutput = true;

                    System.exit(1);
                }

                List<String> lines = new ArrayList<>();
                List<ConfigData> configDataList = this.createConfigDataEnum();

                lines.add("# This is the config file for the Massiner Project.");
                lines.add("");

                for (ConfigData configData : configDataList) {
                    lines.add(configData.getName() + "=" + configData.getValue());
                }

                this.fileUtil.writeFile(this.file, lines.toArray(new String[0]));

                System.out.println("Config file created! (File: " + this.file.getAbsolutePath() + ")");
//                this.logger.info("Config file created! (File: " + this.file.getAbsolutePath() + ")");

//                Main.noOutput = true;

                System.out.println("Please edit the config file and rerun the program!");
//                this.logger.info("Please edit the config file and rerun the program!");

//                Utilities.printLine();

                System.exit(0);
            }
        } catch (IOException e) {
            System.err.println("Failed to check config file! (File: " + this.file.getAbsolutePath() + ")");
//            this.logger.fatal("Failed to check config file! (File: " + this.file.getAbsolutePath() + ")");
        }
    }

    public Map<String, String> getConfig() {
        Map<String, String> config = new HashMap<>();

        List<ConfigData> configDataList = this.createConfigDataEnum();

        try {
            String[] lines = this.fileUtil.getLines(this.file);

            for (String line : lines) {
                if (line.startsWith("#")) {
                    continue;
                }

                line = line.trim();

                String[] split = line.split("=");

                String key = split[0];
                String value = null;
                if (split.length > 1) {
                    value = split[1];
                } else {
                    value = "";
                }

                for (ConfigData configData : configDataList) {
                    if (configData.getName().equals(key)) {
                        configData.setValue(value);
                    }
                }
            }

            for (ConfigData configData : configDataList) {
                config.put(configData.getName(), configData.getValue());
            }
        } catch (IOException e) {
//            Utilities.printLine();
            System.err.println("Failed to read config file! (File: " + this.file.getAbsolutePath() + ")");
//            this.logger.fatal("Failed to read config file! (File: " + this.file.getAbsolutePath() + ")");
        }

        return config;
    }

    private List<ConfigData> createConfigDataEnum() {
        ConfigData
                Threads = ConfigData.THREADS,
                Timeout = ConfigData.TIMEOUT,
                Database_Host = ConfigData.DATABASE_HOST,
                Database_Port = ConfigData.DATABASE_PORT,
                Database_Username = ConfigData.DATABASE_USERNAME,
                Database_Password = ConfigData.DATABASE_PASSWORD,
                Database_Name = ConfigData.DATABASE_NAME,
                Table_Name_Online_Servers = ConfigData.TABLE_NAME_ONLINE_SERVERS,
                Table_Name_Mixed_Servers = ConfigData.TABLE_NAME_MIXED_SERVERS,
                Table_Name_Last_Update = ConfigData.TABLE_NAME_LAST_UPDATE,
                Update_Last_Server_By_Random = ConfigData.UPDATE_LAST_SERVER_BY_RANDOM,
                Mode_Update_Last_Server = ConfigData.MODE_UPDATE_LAST_SERVER,
                Mode_Update_Last_Server_Server_Per_Update = ConfigData.MODE_UPDATE_LAST_SERVER_SERVER_PER_UPDATE,
                Address_Iterator_Port_Range = ConfigData.ADDRESS_ITERATOR_PORT_RANGE;

        List<ConfigData> configDataList = Lists.newArrayList(Threads, Timeout, Database_Host, Database_Port, Database_Username, Database_Password, Database_Name, Table_Name_Online_Servers, Table_Name_Mixed_Servers, Table_Name_Last_Update, Update_Last_Server_By_Random, Mode_Update_Last_Server, Mode_Update_Last_Server_Server_Per_Update, Address_Iterator_Port_Range);

        return configDataList;
    }

    public File getFile() {
        return this.file;
    }

    public static enum ConfigData {
        THREADS("threads", "300"),
        TIMEOUT("timeout", "4000"),

        DATABASE_HOST("database_host", "localhost"),
        DATABASE_PORT("database_port", "3306"),
        DATABASE_USERNAME("database_username", "root"),
        DATABASE_PASSWORD("database_password", "password"),
        DATABASE_NAME("database_name", "massiner"),

        TABLE_NAME_ONLINE_SERVERS("table_name_online_servers", "online_servers"),
        TABLE_NAME_MIXED_SERVERS("table_name_mixed_servers", "mixed_servers"),

        TABLE_NAME_LAST_UPDATE("table_name_last_update", "last_update"),

        UPDATE_LAST_SERVER_BY_RANDOM("update_last_server_by_random", "true"),
        MODE_UPDATE_LAST_SERVER("mode_update_last_server", "1"),
        MODE_UPDATE_LAST_SERVER_SERVER_PER_UPDATE("mode_update_last_server_server_per_update", "300"),

        ADDRESS_ITERATOR_PORT_RANGE("address_iterator_port_range", "1-65535"),

        ;

        private final String name;
        private String value;

        ConfigData(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return this.name;
        }

        public String getValue() {
            return this.value.toString();
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
