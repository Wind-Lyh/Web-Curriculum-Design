package com.community.util;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

public class SlideImageMaker {
    private static Random random = new Random();
    private static ServletContext servletContext;

    // 滑动验证码结果类
    public static class SlideResult {
        private int targetPosition;
        private int puzzleY;
        private String backgroundImage;
        private String puzzleImage;

        public SlideResult(int targetPosition, int puzzleY, String backgroundImage, String puzzleImage) {
            this.targetPosition = targetPosition;
            this.puzzleY = puzzleY;
            this.backgroundImage = backgroundImage;
            this.puzzleImage = puzzleImage;
        }

        public int getTargetPosition() { return targetPosition; }
        public int getPuzzleY() { return puzzleY; }
        public String getBackgroundImage() { return backgroundImage; }
        public String getPuzzleImage() { return puzzleImage; }
    }

    // 设置ServletContext
    public static void setServletContext(ServletContext context) {
        servletContext = context;
        System.out.println("✅ SlideImageMaker - ServletContext已设置");
    }

    // 生成滑动验证码
    public static SlideResult generateSlideCaptcha() {
        try {
            System.out.println("🔄 开始生成滑动验证码...");

            // 随机选择本地图片
            String imagePath = getLocalImagePath();
            if (imagePath == null) {
                System.err.println("❌ 无法获取图片路径，创建测试图片");
                return createTestSlideResult();
            }

            BufferedImage originalImage = loadImage(imagePath);
            if (originalImage == null) {
                System.err.println("❌ 无法加载图片，创建测试图片");
                return createTestSlideResult();
            }

            // 生成目标位置 (30%-70%之间)
            int targetPosition = 30 + random.nextInt(40);
            System.out.println("目标位置: " + targetPosition + "%");

            // 生成滑动验证码图片
            SlideImages slideImages = createSlideImages(originalImage, targetPosition);

            System.out.println("✅ 滑动验证码生成成功");
            System.out.println("  目标位置: " + targetPosition + "%");
            System.out.println("  拼图Y坐标: " + slideImages.puzzleY + "px");
            System.out.println("  背景图Base64长度: " + slideImages.backgroundBase64.length());
            System.out.println("  拼图块Base64长度: " + slideImages.puzzleBase64.length());

            return new SlideResult(
                    targetPosition,
                    slideImages.puzzleY,
                    slideImages.backgroundBase64,
                    slideImages.puzzleBase64
            );

        } catch (Exception e) {
            System.err.println("❌ 生成滑动验证码异常: " + e.getMessage());
            e.printStackTrace();
            return createTestSlideResult();
        }
    }

    // 获取本地图片路径
    private static String getLocalImagePath() {
        if (servletContext == null) {
            System.err.println("❌ SlideImageMaker - ServletContext未设置");
            return null;
        }

        try {
            String webAppPath = servletContext.getRealPath("/");
            String imageDirPath = webAppPath + "static/images";
            File imageDir = new File(imageDirPath);

            System.out.println("滑动验证码 - 图片目录路径: " + imageDir.getAbsolutePath());
            System.out.println("目录是否存在: " + imageDir.exists());
            System.out.println("是否是目录: " + imageDir.isDirectory());

            if (imageDir.exists() && imageDir.isDirectory()) {
                File[] allFiles = imageDir.listFiles();
                if (allFiles != null && allFiles.length > 0) {
                    System.out.println("找到 " + allFiles.length + " 张图片:");
                    for (File file : allFiles) {
                        System.out.println("  - " + file.getName() + " (大小: " + file.length() + " bytes)");
                    }

                    // 随机选择一张图片
                    File randomFile = allFiles[random.nextInt(allFiles.length)];
                    System.out.println("✅ 滑动验证码选择图片: " + randomFile.getAbsolutePath());
                    return randomFile.getAbsolutePath();
                } else {
                    System.out.println("❌ 在图片目录中未找到任何图片文件");
                }
            } else {
                System.out.println("❌ 图片目录不存在或不是目录");
            }
        } catch (Exception e) {
            System.err.println("❌ 获取图片路径时出错: " + e.getMessage());
        }

        return null;
    }

