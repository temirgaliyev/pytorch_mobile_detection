package org.pytorch.helloworld.SSD;

public class Prior {

    private float centerX, centerY;
    private float width, height;

    public Prior(float centerX, float centerY, float width, float height) {
        this.centerX = Math.min(Math.max(centerX, 0f), 1f);
        this.centerY = Math.min(Math.max(centerY, 0f), 1f);
        this.width = Math.min(Math.max(width, 0f), 1f);
        this.height = Math.min(Math.max(height, 0f), 1f);
    }

    public float getCenterX() {
        return centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
