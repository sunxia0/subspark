import static org.subspark.SubSpark.*;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public class WebServer {
    final static Logger logger = LogManager.getLogger(WebServer.class);

    public static void main(String[] args) {
        org.apache.logging.log4j.core.config.Configurator.setLevel("org.subspark", Level.DEBUG);

        post("/say/*/to/*/:k1/:k2", (req, res) -> {
            System.out.println("In post /say/*/to/*/:k1/:k2");

            System.out.println("Params:");
            Map<String, String> namedParams = req.namedParams();
            for (Map.Entry<String, String> e : namedParams.entrySet()) {
                System.out.println(e.getKey() + " -> " + e.getValue());
            }

            System.out.println("Wildcards");
            List<String> wildcards = req.wildcards();
            for (String s : wildcards) {
                System.out.println(s);
            }

            return "reach /say/*/to/*/:k1/:k2";
        });

        init();
    }
}
