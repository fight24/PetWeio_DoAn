package com.petweio.projectdoan.Model;

public class DeViceMenuV2 extends HomeCategory{


    public DeViceMenuV2(int imgDevice, String textName, String type) {
        super(imgDevice, textName, type);
    }

    public DeViceMenuV2() {
    }

    public DeViceMenuV2(int batteryImg) {
        super(batteryImg);
    }

    public DeViceMenuV2(int statusImg, int batteryImg, int imgDevice, String textName, String type) {
        super(statusImg, batteryImg, imgDevice, textName, type);
    }
}
