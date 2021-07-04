package org.subspark;


import static spark.Spark.*;

public class SparkSample {
    public static void main(String[] args) {
        init();
        get("/hello", (req, res) -> "Hello World!");
    }
}
