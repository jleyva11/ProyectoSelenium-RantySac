package com.nopcommerce.dataproviders;

import org.testng.annotations.DataProvider;

public class LoginDataProvider {

    @DataProvider(name = "loginData")
    public static Object[][] loginData() {
        return new Object[][]{
                // email, password, expectedResult
                {"admin@juice-sh.op", "admin123", "SUCCESS"},
                {"fake@fake.com", "wrong", "INVALID"},
                {"", "", "REQUIRED"}
        };
    }
}