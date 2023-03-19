package com.litte.v1;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * @Description :
 * @Author : 田迎
 * @Date : 2023/3/17 22:33
 * @Version : 1.0.0
 **/
public class TDDDemoTest {
    //编写一个函数，返回小于给定值max的所有 素数组成的数组
    //public static int[] getPrimes(int max);

    @Test
    public void testGetPrimes() {
        // 先做一个简单的任务分解
        // 边界条件  getPrimes(0), getPrimes(-1) , getPrimes(2) 应该返回什么？
        // 正常输入  getPrimes(9), getPrimes(17), getPrimes(30)
        // 前提什么是素数
        // 一个大于1的正整数，假如除了1和它本身以外，不能被其他正整数整除，就叫素数
        Assert.assertArrayEquals(TDDDemo.EMPTIES, TDDDemo.getPrimes(0));
        Assert.assertArrayEquals(TDDDemo.EMPTIES, TDDDemo.getPrimes(-1));
        Assert.assertArrayEquals(TDDDemo.EMPTIES, TDDDemo.getPrimes(2));

        Assert.assertArrayEquals(new int[]{2,3,5,7}, TDDDemo.getPrimes(9));
        Assert.assertArrayEquals(new int[]{2,3,5,7,11,13}, TDDDemo.getPrimes(17));
        Assert.assertArrayEquals(new int[]{2,3,5,7,11,13,17}, TDDDemo.getPrimes(19));
        Assert.assertArrayEquals(new int[]{2,3,5,7,11,13,17,19,23,29}, TDDDemo.getPrimes(30));
    }

}

class TDDDemo {
    public static int[] EMPTIES = {};

    /**
     *  编写一个函数，返回小于给定值max的所有 素数组成的数组
     * @param max
     * @return
     */
    public static int[] getPrimes(int max) {
        if (max <= 2) {
            return EMPTIES;
        }

        int ct = 0;
        int[] res = new int[max];
        for (int num = 2; num < max; num++) {
            if (isPrime(num)) {
                res[ct++] = num;
            }
        }
        return Arrays.copyOf(res, ct);
    }

    /**
     * 判断一个数是不是素数
     * @param num
     * @return
     */
    private static boolean isPrime(int num) {
        return IntStream.range(2, num / 2 + 1).noneMatch(i -> num % i == 0);
    }
}
