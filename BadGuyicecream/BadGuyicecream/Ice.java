import greenfoot.*;

// 冰块类，继承自Greenfoot的Actor类，代表游戏中的冰块对象，包含了冰块相关的属性和行为逻辑
public class Ice extends Actor {
    // 用于记录破碎图片阶段，0表示未开始破碎，1表示显示break1.png，2表示显示break2.png，3表示消失，以此来控制冰块破碎过程中的不同显示状态
    private int breakStage = 0; 
    // 定义第一张破碎图片的文件名，用于后续设置冰块破碎时显示的对应图片，可根据实际图片资源名称进行调整
    private static final String BREAK1_IMAGE = "break1.png";
    // 定义第二张破碎图片的文件名
    private static final String BREAK2_IMAGE = "break2.png";
    // 每张破碎图片显示的时间（帧数等，这里简单用一个计数来模拟时间，可根据实际帧率等调整），用于控制每张破碎图片在屏幕上停留的时长
    private static final int DISPLAY_TIME = 10; 
    // 记录当前图片已显示的计数，随着游戏循环每帧增加，达到DISPLAY_TIME时切换破碎图片或执行相应操作
    private int displayCount = 0; 
    // 新增成员变量，用于播放冰块破碎音效，通过加载对应的音效文件来增强游戏的交互体验，使冰块破碎时有相应声音反馈
    private GreenfootSound breakSound; 
    
    // 默认构造函数，用于创建冰块对象，设置初始图像为"ice.png"，并调用方法缩放图片以及加载音效
    public Ice() {
        setImage("ice.png"); // 假设已经导入了冰块图片，可根据实际情况修改
        scaleIceImageToFitSize();
        loadSounds(); // 调用加载音效的方法
    }

    // 带参数的构造函数，根据传入的GreenfootImage对象来设置冰块的图像，同样进行缩放图片和加载音效操作
    public Ice(GreenfootImage image) {
        setImage(image);
        scaleIceImageToFitSize();
        loadSounds(); // 调用加载音效的方法
    }
    
    // 新增方法用于加载音效文件，尝试创建对应的GreenfootSound对象来加载"breakice.wav"音效文件，如果出错则打印错误信息并可按需做进一步处理
    private void loadSounds() {
        try {
            breakSound = new GreenfootSound("breakice.wav");
        } catch (Exception e) {
            System.out.println("加载冰块破碎音效出错: " + e.getMessage());
            // 这里可以根据具体情况做进一步处理，比如显示错误提示等
        }
    }

    
    // 新增方法用于缩放图片以适配50px正方形大小（原代码中的该方法保留），通过计算合适的缩放比例，使冰块图片尺寸符合游戏中的显示要求
    private void scaleIceImageToFitSize() {
        GreenfootImage originalImage = getImage();
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        // 计算缩放比例，使图片最长边为50px，取宽度和高度缩放比例中的较小值，避免图片变形
        double scaleFactor = Math.min(50.0 / originalWidth, 50.0 / originalHeight);

        originalImage.scale((int) (originalWidth * scaleFactor), (int) (originalHeight * scaleFactor));

        setImage(originalImage);
    }

    // 重写Actor类的act方法，用于定义冰块在每一帧（游戏循环一次）需要执行的逻辑，这里根据破碎阶段来决定是否更新破碎图片
    public void act() {
        if (breakStage > 0) {
            updateBreakImage();
        }
    }

    // 私有方法，用于更新破碎图片的显示，根据当前破碎阶段和已显示计数来切换图片、重置计数以及播放音效等操作
    private void updateBreakImage() {
        if (displayCount >= DISPLAY_TIME) {
            if (breakStage == 1) {
                // 当破碎阶段为1时，设置冰块图像为BREAK1_IMAGE对应的图片，并调用方法缩放该图片以适配尺寸
                setImage(BREAK1_IMAGE);
                scaleBreakImageToFitSize();
            } else if (breakStage == 2) {
                setImage(BREAK2_IMAGE);
                scaleBreakImageToFitSize();
            } else if (breakStage == 3) {
                // 当破碎阶段达到3时，表示冰块要消失，从游戏世界中移除该冰块对象
                getWorld().removeObject(this); 
                return;
            }
            displayCount = 0;
            breakStage++;
            playBreakSound(); // 在切换破碎图片阶段时播放音效，增强破碎效果的反馈
        }
        displayCount++;
    }

    // 私有方法，用于缩放破碎图片以适配50px正方形大小，原理与之前缩放冰块初始图片的方法类似
    private void scaleBreakImageToFitSize() {
        GreenfootImage image = getImage();
        int originalWidth = image.getWidth();
        int originalHeight = image.getHeight();

        // 计算缩放比例，使图片最长边为50px
        double scaleFactor = Math.min(50.0 / originalWidth, 50.0 / originalHeight);

        image.scale((int) (originalWidth * scaleFactor), (int) (originalHeight * scaleFactor));

        setImage(image);
    }

    // 公开方法，用于启动冰块的破碎流程，将破碎阶段设置为1，即开始显示第一张破碎图片阶段，外部可调用此方法触发冰块破碎
    public void startBreaking() {
        breakStage = 1; 
    }

    // 新增方法用于播放破碎音效，在音效对象不为空的情况下，调用其play方法播放冰块破碎音效
    private void playBreakSound() {
        if (breakSound!= null) {
            breakSound.play();
        }
    }

}