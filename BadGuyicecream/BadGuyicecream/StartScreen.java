import greenfoot.*;
import greenfoot.Color;

// 开始游戏界面类
public class StartScreen extends Actor {

    private GreenfootImage background;
    private GreenfootImage startButtonImage;
    private String rules = "游戏规则：\n- 使用方向键移动。\n- 按空格键吐冰和破冰。\n- 可以选择重新开始本关。\n- 拾取完所有水果进入下一关，共五关。";
    
    //开始界面
    public StartScreen() {

        background = new GreenfootImage("start_screen_bg.png");

        startButtonImage = new GreenfootImage("start_button.png"); // 确保你有这个图像文件
        // 缩小按钮图片，这里示例将宽度和高度都缩小为原来的一半，你可以根据实际按钮图片效果调整缩放比例
        startButtonImage.scale(startButtonImage.getWidth() / 2, startButtonImage.getHeight() / 2);

        // 计算按钮水平和垂直居中的位置
        int buttonX = (background.getWidth() - startButtonImage.getWidth()) / 2-10;
        int buttonY = (background.getHeight() - startButtonImage.getHeight()) / 2+280;
        background.drawImage(startButtonImage, buttonX, buttonY);


        // 绘制规则说明，设置合适字体大小（这里设置为25，可根据需求调整）
        background.setFont(background.getFont().deriveFont(25f));
        background.setColor(new Color(0, 0, 0));
        // 拆分规则说明字符串为行数组，方便逐行绘制并控制垂直间距
        String[] ruleLines = rules.split("\n");
        int yOffset = 480; // 从何处开始绘制规则说明的垂直起始偏移量，可调整
        for (String ruleLine : ruleLines) {
            background.drawString(ruleLine, (background.getWidth() / 2) - (ruleLine.length() * 8)-30, yOffset); // 根据每行文字长度大致居中，可微调
            yOffset += 30; // 每行文字之间的垂直间距，可调整
        }

        setImage(background);
    }

    public void act() {
        if (Greenfoot.mouseClicked(this)) {
            MyWorld myWorld = (MyWorld) getWorld();
            myWorld.startGame();
        }
    }
}