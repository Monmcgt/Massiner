package me.monmcgt.code.massiner.utilities;

import me.monmcgt.code.massiner.Main;
import me.monmcgt.code.massiner.managers.ConfigManager;

import java.util.List;

public class AddressIterator {
    private final boolean LEGACY_METHOD = false;
    private final List<Integer> PortRange;

    private int first;
    private int second;
    private int third;
    private int fourth;
    private int port;

    public AddressIterator() {
        this(0, 0, 0, 0, 1);
    }

    public AddressIterator(int first, int second, int third, int fourth, int port) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
        this.port = port;

        if (this.LEGACY_METHOD) {
            this.PortRange = null;
        } else {
            this.PortRange = Utilities.rangeToList(Main.configs.get(ConfigManager.ConfigData.ADDRESS_ITERATOR_PORT_RANGE.getName()));

            System.out.print("Port Range: ");
            for (int i = 0; i < this.PortRange.size(); i++) {
                System.out.print(this.PortRange.get(i) + " ");
            }
            System.out.println();
        }
    }

    public synchronized String getAddress() {
        return String.format("%d.%d.%d.%d:%d", first, second, third, fourth, port);
    }

    public synchronized boolean hasNext() {
        return this.port != 65535 || this.first != 255;
    }

    public synchronized void next() {
//        System.out.print("Port: " + this.port);
        this.increasePort();
//        System.out.println(" -> " + this.port);
    }

    public synchronized void next(long amount) {
        if (amount > 0) {
            for (long i = 0; i < amount; i++) {
                this.next();
            }
        }
    }

    public synchronized void previous() {
//        System.out.print("Address: " + this.getAddress());

        this.setPort(this.port - 1);

        if (this.port == 0) {
            this.setFourth(this.fourth - 1);
            this.setPort(65535);
        }

        if (this.fourth == -1) {
            this.setThird(this.third - 1);
            this.setFourth(255);
        }

        if (this.third == -1) {
            this.setSecond(this.second - 1);
            this.setThird(255);
        }

        if (this.second == -1) {
            this.setFirst(this.first - 1);
            this.setSecond(255);
        }

//        System.out.println(" -> " + this.getAddress());
    }

    public synchronized void increaseWholeFirst() {
        do {
            this.increaseFirst();
        } while (this.first != 0);

        this.setPort(1);
        this.setFourth(0);
        this.setThird(0);
        this.setSecond(0);
    }

    public synchronized void increaseWholeSecond() {
        do {
            this.increaseSecond();
        } while (this.second != 0);

        this.setPort(1);
        this.setFourth(0);
        this.setThird(0);
    }

    public synchronized void increaseWholeThird() {
        do {
            this.increaseThird();
        } while (this.third != 0);

        this.setPort(1);
        this.setFourth(0);
    }

    public synchronized void increaseWholeFourth() {
        do {
            this.increaseFourth();
        } while (this.fourth != 0);

        this.setPort(1);
    }

    public synchronized void increaseWholePort() {
        do {
            this.increasePort();
        } while (this.port != 1);
    }

    private synchronized void increasePort() {
//        System.out.print("Address: " + this.getAddress());
        if (this.LEGACY_METHOD) {
            if (this.port == 65535) {
                this.setPort(1);
                this.increaseFourth();
            } else {
                this.setPort(this.port + 1);
            }
        } else {
            if (this.port == this.PortRange.get(this.PortRange.size() - 1)) {
                this.setPort(this.PortRange.get(0));
                this.increaseFourth();
            } else {
                this.setPort(this.PortRange.get(this.PortRange.indexOf(this.port) + 1));
            }
        }
//        System.out.println(" -> " + this.getAddress());
    }

    private synchronized void increaseFourth() {
        if (this.fourth == 255) {
            this.setFourth(0);
            this.increaseThird();
        } else {
            this.setFourth(this.fourth + 1);
        }
    }

    private synchronized void increaseThird() {
        if (this.third == 255) {
            this.setThird(0);
            this.increaseSecond();
        } else {
            this.setThird(this.third + 1);
        }
    }

    private synchronized void increaseSecond() {
        if (this.second == 255) {
            this.setSecond(0);
            this.increaseFirst();
        } else {
            this.setSecond(this.second + 1);
        }
    }

    private synchronized void increaseFirst() {
        if (this.first < 255) {
            this.setFirst(this.first + 1);
        }
    }

    public synchronized void setFirst(int first) {
        this.first = first;
    }

    public synchronized void setSecond(int second) {
        this.second = second;
    }

    public synchronized void setThird(int third) {
        this.third = third;
    }

    public synchronized void setFourth(int fourth) {
        this.fourth = fourth;
    }

    public synchronized void setPort(int port) {
//        System.out.print("Address: " + this.getAddress());
        this.port = port;
//        System.out.println(" -> " + this.getAddress());
    }
}
