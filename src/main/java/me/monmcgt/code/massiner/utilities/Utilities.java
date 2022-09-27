package me.monmcgt.code.massiner.utilities;

import java.util.ArrayList;
import java.util.List;

public class Utilities {
    public static void sortList(List<String> list) {
        list.sort((o1, o2) -> {
            String[] split1 = o1.split(":");
            String[] split2 = o2.split(":");

            String ip1 = split1[0];
            String ip2 = split2[0];

            String[] ipSplit1 = ip1.split("\\.");
            String[] ipSplit2 = ip2.split("\\.");

            int ip1_1 = Integer.parseInt(ipSplit1[0]);
            int ip1_2 = Integer.parseInt(ipSplit1[1]);
            int ip1_3 = Integer.parseInt(ipSplit1[2]);
            int ip1_4 = Integer.parseInt(ipSplit1[3]);
            int ip2_1 = Integer.parseInt(ipSplit2[0]);
            int ip2_2 = Integer.parseInt(ipSplit2[1]);
            int ip2_3 = Integer.parseInt(ipSplit2[2]);
            int ip2_4 = Integer.parseInt(ipSplit2[3]);

            String port1 = split1[1];
            String port2 = split2[1];

            if (ip1_1 > ip2_1) {
                return 1;
            } else if (ip1_1 < ip2_1) {
                return -1;
            } else {
                if (ip1_2 > ip2_2) {
                    return 1;
                } else if (ip1_2 < ip2_2) {
                    return -1;
                } else {
                    if (ip1_3 > ip2_3) {
                        return 1;
                    } else if (ip1_3 < ip2_3) {
                        return -1;
                    } else {
                        if (ip1_4 > ip2_4) {
                            return 1;
                        } else if (ip1_4 < ip2_4) {
                            return -1;
                        } else {
                            return Integer.compare(port1.compareTo(port2), 0);
                        }
                    }
                }
            }
        });
    }

    public static List<Integer> rangeToList(String range) {
        List<Integer> intRange = new ArrayList<>();

        String[] splitRange = range.replaceAll(" ", "").split(",");

        for (String s : splitRange) {
            if (s.contains("-")) {
                String[] splitRange2 = s.split("-");
                for (int i = Integer.parseInt(splitRange2[0]); i <= Integer.parseInt(splitRange2[1]); i++) {
                    intRange.add(i);
                }
            } else {
                intRange.add(Integer.parseInt(s));
            }
        }

        return intRange;
    }
}
