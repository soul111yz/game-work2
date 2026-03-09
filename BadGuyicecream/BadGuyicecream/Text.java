import greenfoot.*;
import java.awt.Color;

// 文本类，继承自Greenfoot的Actor类
public class Text extends Actor
{
    private String text;
    private int fontSize;
    private Color textColor;

    // 构造函数，初始化文本内容、字体大小和颜色
    public Text(String text, int fontSize, Color textColor)
    {
        this.text = text;
        this.fontSize = fontSize;
        this.textColor = textColor;
        setText(text);
    }

     // 设置要显示的文本内容，并更新图像
    public void setText(String text) {
      this.text = text;

      // 将java.awt.Color转换为greenfoot.Color
      greenfoot.Color greenfootColor = new greenfoot.Color(textColor.getRed(), textColor.getGreen(), textColor.getBlue());

      GreenfootImage image = new GreenfootImage(text, fontSize, greenfootColor, null);
      setImage(image);
      }
}