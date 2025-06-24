package com.sep490.gshop.utils;

import java.util.Random;

public class RandomUtil {
        private RandomUtil() {
        }
        public static String randomNumber(int length) {
            if (length <= 0) {
                throw new IllegalArgumentException("Số chữ số phải > 0");
            }
            int min = (int) Math.pow(10, length - 1);
            int max = (int) Math.pow(10, length) - 1;

            return String.valueOf(min + new Random().nextInt(max - min + 1));
        }
}
