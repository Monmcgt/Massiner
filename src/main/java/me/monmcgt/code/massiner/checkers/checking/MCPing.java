package me.monmcgt.code.massiner.checkers.checking;


import com.google.gson.Gson;
import me.monmcgt.code.massiner.checkers.checking.data.*;

import java.io.IOException;
import java.net.InetSocketAddress;


public class MCPing {
//    private final Logger logger = Main.INSTANCE.getLogger();

    public FinalResponse getPing(PingOptions options) throws IOException {
        Gson gson = new Gson();
        Pinger a = new Pinger();
        a.setAddress(new InetSocketAddress(options.getHostname(), options.getPort()));
        a.setTimeout(options.getTimeout());
        String json = null;
        try {
            json = a.fetchData();
        } catch (IOException e) {
            /*if (Main.debug) {
//                System.err.println("Error: " + e.getMessage());
                this.logger.debug("Error: " + e.getMessage());
            }*/

            throw new IOException("Error: " + e.getMessage());
        }

        /*if (Main.debug) {
//            System.out.println("json = " + json);
            this.logger.debug("json = " + json);
        }*/

        if (json != null &&
                json.contains("{")) {

            if (json.contains("\"modid\"") && json.contains("\"translate\"")) {
                return ((ForgeResponseTranslate) gson.fromJson(json, ForgeResponseTranslate.class)).toFinalResponse();
            }
            if (json.contains("\"modid\"") && json.contains("\"text\"")) {
                return ((ForgeResponse) gson.fromJson(json, ForgeResponse.class)).toFinalResponse();
            }
            if (json.contains("\"modid\"")) {
                return ((ForgeResponseOld) gson.fromJson(json, ForgeResponseOld.class)).toFinalResponse();
            }
            if (json.contains("\"extra\"")) {
                return ((ExtraResponse) gson.fromJson(json, ExtraResponse.class)).toFinalResponse();
            }
            if (json.contains("\"text\"")) {
                return ((NewResponse) gson.fromJson(json, NewResponse.class)).toFinalResponse();
            }

            return ((OldResponse) gson.fromJson(json, OldResponse.class)).toFinalResponse();
        }

        return null;
    }
}


