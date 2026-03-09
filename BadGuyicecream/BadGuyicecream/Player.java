import greenfoot.*;
import java.util.List;
import java.util.ArrayList;
// 玩家类，继承自Greenfoot的Actor类
public class Player extends Actor {
    // 以下变量用于记录玩家在游戏世界中的位置信息
    private int i;
    private int j;
    // 玩家的移动速度，用于控制每次移动的距离，数值越大在单位时间内移动越远
    public int moveSpeed = 1;
    // 标记玩家是否死亡，初始为false，当玩家满足死亡条件（如碰到怪物等）时会被设为true
    public boolean isDead = false;
    // 玩家朝上方向的图像，用于根据玩家移动方向切换对应的正确图片，增强视觉效果
    private GreenfootImage upImage;
    // 玩家朝下方向的图像
    private GreenfootImage downImage;
    // 玩家朝左方向的图像
    private GreenfootImage leftImage;
    // 玩家朝右方向的图像
    private GreenfootImage rightImage;
    // 标记玩家是否正在移动，方便在其他逻辑中判断玩家当前的运动状态
    public boolean isMoving = false;
    // 记录玩家当前移动方向，0表示无移动方向，1表示上，2表示下，3表示左，4表示右，用于控制玩家移动逻辑和显示对应的方向图像
    public int moveDirection = 0; 
    // 用于控制移动间隔的计时器，每经过一帧（游戏循环一次）会变化，达到一定值后允许玩家再次移动，以此控制移动频率
    public int moveTimer = 0; 
    // 移动间隔时间，可根据需要调整，决定玩家连续两次移动操作之间的间隔帧数等
    private static final int MOVE_INTERVAL = 10; 
    // 定义椰子水果冰冻状态下对应的图片文件名，用于后续处理冰冻水果相关的图像显示
    private static final String COCONUT_ICE_IMAGE = "icecoconut.png";
    // 定义香蕉水果冰冻状态下对应的图片文件名
    private static final String BANANA_ICE_IMAGE = "icebanana.png";
    // 记录上一次执行吐冰或破冰操作的时间（以系统毫秒数为单位），用于实现操作的时间间隔控制，避免频繁操作 
    // 记录上一次执行吐冰或破冰操作的时间（以系统毫秒数为单位）
    private long lastIceActionTime = 0; 
    // 定义吐冰或破冰操作的时间间隔，这里设置为1秒，1000毫秒
    private static final int ICE_ACTION_INTERVAL = 1000; 
   // 新增成员变量，用于存储吐冰音效对象
    private GreenfootSound spitIceSound;
    // 构造函数，设置玩家初始图像并进行缩放
    public Player() {
        // 加载各个方向的图片
        upImage = new GreenfootImage("player_up.png");
        rightImage = new GreenfootImage("player_right.png");
        downImage = new GreenfootImage("player_down.png");
        leftImage = new GreenfootImage("player_left.png");

        // 根据初始朝向方向设置图片
        setImage(upImage);

        // 对各个方向图片进行缩放操作，使图片最长边为50px
        scaleImage(upImage);
        scaleImage(rightImage);
        scaleImage(downImage);
        scaleImage(leftImage);
        
         // 初始化吐冰音效对象
        try {
            spitIceSound = new GreenfootSound("spitice.wav");
        } catch (Exception e) {
            System.out.println("加载吐冰音效出错: " + e.getMessage());
            // 可以根据具体情况做进一步处理，比如提示玩家音效加载失败等
        }
    }

    // 方法用于缩放图片，使其最长边为50px
    private void scaleImage(GreenfootImage image) {
        int originalWidth = image.getWidth();
        int originalHeight = image.getHeight();

        // 计算缩放比例，使图片最长边为50px
        double scaleFactor = Math.min(50.0 / originalWidth, 50.0 / originalHeight);

        image.scale((int) (originalWidth * scaleFactor), (int) (originalHeight * scaleFactor));
    }

    // 向左移动方法
    public void moveLeft() {
        if (!isDead) {
            MyWorld myWorld = (MyWorld) getWorld();
            setImage(leftImage);
            int newX = (getX() / myWorld.getBLOCK_SIZE()) * myWorld.getBLOCK_SIZE() - myWorld.getBLOCK_SIZE();
            if (newX >= 0 && canMoveTo(newX, getY())) {
                setLocation(newX, getY());
            }
        }
    }

