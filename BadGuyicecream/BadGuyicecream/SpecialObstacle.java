import greenfoot.*;
import java.util.List;

// SpecialObstacle类，表示一种特殊障碍物
public class SpecialObstacle extends Actor {
    private boolean breakable = false; // 是否可被破冰操作影响而移除，初始为false
    private boolean removableByIce = false; // 是否可被吐冰移除，初始为false
    public long creationTime; // 记录特殊障碍物的生成时间
    private boolean iceBrokenNearby = false; // 新增变量，用于记录附近是否发生了破冰操作，初始为false

    public SpecialObstacle() {
        // 可以在这里设置SpecialObstacle的初始图片等外观相关设置，例如加载特定图片
        GreenfootImage image = new GreenfootImage("wall.png"); // 假设图片文件名
        image.scale(50, 50); // 根据游戏中其他元素的尺寸进行缩放，这里假设为50x50大小
        setImage(image);

        creationTime = System.currentTimeMillis(); // 记录生成时间，获取当前系统时间作为生成时间
    }

    // 用于设置是否可被破冰移除的状态
    public void setBreakable(boolean breakable) {
        this.breakable = breakable;
    }

    // 新增方法，用于在外部（比如从Player类的破冰方法中）调用，告知该特殊障碍物附近发生了破冰操作
    public void setIceBrokenNearby(boolean iceBrokenNearby) {
        this.iceBrokenNearby = iceBrokenNearby;
    }

    @Override
    public void act() {
        MyWorld myWorld = (MyWorld) getWorld();
        long currentTime = System.currentTimeMillis();
        // 修改判断逻辑，只有当附近发生破冰操作且生成时间超过10秒时，才移除该特殊障碍物
        if (iceBrokenNearby && currentTime - creationTime > 10000) {
            myWorld.removeObjectSafely(this);
        }

    }
}