package com.mingyi.dataroute.persistence;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Test {

    public static void main(String[] args) {
        BigDecimal a = new BigDecimal(1);
        BigDecimal b = new BigDecimal(2787);
        BigDecimal divide = a.divide(b, 2, RoundingMode.HALF_UP
        );
        System.out.println(divide);
    }
}
