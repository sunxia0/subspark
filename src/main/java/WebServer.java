import static org.subspark.SubSpark.*;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.subspark.http.Status;

public class WebServer {
    final static Logger logger = LogManager.getLogger(WebServer.class);

    public static void main(String[] args) {
        org.apache.logging.log4j.core.config.Configurator.setLevel("org.subspark", Level.DEBUG);

        get("/test1", (req, res) -> {
            res.redirect("/test2", Status.MOVED_PERMANENTLY);
            return null;
        });

        get("/test2", (req, res) -> {
            return "post /test2";
        });

        init();
    }
}