    // 向下移动方法
    public void moveDown() {
        if (!isDead) {
            isMoving = true;
            MyWorld myWorld = (MyWorld) getWorld();
             setImage(downImage);
            int newY = getY() + myWorld.getBLOCK_SIZE();
            if (newY < getWorld().getHeight() && canMoveTo(getX(), newY)) {
                setLocation(getX(), newY);
            }
            isMoving = false;
            moveTimer = MOVE_INTERVAL;
        }
    }

    // 向右移动方法
    public void moveRight() {
        if (!isDead) {
            MyWorld myWorld = (MyWorld) getWorld();
            setImage(rightImage);
            int newX = getX() + myWorld.getBLOCK_SIZE();
            if (newX < myWorld.getWidth() && canMoveTo(newX, getY())) {
                setLocation(newX, getY());
            }
            isMoving = false;
            moveTimer = MOVE_INTERVAL;
        }
    }

    // 向上移动方法
    public void moveUp() {
        if (!isDead) {
            MyWorld myWorld = (MyWorld) getWorld();
             setImage(upImage);
            // 计算新的Y坐标，确保玩家正好位于格子边界上
            int newY = (getY() / myWorld.getBLOCK_SIZE()) * myWorld.getBLOCK_SIZE() - myWorld.getBLOCK_SIZE();
            if (newY >= 0 && canMoveTo(getX(), newY)) {
                setLocation(getX(), newY);
            }
        }
    }

    // 判断是否可以移动到指定位置
    private boolean canMoveTo(int x, int y) {
        Actor obstacle = getOneObjectAtOffset(x - getX(), y - getY(), Actor.class);
    if (obstacle!= null) {
        if (obstacle instanceof Fruit && ((Fruit) obstacle).isFrozen()) {
            return false; // 如果是冰冻水果，阻止玩家移动
        }
        if (obstacle instanceof Obstacle) {  // 添加对新障碍物的判断
                return false;
            }
        if (!(obstacle instanceof Fruit)) {
            return false;
        }
    }
    MyWorld myWorld = (MyWorld) getWorld();
    return x >= 0 && x < myWorld.getWidth() && y >= 0 && y < myWorld.getHeight();
}

    // 创建冰块方法
    public void createIce() {
        int newIceI = 0;
        int newIceJ = 0;

        switch (getRotation()) {
            case 0:
                newIceI = getY() / 50 - 1;
                newIceJ = getX() / 50;
                break;
            case 90:
                newIceI = getY() / 50;
                newIceJ = getX() / 50 - 1;
                break;
            case 180:
                newIceI = getY() / 50 + 1;
                newIceJ = getX() / 50;
                break;
            case 270:
                newIceI = getY() / 50;
                newIceJ = getX() / 50 + 1;
                break;
        }
        

        if (canCreateIceAt(newIceI, newIceJ)) {
            World currentWorld = getWorld();
            if (currentWorld.getObjectsAt(newIceJ * 50, newIceI * 50, Actor.class).isEmpty()) {
                currentWorld.addObject(new Ice(), newIceJ * 50, newIceI * 50);
            }
        }
    }

    // 判断是否在水果上
    public boolean isOnFruit() {
        Fruit fruit = (Fruit) getOneIntersectingObject(Fruit.class);
        return fruit!= null &&!fruit.isEaten();
    }

    // 判断是否可以在指定位置创建冰块
    private boolean canCreateIceAt(int x, int y) {
        MyWorld myWorld = (MyWorld) getWorld();
        if (x < 0 || y < 0 || x >= myWorld.getWidth() || y >= myWorld.getHeight()) {
            return false; // 超出边界
        }
        List<Actor> obstacles = myWorld.getObjectsAt(x, y, Actor.class);
        for (Actor obstacle : obstacles) {
            if (obstacle instanceof Ice || obstacle instanceof Wall || obstacle instanceof Monster|| obstacle instanceof Obstacle ) {
                return false; // 碰到冰块、怪兽、障碍物或墙时停止
            }
        }
        return true;
    }

    // 吃掉水果方法
    public void consumeFruit(Fruit fruit) {
        if (fruit!= null &&!fruit.isEaten()) {
            fruit.setEaten(true); // 标记为已吃
            MyWorld myWorld = (MyWorld) getWorld();
            myWorld.removeFruitFromWorld(fruit); // 通知 MyWorld 移除水果
        }
    }

    // 新增方法，用于在MyWorld类中判断是否与怪物类的实例接触
    public boolean isTouchingMonster() {
        return isTouching(Monster.class);
    }

