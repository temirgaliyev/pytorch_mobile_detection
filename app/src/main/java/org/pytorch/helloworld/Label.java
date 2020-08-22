//package org.pytorch.helloworld;
//
//public class Label {
//
//    private float[] classes;
//
//    public Label(float[] classes) {
//        this.classes = classes;
//    }
//
//    public float[] getClasses() {
//        return classes;
//    }
//
//    public static float[] softmax(float[] confidences){
//        float[] softmax = new float[confidences.length];
//        float expSum = 0;
//
//        for (int i = 0; i < confidences.length; i++) {
//            softmax[i] = (float) Math.exp(confidences[i]);
//            expSum += softmax[i];
//        }
//
//        for (int i = 0; i < softmax.length; i++) {
//            softmax[i] /= expSum;
//        }
//        return softmax;
//    }
//
//    /**
//     * Get index with maximum value;
//     * Similar to torch.argmax See:
//     * https://pytorch.org/docs/stable/generated/torch.argmax.html
//     *
//     * @param array - float[]
//     * @return int, index with maximum value in @array
//     */
//    public static int getMaxIndex(float[] array){
//        int maxIndex = 0;
//
//        for (int i = 0; i < array.length; i++) {
//            if (array[i] > array[maxIndex]){
//                maxIndex = i;
//            }
//        }
//
//        return maxIndex;
//    }
//
//}
