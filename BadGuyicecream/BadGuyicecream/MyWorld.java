import greenfoot.*;
import java.util.ArrayList;
import java.util.List;
import java.util.*;

// 游戏世界类，继承自Greenfoot的World类
public class MyWorld extends World
{
    // 地图相关常量
    public static final int MAP_HEIGHT = 14;
    public static final int MAP_WIDTH = 16;
    public static final int BLOCK_SIZE = 50;
    private int totalFruits;
    // 分别存储水果1和水果2的列表
    private ArrayList<Fruit> remainingFruits1;
    private ArrayList<Fruit> remainingFruits2;
   // 新增成员变量，用于记录胜利后是否需要进入下一关
    private boolean winAndNextLevel = false;
    // 新增成员变量，用于记录进入下一关的倒计时
    private int nextLevelTimer = 0;
     // 新增变量，用于控制怪物移动状态，初始为true表示可移动
    private boolean monstersCanMove = true;

    // 关卡相关变量
    public int currentLevel = 1;
    private int stage = 1;
    private int stage1TargetNum;
    private int stage2TargetNum;
    public int achievedNum = 0;
    private int remainingFruitsStage1;
    private int remainingFruitsStage2;

    // 玩家对象
    private Player player;

    // 怪物数组
    private Monster[] monsters;

    // 音效相关
    private GreenfootSound backgroundMusic;
    private GreenfootSound eatSound;
    private GreenfootSound createIceSound;
    private GreenfootSound winSound;
    private GreenfootSound loseSound;
    
     // 新增成员变量，用于记录背景音乐是否正在播放，初始化为true表示默认正在播放
    private boolean isBackgroundMusicPlaying = true; 

