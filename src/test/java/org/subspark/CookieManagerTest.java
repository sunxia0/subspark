package org.subspark;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CookieManagerTest {
    @Test
    public void basicTest() {
        CookieManager manager = new CookieManager();
        manager.cookie("id", "01", -1, false);
        Assert.assertEquals("set-cookie: id=01\r\n", manager.toSetCookieString());
    }

    @Test
    public void maxAgeTest() {
        CookieManager manager = new CookieManager();
        manager.cookie("id", "02", 3600, false);
        Assert.assertEquals("set-cookie: id=02; Max-Age=3600\r\n", manager.toSetCookieString());
    }

    @Test
    public void httpOnlyTest() {
        CookieManager manager = new CookieManager();
        manager.cookie("id", "03", 3600, true);
        Assert.assertEquals("set-cookie: id=03; Max-Age=3600; HttpOnly\r\n", manager.toSetCookieString());
    }

    @Test
    public void pathTest() {
        CookieManager manager = new CookieManager();
        manager.cookie("/path", "id", "04", 3600, true);
        Assert.assertEquals("set-cookie: id=04; Path=/path; Max-Age=3600; HttpOnly\r\n", manager.toSetCookieString());
    }

    @Test
    public void rootPathTest() {
        CookieManager manager = new CookieManager();
        manager.cookie("/", "id", "05", 3600, true);
        manager.cookie("id", "05", 7200, true);

        // Only one `set-cookie` exists
        Assert.assertEquals("set-cookie: id=05; Max-Age=7200; HttpOnly\r\n", manager.toSetCookieString());
    }

    @Test
    public void multipleCookiesTest() {
        CookieManager manager = new CookieManager();
        manager.cookie("student1", "Tom", -1, false);
        manager.cookie("student2", "Jerry", 3600, false);
        manager.cookie("student3", "Alice", -1, true);
        manager.cookie("/path1", "student1", "Jim", -1, false);
        manager.cookie("/path2", "student2", "Lily", 3600, true);

        String expected1 = "set-cookie: student1=Tom";
        String expected2 = "set-cookie: student2=Jerry; Max-Age=3600";
        String expected3 = "set-cookie: student3=Alice; HttpOnly";
        String expected4 = "set-cookie: student1=Jim; Path=/path1";
        String expected5 = "set-cookie: student2=Lily; Path=/path2; Max-Age=3600; HttpOnly";

        String[] setCookieStrings = manager.toSetCookieString().split("\r\n");
        Set<String> stringSet = new HashSet<>(Arrays.asList(setCookieStrings));

        Assert.assertTrue(stringSet.contains(expected1));
        Assert.assertTrue(stringSet.contains(expected2));
        Assert.assertTrue(stringSet.contains(expected3));
        Assert.assertTrue(stringSet.contains(expected4));
        Assert.assertTrue(stringSet.contains(expected5));
    }
}
