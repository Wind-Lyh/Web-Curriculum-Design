package com.community.util;

import com.community.model.Photo;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

public class Image_photo_make {
    private static Random random = new Random();
    private static File[] allFiles;

    // 添加ServletContext引用
    private static ServletContext servletContext;

    // 设置ServletContext的方法
    public static void setServletContext(ServletContext context) {
        servletContext = context;
        System.out.println("✅ ServletContext已设置: " + (context != null));
        if (context != null) {
            System.out.println("Web应用真实路径: " + context.getRealPath("/"));
        }
        get_local_Image_Path();
    }

    // 生成旋转验证码图片 - 方案一：固定初始方向
    public static Photo Image_photo() {
        try {
            // 随机选择本地图片
            String imagePath = get_local_Image_Path();
            BufferedImage originalImage = load_Image(imagePath);

            // 固定初始角度为0度，只随机正确角度
            int initialAngle = 0; // 固定初始方向
            int correctAngle = random.nextInt(360); // 随机正确角度

            // 生成旋转后的图片（正确角度）
            String rotatedImage = create_Rotate_Image(originalImage, correctAngle);
            // 生成初始角度的图片（固定为0度）
            String initialImage = create_Rotate_Image(originalImage, initialAngle);

            System.out.println("旋转验证码生成成功，正确角度: " + correctAngle + "°, 初始角度: " + initialAngle + "°");

            // 使用正确的构造函数创建Photo对象
            return new Photo("", correctAngle, rotatedImage, initialImage, initialAngle);

        } catch (Exception e) {
            System.err.println("❌ 生成旋转验证码异常: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("生成旋转验证码失败", e);
        }
    }

    // 获取随机本地图片路径
    private static String get_local_Image_Path() {
        // 如果ServletContext可用，使用Web应用的真实路径
        if (servletContext != null) {
            try {
                String webAppPath = servletContext.getRealPath("/");
                // 构建图片目录路径
                String imageDirPath = webAppPath + "image";
                File imageDir = new File(imageDirPath);

                System.out.println("图片目录路径: " + imageDir.getAbsolutePath());
                System.out.println("图片目录是否存在: " + imageDir.exists());
                System.out.println("是否是目录: " + imageDir.isDirectory());

                if (imageDir.exists() && imageDir.isDirectory()) {
                    // 列出目录中的所有文件用于调试
                    allFiles = imageDir.listFiles();
                    if (allFiles != null && allFiles.length > 0) {
                        System.out.println("目录中的文件数量: " + allFiles.length);
                        // 随机选择一个存在的文件
                        File random_file = allFiles[random.nextInt(allFiles.length)];
                        System.out.println("✅ 选择图片: " + random_file.getAbsolutePath());
                        return random_file.getAbsolutePath();
                    } else {
                        System.out.println("❌ 在图片目录中未找到任何图片文件");
                    }
                } else {
                    System.out.println("❌ 图片目录不存在或不是目录");
                }
            } catch (Exception e) {
                System.err.println("❌ 使用ServletContext获取路径时出错: " + e.getMessage());
            }
        } else {
            System.out.println("⚠️ ServletContext未设置，使用备用路径查找");
        }
        return null;
    }

    // 加载本地图片
    private static BufferedImage load_Image(String imagePath) {
        File file = new File(imagePath);
        BufferedImage image = null;
        try {
            image = ImageIO.read(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("✅ 成功加载图片: " + file.getAbsolutePath() +
                " (尺寸: " + image.getWidth() + "x" + image.getHeight() + ")");
        return image;
    }

    // 创建旋转图片
    private static String create_Rotate_Image(BufferedImage originalImage, double angle) {
        try {
            System.out.println("创建旋转图片，角度: " + angle + "°");
            int size = 600;

            // 创建透明背景的图片
            BufferedImage rotatedImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = rotatedImage.createGraphics();

            // 设置透明背景
            g2d.setComposite(AlphaComposite.Clear);
            g2d.fillRect(0, 0, size, size);
            g2d.setComposite(AlphaComposite.SrcOver);

            // 开启高质量渲染
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            // 计算缩放比例
            double scale = Math.min((double) size * 0.8 / originalImage.getWidth(),
                    (double) size * 0.8 / originalImage.getHeight());

            int scaled_width = (int) (originalImage.getWidth() * scale);
            int scaled_height = (int) (originalImage.getHeight() * scale);

            // 移动到中心并旋转
            g2d.translate(size / 2, size / 2);
            g2d.rotate(Math.toRadians(angle));

            // 绘制图片
            g2d.drawImage(originalImage, -scaled_width / 2, -scaled_height / 2, scaled_width, scaled_height, null);

            // 绘制中心点
            g2d.setColor(Color.BLUE);
            g2d.fillOval(-3, -3, 6, 6);

            g2d.dispose();
            // 转换为Base64
            String base64 = image_Base64(rotatedImage);
            System.out.println("旋转图片创建完成，Base64长度: " + base64.length());
            return base64;

        } catch (Exception e) {
            throw new RuntimeException("创建旋转图片失败", e);
        }
    }

    // 图片转Base64
    private static String image_Base64(BufferedImage image) throws IOException {
        ByteArrayOutputStream base = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", base);
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(base.toByteArray());
    }

    // 验证旋转角度 - 容差输入tolerance
    public static boolean pd_angle(int userAngle, int correctAngle, int tolerance) {
        // 规范化角度到0-359范围
        userAngle = normal_angle(userAngle);
        correctAngle = normal_angle(correctAngle);

        int diff = Math.abs(userAngle - correctAngle);
        diff = Math.min(diff, 360 - diff);
        System.out.println("角度验证 - 用户角度: " + userAngle + "°, 正确角度: " + correctAngle + "°, 差值: " + diff + "°, 容差: " + tolerance);

        boolean result = diff <= tolerance;
        System.out.println("验证结果: " + (result ? "通过" : "失败"));
        return result;
    }

    // 规范化角度到0-359范围
    private static int normal_angle(int angle) {
        // 处理负角度和超过360度的角度
        angle = angle % 360;
        if (angle < 0) {
            angle += 360;
        }
        return angle;
    }

    public static File[] getServletContext() {
        return allFiles;
    }
}