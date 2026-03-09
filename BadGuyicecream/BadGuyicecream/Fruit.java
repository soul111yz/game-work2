import greenfoot.*;
import greenfoot.GreenfootImage;
import java.util.List;

// Fruit类继承自Actor类，表示游戏中的水果对象，包含了水果的各种属性和行为逻辑
public class Fruit extends Actor {
    // 表示水果所属阶段，取值为1或2，可能用于区分不同关卡阶段出现的不同类型水果或者有不同游戏逻辑相关的水果分类
    public int stage; 
    // 标记水果是否已经被吃掉，初始化为false，表示初始状态下水果未被吃掉
    private boolean isEaten = false; 
    // 新增变量，用于标记水果是否处于冰冻状态，初始化为false表示未冰冻，后续可基于此实现与冰冻相关的游戏逻辑，比如冰冻后停止移动等
    private boolean isFrozen = false; 
    // 保存水果的原始图像，用于后续恢复图片等操作，比如水果被隐藏或改变状态后能还原到初始显示样子
    public GreenfootImage originalImage; 
    // 用于记录方向更新的计数器，每经过一帧（游戏循环一次）会增加，达到一定值后更新水果移动方向
    private int directionUpdateCounter = 0; 
    // 每隔多少帧更新一次移动方向，可根据实际情况调整该值来改变水果移动方向变化的频率，数值越大方向变化越不频繁
    private int directionUpdateInterval = 50; 
    // 记录上一次移动的方向，初始化为 -1 表示还未移动过，方便后续在生成新移动方向时避免连续两次相同方向等情况
    private int lastMoveDirection = -1; 

    // 构造函数，根据传入的阶段参数来设置水果的图像，不再获取初始坐标（可能坐标会在添加到游戏世界时另行设置）
    public Fruit(int stage) {
        this.stage = stage;
        if (stage == 1) {
            // 设置阶段为1时水果对应的图像为"coconut.png"
            setImage("coconut.png");
            // 调用方法缩放图像以适配一定大小（这里是适配后续游戏中合适的显示尺寸）
            scaleImageToFitSize();
        } else if (stage == 2) {
            setImage("banana.png");
            scaleImageToFitSize();
        }
        // 保存当前水果的原始图像副本，用于后续可能的恢复操作
        originalImage = new GreenfootImage(getImage());
    }

    // 新增方法用于返回水果类型，这里简单返回stage作为类型标识，方便在其他地方根据这个值来区分不同类型水果进行不同处理
    public int getType() {
        return this.stage;
    }

    // 新增方法用于缩放图片以适配50px正方形大小，保证水果图像在游戏中显示尺寸统一且合适
    private void scaleImageToFitSize() {
        GreenfootImage originalImage = getImage();
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        // 计算缩放比例，使图片最长边为50px，确保图片缩放后是正方形，取宽度和高度缩放比例中的较小值，避免图片变形
        double scaleFactor = Math.min(50.0 / originalWidth, 50.0 / originalHeight);
        originalImage.scale((int) (originalWidth * scaleFactor), (int) (originalHeight * scaleFactor));

        // 再次检查图片宽度和高度，确保是50px（可能由于舍入误差等原因需要微调），若不符合则强制设置为50px
        if (originalImage.getWidth()!= 50) {
            originalImage.scale(50, 50);
        }
        if (originalImage.getHeight()!= 50) {
            originalImage.scale(50, 50);
        }

        setImage(originalImage);
    }

    // 水果移动的方法，定义了水果在游戏世界中的移动逻辑，但只有当水果未处于冰冻状态时才执行移动逻辑（通过新增的冰冻状态判断实现）
    public void move() {
        if (!isFrozen()) { 
            int moveDirection = -1;

            directionUpdateCounter++;
            if (directionUpdateCounter >= directionUpdateInterval) {
                // 随机生成移动方向，但要排除上一次移动的方向，避免水果来回反复移动等情况，使移动更自然
                do {
                    moveDirection = Greenfoot.getRandomNumber(4); // 随机生成移动方向，0:上，1:左，2:下，3:右
                } while (moveDirection == lastMoveDirection);
                lastMoveDirection = moveDirection; // 更新上一次移动的方向

                // 随机决定移动1格还是2格，增加移动的随机性
                int moveDistance = Greenfoot.getRandomNumber(2) + 1; // 生成1或2，表示移动的格数

                int initialX = getX();
                int initialY = getY();
                int newX = initialX;
                int newY = initialY;

                for (int i = 0; i < moveDistance; i++) {
                    switch (moveDirection) {
                        case 0:
                            newY = initialY - MyWorld.BLOCK_SIZE;
                            break;
                        case 1:
                            newX = initialX - MyWorld.BLOCK_SIZE;
                            break;
                        case 2:
                            newY = initialY + MyWorld.BLOCK_SIZE;
                            break;
                        case 3:
                            newX = initialX + MyWorld.BLOCK_SIZE;
                            break;
                    }

                    // 检查每移动1格后是否超出地图边界或者遇到障碍物（墙、其他障碍物等），如果是则停止移动，确保水果移动在合理范围内且符合游戏场景设定
                    if (!isValidMove(newX, newY)) {
                        break;
                    }

                    initialX = newX;
                    initialY = newY;
                }

                if (isValidMove(newX, newY)) {
                    setLocation(newX, newY);
                }

                directionUpdateCounter = 0; // 重置方向更新计数器，准备下一次方向更新循环
            }
        }
    }

