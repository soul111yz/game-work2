import greenfoot.*;

public class ResetButton extends Actor {
    //重新开始本关按钮图片
    public ResetButton() {
        setImage("reset.PNG"); // 确保 images 文件夹有 reset.png
    }

     public void act() {
    if (Greenfoot.mouseClicked(this)) {
        MyWorld world = (MyWorld) getWorld();
        int currentLevel = world.getCurrentLevel();  // 获取当前关卡

        // 创建一个新的MyWorld实例，并传递当前关卡数
        MyWorld newWorld = new MyWorld(currentLevel);

        Greenfoot.setWorld(newWorld);  // 切换到新世界
    }
}
}