    // 玩家死亡方法
    public void die() {
        isDead = true;
        setImage("dead.png");
        scaleDeadImageToFitSize();
       
    }

    // 新增方法用于缩放死亡图片以适配50px正方形大小
    private void scaleDeadImageToFitSize() {
        GreenfootImage originalImage = getImage();
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        // 计算缩放比例，使图片最长边为50px
        double scaleFactor = Math.min(50.0 / originalWidth, 50.0 / originalHeight);

        originalImage.scale((int) (originalWidth * scaleFactor), (int) (originalHeight * scaleFactor));

        setImage(originalImage);
    }

    // 获取与玩家相交的水果列表
    public List<Fruit> getIntersectingFruits() {
        return getObjectsAtOffset(0, 0, Fruit.class);
    }

    // 吐冰或破冰的核心方法，添加时间间隔判断
    public void createIcetwo() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastIceActionTime >= ICE_ACTION_INTERVAL) {
            MyWorld myWorld = (MyWorld) getWorld();
            int blockSize = myWorld.getBLOCK_SIZE();
            int dx = 0, dy = 0;

            // 根据玩家方向确定操作方向
            switch (moveDirection) {
                case 1:
                    dy = -1;
                    break; // 上
                case 2:
                    dy = 1;
                    break;  // 下
                case 3:
                    dx = -1;
                    break; // 左
                case 4:
                    dx = 1;
                    break;  // 右
            }

            int x = getX() + dx * blockSize;
            int y = getY() + dy * blockSize;

            // 检查前方是否有冰块
              List<Ice> iceBlocks = myWorld.getObjectsAt(x, y, Ice.class);
            if (!iceBlocks.isEmpty()) {
                breakIce(x, y, dx, dy); // 前方是冰块时，破冰
            } else {
                spitIce(x, y, dx, dy);  // 前方无冰块时，吐冰
             // 新增代码，播放吐冰音效
              if (spitIceSound!= null) {
                spitIceSound.play();
               }
            }
            lastIceActionTime = currentTime;
        }
    }

    // 破冰逻辑
private void breakIce(int startX, int startY, int dx, int dy) {
    MyWorld myWorld = (MyWorld) getWorld();
    int blockSize = myWorld.getBLOCK_SIZE();

    int x = startX, y = startY;
    while (true) {
        List<Ice> iceBlocks = myWorld.getObjectsAt(x, y, Ice.class);
        List<Fruit> allFruits = myWorld.getObjectsAt(x, y, Fruit.class);
        List<Fruit> frozenFruits = new ArrayList<>();

        // 遍历当前格子的所有水果，筛选出冰冻水果添加到frozenFruits列表
        for (Fruit fruit : allFruits) {
            if (fruit.isFrozen()) {
                frozenFruits.add(fruit);
            }
        }
        // 如果当前格子既没有冰块也没有冰冻水果，停止
        if (iceBlocks.isEmpty() && frozenFruits.isEmpty()) break;

        // 只要有普通冰块存在，就移除普通冰块，改为启动冰块破碎流程
        if (!iceBlocks.isEmpty()) {
            for (Ice ice : iceBlocks) {
                ice.startBreaking();
            }               
        // 通知周围的SpecialObstacle实例附近发生了破冰操作（添加时间条件判断）
        List<SpecialObstacle> specialObstaclesNearby = getSpecialObstaclesNearby(x, y, myWorld);
        long currentTime = System.currentTimeMillis();
        for (SpecialObstacle specialObstacle : specialObstaclesNearby) {
            if (currentTime - specialObstacle.creationTime > 10000) {
                specialObstacle.setIceBrokenNearby(true);
            }
        }
        }

        // 仅当有冰冻水果时，才执行解冻冰冻水果的操作
        if (!frozenFruits.isEmpty()) {
            // 解冻冰冻水果
            for (Fruit fruit : frozenFruits) {
                unfreezeFruit(fruit);
            }
        }

        // 前进到下一格
        x += dx * blockSize;
        y += dy * blockSize;
    }
}

