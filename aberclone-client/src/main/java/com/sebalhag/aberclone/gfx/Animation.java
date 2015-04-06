package com.sebalhag.aberclone.gfx;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;


public class Animation {

    private int frameCount;                 // Counts ticks for change
    private int frameDelay;                 // frame delay 1-12 
    private int currentFrame;               // animations current frame
    private int animationDirection;         // animation direction 
    private int totalFrames;                // total amount of frames for your animation

    private boolean stopped;                // has animations stopped
    private boolean playBack = false;

    private List<Frame> frames = new ArrayList<Frame>();    // Arraylist of frames 
	private boolean revert = false;
	

    public Animation(BufferedImage[] frames, int frameDelay, boolean playBack) {
        this.frameDelay = frameDelay;
        this.stopped = true;
        this.playBack = playBack;

        for (int i = 0; i < frames.length; i++) {
            addFrame(frames[i], frameDelay);
        }

        this.frameCount = 0;
        this.frameDelay = frameDelay;
        this.currentFrame = 0;
        this.animationDirection = 1;
        this.totalFrames = this.frames.size();

    }

    public void start() {
        if (!stopped || revert) {
            return;
        }

        if (frames.size() == 0) {
            return;
        }

        stopped = false;
        animationDirection = 1;
    }

    public void stop() {
        if (frames.size() == 0) {
            return;
        }

        stopped = true;
    }

    public void restart() {
        if (frames.size() == 0) {
            return;
        }

        stopped = false;
        currentFrame = 0;
    }
    public void revert(){
    	if(!revert){
    		this.revert = true;
    		this.stopped = true;
			//this.animationDirection = 1;
    	}
    }
    public void reset() {
        this.frameCount = 0;
        this.currentFrame = 0;
    	this.stopped = true;
    	this.revert = false;
    }

    private void addFrame(BufferedImage frame, int duration) {
        if (duration <= 0) {
            System.err.println("Invalid duration: " + duration);
            throw new RuntimeException("Invalid duration: " + duration);
        }

        frames.add(new Frame(frame, duration));
        currentFrame = 0;
    }

    public BufferedImage getSprite() {
        return frames.get(currentFrame).getFrame();
    }

    public void update() {
        if (!stopped) {
            frameCount++;

            if (frameCount > frameDelay) {
                frameCount = 0;
                
                currentFrame += animationDirection;

                if (currentFrame > totalFrames - 1) {
                	if(playBack){
                		currentFrame = totalFrames - 1;
                		animationDirection = -1;
                	}else{
                		currentFrame = 0;
                	}
                }
                else if (currentFrame < 0) {
                	if(playBack){
                		currentFrame = 0;
                		animationDirection = 1;
                	}else{
                		currentFrame = totalFrames - 1;
                	}
                }
            }
        }
        if (revert) {
        	frameCount++;

            if (frameCount > frameDelay) {
                frameCount = 0;
                
                if (currentFrame < totalFrames  && animationDirection == 1) {
                	currentFrame += 1;
                }
                if(currentFrame == totalFrames){
                	animationDirection = -1;
                }
                if(currentFrame > 0 && animationDirection == -1){
                	currentFrame -= 1;
                }
                if(currentFrame == 0){
                	revert = false;
                }
            }
        }
    }
}