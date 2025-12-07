package com.community.util;

import com.community.model.Photo;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;
import java.util.Random;

public class Image_photo_make {
    private static Random random = new Random();
    private static File[] allFiles;

    // 添加ServletContext引用
    private static ServletContext servletContext;

    public static File[] getImageFiles() {
        return allFiles;
    }

    public static String getImageDirectoryPath() {
        if (servletContext != null) {
            String webAppPath = servletContext.getRealPath("/");
            return webAppPath + "static/images/";
        }
        return null;
    }

    // 设置ServletContext的方法
    public static void setServletContext(ServletContext context) {
        servletContext = context;
        System.out.println("✅ ServletContext已设置: " + (context != null));
        if (context != null) {
            System.out.println("Web应用真实路径: " + context.getRealPath("/"));
        }
        get_local_Image_Path();
    }

    public static Photo Image_photo() {
        try {
            System.out.println("🎯 Image_photo_make.Image_photo() 开始执行");

            // 确保ServletContext已设置
            if (servletContext == null) {
                System.err.println("❌ 错误: ServletContext未设置");
                throw new RuntimeException("ServletContext未初始化");
            }

            // 随机选择本地图片
            String imagePath = get_local_Image_Path();
            System.out.println("获取到的图片路径: " + imagePath);

            if (imagePath == null) {
                System.err.println("❌ 无法获取图片路径");
                throw new RuntimeException("无法获取图片路径");
            }

            BufferedImage originalImage = load_Image(imagePath);

            // 固定初始角度为0度，只随机正确角度
            int initialAngle = 0;
            int correctAngle = random.nextInt(360);

            System.out.println("生成验证码参数 - 初始角度: " + initialAngle + "°, 正确角度: " + correctAngle + "°");

            // 生成旋转后的图片（正确角度）
            String rotatedImage = create_Rotate_Image(originalImage, correctAngle);
            System.out.println("旋转图片生成完成，Base64长度: " + rotatedImage.length());

            // 生成初始角度的图片（固定为0度）
            String initialImage = create_Rotate_Image(originalImage, initialAngle);
            System.out.println("初始图片生成完成，Base64长度: " + initialImage.length());

            // 使用正确的构造函数创建Photo对象
            Photo photo = new Photo("", correctAngle, rotatedImage, initialImage, initialAngle);
            System.out.println("✅ Photo对象创建成功");

            return photo;

        } catch (Exception e) {
            System.err.println("❌ Image_photo_make.Image_photo() 异常: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("生成旋转验证码失败: " + e.getMessage(), e);
        }
    }

    // 修改 get_local_Image_Path() 方法，添加调试和编码处理
    private static String get_local_Image_Path() {
        if (servletContext != null) {
            try {
                String webAppPath = servletContext.getRealPath("/");
                String imageDirPath = webAppPath + "static/images";
                File imageDir = new File(imageDirPath);

                System.out.println("图片目录路径: " + imageDir.getAbsolutePath());
                System.out.println("目录是否存在: " + imageDir.exists());
                System.out.println("是否是目录: " + imageDir.isDirectory());

                if (imageDir.exists() && imageDir.isDirectory()) {
                    // 列出所有文件
                    File[] allFiles = imageDir.listFiles();

                    if (allFiles != null && allFiles.length > 0) {
                        System.out.println("找到 " + allFiles.length + " 个文件:");
                        for (File file : allFiles) {
                            System.out.println("  - " + file.getName() +
                                    " (可读: " + file.canRead() +
                                    ", 大小: " + file.length() + " bytes)");
                        }

                        // 随机选择一个文件
                        File random_file = allFiles[random.nextInt(allFiles.length)];
                        System.out.println("✅ 选择图片: " + random_file.getAbsolutePath());

                        // 检查文件是否可以读取
                        if (!random_file.canRead()) {
                            System.err.println("❌ 文件不可读取，尝试修复权限");
                            random_file.setReadable(true);
                        }

                        // 检查文件大小
                        if (random_file.length() == 0) {
                            System.err.println("❌ 文件大小为0，跳过");
                            return null;
                        }

                        return random_file.getAbsolutePath();
                    } else {
                        System.out.println("❌ 在图片目录中未找到任何文件");
                    }
                } else {
                    System.out.println("❌ 图片目录不存在或不是目录");
                }
            } catch (Exception e) {
                System.err.println("❌ 获取图片路径时出错: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("⚠️ ServletContext未设置");
        }
        return null;
    }

    // 修改 load_Image() 方法，增强错误处理
    private static BufferedImage load_Image(String imagePath) {
        File file = new File(imagePath);
        BufferedImage image = null;
        try {
            System.out.println("尝试加载图片: " + file.getAbsolutePath());
            System.out.println("文件存在: " + file.exists());
            System.out.println("文件大小: " + file.length());

            // 尝试不同方式读取图片
            image = ImageIO.read(file);

            if (image == null) {
                System.err.println("❌ ImageIO.read() 返回 null，可能是格式不支持");

                // 尝试使用 ImageIO 获取支持的格式
                String[] formats = ImageIO.getReaderFormatNames();
                System.out.println("支持的图片格式: " + String.join(", ", formats));

                // 尝试手动处理 JPG
                if (imagePath.toLowerCase().endsWith(".jpg") || imagePath.toLowerCase().endsWith(".jpeg")) {
                    System.out.println("尝试使用 ImageIO 读取 JPG 文件...");
                    InputStream is = new FileInputStream(file);
                    image = ImageIO.read(is);
                    is.close();
                }
            }

            if (image != null) {
                System.out.println("✅ 成功加载图片: " + file.getName() +
                        " (尺寸: " + image.getWidth() + "x" + image.getHeight() +
                        ", 类型: " + image.getType() + ")");
            } else {
                System.err.println("❌ 无法加载图片，创建测试图片");
                image = createTestImage();
            }

        } catch (IOException e) {
            System.err.println("❌ 加载图片异常: " + e.getMessage());
            e.printStackTrace();

            // 创建测试图片作为后备
            System.out.println("创建测试图片作为后备方案");
            image = createTestImage();
        }
        return image;
    }

    // 添加 createTestImage 方法
    private static BufferedImage createTestImage() {
        System.out.println("创建测试图片...");
        int width = 400;
        int height = 400;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // 设置背景
        GradientPaint gradient = new GradientPaint(0, 0, Color.CYAN, width, height, Color.MAGENTA);
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height);

        // 绘制图案
        g2d.setColor(Color.YELLOW);
        g2d.fillOval(50, 50, 300, 300);

        // 添加文字
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 36));
        g2d.drawString("验证码", 120, 220);
        g2d.drawString("测试图片", 110, 280);

        g2d.dispose();
        System.out.println("测试图片创建完成");
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
        System.out.println("开始转换图片为Base64，图片尺寸: " + image.getWidth() + "x" + image.getHeight());

        ByteArrayOutputStream base = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", base);

        String base64 = Base64.getEncoder().encodeToString(base.toByteArray());
        String result = "data:image/png;base64," + base64;

        System.out.println("Base64转换完成，总长度: " + result.length());
        System.out.println("Base64前缀: " + result.substring(0, 50) + "...");

        return result;
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