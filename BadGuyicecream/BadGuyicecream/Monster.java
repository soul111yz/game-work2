import greenfoot.*;

// 怪物类，继承自Greenfoot的Actor类，代表游戏中的怪物对象，包含了怪物自身的属性以及移动、碰撞检测等相关行为逻辑
public class Monster extends Actor {
    // 怪物的移动速度，用于控制怪物在游戏世界中每帧移动的距离，数值越大移动越快
    private int speed; 
    // 标记怪物是否死亡，初始化为false，表示怪物初始状态是存活的，当满足死亡条件时会被设置为true
    private boolean isDead = false; 
    // 怪物移动方向，1表示向右， -1表示向左，用于决定怪物在水平方向上的移动趋势
    private int direction; 
    // 怪物朝右时的图像，用于根据怪物朝向显示对应的正确图片，增强视觉效果
    private GreenfootImage rightImage; 
    // 怪物朝左时的图像
    private GreenfootImage leftImage; 
    // 记录怪物当前朝向，1表示向右， -1表示向左，初始化为随机方向，以便怪物初始出现时可能朝左或朝右移动
    private int facingDirection; 

    // 构造函数，用于创建怪物对象，设置怪物初始图像、速度等属性，完成初始化相关操作
    public Monster() {
        // 加载怪物朝右的图片，假设已经有对应的"monster_right.png"图片资源存在，可根据实际情况调整文件名
        rightImage = new GreenfootImage("monster_right.png");
        // 加载怪物朝左的图片
        leftImage = new GreenfootImage("monster_left.png");
        // 根据初始朝向方向设置图片，初始化为随机方向（通过随机数判断是朝左还是朝右）
        facingDirection = Greenfoot.getRandomNumber(2) == 0? -1 : 1;
        if (facingDirection == 1) {
            setImage(rightImage);
        } else {
            setImage(leftImage);
        }

        scaleImageToFitSize(); // 调用缩放图片方法，使怪物图片适配一定大小（这里是适配到合适的游戏显示尺寸）

        speed = 5;
        // 同样随机初始化怪物的移动方向，决定初始是向左还是向右移动
        direction = Greenfoot.getRandomNumber(2) == 0? -1 : 1;
    }
    
    // 新增方法用于缩放图片以适配50px正方形大小，通过计算合适的缩放比例，保证怪物图片在游戏中显示尺寸统一且合适
    private void scaleImageToFitSize() {
        GreenfootImage currentImage = getImage();
        int originalWidth = currentImage.getWidth();
        int originalHeight = currentImage.getHeight();

        // 计算缩放比例，使图片最长边为50px，取宽度和高度缩放比例中的较小值，避免图片变形
        double scaleFactor = Math.min(50.0 / originalWidth, 50.0 / originalHeight);

        currentImage.scale((int) (originalWidth * scaleFactor), (int) (originalHeight * scaleFactor));

        setImage(currentImage);
    }

    // 怪物移动方法，定义了怪物在游戏世界中的移动逻辑，包含移动、方向改变、碰撞检测等相关操作
    public void move() {
        MyWorld myWorld = (MyWorld) getWorld();
        // 如果怪物未死亡且游戏世界中允许怪物移动（通过canMonstersMove方法判断，可能与游戏关卡状态等相关）
        if (!isDead && myWorld.canMonstersMove()){
            // 计算怪物下一个位置的X坐标，根据当前位置、移动速度和移动方向来确定
            int newX = getX() + speed * direction;
            // 检查新位置是否在游戏世界范围内（X坐标大于等于0且小于世界宽度）并且可以移动到该位置（通过canMoveTo方法判断是否有障碍物等）
            if (newX >= 0 && newX < getWorld().getWidth() && canMoveTo(newX, getY())) {
                setLocation(newX, getY());

                // 根据移动方向更新朝向方向，并设置对应的图片，保持怪物图像与实际移动方向一致
                if (direction == 1) {
                    facingDirection = 1;
                    setImage(rightImage);
                } else {
                    facingDirection = -1;
                    setImage(leftImage);
                }

                scaleImageToFitSize();

                // 检查是否碰到水果，调用相应方法进行处理（比如碰到冰冻水果或普通水果的不同逻辑）
                checkFruitCollision(newX, getY());

            } else {
                direction = -direction; // 碰到边界或障碍物，改变方向，使怪物往相反方向移动

                // 根据新的方向更新朝向方向，并设置对应的图片
                if (direction == 1) {
                    facingDirection = 1;
                    setImage(rightImage);
                } else {
                    facingDirection = -1;
                    setImage(leftImage);
                }

                scaleImageToFitSize();
            }
        }
    }

    // 检查怪物是否碰到水果的私有方法，根据怪物所在位置检测是否与水果对象重叠，并根据水果状态执行不同操作
    private void checkFruitCollision(int x, int y) {
        // 获取与怪物在特定位置偏移量处的一个指定类型（Fruit类）的对象，即检测是否碰到水果
        Actor fruit = getOneObjectAtOffset(x - getX(), y - getY(), Fruit.class);
        if (fruit!= null) {
            Fruit fruitObj = (Fruit) fruit;
            if (fruitObj.isFrozen()) {
                // 如果碰到的水果是冰冻状态，怪物掉头，改变移动方向
                turnAround();
            } else {
                // 如果是普通水果，隐藏水果图片，可能意味着水果被怪物“吃掉”或触发其他相关游戏逻辑（具体看整体设计）
                fruitObj.hideFruit(); 
            }
        }
    }

    // 判断是否可以移动到指定位置的私有方法，通过检测指定位置是否有障碍物（除水果外的其他阻挡对象）来决定能否移动
    private boolean canMoveTo(int x, int y) {
        // 获取在指定位置偏移量处的一个任意类型（Actor类）的对象，用于检测是否有障碍物
        Actor obstacle = getOneObjectAtOffset(x - getX(), y - getY(), Actor.class);
        // 如果障碍物不是水果，返回 false，表示不能移动
        if (obstacle!= null && obstacle instanceof Fruit) {
            return true; // 忽略水果，继续移动，说明怪物可以从水果所在位置经过
        }
        // 返回判断结果，来处理其他的障碍物，若没有障碍物（obstacle为null）则可以移动，返回true；有障碍物则返回false
        return obstacle == null;
    }

    // 怪物死亡方法
    public void die() {
        isDead = true;
        getWorld().removeObject(this);
    }

    // 怪物掉头方法，改变怪物的移动方向，并根据新方向设置对应的图片，调整图片大小以适配显示要求
    public void turnAround() {
        direction = -direction; // 改变移动方向
        if (direction == 1) {
            setImage(rightImage);
        } else {
            setImage(leftImage);
        }
        scaleImageToFitSize(); // 调整图片大小
    }
}