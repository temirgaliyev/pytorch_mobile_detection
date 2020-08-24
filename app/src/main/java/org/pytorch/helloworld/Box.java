package org.pytorch.helloworld;

import java.util.Comparator;
import java.util.List;

public class Box {
    public static Comparator boxesComparator = new Comparator<Box>() {
        @Override
        public int compare(Box box1, Box box2) {
            return Float.compare(box1.getMaxProbability(), box2.getMaxProbability());
        }
    };
    private float centerX, centerY;
    private float width, height;
    private float topLeftX, topLeftY;
    private float botRightX, botRightY;
    private float[] scores;
    private float maxProbability;
    private int maxIndex;

    private Box(float centerX, float centerY, float width, float height, float[] scores) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.width = width;
        this.height = height;

        this.topLeftX = centerX - width / 2;
        this.topLeftY = centerY - height / 2;
        this.botRightX = centerX + width / 2;
        this.botRightY = centerY + height / 2;

        this.scores = scores;
        this.maxProbability = scores[0];
        for (int i = 0; i < scores.length; i++) {
            if (scores[i] > this.maxProbability) {
                this.maxProbability = scores[i];
                this.maxIndex = i;
            }
        }
    }

    public static Box fromCentered(float centerX, float centerY, float width, float height, float[] scores) {
        return new Box(centerX, centerY, width, height, scores);
    }

    public static float IOU(Box boxA, Box boxB) {
        float xA = Math.max(boxA.getTopLeftX(), boxB.getTopLeftX());
        float yA = Math.max(boxA.getTopLeftY(), boxB.getTopLeftY());
        float xB = Math.min(boxA.getBotRightX(), boxB.getBotRightX());
        float yB = Math.min(boxA.getBotRightY(), boxB.getBotRightY());

        float interArea = Math.max(0, xB - xA + 1) * Math.max(0, yB - yA + 1);

        float boxAArea = (boxA.getBotRightX() - boxA.getTopLeftX() + 1) * (boxA.getBotRightY() - boxA.getTopLeftY() + 1);
        float boxBArea = (boxB.getBotRightX() - boxB.getTopLeftX() + 1) * (boxB.getBotRightY() - boxB.getTopLeftY() + 1);

        return interArea / (boxAArea + boxBArea - interArea);
    }

    public float[] IOU(List<Box> boxes) {
        float[] ious = new float[boxes.size()];

        for (int i = 0; i < ious.length; i++) {
            ious[i] = IOU(this, boxes.get(i));
        }
        return ious;
    }

    public void scaleBox(float imageWidth, float imageHeight) {
        this.centerX *= imageWidth;
        this.centerY *= imageHeight;
        this.width *= imageWidth;
        this.height *= imageHeight;

        this.topLeftX *= imageWidth;
        this.topLeftY *= imageHeight;
        this.botRightX *= imageWidth;
        this.botRightY *= imageHeight;
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

    public float getTopLeftX() {
        return topLeftX;
    }

    public float getTopLeftY() {
        return topLeftY;
    }

    public float getBotRightX() {
        return botRightX;
    }

    public float getBotRightY() {
        return botRightY;
    }

    public float[] getScores() {
        return scores;
    }

    public float getMaxProbability() {
        return maxProbability;
    }

    public int getMaxIndex() {
        return maxIndex;
    }

}
