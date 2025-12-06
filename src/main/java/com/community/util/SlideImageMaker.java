package com.community.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

public class SlideImageMaker {
    private static Random random = new Random();

    // 滑动验证码结果类
    public static class SlideResult {
        private int targetPosition;
        private String backgroundImage;
        private String puzzleImage;

        public SlideResult(int targetPosition, String backgroundImage, String puzzleImage) {
            this.targetPosition = targetPosition;
            this.backgroundImage = backgroundImage;
            this.puzzleImage = puzzleImage;
        }

        // getters
        public int getTargetPosition() { return targetPosition; }
        public String getBackgroundImage() { return backgroundImage; }
        public String getPuzzleImage() { return puzzleImage; }
    }

    // 生成滑动验证码
    public static SlideResult generateSlideCaptcha() {
        try {
            // 随机选择本地图片
            String imagePath = getLocalImagePath();
            if (imagePath == null) {
                throw new RuntimeException("无法获取图片路径");
            }

            BufferedImage originalImage = loadImage(imagePath);

            // 生成目标位置 (30%-70%之间)
            int targetPosition = 30 + random.nextInt(40);

            // 生成滑动验证码图片
            SlideImages slideImages = createSlideImages(originalImage, targetPosition);

            System.out.println("✅ 滑动验证码生成成功，目标位置: " + targetPosition + "%");

            return new SlideResult(targetPosition, slideImages.backgroundBase64, slideImages.puzzleBase64);

        } catch (Exception e) {
            System.err.println("❌ 生成滑动验证码异常: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("生成滑动验证码失败", e);
        }
    }

    // 获取本地图片路径（复用旋转验证码的图片源）
    private static String getLocalImagePath() {
        File[] allFiles = Image_photo_make.getServletContext();
        if (allFiles != null && allFiles.length > 0) {
            System.out.println("滑动验证码 - 目录中的文件数量: " + allFiles.length);
            // 随机选择一个存在的文件
            File random_file = allFiles[random.nextInt(allFiles.length)];
            System.out.println("✅ 滑动验证码选择图片: " + random_file.getAbsolutePath());
            return random_file.getAbsolutePath();
        } else {
            System.out.println("❌ 滑动验证码 - 在图片目录中未找到任何图片文件");
            return null;
        }
    }

    // 加载图片
    private static BufferedImage loadImage(String imagePath) throws IOException {
        File file = new File(imagePath);
        BufferedImage image = ImageIO.read(file);
        if (image == null) {
            throw new IOException("无法加载图片: " + imagePath);
        }
        System.out.println("✅ 滑动验证码成功加载图片: " + file.getAbsolutePath() +
                " (尺寸: " + image.getWidth() + "x" + image.getHeight() + ")");
        return image;
    }

    // 创建滑动验证码图片
    private static SlideImages createSlideImages(BufferedImage originalImage, int targetPosition) throws IOException {
        int width = 400;
        int height = 300;
        int puzzleSize = 80;

        // 缩放原图到合适尺寸
        BufferedImage scaledImage = scaleImage(originalImage, width, height);

        // 生成拼图块位置
        int puzzleX = (int) ((targetPosition / 100.0) * (width - puzzleSize));
        int puzzleY = random.nextInt(height - puzzleSize - 20) + 10;

        // 创建带缺口的背景图
        BufferedImage backgroundWithGap = createBackgroundWithGap(scaledImage, puzzleX, puzzleY, puzzleSize);

        // 创建拼图块
        BufferedImage puzzlePiece = createPuzzlePiece(scaledImage, puzzleX, puzzleY, puzzleSize);

        String backgroundBase64 = imageToBase64(backgroundWithGap);
        String puzzleBase64 = imageToBase64(puzzlePiece);

        System.out.println("滑动验证码图片创建完成，背景图Base64长度: " + backgroundBase64.length() +
                ", 拼图块Base64长度: " + puzzleBase64.length());

        return new SlideImages(backgroundBase64, puzzleBase64);
    }

    // 缩放图片
    private static BufferedImage scaleImage(BufferedImage original, int width, int height) {
        BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = scaled.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(original, 0, 0, width, height, null);
        g2d.dispose();
        return scaled;
    }

    // 创建带缺口的背景图
    private static BufferedImage createBackgroundWithGap(BufferedImage image, int x, int y, int size) {
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = result.createGraphics();

        // 绘制原图
        g2d.drawImage(image, 0, 0, null);

        // 绘制缺口（半透明黑色）
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
        g2d.setColor(Color.BLACK);
        g2d.fillRoundRect(x, y, size, size, 15, 15);

        // 绘制缺口边框
        g2d.setComposite(AlphaComposite.SrcOver);
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(x, y, size, size, 15, 15);

        g2d.dispose();
        return result;
    }

    // 创建拼图块
    private static BufferedImage createPuzzlePiece(BufferedImage image, int x, int y, int size) {
        BufferedImage puzzle = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = puzzle.createGraphics();

        // 设置高质量渲染
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 绘制拼图形状（带圆角）
        g2d.setColor(new Color(255, 255, 255, 200));
        g2d.fillRoundRect(0, 0, size, size, 15, 15);

        // 从原图复制对应区域
        g2d.setComposite(AlphaComposite.SrcAtop);
        g2d.drawImage(image, 0, 0, size, size, x, y, x + size, y + size, null);

        // 绘制边框
        g2d.setComposite(AlphaComposite.SrcOver);
        g2d.setColor(Color.BLUE);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(1, 1, size - 2, size - 2, 15, 15);

        g2d.dispose();
        return puzzle;
    }

    // 图片转Base64
    private static String imageToBase64(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    // 内部类用于返回两张图片
    private static class SlideImages {
        String backgroundBase64;
        String puzzleBase64;

        SlideImages(String backgroundBase64, String puzzleBase64) {
            this.backgroundBase64 = backgroundBase64;
            this.puzzleBase64 = puzzleBase64;
        }
    }

    // 验证滑动位置
    public static boolean validatePosition(int userPosition, int targetPosition, int tolerance) {
        int diff = Math.abs(userPosition - targetPosition);
        System.out.println("滑动验证 - 用户位置: " + userPosition + "%, 目标位置: " + targetPosition + "%, 差值: " + diff + "%, 容差: " + tolerance);

        boolean result = diff <= tolerance;
        System.out.println("验证结果: " + (result ? "通过" : "失败"));
        return result;
    }
}