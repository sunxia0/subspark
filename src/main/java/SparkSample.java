import static spark.Spark.*;

public class SparkSample {
    public static void main(String[] args) {
        port(8080);

        init();

        before("/test", (req, res) -> {
            throw new RuntimeException("Runtime Exception in before");
        });

        get("/test" , (req, res) -> {
            return "Trig get /test";
        });

        post("/say/hello/to/you/*", (req, res) -> {
            return "reach /say/hello/to/you/*";
        });

        post("/say/:key1/to/:key2", (req, res) -> {
            return "reach /say/:key1/to/:key2";
        });

        post("/say/*", (req, res) -> {
            System.out.println("In post /say/*");
            System.out.println("Splat: ");
            for (String s : req.splat()) {
                System.out.println(s);
            }
            return "reach /say/*";
        });
    }
}
