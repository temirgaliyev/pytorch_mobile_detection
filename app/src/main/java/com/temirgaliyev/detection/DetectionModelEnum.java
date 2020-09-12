package com.temirgaliyev.detection;

public enum DetectionModelEnum {
    DETR, SSD;

    private static DetectionModelEnum[] vals = values();

    public DetectionModelEnum next(){
        return vals[(this.ordinal()+1) % vals.length];
    }

//    public DetectionModelEnum prev(){
//        return vals[(this.ordinal()-1+vals.length) % vals.length];
//    }
}