// 新增辅助方法，用于获取指定位置附近的SpecialObstacle实例列表
private List<SpecialObstacle> getSpecialObstaclesNearby(int x, int y, MyWorld myWorld) {
    List<SpecialObstacle> nearbySpecialObstacles = new ArrayList<>();
    int range = myWorld.getBLOCK_SIZE(); // 定义附近范围，这里简单以一个格子边长为范围，可根据实际调整
    List<Actor> actorsInRange = myWorld.getObjectsInRange(range, Actor.class);
    for (Actor actor : actorsInRange) {
        if (actor instanceof SpecialObstacle) {
            nearbySpecialObstacles.add((SpecialObstacle) actor);
        }
    }
    return nearbySpecialObstacles;
}
    // 解冻冰冻水果
    private void unfreezeFruit(Fruit fruit) {
        if (fruit!= null&&fruit.isFrozen()) {
            GreenfootImage normalImage = null;
            if (fruit.stage == 1) {
                normalImage = new GreenfootImage("coconut.png");
            } else if (fruit.stage == 2) {
                normalImage = new GreenfootImage("banana.png");
            }
            normalImage.scale(50, 50);
            fruit.setImage(normalImage);
            fruit.setFrozen(false); // 确保解冻后将冰冻状态设置为false
            fruit.setEaten(false); // 恢复为可拾取状态
          // 新增代码，重新关联原始图片，确保再次冰冻时能正确获取冰封图片
          fruit.originalImage = new GreenfootImage(getImage()); 
        }
    }

// 吐冰逻辑
private void spitIce(int startX, int startY, int dx, int dy) {
    MyWorld myWorld = (MyWorld) getWorld();
    int blockSize = myWorld.getBLOCK_SIZE();

    int x = startX;
    int y = startY;
    while (true) {
        // 检查是否超出地图边界
        if (!canCreateIceAt(x, y)) break;

        // 获取当前位置的障碍物
        List<Actor> obstacles = myWorld.getObjectsAt(x, y, Actor.class);
        boolean stop = false;
        // 用于标记是否遇到需要特殊处理的对象（水果）
        boolean hasFruit = false;
        for (Actor actor : obstacles) {
            if (actor instanceof Monster) {
                stop = true; // 停止生成冰块
                // 怪兽遇到冰块后转向
                ((Monster) actor).turnAround();
                break;
            } else if (actor instanceof House || actor instanceof IceHouse) {
                stop = true; // 遇到大房子或冰房子，停止生成冰块
                break;
            } else if (actor instanceof Fruit) {
                Fruit fruit = (Fruit) actor;
                hasFruit = true;
                // 冻结水果但不阻止冰块生成，新增设置对应冰封图片逻辑
                freezeFruit(fruit);
            } else if (actor instanceof Ice || actor instanceof Wall || actor instanceof Obstacle || actor instanceof SpecialObstacle) {
                // 碰到冰块、墙体、障碍物或SpecialObstacle，停止生成
                stop = true;
                break;
            }
        }

        if (stop) break;

        // 根据是否遇到水果来决定添加哪种类型的图片（冰块或对应水果冰封图片）
        GreenfootImage imageToAdd = null;
        if (hasFruit) {
            // 获取当前位置第一个水果对象（假设同一位置不会有多个水果重叠，如果有需要更复杂判断）
            Fruit fruitAtPos = (Fruit) getOneObjectAtOffset(x - getX(), y - getY(), Fruit.class);
            if (fruitAtPos!= null) {
                String frozenImageFileName = fruitAtPos.getFrozenImageFileName();
                if (frozenImageFileName!= null) {
                    imageToAdd = new GreenfootImage(frozenImageFileName);
                }
            }
        }
        if (imageToAdd == null) {
            // 如果没有水果，添加普通冰块图片（这里假设Ice类有对应的默认图片表示普通冰块）
            imageToAdd = new GreenfootImage("ice.png");
        }

        // 创建对应的图片对象（冰块或冰封水果图片）
        myWorld.addObject(new Ice(imageToAdd), x, y);

        // 更新坐标，继续生成下一格
        x += dx * blockSize;
        y += dy * blockSize;
    }
}

    // 冻结水果
    private void freezeFruit(Fruit fruit) {
       if (fruit!= null &&!fruit.isFrozen()) {  // 添加判断，只有当水果未冰冻时才执行冰冻操作
        GreenfootImage frozenImage = getFrozenImageForFruit(fruit);
        fruit.setImage(frozenImage);
        fruit.setEaten(true); 
        fruit.setFrozen(true); 
    }
    }

    // 根据水果阶段获取对应的冻结图片
    private GreenfootImage getFrozenImageForFruit(Fruit fruit) {
        if (fruit.stage == 1) {
            return new GreenfootImage(COCONUT_ICE_IMAGE);
        } else if (fruit.stage == 2) {
            return new GreenfootImage(BANANA_ICE_IMAGE);
        }
        return null;
    }
    
}