    // 创建测试滑动验证码结果
    private static SlideResult createTestSlideResult() {
        try {
            System.out.println("创建测试滑动验证码...");

            int width = 400;
            int height = 300;
            int puzzleSize = 80;

            // 创建测试背景图
            BufferedImage testImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = testImage.createGraphics();

            // 绘制渐变背景
            GradientPaint gradient = new GradientPaint(0, 0, Color.CYAN, width, height, Color.MAGENTA);
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, width, height);

            // 绘制一些图形
            g2d.setColor(Color.YELLOW);
            g2d.fillOval(100, 50, 200, 200);

            // 添加文字
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 24));
            g2d.drawString("滑动验证", 140, 160);
            g2d.drawString("测试图片", 140, 190);

            g2d.dispose();

            // 生成目标位置
            int targetPosition = 50; // 中间位置
            int puzzleY = 110;

            // 创建带缺口的背景图
            int puzzleX = (int) ((targetPosition / 100.0) * (width - puzzleSize));
            BufferedImage backgroundWithGap = createBackgroundWithGap(testImage, puzzleX, puzzleY, puzzleSize);

            // 创建拼图块
            BufferedImage puzzlePiece = createPuzzlePiece(testImage, puzzleX, puzzleY, puzzleSize);

            String backgroundBase64 = imageToBase64(backgroundWithGap);
            String puzzleBase64 = imageToBase64(puzzlePiece);

            System.out.println("✅ 测试滑动验证码创建完成");

            return new SlideResult(targetPosition, puzzleY, backgroundBase64, puzzleBase64);

        } catch (Exception e) {
            System.err.println("❌ 创建测试滑动验证码失败: " + e.getMessage());
            throw new RuntimeException("无法生成滑动验证码");
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

        System.out.println("拼图位置 - X: " + puzzleX + ", Y: " + puzzleY);

        // 创建带缺口的背景图
        BufferedImage backgroundWithGap = createBackgroundWithGap(scaledImage, puzzleX, puzzleY, puzzleSize);

        // 创建拼图块
        BufferedImage puzzlePiece = createPuzzlePiece(scaledImage, puzzleX, puzzleY, puzzleSize);

        String backgroundBase64 = imageToBase64(backgroundWithGap);
        String puzzleBase64 = imageToBase64(puzzlePiece);

        return new SlideImages(backgroundBase64, puzzleBase64, puzzleY);
    }

    // 缩放图片
    private static BufferedImage scaleImage(BufferedImage original, int width, int height) {
        BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = scaled.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
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
        g2d.setColor(new Color(0, 0, 0, 180));
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
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // 绘制拼图形状（带圆角）
        g2d.setColor(new Color(255, 255, 255, 200));
        g2d.fillRoundRect(0, 0, size, size, 15, 15);

        // 从原图复制对应区域
        g2d.setComposite(AlphaComposite.SrcAtop);
        g2d.drawImage(image, 0, 0, size, size, x, y, x + size, y + size, null);

        // 绘制边框
        g2d.setComposite(AlphaComposite.SrcOver);
        g2d.setColor(new Color(0, 123, 255));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(1, 1, size - 2, size - 2, 15, 15);

        // 添加阴影效果
        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.fillRoundRect(2, 2, size - 2, size - 2, 15, 15);

        g2d.dispose();
        return puzzle;
    }

    // 图片转Base64
    private static String imageToBase64(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    // 内部类用于返回图片数据
    private static class SlideImages {
        String backgroundBase64;
        String puzzleBase64;
        int puzzleY;

        SlideImages(String backgroundBase64, String puzzleBase64, int puzzleY) {
            this.backgroundBase64 = backgroundBase64;
            this.puzzleBase64 = puzzleBase64;
            this.puzzleY = puzzleY;
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