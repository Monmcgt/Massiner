package me.monmcgt.code.massiner.checkers;

import me.monmcgt.code.massiner.checkers.checking.MCPing;
import me.monmcgt.code.massiner.checkers.checking.PingOptions;
import me.monmcgt.code.massiner.checkers.checking.Results;
import me.monmcgt.code.massiner.checkers.checking.data.FinalResponse;
import me.monmcgt.code.massiner.managers.LoopManager;
import me.monmcgt.code.massiner.managers.ResultManager;
import me.monmcgt.code.massiner.utilities.Time;

import java.io.IOException;

public class ServerChecker implements Runnable {
    private final String ip;
    private final int port;
    private final int timeout;

    private boolean working;

    private IOException ioException;

//    private final Logger logger = Main.INSTANCE.getLogger();

    public ServerChecker(String ip, int port, int timeout) {
        this.ip = ip;
        this.port = port;
        this.timeout = timeout;
    }

    @Override
    public void run() {
        try {
//            ServerCheckerManager.INSTANCE.add(this);

//            Thread.currentThread().setName(ip + ":" + port);

            MCPing mcPing = new MCPing();

            PingOptions pingOptions = new PingOptions(this.ip, this.port, timeout);

            /*if (Main.debug) {
//                System.out.println("[ServerChecker] Checking " + this.ip + ":" + this.port + " ...");
                this.logger.debug("[ServerChecker] Checking " + this.ip + ":" + this.port + " ...");
            }*/

            FinalResponse finalResponse = null;

            try {
                finalResponse = mcPing.getPing(pingOptions);

                /*if (Main.debug || Main.old_debug) {
                    *//*System.out.println("\033[0;91m" +
                            this.ip + ":" + this.port +
                            " \033[0m>> Version: \033[0;91m" +
                            finalResponse.getVersion().getName() +
                            " \033[0m||\033[0m Players: \033[0;91m" +
                            finalResponse.getPlayers().getOnline() + "/" + finalResponse.getPlayers().getMax() +
                            " \033[0m||\033[0m Description: \033[0;91m" +
                            CleanDescription.clean(finalResponse.getDescription()) + "\033[0m");*//*

                    this.logger.debug("\033[0;91m" +
                            this.ip + ":" + this.port +
                            " \033[0m>> Version: \033[0;91m" +
                            finalResponse.getVersion().getName() +
                            " \033[0m||\033[0m Players: \033[0;91m" +
                            finalResponse.getPlayers().getOnline() + "/" + finalResponse.getPlayers().getMax() +
                            " \033[0m||\033[0m Description: \033[0;91m" +
                            CleanDescription.clean(finalResponse.getDescription()) + "\033[0m");
                }*/

                this.working = true;
            } catch (IOException e) {
                this.ioException = e;
            }

            Results result;

            if (this.ioException == null) {
                this.ioException = new IOException("Unknown error.");
            }

            if (this.working && finalResponse != null) {
                if (this.ip == null || this.port == 0 || finalResponse.getVersion() == null || finalResponse.getFavicon() == null || finalResponse.getDescription() == null || finalResponse.getPlayers() == null) {
//                    System.err.println("[ServerChecker] Error: " + this.ip + ":" + this.port + " caused an error.");

                    return;
                }

                result = new Results(this.ip + ":" + this.port, this.ip, this.port, finalResponse.getVersion().getName(), finalResponse.getFavicon(), finalResponse.getDescription(), finalResponse.getPlayers());
            } else {
                result = new Results(this.ip + ":" + this.port, this.ip, this.port, this.ioException);
            }

            // current time: year month day hour minute second
            result.setTime(Time.now());

            /*result.setDate(new String[] {year, month, day});
            result.setTime(new String[] {hour, minute, second});*/

//            Main.addResult(result);
            ResultManager.INSTANCE.receiveResult(result);

            /*if (Main.debug) {
//                System.out.println("[ServerChecker] Finished checking " + this.ip + ":" + this.port + ".");
                this.logger.debug("[ServerChecker] Finished checking " + this.ip + ":" + this.port + ".");
            }*/
        } finally {
//            ServerCheckerManager.INSTANCE.remove(this);
            LoopManager.instance.finishedThread();

            // destroy this thread (current thread) to remove from memory
            this.closeThread();
        }
    }

    public void closeThread() {
        try {
            Thread.currentThread().interrupt();
            Thread.currentThread().destroy();
        } catch (Exception e) {
//            System.err.println("[ServerChecker] Error: " + e.getMessage());
//            this.logger.error("[ServerChecker] Error: " + e.getMessage());
        } finally {
//            ServerCheckerManager.INSTANCE.remove(this);
        }
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public int getTimeout() {
        return timeout;
    }

    public boolean isWorking() {
        return working;
    }

    public IOException getIoException() {
        return ioException;
    }
}
