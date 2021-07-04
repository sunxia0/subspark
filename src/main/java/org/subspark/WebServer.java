package org.subspark;

import static org.subspark.SubSpark.*;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WebServer {
    final static Logger logger = LogManager.getLogger(WebServer.class);

    public static void main(String[] args) {
        org.apache.logging.log4j.core.config.Configurator.setLevel("org.subspark", Level.DEBUG);

//        int port = 45555;
//        String directory = "/Users/xiao/Codes/Java/subspark/static";

        if (args.length == 2) {
            int port = Integer.parseInt(args[0]);
            String directory = args[1];

            System.out.println(port);
            System.out.println(directory);

            staticFileLocation(directory);
            port(port);

            // All user routes should go below here...

            // ... and above here. Leave this comment for the Spark comparator tool

            System.out.println("Waiting to handle requests!");
            awaitInitialization();
        }
        else {
            System.out.println("Wrong args!");
        }
    }
}
