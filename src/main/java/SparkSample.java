import static spark.Spark.*;

public class SparkSample {
    public static void main(String[] args) {
        port(8080);

        init();

        before("/test", (req, res) -> System.out.println("In before"));
//        head("/test", (req, res) -> {
//            System.out.println("In head /test");
//            return "123";
//        });
        get("/test", (req, res) -> {
            System.out.println("In get /test");
            return "123";
        });
//        post("/test", (req, res) -> {
//            System.out.println("In post /test");
//            return "123";
//        });
        after("/test", (req, res) -> System.out.println("In after"));
    }
}
