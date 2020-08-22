package org.pytorch.helloworld;

public class Prior {

    private float center_x, center_y;
    private float width, height;

    public Prior(float center_x, float center_y, float width, float height) {
        this.center_x = Math.min(Math.max(center_x, 0f), 1f);
        this.center_y = Math.min(Math.max(center_y, 0f), 1f);
        this.width = Math.min(Math.max(width, 0f), 1f);
        this.height = Math.min(Math.max(height, 0f), 1f);
    }

    public float getCenter_x() {
        return center_x;
    }

    public float getCenter_y() {
        return center_y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
