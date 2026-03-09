import greenfoot.*;
import greenfoot.Color;

public class GameOverPopup extends Actor {
    private GreenfootImage backgroundImage;

    public GameOverPopup() {
        // 先创建一个空白的图片对象，暂时不设置尺寸等具体属性
        backgroundImage = new GreenfootImage(1, 1);
    }

    public void initialize() {
        // 获取当前所在的世界实例（此时确保已经添加到世界中了，应该不会为null）
        MyWorld world = (MyWorld) getWorld();
        if (world!= null) {
            // 根据世界的大小重新创建背景图片
            backgroundImage = new GreenfootImage(world.getWidth(), world.getHeight());
            backgroundImage.setColor(Color.WHITE);
            backgroundImage.fillRect(0, 0, world.getWidth(), world.getHeight());

            // 加载游戏结束的图片（假设名为gameover_image.png，你可根据实际替换）
            GreenfootImage gameOverImage = new GreenfootImage("gameover.png");
            int scaledWidth = world.getWidth() / 2;  // 根据场景宽度调整图片大小，这里示例为宽度一半，可按需调整
            int scaledHeight = world.getHeight() / 4; // 根据场景高度调整图片大小，这里示例为高度四分之一，可按需调整
            gameOverImage.scale(scaledWidth, scaledHeight);

            // 按照新的布局方式设置图片位置（水平居中和垂直居上三分之一处）
            int imageX = (world.getWidth() - scaledWidth) / 2;
            int imageY = (world.getHeight() - scaledHeight) / 3;
            backgroundImage.drawImage(gameOverImage, imageX, imageY);

            // 创建ResetButton实例作为重置按钮
            ResetButton resetButton = new ResetButton();
            // 按照新的布局方式设置按钮位置（水平居中和垂直居中偏下，放在图片下方合适位置，示例中是 +100 的偏移，可按需调整）
            int buttonX = world.getWidth() / 2;
            int buttonY = world.getHeight() / 2 + 150;
            world.addObject(resetButton, buttonX, buttonY);

            setImage(backgroundImage);
        }
    }
}