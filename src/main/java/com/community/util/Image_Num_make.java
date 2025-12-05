package com.community.util;

import com.community.model.Captcha;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Random;

public class Image_Num_make {
    private static Random random = new Random();
    private static Color[] colors = {Color.BLACK, Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.MAGENTA};

    // 生成普通图形验证码图片（Base64格式）
    public static String Image_Num(Captcha captcha, int width, int height) {
        try {
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 绘制背景
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, width, height);

            // 绘制干扰线
            draw_lines(g2d, width, height, 5);

            // 绘制验证码文本
            String displayText = captcha.getSee();
            drawText(g2d, displayText, width, height);

            // 绘制噪点
            drawNoise(g2d, width, height, 50);

            g2d.dispose();

            // 转换为Base64
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", baos);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());

        } catch (Exception e) {
            throw new RuntimeException("生成验证码图片失败", e);
        }
    }

    // 绘制干扰线
    private static void draw_lines(Graphics2D g2d, int width, int height, int lineCount) {
        for (int i = 0; i < lineCount; i++) {
            g2d.setColor(colors[random.nextInt(colors.length)]);
            int x1 = random.nextInt(width);
            int y1 = random.nextInt(height);
            int x2 = random.nextInt(width);
            int y2 = random.nextInt(height);
            g2d.drawLine(x1, y1, x2, y2);
        }
    }

    // 绘制文本
    private static void drawText(Graphics2D g2d, String text, int width, int height) {
        int textLength = text.length();
        int charWidth = width / (textLength + 1);

        for (int i = 0; i < textLength; i++) {
            g2d.setColor(colors[random.nextInt(colors.length)]);
            int fontSize = 18 + random.nextInt(6);
            Font font = get_China_Font(fontSize);
            g2d.setFont(font);
            int x = i * charWidth + random.nextInt(10);
            int y = height / 2 + random.nextInt(15) - 7;
            g2d.drawString(String.valueOf(text.charAt(i)), x, y);
        }
    }

    // 获取支持中文的字体
    private static Font get_China_Font(int fontSize) {
        String[] chineseFonts = {"Microsoft YaHei", "SimSun", "SimHei", "KaiTi", "FangSong", "NSimSun", "STSong", "STKaiti"};

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fonts = ge.getAvailableFontFamilyNames();

        for (String fontName : chineseFonts) {
            for (String font : fonts) {
                if (font.equals(fontName)) {
                    return new Font(fontName, Font.BOLD, fontSize);
                }
            }
        }
        return new Font(Font.SANS_SERIF, Font.BOLD, fontSize);
    }

    // 绘制噪点
    private static void drawNoise(Graphics2D g2d, int width, int height, int noiseCount) {
        for (int i = 0; i < noiseCount; i++) {
            g2d.setColor(colors[random.nextInt(colors.length)]);
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            g2d.fillRect(x, y, 1, 1);
        }
    }
}