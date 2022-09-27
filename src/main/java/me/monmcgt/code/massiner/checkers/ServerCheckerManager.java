package me.monmcgt.code.massiner.checkers;

/*import me.monmcgt.code.mcchecker.Main;
import me.monmcgt.code.mcchecker.utils.Utilities;
import org.apache.logging.log4j.core.Logger;*/

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ServerCheckerManager {
    public static ServerCheckerManager INSTANCE;

    private ExecutorService executorService;

    private final List<String[]> servers;
    private final short thread;
    private final int timeout;

    private final int serverCount;
    private long serverLeft;

    public static List<ServerChecker> runningThreads = new ArrayList<>();

//    private final Logger logger = Main.INSTANCE.getLogger();

    public ServerCheckerManager(List<String[]> servers, short thread, int timeout) {
        this.servers = servers;
        this.thread = thread;
        this.timeout = timeout;

        this.serverCount = servers.size();
        this.serverLeft = this.serverCount;

        INSTANCE = this;
    }

    public void start() {
        this.executorService = Executors.newFixedThreadPool(this.thread);

        for (String[] server : this.servers) {
            String ip = server[0];
            int port = Integer.parseInt(server[1]);

            /*if (Main.debug) {
//                System.out.println("[ServerChecker] Starting server checker for " + server + ":" + port);
                this.logger.debug("[ServerChecker] Starting server checker for " + ip + ":" + port);
            }*/

            executorService.submit(new ServerChecker(ip, port, this.timeout));
        }
    }

    public void waitThread() {
        try {
            this.executorService.shutdown();
            if (!this.executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS)) {
//                System.err.println("[ServerChecker] Threads did not terminate");
//                this.logger.error("[ServerChecker] Threads did not terminate");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /*public synchronized void add(ServerChecker serverChecker) {
        Utilities.addRunningThread(serverChecker);
    }*/

    public synchronized void remove(ServerChecker serverChecker) {
//        Utilities.removeRunningThread(serverChecker);

        this.removeLeftServer();
    }

    public synchronized void removeLeftServer() {
        this.serverLeft--;
    }

    public long getServerLeft() {
        return this.serverLeft;
    }
}
