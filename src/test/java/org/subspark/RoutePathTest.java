package org.subspark;

import org.junit.Assert;
import org.junit.Test;
import org.subspark.utils.PathUtils;

public class RoutePathTest {
    @Test
    public void pathMatchingTest() {
        Assert.assertTrue(PathUtils.isPathMatch("/say/*/to/*", "/say/hello/to/you"));
        Assert.assertTrue(PathUtils.isPathMatch("/say/*", "/say/hello/to/you"));
        Assert.assertTrue(PathUtils.isPathMatch("/say/:what/to/:who", "/say/hello/to/you"));
        Assert.assertTrue(PathUtils.isPathMatch("/say/:what/*", "/say/hello/to/you"));
        Assert.assertTrue(PathUtils.isPathMatch("/say/hello/to/you/*", "/say/hello/to/you/"));

        Assert.assertFalse(PathUtils.isPathMatch("/say/hello/to/:who", "/say/hello/to/"));
        Assert.assertFalse(PathUtils.isPathMatch("/say/bye/to/you", "/say/hello/to/you"));
        Assert.assertFalse(PathUtils.isPathMatch("/say/hello/to/you/*", "/say/hello/to/you"));
        Assert.assertFalse(PathUtils.isPathMatch("/say/hello/to/:who", "/say/hello/to/you/hah"));
    }
}
