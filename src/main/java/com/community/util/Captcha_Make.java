package com.community.util;

import com.community.model.Captcha;

import java.util.Objects;
import java.util.Random;

public class Captcha_Make {
    private static String Num = "0123456789";
    private static String English = "abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ";
    private static String mixed_pd = "23456789abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ";
    private static String China = "天地人日月金木水火土山川大小多少前后左右上下中出入开关坐站立走跑跳说听看吃喝睡红黄蓝绿黑白";

    private static Random random = new Random();

    public Captcha CaptchaResult(int a, int length) {
        String code;
        String displayText;
        switch (a) {
            case 1:
                code = num_pdCode(length);
                displayText = code;
                break;
            case 2:
                code = English_pdCode(length);
                displayText = code;
                break;
            case 3:
                code = China_pdCode(length);
                displayText = code;
                break;
            case 4:
                Captcha arithmetic = Arithmetic_pdCode();
                code = arithmetic.getCode();
                displayText = arithmetic.getSee();
                break;
            case 5:
            default:
                code = mixed_pdCode(length);
                displayText = code;
                break;
        }
        return new Captcha(code, displayText);
    }

    private static String num_pdCode(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append( Num.charAt(random.nextInt( Num.length())));
        }
        return sb.toString();
    }

    private static String English_pdCode(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(English.charAt(random.nextInt(English.length())));
        }
        return sb.toString();
    }

    private static String mixed_pdCode(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(mixed_pd.charAt(random.nextInt(mixed_pd.length())));
        }
        return sb.toString();
    }

    private static String China_pdCode(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(China.length());
            sb.append(China.charAt(index));
        }
        return sb.toString();
    }

    private static Captcha Arithmetic_pdCode() {
        int a = random.nextInt(10) + 1;
        int b = random.nextInt(10) + 1;
        int operator = random.nextInt(3);

        int result;
        String operatorStr;
        switch (operator) {
            case 0: // 加法
                result = a + b;
                operatorStr = "+";
                break;
            case 1: // 减法
                if (a < b) {
                    int temp = a;
                    a = b;
                    b = temp;
                }
                result = a - b;
                operatorStr = "-";
                break;
            case 2: // 乘法
                a = random.nextInt(9) + 1;
                b = random.nextInt(9) + 1;
                result = a * b;
                operatorStr = "×";
                break;
            default:
                result = a + b;
                operatorStr = "+";
        }
        return new Captcha(String.valueOf(result),a + operatorStr + b + "=?");
    }

    //验证码结果
    public boolean pd_finally(Captcha captcha,String num) {
        return Objects.equals(num, captcha.getCode());
    }
}