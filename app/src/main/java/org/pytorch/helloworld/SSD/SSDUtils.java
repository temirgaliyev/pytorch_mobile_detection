package org.pytorch.helloworld.SSD;

import org.pytorch.helloworld.Box;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

public class SSDUtils {

    public static ArrayList<Box> convertLocations2Boxes(ArrayList<Box> locations, ArrayList<Prior> priors, float centerVariance, float sizeVariance) {
        ArrayList<Box> boxes = new ArrayList<>();

        float center_x, center_y, width, height;

        for (int i = 0; i < locations.size(); i++) {
            Box location = locations.get(i);
            Prior prior = priors.get(i);

            center_x = location.getCenterX() * centerVariance * prior.getCenterX() + prior.getWidth();
            center_y = location.getCenterY() * centerVariance * prior.getCenterY() + prior.getHeight();
            width = (float) (Math.exp(location.getWidth() * sizeVariance) * prior.getWidth());
            height = (float) (Math.exp(location.getHeight() * sizeVariance) * prior.getHeight());

            boxes.add(Box.fromCentered(center_x, center_y, width, height, location.getScores()));
        }
        return boxes;
    }


    public static ArrayList<Box> nonMaximumSuppression(LinkedList<Box> boxes, float iouThreshold, float topK, float candidate_size) {
        ArrayList<Box> notSuppressedBoxes = new ArrayList<>();
        Collections.sort(boxes, Collections.<Box>reverseOrder(Box.boxesComparator));

        if (candidate_size < boxes.size()) {
            boxes.subList((int) candidate_size, boxes.size()).clear();
        }

        while (boxes.size() > 0) {
            Box currentBox = boxes.removeFirst();
            notSuppressedBoxes.add(currentBox);

            if ((topK >= 0 && topK == notSuppressedBoxes.size()) || boxes.size() == 1) {
                break;
            }

            float[] ious = currentBox.IOU(boxes);
            int removed = 0;
            for (int i = 0; i < ious.length; i++) {
                if (ious[i] > iouThreshold) {
                    boxes.remove(i - removed);
                    removed++;
                }
            }
        }
        return notSuppressedBoxes;
    }

    public static ArrayList<Box> nonMaximumSuppression(LinkedList<Box> boxes, float iouThreshold) {
        return nonMaximumSuppression(boxes, iouThreshold, -1, 200);
    }

    public static float[] softmax(float[] confidences) {
        float[] softmax = new float[confidences.length];
        float expSum = 0;

        for (int i = 0; i < confidences.length; i++) {
            softmax[i] = (float) Math.exp(confidences[i]);
            expSum += softmax[i];
        }

        for (int i = 0; i < softmax.length; i++) {
            softmax[i] /= expSum;
        }
        return softmax;
    }

    public static ArrayList<Prior> generateSSDPriors(SSDSpec[] specs, int imageSize) {

        ArrayList<Prior> priors = new ArrayList<>();
        float x_center, y_center;
        float size, h, w;
        float ratio;
        for (SSDSpec spec : specs) {
            float scale = (float) imageSize / (float) spec.getShrinkage();

            for (int j = 0; j < spec.getFeatureMapSize(); j++) {
                for (int i = 0; i < spec.getFeatureMapSize(); i++) {
                    x_center = (float) (i + 0.5) / scale;
                    y_center = (float) (j + 0.5) / scale;

                    size = spec.getBoxSizes().getMin();
                    h = size / imageSize;
                    w = h;
                    priors.add(new Prior(x_center, y_center, w, h));

                    size = (float) Math.sqrt(spec.getBoxSizes().getMax() * spec.getBoxSizes().getMin());
                    h = size / imageSize;
                    w = h;
                    priors.add(new Prior(x_center, y_center, w, h));

                    size = spec.getBoxSizes().getMin();
                    h = size / imageSize;
                    w = h;

                    for (int aspectRatio : spec.getAspectRatios()) {
                        ratio = (float) Math.sqrt(aspectRatio);
                        priors.add(new Prior(x_center, y_center, w * ratio, h / ratio));
                        priors.add(new Prior(x_center, y_center, w / ratio, h * ratio));
                    }
                }
            }

        }
        return priors;
    }


}
