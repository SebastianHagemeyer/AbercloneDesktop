package util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Sprite {

    private static BufferedImage spriteSheet;

    private static BufferedImage loadSprite(String file) {

        BufferedImage sprite = null;

        try {
            sprite = ImageIO.read(new File("res/" + file + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sprite;
    }

    public static BufferedImage getSprite(int xPos, int yPos, int xSize, int ySize) {

        if (spriteSheet == null) {
            spriteSheet = loadSprite("sheet");
        }

        return spriteSheet.getSubimage(xPos*8, yPos*8, xSize, ySize);
    }
}