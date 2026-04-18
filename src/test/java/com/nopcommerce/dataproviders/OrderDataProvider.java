package com.nopcommerce.dataproviders;

import org.testng.annotations.DataProvider;

public class OrderDataProvider {

    @DataProvider(name = "orderAddQuantityData")
    public static Object[][] orderAddQuantityData() {
        return new Object[][]{
                {1, 1},
                {2, 2},
                {3, 3}
        };
    }

    @DataProvider(name = "orderChangeQuantityData")
    public static Object[][] orderChangeQuantityData() {
        return new Object[][]{
                {1, 3},
                {2, 1},
                {3, 2}
        };
    }
}