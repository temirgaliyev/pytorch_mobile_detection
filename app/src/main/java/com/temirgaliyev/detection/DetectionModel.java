package com.temirgaliyev.detection;

public enum DetectionModel {
    DETR, SSD;

    private static DetectionModel[] vals = values();

    public DetectionModel next(){
        return vals[(this.ordinal()+1) % vals.length];
    }

//    public DetectionModel prev(){
//        return vals[(this.ordinal()-1+vals.length) % vals.length];
//    }
}