    // 新增方法用于检查给定位置是否是有效可移动位置（未超出边界且无障碍物），用于在水果移动过程中判断能否移动到目标位置
    private boolean isValidMove(int targetX, int targetY) {
        MyWorld myWorld = (MyWorld) getWorld();
        if (targetX < 0 || targetX >= (myWorld.getMAP_WIDTH() * myWorld.getBLOCK_SIZE()) ||
                targetY < 0 || targetY >= (myWorld.getMAP_HEIGHT() * myWorld.getBLOCK_SIZE())) {
            return false;
        }

        // 检查是否与其他水果重叠（这里可根据实际需求确定是否允许重叠等情况，目前按照不能重叠处理），避免水果不合理的重叠显示等问题
        List<Fruit> fruitsAtTarget = myWorld.getObjectsAt(targetX, targetY, Fruit.class);
        if (!fruitsAtTarget.isEmpty()) {
            return false;
        }

        // 检查是否与怪物重叠，若重叠则不能移动到该位置，可能会触发其他游戏逻辑（比如被怪物吃掉等情况，具体看整体游戏设计）
        List<Monster> monstersAtTarget = myWorld.getObjectsAt(targetX, targetY, Monster.class);
        if (!monstersAtTarget.isEmpty()) {
            return false;
        }

        // 检查是否与其他障碍物（这里假设Wall、Obstacle、SpecialObstacle等都是障碍物类型，可根据实际调整）重叠，遇到障碍物则不能移动过去
        List<Actor> obstaclesAtTarget = myWorld.getObjectsAt(targetX, targetY, Actor.class);
        for (Actor actor : obstaclesAtTarget) {
            if (actor instanceof Wall || actor instanceof Obstacle || actor instanceof SpecialObstacle || actor instanceof Ice || actor instanceof House|| actor instanceof Player) {
                return false;
            }
        }

        return true;
    }

    // 判断目标位置是否为空的方法，代码保持不变，可根据实际情况决定是否保留（目前暂时保留，因为和isValidMove有一定区别，一个侧重整体移动合法性，一个侧重目标位置是否为空）
    private boolean isTargetEmpty(int targetX, int targetY) {
        MyWorld myWorld = (MyWorld) getWorld();
        // 检查是否与其他水果重叠
        List<Fruit> fruitsAtTarget = myWorld.getObjectsAt(targetX, targetY, Fruit.class);
        if (!fruitsAtTarget.isEmpty()) {
            return false;
        }
        // 检查是否与怪物重叠
        List<Monster> monstersAtTarget = myWorld.getObjectsAt(targetX, targetY, Monster.class);
        if (!monstersAtTarget.isEmpty()) {
            return false;
        }
        // 检查是否与其他障碍物（这里假设Wall、Obstacle、SpecialObstacle等都是障碍物类型，可根据实际调整）重叠
        List<Actor> obstaclesAtTarget = myWorld.getObjectsAt(targetX, targetY, Actor.class);
        for (Actor actor : obstaclesAtTarget) {
            if (actor instanceof Wall || actor instanceof Obstacle || actor instanceof SpecialObstacle || actor instanceof Ice || actor instanceof House) {
                return false;
            }
        }
        return true;
    }

    // 获取水果是否已被吃掉的状态，供其他类或方法判断使用
    public boolean isEaten() {
        return isEaten;
    }

    // 用于设置水果为已被吃掉状态，改变水果的相应状态标记，可能会触发其他与水果被吃掉相关的游戏逻辑（比如更新分数、关卡进度等）
    public void setEaten() {
        isEaten = true;
    }

    // 可传入参数来设置水果是否被吃掉的状态，更灵活地控制水果的这个属性状态
    public void setEaten(boolean eaten) {
        isEaten = eaten;
    }

    // 获取水果的原始图像，可用于在需要恢复原始图像显示等情况下使用
    public GreenfootImage getOriginalImage() {
        return originalImage;
    }

    // 隐藏水果图片，使水果在游戏世界中“消失”，通过设置为空白图像实现，并标记为已经被吃掉（可根据实际需求调整是否这样关联这两个操作）
    public void hideFruit() {
        if (!isEaten) {
            setImage(new GreenfootImage(50, 50)); // 设置为空白图像，使水果“消失”
            isEaten = true; // 标记为已经被吃掉
        }
    }

    // 恢复水果图片，将水果图像还原到初始的对应阶段图像（如椰子或香蕉图像），并标记为未被吃掉，用于水果重新出现等情况
    public void restoreFruit() {
        if (isEaten) {
            if (stage == 1) {
                setImage("coconut.png");
            } else if (stage == 2) {
                setImage("banana.png");
            }
            scaleImageToFitSize(); // 恢复原始图像大小
            isEaten = false; // 标记为未被吃掉
        }
    }

    // 新增方法获取对应冰封图片文件名，根据水果所属阶段返回相应的冰封状态下图片文件名，方便后续加载对应图像来显示冰冻效果等操作
    public String getFrozenImageFileName() {
        if (stage == 1) {
            return "icecoconut.png";
        } else if (stage == 2) {
            return "icebanana.png";
        }
        return null;
    }

    // 新增方法用于判断水果是否处于冰冻状态，供其他地方判断使用，以此来决定是否执行与冰冻相关的不同逻辑（比如能否移动等）
    public boolean isFrozen() {
        return isFrozen;
    }

    // 新增方法用于设置水果为冰冻状态，可在游戏中根据相应条件（比如玩家使用冰冻技能等情况）来改变水果的这个状态属性
    public void setFrozen(boolean frozen) {
        isFrozen = frozen;
    }
}