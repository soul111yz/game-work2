import greenfoot.*;

// 墙体类，继承自Greenfoot的Actor类
public class Wall extends Actor
{
    public Wall() {
        setImage("wall.png");
        scaleImageToFitSize(); // 调用缩放图片方法
    }

    // 新增方法用于缩放图片以适配50px正方形大小
    private void scaleImageToFitSize() {
        GreenfootImage originalImage = getImage();
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        // 计算缩放比例，使图片最长边为50px
        double scaleFactor = Math.min(50.0 / originalWidth, 50.0 / originalHeight);

        originalImage.scale((int) (originalWidth * scaleFactor), (int) (originalHeight * scaleFactor));

        setImage(originalImage);
    }

}