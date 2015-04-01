package util;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Replace {
	 	public BufferedImage[] replaceArray(int color,int newColor,BufferedImage[] image){
	    	BufferedImage[] imageIn = new BufferedImage[image.length];
	    	for(int i =0 ; i < image.length; i ++){
	    		imageIn[i] = new BufferedImage(image[i].getWidth(),image[i].getHeight(),image[i].getType());
	    		for(int x = 0; x < image[i].getWidth(); x++){
		    		for(int y = 0; y < image[i].getHeight(); y++){
		    			int pixel = image[i].getRGB(x, y);
	    				imageIn[i].setRGB(x,y,pixel);
		    		}
		    	}
	    	}
	    	
	 		for(int i = 0; i<imageIn.length; i++){
				BufferedImage temp = imageIn[i];
				for(int x = 0; x < temp.getWidth(); x++){
					for(int y = 0; y < temp.getHeight(); y++){
						int pixel = temp.getRGB(x, y);
						if(pixel == color){
							imageIn[i].setRGB(x,y,newColor);
						}
					}
				}
			}
			return imageIn;
	    }
	    public BufferedImage replaceImage(int color,int newColor,BufferedImage image){
	    	BufferedImage imageIn = new BufferedImage(image.getWidth(),image.getHeight(),image.getType());
	    	
	    	for(int x = 0; x < image.getWidth(); x++){
	    		for(int y = 0; y < image.getHeight(); y++){
	    			int pixel = image.getRGB(x, y);
    				imageIn.setRGB(x,y,pixel);
	    		}
	    	}
	    	
	    	
	    	for(int x = 0; x < imageIn.getWidth(); x++){
	    		for(int y = 0; y < imageIn.getHeight(); y++){
	    			int pixel = image.getRGB(x, y);
	    			if(pixel == color){
	    				imageIn.setRGB(x,y,newColor);
	    			}
	    		}
	    	}
	    	return imageIn;
	    }
	    
	    public Color brighten(Color color, double fraction) {

	        int red = (int) Math.round(Math.min(255, color.getRed() + 255 * fraction));
	        int green = (int) Math.round(Math.min(255, color.getGreen() + 255 * fraction));
	        int blue = (int) Math.round(Math.min(255, color.getBlue() + 255 * fraction));

	        int alpha = color.getAlpha();
	        if(blue > 255){
	        	blue = 255;
	        }if(green > 255){
	        	green = 255;
	        } if(red > 255){
	        	red = 255;
	        }
	        if(blue < 0){
	        	blue = 0;
	        }if(green < 0){
	        	green = 0;
	        } if(red < 0){
	        	red = 0;
	        }
	        return new Color(red, green, blue, alpha);

	    }
}
