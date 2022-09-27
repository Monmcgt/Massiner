package me.monmcgt.code.massiner.managers;

import me.monmcgt.code.massiner.Main;
import me.monmcgt.code.massiner.checkers.ServerChecker;
import me.monmcgt.code.massiner.utilities.AddressIterator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoopManager {
    public static LoopManager instance;

    private List<String> exceptionList;
    private List<String> superExceptionList;

    private final int numThreads;

    private AddressIterator addressIterator;

    private ExecutorService executorService;

    private boolean addNewThreadWhenAvailable;

    public LoopManager(int numThreads) {
        this(numThreads, new ArrayList<>(), new ArrayList<>());
    }

    public LoopManager(int numThreads, List<String> exception, List<String> superException) {
        instance = this;

        this.numThreads = numThreads;

        this.exceptionList = exception;
        this.superExceptionList = superException;

        this.addressIterator = new AddressIterator();

        this.addNewThreadWhenAvailable = false;
    }

    public void init() {
        this.executorService = Executors.newFixedThreadPool(this.numThreads);
    }

    public CheckExceptionData checkExceptions(String address) {
        for (String superException : this.superExceptionList) {
            if (address.startsWith(superException)) {
                System.out.println("Address: " + address + " is a super exception.");

                CheckExceptionData checkExceptionData = new CheckExceptionData(null, -1, null, -1);
                checkExceptionData.superExceptionAddress = superException;
                return checkExceptionData;
            } else {
//                System.out.println("Address: " + address + " does not start with: " + superException);
            }
        }

        for (String exception : this.exceptionList) {
            String[] exAddress = exception.split(":");
            String exIp = exAddress[0];
            int exPort = Integer.parseInt(exAddress.length < 2 || exAddress[1].isEmpty() ? "0" : exAddress[1]);
            String[] exIpSplit = exIp.split("\\.");
            String e_ip_1 = exIpSplit[0];
            String e_ip_2 = exIpSplit[1];
            String e_ip_3 = exIpSplit[2];
            String e_ip_4 = exIpSplit[3];

            String[] addressSplit = address.split(":");
            String ip = addressSplit[0];
            int port = Integer.parseInt(addressSplit[1]);
            String[] ipSplit = ip.split("\\.");
            String ip_1 = ipSplit[0];
            String ip_2 = ipSplit[1];
            String ip_3 = ipSplit[2];
            String ip_4 = ipSplit[3];

            if (ip_1.equals("*") || ip_1.equals(e_ip_1)) {
                if (ip_2.equals("*") || ip_2.equals(e_ip_2)) {
                    if (ip_3.equals("*") || ip_3.equals(e_ip_3)) {
                        if (ip_4.equals("*") || ip_4.equals(e_ip_4)) {
                            if (exPort == 0 || exPort == port) {
//                                System.out.println("exception: " + e_ip_1 + "." + e_ip_2 + "." + e_ip_3 + "." + e_ip_4 + ":" + exPort + " |  ip: " + ip_1 + "." + ip_2 + "." + ip_3 + "." + ip_4 + ":" + port);

                                return new CheckExceptionData(new String[] {e_ip_1, e_ip_2, e_ip_3, e_ip_4}, exPort, ip, port);
                            } else {
//                                System.out.println("exPort: " + exPort + " port: " + port);
                            }
                        }
                    }
                }
            }
        }

//        System.out.println("[LoopManager] Exception: " + address + " is not in the exception list.");

        return new CheckExceptionData(null, 0, null, 0);
    }

    public void initAddNewThread() {
        List<ServerChecker> serverCheckerList = new ArrayList<>();

        for (int i = 0; i < this.numThreads; i++) {
            String address = this.getNextAddress();
            String[] splitAddress = this.splitAddress(address);

            if (!address.isEmpty()) {
                serverCheckerList.add(new ServerChecker(splitAddress[0], Integer.parseInt(splitAddress[1]), Integer.parseInt(Main.configs.get(ConfigManager.ConfigData.TIMEOUT.getName()))));
            }
        }

        this.addNewThreadWhenAvailable = true;

        serverCheckerList.forEach((this::addNewThread));
    }

    public void setStartAddress(int _1, int _2, int _3, int _4, int port) {
        this.addressIterator = new AddressIterator(_1, _2, _3, _4, port);
    }

    public synchronized void addNewThread(Thread thread) {
        this.executorService.submit(thread);
    }

    public synchronized void addNewThread(Runnable runnable) {
        this.executorService.submit(runnable);
    }

    public synchronized String getNextAddress() {
        return this.getNextAddress(true);
    }

    public synchronized String getNextAddress(boolean nextPort) {

        if (this.addressIterator.hasNext()) {
            try {
                String address = this.addressIterator.getAddress();

                CheckExceptionData exceptionData = this.checkExceptions(address);

                if (exceptionData.exceptionPort == -1) {
//                    this.addressIterator.increaseWholeSecond();
                    String[] superException = exceptionData.superExceptionAddress.split("\\.");
                    short count = 0;
                    for (String exAddress : superException) {
                        if (!exAddress.isEmpty()) {
                            count++;
                        }
                    }

                    switch (count) {
                        case 1 :
                            this.addressIterator.increaseWholeFirst();
                            break;
                        case 2 :
                            this.addressIterator.increaseWholeSecond();
                            break;
                        case 3 :
                            this.addressIterator.increaseWholeThird();
                            break;
                        case 4 :
                            this.addressIterator.increaseWholeFourth();
                            break;
                    }

                    return this.getNextAddress(false);
                }

                if (exceptionData.exceptionIp == null) {
                    return address;
                } else {
                    /*if (exceptionData.exceptionIp[2].equals("*")) {
                        this.addressIterator.increaseWholeThird();
                        this.addressIterator.setSecond(0);
                        this.addressIterator.setFirst(0);
                        this.addressIterator.setPort(1);
                    } */

                    if (exceptionData.exceptionPort == 0) {
                        this.addressIterator.increaseWholePort();

                        return this.getNextAddress(false);
                    }

                    return this.getNextAddress();
                }
            } finally {
                if (nextPort) {
                    this.addressIterator.next();
                }
            }
        }

        return "";
    }

    public synchronized void finishedThread() {
        if (this.addNewThreadWhenAvailable) {
            String address = this.getNextAddress();
            String[] splitAddress = this.splitAddress(address);

            if (!address.isEmpty()) {
                this.addNewThread(new ServerChecker(splitAddress[0], Integer.parseInt(splitAddress[1]), Integer.parseInt(Main.configs.get(ConfigManager.ConfigData.TIMEOUT.getName()))));
            }
        }
    }

    public String[] splitAddress(String address) {
        return address.split(":");
    }

    private static class CheckExceptionData {
        public String[] exceptionIp;
        public int exceptionPort;

        public String foundIp;
        public int foundPort;

        public String superExceptionAddress;

        public CheckExceptionData(String[] exceptionIp, int exceptionPort, String foundIp, int foundPort) {
            this.exceptionIp = exceptionIp;
            this.exceptionPort = exceptionPort;
            this.foundIp = foundIp;
            this.foundPort = foundPort;
        }
    }
}
