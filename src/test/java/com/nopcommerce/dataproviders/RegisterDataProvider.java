package com.nopcommerce.dataproviders;

import org.testng.annotations.DataProvider;

public class RegisterDataProvider {

    @DataProvider(name = "registerData")
    public static Object[][] registerData() {

        String q = "Your eldest siblings middle name?"; // pregunta que exista en dropdown
        String a = "test";

        String uniqueEmail1 = "test" + System.currentTimeMillis() + "@mail.com";
        String uniqueEmail2 = "test" + (System.currentTimeMillis() + 1) + "@mail.com";

        return new Object[][]{
                {uniqueEmail1, "Test12345!", "Test12345!", q, "test", "SUCCESS"},
                {"", "", "", "", "", "REQUIRED"},                 // todos vacíos
                {"invalid-email", "Test12345!", "Test12345!", q, "test", "INVALID"},
                {uniqueEmail2, "Test12345!", "Different123!", q, "test", "MISMATCH"}
        };
    }
}