    // 游戏是否结束标志
    private boolean gameOver = false;
    private long startTime; // 用于记录第四关开始的时间
    private boolean isFourthLevel = false; // 用于标记是否处于第四关
   // 关卡地图数据，修改为char[][][]类型
    public char[][][] levelMaps = {
    {   // 第一关地图数据
        {'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e'},
        {'e', 'e', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'e', 'e'},
        {'e', 'a', '2', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', '2', 'f', 'e'},
        {'e', 'f', 'i', 'i', 'f', 'i', 'i', '2', '2', 'i', 'i', 'f', 'i', 'i', 'f', 'e'},
        {'e', 'f', 'm', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'e'},
        {'e', 'f', 'i', 'i', 'f', '1', 'f', 'f', 'f', 'f', '1', 'f', 'i', 'i', 'f', 'e'},
        {'e', 'f', 'i', 'i', 'f', '2', 'f', 'h', '0', 'f', '2', 'f', 'i', 'i', 'f', 'e'},
        {'e', 'f', 'i', 'i', 'f', '2', 'f', '0', '0', 'f', '2', 'f', 'i', 'i', 'f', 'e'},
        {'e', 'f', 'i', 'i', 'f', '1', 'f', 'f', 'f', 'f', '1', 'f', 'i', 'i', 'f', 'e'},
        {'e', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'n', 'f', 'e'},
        {'e', 'f', 'i', 'i', 'f', 'i', 'i', '2', '2', 'i', 'i', 'f', 'i', 'i', 'f', 'e'},
        {'e', 'f', '2', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', '2', 'b', 'e'},
        {'e', 'e', 'f', 'f', 'f', 'f', 'p', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'e', 'e'},
        {'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e'}
    },
    {   // 第二关地图数据
        {'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e'},
        {'e', '2', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', '2', 'e'},
        {'e', '1', '1', 'm', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', '1', '1', 'e'},
        {'e', '1', 'i', 'i', 'i', 'f', 'f', 'p', 'f', 'f', 'f', 'i', 'i', 'i', '1', 'e'},
        {'e', 'f', 'i', '2', '2', 'f', 'f', 'f', 'n', 'f', 'f', '2', '2', 'i', 'f', 'e'},
        {'e', 'i', 'i', '2', '2', '0', '0', '0', '0', '0', 'f', '2', '2', 'i', 'i', 'e'},
        {'e', '2', 'i', 'f', 'f', '0', '0', 'z', '0', '0', 'f', 'f', 'f', 'i', '2', 'e'},
        {'e', 'i', 'i', 'f', 'f', '0', '0', '0', '0', '0', 'f', 'f', 'f', 'i', 'i', 'e'},
        {'e', 'f', 'i', '2', '2', '0', '0', '0', '0', '0', 'f', '2', '2', 'i', 'f', 'e'},
        {'e', 'f', 'i', '2', '2', 'a', 'f', 'f', 'f', 'f', 'f', '2', '2', 'i', 'f', 'e'},
        {'e', '1', 'i', 'i', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'i', 'i', 'i', '1', 'e'},
        {'e', '1', '1', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'b', '1', '1', 'e'},
        {'e', '2', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', '2', 'e'},
        {'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e'}
    },
      {   // 第三关地图数据
        {'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e'},
        {'e', '2', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', '2', 'e'},
        {'e', 'f', 's', 's', 's', 's', 's', 's', 's', 's', 's', 's', 's', 's', 'f', 'e'},
        {'e', 'm', '1', '1', '1', 'f', 'f', '2', '2', 'f', 'f', '1', '1', '1', 'f', 'e'},
        {'e', 'f', 's', 's', 's', 's', 's', 's', 's', 's', 'i', 'i', 's', 's', 'f', 'e'},
        {'e', 'f', '2', '2', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', '2', '2', 'n', 'e'},
        {'e', 'f', 's', 's', 's', 's', 's', 's', 's', 's', 's', 's', 's', 's', 'f', 'e'},
        {'e', 'a', '2', '2', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', '2', '2', 'f', 'e'},
        {'e', 'f', 's', 's', 'i', 'i', 's', 's', 's', 's', 's', 's', 's', 's', 'f', 'e'},
        {'e', 'f', '1', '1', '1', 'f', 'f', '2', '2', 'f', 'f', '1', '1', '1', 'b', 'e'},
        {'e', 'f', 's', 's', 's', 's', 's', 's', 's', 's', 's', 's', 's', 's', 'f', 'e'},
        {'e', '2', 'f', 'f', 'f', 'f', 'f', 'p', 'f', 'f', 'f', 'f', 'f', 'f', '2', 'e'},
        {'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e'},
        {'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e'}
    },
    {   // 第四关地图数据
        {'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e'},
        {'e', 'm', 'f', 'f', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'f', 'f', 'n', 'e'},
        {'e', 'e', 'e', 'e', 'e', 'e', 'e', 'h', '0', 'e', 'e', 'e', 'e', 'e', 'e', 'e'},
        {'e', 'e', 'h', '0', 'e', 'e', 'e', '0', '0', 'e', 'e', 'e', 'h', '0', 'e', 'e'},
        {'e', 'e', '0', '0', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', '0', '0', 'e', 'e'},
        {'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e'},
        {'e', 'p', 'f', 'f', 'f', 'f', 'f', 'f', '1', 'f', 'f', 'f', 'f', 'c', '2', 'e'},
        {'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e'},
        {'e', 'e', 'h', '0', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'h', '0', 'e', 'e'},
        {'e', 'e', '0', '0', 'e', 'e', 'e', 'h', '0', 'e', 'e', 'e', '0', '0', 'e', 'e'},
        {'e', 'e', 'e', 'e', 'e', 'e', 'e', '0', '0', 'e', 'e', 'e', 'e', 'e', 'e', 'e'},
        {'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e'},
        {'e', 'a', 'f', 'f', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'f', 'f', 'b', 'e'},
        {'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e'}
    },
    {   // 第五关地图数据
        {'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e'},
        {'e', 's', 'e', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'e', 's', 'e'},
        {'e', 'e', '2', '2', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', '2', '2', 'e', 'e'},
        {'e', 'm', '2', '2', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', '2', '2', 'f', 'e'},
        {'e', 'f', 'f', 'f', 'h', '0', 'e', 's', 's', 'e', 'h', '0', 'f', 'f', 'f', 'e'},
        {'e', 'f', 'f', 'f', '0', '0', 'i', 'i', 'i', 'i', '0', '0', 'f', 'f', 'f', 'e'},
        {'e', 'f', 'f', 'f', 'f', 'f', 'f', '1', '1', 'f', 'f', 'f', 'f', 'f', 'n', 'e'},
        {'e', 'a', 'f', 'f', 'f', 'f', 'f', '1', '1', 'f', 'f', 'f', 'f', 'f', 'f', 'e'},
        {'e', 'f', 'f', 'f', 'h', '0', 'i', 'i', 'i', 'i', 'h', '0', 'f', 'f', 'f', 'e'},
        {'e', 'f', 'f', 'f', '0', '0', 'e', 's', 's', 'e', '0', '0', 'f', 'f', 'f', 'e'},
        {'e', 'f', '2', '2', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', '2', '2', 'b', 'e'},
        {'e', 'e', '2', '2', 'f', 'f', 'f', 'f', 'f', 'f', 'f', 'f', '2', '2', 'e', 'e'},
        {'e', 's', 'e', 'f', 'f', 'f', 'f', 'f', 'p', 'f', 'f', 'f', 'f', 'e', 's', 'e'},
        {'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e'}
      }
   };


    // 无参构造函数，用于初始化游戏世界，调用了一系列方法来完成初始设置，包括显示开始界面、初始化关卡以及加载音效等
    public MyWorld() {
        // 调用父类（World类）的构造函数，传入根据地图宽度、高度
        super(MAP_WIDTH * BLOCK_SIZE + 50, MAP_HEIGHT * BLOCK_SIZE + 50, 1);
        // 显示游戏开始界面，该界面会遮挡住游戏内容，通常用于展示一些初始信息、提示或者加载动画等
        showStartScreen();  
        // 初始化当前关卡，包括创建和布置关卡内的各种元素，如角色、道具、障碍物等
        initializeLevel();
        // 加载游戏所需的各种音效，如背景音乐、吃水果音效等
        loadSounds();
    }

    // 带参数的构造函数，用于根据指定的关卡数来初始化游戏世界，同样进行了显示开始界面、初始化对应关卡以及加载音效等操作
    public MyWorld(int level) {
        super(MAP_WIDTH * BLOCK_SIZE + 50, MAP_HEIGHT * BLOCK_SIZE + 50, 1);
        // 设置当前关卡数为传入的参数值
        this.currentLevel = level;
        showStartScreen();
        initializeLevel();
        loadSounds();
    }

    // 私有方法，用于显示游戏开始界面，创建一个StartScreen实例并添加到游戏世界中，同时设置其显示层级等属性
    private void showStartScreen() {
        // 创建一个开始界面的实例，具体界面的内容和样式由StartScreen类定义
        StartScreen startScreen = new StartScreen();
        // 将开始界面添加到游戏世界的中心位置
        addObject(startScreen, getWidth() / 2, getHeight() / 2);
        // 设置开始界面为当前世界的顶层对象
        setPaintOrder(StartScreen.class, Actor.class);
        // 设置开始界面的透明度为255，即完全不透明
        startScreen.getImage().setTransparency(255);  
    }

    // 用于开始游戏的方法，通过移除开始界面相关对象，来显示游戏实际内容，开启游戏进程
    public void startGame() {
        removeObjects(getObjects(StartScreen.class));
    }

    // 获取当前关卡数的方法，简单返回当前记录的关卡数值
    public int getCurrentLevel() {
        return currentLevel;
    }

    // 设置当前关卡数的方法，更新当前关卡数，并重新初始化对应关卡，以加载新关卡的游戏元素和设置等
    public void setCurrentLevel(int level) {
        this.currentLevel = level;
        initializeLevel();  
    }
    // 初始化当前关卡
    private void initializeLevel() {
        // 清除所有旧的对象
    removeObjects(getObjects(Player.class));
    removeObjects(getObjects(Monster.class));
    removeObjects(getObjects(Fruit.class));    
    removeObjects(getObjects(Text.class));
    removeObjects(getObjects(Ice.class));
    removeObjects(getObjects(Wall.class));  
    removeObjects(getObjects(House.class)); 
    removeObjects(getObjects(IceHouse.class));
    removeObjects(getObjects(Obstacle.class));
    removeObjects(getObjects(SpecialObstacle.class));   
    totalFruits = 0;
    remainingFruits1 = new ArrayList<>();
    remainingFruits2 = new ArrayList<>();

    // 创建玩家对象并添加到世界中
    player = new Player();
    addObject(player, getWidth() / 2 + 50, getHeight() / 2 + 50);

    // 创建怪物数组并添加怪物到世界中
    monsters = new Monster[4];
    for (int i = 0; i < 4; i++) {
        monsters[i] = new Monster();
        addObject(monsters[i], 50, 50);
    }

    // 加载当前关卡地图数据
    char[][] currentLevelMap = levelMaps[currentLevel - 1];
    for (int i = 0; i < MAP_HEIGHT; i++) {
        for (int j = 0; j < MAP_WIDTH; j++) {
            if (currentLevelMap[i][j] == 'e') {
                addObject(new Wall(), j * BLOCK_SIZE + 50, i * BLOCK_SIZE + 50);
            } else if (currentLevelMap[i][j] == 'f') {
                // 空地，可不添加特定对象
            } else if (currentLevelMap[i][j] == 'i') {
                addObject(new Ice(), j * BLOCK_SIZE + 50, i * BLOCK_SIZE + 50);
            } else if (currentLevelMap[i][j] == '1') {
                if (stage == 1) {
                    totalFruits++;
                    Fruit fruit = new Fruit(1);
                    addObject(fruit, j * BLOCK_SIZE + 50, i * BLOCK_SIZE + 50);
                    remainingFruits1.add(fruit);
                }
            } else if (currentLevelMap[i][j] == '2') {
                if (stage == 2) {
                    totalFruits++;
                    Fruit fruit = new Fruit(2);
                    addObject(fruit, j * BLOCK_SIZE + 50, i * BLOCK_SIZE + 50);
                    remainingFruits2.add(fruit);
                }
            } else if (currentLevelMap[i][j] == 'm') {
                monsters[0].setLocation(j * BLOCK_SIZE + 50, i * BLOCK_SIZE + 50);
            } else if (currentLevelMap[i][j] == 'n') {
                monsters[1].setLocation(j * BLOCK_SIZE + 50, i * BLOCK_SIZE + 50);
            } else if (currentLevelMap[i][j] == 'a') {
                monsters[2].setLocation(j * BLOCK_SIZE + 50, i * BLOCK_SIZE + 50);
            } else if (currentLevelMap[i][j] == 'b') {
                monsters[3].setLocation(j * BLOCK_SIZE + 50, i * BLOCK_SIZE + 50);
            } else if (currentLevelMap[i][j] == 'p') {
                player.setLocation(j * BLOCK_SIZE + 50, i * BLOCK_SIZE + 50);
            }else if (currentLevelMap[i][j] == 'h'&&(currentLevel == 1||currentLevel == 4||currentLevel == 5)) {
                House house = new House();
                addObject(house, j * BLOCK_SIZE + 75, i * BLOCK_SIZE + 75);
            } else if (currentLevel == 2&&currentLevelMap[i][j] == 'z') {
                IceHouse iceHouse = new IceHouse();
                addObject(iceHouse, j * BLOCK_SIZE + 75, i * BLOCK_SIZE + 75);
            }else if (currentLevelMap[i][j] == 's') {
                Obstacle obstacle = new Obstacle(); // 假设已经创建了Obstacle类，用于表示新的障碍物
                addObject(obstacle, j * BLOCK_SIZE + 50, i * BLOCK_SIZE + 50);
            }else if (currentLevelMap[i][j] == '0') {
                
            }else if (currentLevelMap[i][j] == 'c') {
                SpecialObstacle specialObstacle = new SpecialObstacle();
                addObject(specialObstacle, j * BLOCK_SIZE + 50, i * BLOCK_SIZE + 50);
            }
        }
    }
}

   // 加载游戏所需音效
   private void loadSounds() {
     try {
        backgroundMusic = new GreenfootSound("bgm" + currentLevel + ".wav");
        if (currentLevel!= 4) {
            // 使用GreenfootSound类提供的playLoop方法来实现循环播放效果
            backgroundMusic.playLoop();
        } else {
            // 仅播放一次（可根据实际需求调整这里的逻辑，比如也可以不播放等）
            backgroundMusic.play();
        }
     } catch (Exception e) {
        System.out.println("加载背景音乐出错: " + e.getMessage());
        // 这里可以根据具体情况做进一步处理，比如显示错误提示给玩家等
     }

    eatSound = new GreenfootSound("eatfruit.wav");
    createIceSound = new GreenfootSound("spitice.wav");
    winSound = new GreenfootSound("win.wav");
    loseSound = new GreenfootSound("dead.wav");
    }

// 游戏世界每一次执行循环（帧更新）时会调用此方法
public void act() {
    // 获取游戏世界中所有的水果对象列表，后续用于遍历操作每个水果对象
    List<Fruit> fruits = getObjects(Fruit.class);
    // 遍历每个水果对象，调用其 restoreFruit 方法，可能用于恢复水果的图片显示状态（比如水果被吃后重新显示等情况）
    for (Fruit fruit : fruits) {
        fruit.restoreFruit(); 
    }

    // 如果游戏未结束，进行以下逻辑处理
    if (!gameOver) {
        // 处理无按键输入时的游戏逻辑更新，比如角色自动行为、对象状态变化等
        updateWithoutInput();
        // 检测是否有指定的按键被按下，如果有则执行相应的输入响应逻辑
        if (Greenfoot.isKeyDown("left") || Greenfoot.isKeyDown("down") || Greenfoot.isKeyDown("right") || Greenfoot.isKeyDown("up") || Greenfoot.isKeyDown(" ") || Greenfoot.isKeyDown("Enter")) {
            updateWithInput();
        }

        // 更新玩家移动计时器，每帧减少其值，用于控制玩家连续移动的间隔或持续时间等相关逻辑
        if (player.moveTimer > 0) {
            player.moveTimer--;
        }

        // 如果按下空格键，调用玩家的 createIcetwo 方法，通常用于实现玩家吐冰或破冰等特定功能（具体功能由该方法内部定义）
        if (Greenfoot.isKeyDown("space")) {
            player.createIcetwo(); 
        }
    }

    // 如果已经胜利且进入下一关的倒计时正在进行（winAndNextLevel 为 true 表示已胜利等待进入下一关）
    if (winAndNextLevel) {
        // 如果倒计时还未结束（nextLevelTimer大于0），则每帧将倒计时减1
        if (nextLevelTimer > 0) {
            nextLevelTimer--;
        } else {
            // 当倒计时结束（nextLevelTimer 为0），调用进入下一关的方法，实现关卡切换
            nextLevel();
        }
    }
}
// 用于重新开始当前关卡的方法，执行一系列操作来重置关卡相关的各种状态、对象以及音效等
private void restartCurrentLevel() {
    // 停止正在播放的背景音乐，如果背景音乐对象不为空且正在播放，则调用其 stop 方法停止播放
    if (backgroundMusic!= null && backgroundMusic.isPlaying()) {
        backgroundMusic.stop();
    }
    // 停止正在播放的吃水果音效，同理，若音效对象存在且正在播放则停止
    if (eatSound!= null && eatSound.isPlaying()) {
        eatSound.stop();
    }
    // 停止正在播放的创建冰相关音效（比如吐冰音效等）
    if (createIceSound!= null && createIceSound.isPlaying()) {
        createIceSound.stop();
    }
    // 停止正在播放的胜利音效
    if (winSound!= null && winSound.isPlaying()) {
        winSound.stop();
    }
    // 停止正在播放的失败音效
    if (loseSound!= null && loseSound.isPlaying()) {
        loseSound.stop();
    }

    // 移除游戏世界中的所有对象，通过调用 removeObjects 方法并传入获取所有对象的参数（null 表示获取所有类型对象）来实现
    removeObjects(getObjects(null));

    // 重置关卡相关的各种状态变量，以下是具体的重置操作：
    // 将剩余水果总数重置为0
    totalFruits = 0;
    // 清空第一种类型水果的剩余列表
    remainingFruits1.clear();
    // 清空第二种类型水果的剩余列表
    remainingFruits2.clear();
    // 将游戏阶段重置为1，通常意味着重新从关卡的初始阶段开始
    stage = 1;
    // 将已获取的水果数量重置为0
    achievedNum = 0;
    // 将第一阶段剩余水果数量重置为0
    remainingFruitsStage1 = 0;
    remainingFruitsStage2 = 0;
    // 将胜利后进入下一关的标志设为 false，取消进入下一关的等待状态
    winAndNextLevel = false;
    // 将进入下一关的倒计时重置为0
    nextLevelTimer = 0;
    // 设置怪物可以移动，恢复怪物的正常移动状态
    monstersCanMove = true;
    // 设置背景音乐播放状态为 true
    isBackgroundMusicPlaying = true;

    // 重新初始化当前关卡
    initializeLevel();
}

    // 处理无按键输入时的游戏逻辑更新的私有方法
private void updateWithoutInput() {
    // 如果游戏已经结束（gameOver 为 true），则直接返回
    if (gameOver) {
        return;
    }

    // 获取游戏世界中的玩家对象
    Player player = (Player) getObjects(Player.class).get(0);
    List<Fruit> intersectingFruits = player.getIntersectingFruits();
    // 如果接触的水果列表为空，将 fruit 设为 null，否则取列表中的第一个水果对象，代表玩家接触到的水果
    Fruit fruit = intersectingFruits.isEmpty()? null : intersectingFruits.get(0);

    // 如果存在接触的水果且该水果还未被吃掉
    if (fruit!= null &&!fruit.isEaten()) {
        // 玩家吃掉水果，调用玩家对象的 consumeFruit 方法来处理吃水果相关逻辑
        player.consumeFruit(fruit);
        // 将水果标记为已被吃掉状态，通过调用水果对象的 setEaten 方法实现
        fruit.setEaten(); 
        // 播放吃水果的音效
        eatSound.play();
        eatSound = null;
        // 创建一个新的吃水果音效实例，重新初始化该音效对象，以便下次吃水果时能正常播放音效
        eatSound = new GreenfootSound("eatfruit.wav");
        // 更新剩余水果总数，将总数减1，反映水果被吃掉的情况
        totalFruits--;
        // 如果被吃掉的水果是第一种类型，从第一种类型水果的剩余列表中移除该水果
        if (fruit.getType() == 1) {
            remainingFruits1.remove(fruit);
        } else if (fruit.getType() == 2) {
            remainingFruits2.remove(fruit);
        }

        // 判断是否本阶段水果全部吃完
        if ((stage == 1 && remainingFruits1.isEmpty()) || (stage == 2 && remainingFruits2.isEmpty())) {
            // 如果当前处于第一阶段且第一种类型水果已全部吃完
            if (stage == 1) {
                // 将游戏阶段切换为第二阶段，准备进入下一阶段的游戏逻辑处理
                stage = 2;
                // 调用更新关卡目标水果的方法，可能用于重新布置本关卡下一阶段的水果等相关操作
                updateLevelTargets();
            } else {
                // 如果当前处于第二阶段且水果已全部吃完
                if (currentLevel == 5) {
                    // 停止背景音乐，如果背景音乐正在播放，通过设置相关标志位以及调用 stop 方法来停止播放
                    if (isBackgroundMusicPlaying) {
                        backgroundMusic.stop();
                        isBackgroundMusicPlaying = false;
                    }
                    // 播放胜利音效，调用 winSound 音效对象的 play 方法来播放胜利时的音效
                    winSound.play();
                    // 游戏胜利时，设置怪物停止移动，通过将 monstersCanMove 标志设为 false 来实现
                    monstersCanMove = false;
                    // 移除原本显示胜利文本的逻辑，改为添加弹窗界面，调用 showNewWinPopup 方法来展示特定的胜利弹窗
                    showNewWinPopup();
                } else {
                    // 如果不是第五关且第二阶段水果吃完，同样执行以下操作：
                    // 停止背景音乐
                    if (isBackgroundMusicPlaying) {
                        backgroundMusic.stop();
                        isBackgroundMusicPlaying = false;
                    }
                    // 播放胜利音效
                    winSound.play();
                    // 设置怪物停止移动
                    monstersCanMove = false;
                    // 显示胜利消息，调用 showWinMessage 方法展示提示玩家即将进入下一关的文本信息
                    showWinMessage("游戏胜利，即将开始下一关！");
                    // 重置倒计时为120秒，用于延迟进入下一关，给玩家一定提示和准备时间
                    nextLevelTimer = 120;
                    // 设置胜利后需要进入下一关的标志为 true，表示准备进入下一关
                    winAndNextLevel = true;
                }
            }
        }
    }

    // 创建一个列表，用于存储所有已被标记为已吃的水果对象，后续将统一移除这些水果
    ArrayList<Fruit> fruitsToRemove = new ArrayList<>();
    // 遍历第一种类型水果的剩余列表，将已被吃掉的水果添加到待移除列表中
    for (Fruit f : remainingFruits1) {
        if (f.isEaten()) {
            fruitsToRemove.add(f);
        }
    }
    // 同理，遍历第二种类型水果的剩余列表，添加已吃的水果到待移除列表
    for (Fruit f : remainingFruits2) {
        if (f.isEaten()) {
            fruitsToRemove.add(f);
        }
    }
    // 遍历待移除的水果列表，调用 removeFruitFromWorld 方法逐个移除这些水果对象
    for (Fruit f : fruitsToRemove) {
        removeFruitFromWorld(f);
    }

    // 根据当前关卡判断水果是否移动，如果该关卡水果需要移动（通过调用 isFruitMove 方法判断）
    if (isFruitMove(currentLevel)) {
        // 遍历游戏世界中所有的水果对象，调用每个水果对象的 move 方法，使水果进行移动（具体移动逻辑由水果类的 move 方法定义）
        for (Fruit f : getObjects(Fruit.class)) {
            f.move();
        }
    }

    // 如果怪物可以移动（monstersCanMove 为 true）
    if (monstersCanMove) {
        // 遍历所有怪物对象，调用每个怪物对象的 move 方法，使怪物进行移动（怪物的移动逻辑由 Monster 类的 move 方法定义）
        for (Monster monster : monsters) {
            monster.move();
        }
    }

    // 检查玩家是否接触到怪物，遍历所有怪物对象进行检测
    for (Monster monster : monsters) {
        // 如果玩家接触到了怪物（通过调用玩家对象的 isTouchingMonster 方法判断）
        if (player.isTouchingMonster()) {
            // 玩家死亡
            player.die();
            // 将游戏结束标志设为 true，表示游戏结束
            gameOver = true;
            // 停止背景音乐，如果正在播放则停止
            if (isBackgroundMusicPlaying) {
                backgroundMusic.stop();
                isBackgroundMusicPlaying = false;
            }
            // 循环播放失败音效
            loseSound.playLoop();
            // 显示游戏结束界面，调用 showGameOverScreen 方法展示相应的界面给玩家
            showGameOverScreen();
            // 一旦检测到玩家接触怪物，结束本次循环，避免重复执行后续不必要的检测操作
            break;
        }
    }
}

// 用于显示游戏结束界面的私有方法，创建并添加游戏结束弹窗到游戏世界的中心位置，并进行相关初始化操作
private void showGameOverScreen() {
    // 创建一个游戏结束弹窗的实例，具体弹窗的样式和内容由 GameOverPopup 类定义
    GameOverPopup gameOverPopup = new GameOverPopup();
    int x = getWidth() / 2;
    int y = getHeight() / 2;
    // 将游戏结束弹窗添加到游戏世界中指定的坐标位置
    addObject(gameOverPopup, x, y);
    // 调用游戏结束弹窗实例的 initialize 方法，进行弹窗的初始化操作，比如设置显示内容、样式等（具体由该方法内部定义）
    gameOverPopup.initialize();
    // 停止背景音乐，如果背景音乐正在播放，通过调用 stop 方法来停止
    if (isBackgroundMusicPlaying) {
        backgroundMusic.stop();
        isBackgroundMusicPlaying = false;
    }
    // 循环播放失败音效，通过调用 loseSound 音效对象的 playLoop 方法来持续播放失败提示音效
    loseSound.playLoop();
}

// 处理有按键输入时的游戏逻辑更新的私有方法，根据不同按键的按下情况执行相应的玩家操作逻辑
private void updateWithInput() {
    // 如果按下向左方向键（left）
    if (Greenfoot.isKeyDown("left")) {
        // 并且玩家移动计时器为0（表示可以进行下一次移动操作，可能用于控制移动频率等）
        if (player.moveTimer == 0) {
            // 设置玩家的移动方向为向左（这里可能通过自定义的方向编码表示，3 代表向左，具体定义需看代码其他部分）
            player.moveDirection = 3;
            // 重置玩家移动计时器，给一个初始值（这里设为10，可能表示可以连续移动的帧数等，具体作用由相关逻辑决定）
            player.moveTimer = 10;
            // 调用玩家对象的 moveLeft 方法，执行玩家向左移动的具体逻辑，比如改变坐标位置等（由该方法内部定义）
            player.moveLeft();
        }
    } else if (Greenfoot.isKeyDown("down")) {
        // 同理，以下是按下向下方向键时的逻辑处理，和向左方向键类似，只是方向相关设置不同
        if (player.moveTimer == 0) {
            player.moveDirection = 2;
            player.moveTimer = 10;
            player.moveDown();
        }
    } else if (Greenfoot.isKeyDown("right")) {
        if (player.moveTimer == 0) {
            player.moveDirection = 4;
            player.moveTimer = 10;
            player.moveRight();
        }
    } else if (Greenfoot.isKeyDown("up")) {
        if (player.moveTimer == 0) {
            player.moveDirection = 1;
            player.moveTimer = 10;
            player.moveUp();
        }
    } else if (Greenfoot.isKeyDown(" ")) {
        // 如果按下空格键，调用玩家的 createIce 方法，可能用于实现玩家创建冰（比如放置冰块等功能，具体由该方法定义）
        player.createIce();
        // 播放创建冰相关的音效，通过调用 createIceSound 音效对象的 play 方法来播放相应音效
        createIceSound.play();
    } else if (Greenfoot.isKeyDown("b")) {
        // 如果按下 'b' 键，执行以下操作：
        // 移除游戏世界中的所有对象，通过调用 removeObjects 方法实现
        removeObjects(getObjects(null));
        // 重新初始化当前关卡，调用 initializeLevel 方法来重新布置关卡元素等
        initializeLevel();
        // 将游戏结束标志设为 false，恢复游戏正常状态（可能用于重新开始当前关卡等情况）
        gameOver = false;
        // 停止正在播放的失败音效，通过调用 stop 方法实现
        loseSound.stop();
    } else if (Greenfoot.isKeyDown("Enter")) {
        // 如果按下回车键，调用 restartCurrentLevel 方法，用于重新开始当前关卡，重置所有相关状态和元素等
        restartCurrentLevel();
    }
}

    // 根据关卡判断水果是否移动
    private boolean isFruitMove(int level)
    {
        boolean[] isFruitMoveArray = {false, false, false, true, true};
        return isFruitMoveArray[level - 1];
    }

    // 更新关卡目标水果
    private void updateLevelTargets() {
    char[][] currentLevelMap = levelMaps[currentLevel - 1];
    for (int i = 0; i < MAP_HEIGHT; i++) {
        for (int j = 0; j < MAP_WIDTH; j++) {
            if (stage == 1 && currentLevelMap[i][j] == '1') {
                Fruit fruit = new Fruit(1);
                addObject(fruit, j * BLOCK_SIZE + 50, i * BLOCK_SIZE + 50);
                remainingFruits1.add(fruit);
            } else if (stage == 2 && currentLevelMap[i][j] == '2') {
                Fruit fruit = new Fruit(2);
                addObject(fruit, j * BLOCK_SIZE + 50, i * BLOCK_SIZE + 50);
                remainingFruits2.add(fruit);
            }
        }
    }
}
    // 显示游戏胜利消息
    private void showWinMessage(String message)
    {
        setPaintOrder(Text.class, Actor.class);
     Text winText = new Text(message, 80, new java.awt.Color(0, 0, 255));
        addObject(winText, getWidth() / 2, getHeight() / 2);
    }
    
    // 获取地图高度常量值的方法
    public int getMAP_HEIGHT() {
         return MAP_HEIGHT;
     }

    // 获取地图宽度常量值的方法
    public int getMAP_WIDTH() {
        return MAP_WIDTH;
     }
     
    public int getBLOCK_SIZE() {
        return BLOCK_SIZE;
    }
    
    // 判断两个Actor对象是否发生碰撞的方法
private boolean isCollision(Actor actor1, Actor actor2) {
    int actor1X = actor1.getX();
    int actor1Y = actor1.getY();
    int actor2X = actor2.getX();
    int actor2Y = actor2.getY();
    // 获取actor1图像的宽度
    int actor1Width = actor1.getImage().getWidth();
    int actor1Height = actor1.getImage().getHeight();
    int actor2Width = actor2.getImage().getWidth();
    int actor2Height = actor2.getImage().getHeight();

    // 通过比较坐标与宽高，判断两个对象的边界框是否相交，以此确定是否碰撞
    return actor1X < actor2X + actor2Width &&
            actor1X + actor1Width > actor2X &&
            actor1Y < actor2Y + actor2Height &&
            actor1Y + actor1Height > actor2Y;
}

// 从游戏世界移除水果对象，并更新相关水果数量统计的方法
public void removeFruitFromWorld(Fruit fruit) {
    if (fruit!= null) {
        // 如果水果类型为1且在剩余水果1列表中，执行移除操作
        if (fruit.getType() == 1 && remainingFruits1.contains(fruit)) {
            remainingFruits1.remove(fruit);
            removeObject(fruit);
            totalFruits--;
        } else if (fruit.getType() == 2 && remainingFruits2.contains(fruit)) {
            // 如果水果类型为2且在剩余水果2列表中，执行移除操作
            remainingFruits2.remove(fruit);
            removeObject(fruit);
            totalFruits--;
        }
    }
}

// 安全移除Actor对象的方法，先判断对象是否存在再进行移除
public void removeObjectSafely(Actor actor) {
    if (actor!= null && getObjects(null).contains(actor)) {
        removeObject(actor);
    }
}

// 进入下一关的处理方法，重置各种状态并初始化新关卡相关内容
private void nextLevel() {
    // 重置胜利后进入下一关的标志
    winAndNextLevel = false;
    // 停止胜利音效
    winSound.stop();
    // 移除当前关卡所有对象
    removeObjects(getObjects(null));
    // 关卡数加1
    currentLevel++;
    // 重置关卡阶段为1
    stage = 1;
    // 重置总水果数等相关状态（示例，可按需调整）
    totalFruits = 0;
    remainingFruits1.clear();
    remainingFruits2.clear();
    // 重新初始化关卡，布置新关卡元素
    initializeLevel();
    // 设置怪物可移动
    monstersCanMove = true;
    // 重新加载音效
    loadSounds();
    // 恢复背景音乐播放状态为true（按需）
    isBackgroundMusicPlaying = true;
}

// 获取怪物是否可移动的状态的方法
public boolean canMonstersMove() {
    return monstersCanMove;
}

// 获取指定范围内特定类型Actor对象列表的方法
public List<Actor> getObjectsInRange(int range, Class<? extends Actor> actorClass) {
    int centerX = 13 * BLOCK_SIZE + 50; // 暂以世界中心为示例坐标，可按需传入具体坐标
    int centerY = 6 * BLOCK_SIZE + 50;

    int minX = Math.max(0, centerX - range);
    int maxX = Math.min(getWidth() - 1, centerX + range);
    int minY = Math.max(0, centerY - range);
    int maxY = Math.min(getHeight() - 1, centerY + range);

    List<Actor> result = new ArrayList<>();

    // 遍历范围内坐标，筛选出符合指定类型的Actor对象加入结果列表
    for (int x = minX; x <= maxX; x++) {
        for (int y = minY; y <= maxY; y++) {
            List<Actor> actorsAtPos = getObjectsAt(x, y, Actor.class);
            for (Actor actor : actorsAtPos) {
                if (actorClass.isInstance(actor)) {
                    result.add(actor);
                }
            }
        }
    }

    return result;
}

// 显示新的胜利弹窗的方法，调整弹窗图片大小并添加到游戏世界中心位置
private void showNewWinPopup() {
    WinPopup winPopup = new WinPopup();
    // 获取游戏世界宽度
    int worldWidth = getWidth();
    // 获取游戏世界高度
    int worldHeight = getHeight();
    GreenfootImage popupImage = winPopup.getImage();
    // 缩放弹窗图片与游戏世界大小一致
    popupImage.scale(worldWidth, worldHeight);
    winPopup.setImage(popupImage);
    int x = getWidth() / 2;
    int y = getHeight() / 2;
    addObject(winPopup, x, y);
}
}