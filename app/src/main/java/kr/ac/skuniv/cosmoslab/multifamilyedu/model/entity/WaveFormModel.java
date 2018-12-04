package kr.ac.skuniv.cosmoslab.multifamilyedu.model.entity;

import java.util.List;

/**
 * Created by chunso on 2018-11-19.
 */

public class WaveFormModel {
    private int[] waveData;
    private int[] firstSlopeData;
    private int[] secondSlopeData;
    private int maximumValueIndex;
    private int startIndex;
    private int endIndex;

    private List<Integer> checkPoints;
    private List<Integer> hidenCheckPoints;
    private List<Integer> finalExtremePoints;

    public int[] getWaveData() {
        return waveData;
    }

    public void setWaveData(int[] waveData) {
        this.waveData = waveData;
    }

    public int[] getFirstSlopeData() {
        return firstSlopeData;
    }

    public void setFirstSlopeData(int[] firstSlopeData) {
        this.firstSlopeData = firstSlopeData;
    }

    public int[] getSecondSlopeData() {
        return secondSlopeData;
    }

    public void setSecondSlopeData(int[] secondSlopeData) {
        this.secondSlopeData = secondSlopeData;
    }

    public int getMaximumValueIndex() {
        return maximumValueIndex;
    }

    public void setMaximumValueIndex(int maximumValueIndex) {
        this.maximumValueIndex = maximumValueIndex;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public List<Integer> getCheckPoints() {
        return checkPoints;
    }

    public void setCheckPoints(List<Integer> checkPoints) {
        this.checkPoints = checkPoints;
    }

    public List<Integer> getHidenCheckPoints() {
        return hidenCheckPoints;
    }

    public void setHidenCheckPoints(List<Integer> hidenCheckPoints) {
        this.hidenCheckPoints = hidenCheckPoints;
    }

    public List<Integer> getFinalExtremePoints() {
        return finalExtremePoints;
    }

    public void setFinalExtremePoints(List<Integer> finalExtremePoints) {
        this.finalExtremePoints = finalExtremePoints;
    }
}
