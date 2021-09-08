import static org.subspark.SubSpark.*;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WebServer {
    final static Logger logger = LogManager.getLogger(WebServer.class);

    public static void main(String[] args) {
        org.apache.logging.log4j.core.config.Configurator.setLevel("org.subspark", Level.DEBUG);

        before("/test", (req, res) -> System.out.println("In before /test"));
        get("/test", (req, res) -> {
            System.out.println("In get /test");
            return "Hello,World";
        });
        after("/test", (req, res) -> System.out.println("In after /test"));
    }
}
