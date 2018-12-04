package kr.ac.skuniv.cosmoslab.multifamilyedu.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import kr.ac.skuniv.cosmoslab.multifamilyedu.model.entity.WaveFormModel;

import static java.lang.System.arraycopy;

/**
 * Created by chunso on 2018-11-19.
 */

public class SettingForAnalysisController {
    private static final String TAG = "SettingForAnalysis";
    private final static int NOISE_BOUND = 500 ;    //노이즈 바운드 설정

    private String ORIGINAL_FILE_PATH = null;
    private String RECODE_FILE_PATH = null;

    private WaveFormModel mOriginalData = new WaveFormModel();
    private WaveFormModel mRecodeData = new WaveFormModel();

    public SettingForAnalysisController(String originalFilePath, String recodeFilePath){
        this.ORIGINAL_FILE_PATH = originalFilePath;
        this.RECODE_FILE_PATH = recodeFilePath;
    }

    public WaveFormModel getmOriginalData(){
        return this.mOriginalData;
    }
    public WaveFormModel getmRecodeData(){
        return this.mRecodeData;
    }

    public void controller(){
        DecodeWaveFileController decoderOriginalWAV = new DecodeWaveFileController();
        DecodeWaveFileController decoderRecodeWAV = new DecodeWaveFileController();

        int[] tempOriginalData;
        int[] tempRecodeData;

        try{
            File originalFile = new File(ORIGINAL_FILE_PATH);
            decoderOriginalWAV.ReadFile(originalFile);
        }catch (IOException e){
            e.printStackTrace();
        }

        try{
            File recodeFile = new File(RECODE_FILE_PATH);
            decoderRecodeWAV.ReadFile(recodeFile);
        }catch (IOException e){
            e.printStackTrace();
        }

        tempOriginalData = decoderOriginalWAV.getFrameGains();
        tempRecodeData = decoderRecodeWAV.getFrameGains();

        if(tempOriginalData == null && tempRecodeData == null)
            return;

        // 파형을 부드럽게 만들어 준다.
        int count = 0;
        while(count < 20){
            tempOriginalData = smoothingForDrawWaveform(tempOriginalData, 4);
            tempRecodeData = smoothingForDrawWaveform(tempRecodeData, 4);
            count++;
        }

        // 히스토그램을 사용하여 비율을 구하고 노멀라이징 한다.
        double graphRatio = getGraphRatio(tempOriginalData, tempRecodeData);
        for(int i = 0; i< tempRecodeData.length; i++)
            tempRecodeData[i] = (int)(tempRecodeData[i] * graphRatio);

        int[] tempOriginal = removeNoiseSection(tempOriginalData, "ORIGINAL");
        int[] tempRecode = removeNoiseSection(tempRecodeData, "RECODE");

        syncTwoArr(tempOriginal,tempRecode);

        int[] originalWaveData = mOriginalData.getWaveData();
        int[] recodeWaveData = mRecodeData.getWaveData();
        int maximumValueIndex = 0;

        int max = 0;
        for(int i = 0; i < originalWaveData.length ; i++){
            if(originalWaveData[i] > max) {
                max = originalWaveData[i];
                maximumValueIndex = i;
            }
        }
        mOriginalData.setWaveData(originalWaveData); mOriginalData.setMaximumValueIndex(maximumValueIndex);

        max = 0; maximumValueIndex = 0;
        for(int i = 0; i< recodeWaveData.length ; i++){
            if(recodeWaveData[i] > max) {
                max = recodeWaveData[i];
                maximumValueIndex = i;
            }
        }
        mRecodeData.setWaveData(recodeWaveData); mRecodeData.setMaximumValueIndex(maximumValueIndex);

        int[] originalSlope = findSlopeValue(originalWaveData, 3);
        int[] recodeSlope = findSlopeValue(recodeWaveData,3);

        count = 0;
        while(count < 5){
            originalSlope = smoothingForDrawWaveform(originalSlope, 4);
            recodeSlope = smoothingForDrawWaveform(recodeSlope, 4);
            count++;
        }

        mOriginalData.setFirstSlopeData(originalSlope);
        mRecodeData.setFirstSlopeData(recodeSlope);

        int[] originalSlope1 = findSlopeValue(mOriginalData.getFirstSlopeData(), 3);
        int[] recodeSlope1 = findSlopeValue(mRecodeData.getFirstSlopeData(), 3);

        count = 0;
        while(count < 5){
            originalSlope1 = smoothingForDrawWaveform(originalSlope1, 4);
            recodeSlope1 = smoothingForDrawWaveform(recodeSlope1, 4);
            count++;
        }

        mOriginalData.setSecondSlopeData(originalSlope1);
        mRecodeData.setSecondSlopeData(recodeSlope1);

    }

    //파형 부드럽게 만드는 메소드
    private int[] smoothingForDrawWaveform(int[] inputData, int value){
        int leng = inputData.length;
        int[] outputData = new int[leng];
        float drawableData;

        for(float i=0 ; i<leng ; i++) {
            drawableData=0;

            if (i - value >= 0 && i + value < leng) {
                for (float j = i - value; j <= i + value; j++)
                    drawableData += inputData[(int) j];
                drawableData = drawableData / ((value*2)+1);
            }
            outputData[(int) i] = (int) drawableData;
        }
        return outputData;
    }

    //두개의 waveform의 정규화 할 비율을 구하는 메소드
    private double getGraphRatio(int[] originalWaveData, int[] recodeWaveData){
        double graphRatio;
        graphRatio = Histogram(originalWaveData, originalWaveData.length) / (double) (Histogram(recodeWaveData, recodeWaveData.length));//최댓값으로 비율 구함

        //비율 구하기
        graphRatio = Math.round(graphRatio * 100) / 100.0;//소수점 둘째자리까지 반올림
        graphRatio = Math.abs(graphRatio);//절대값

        return graphRatio;
    }

    //위 메소드의 서브 메소드
    private int Histogram(int[] Gain,int getnumFrames){

        int num = (int) (Math.round(getnumFrames * 0.1));//10% 개수
        int [] HistogramGain = new int[Gain.length];
        arraycopy(Gain,0,HistogramGain,0,Gain.length);

        for(int i = 0;i<getnumFrames; i++) {
            for(int j = i; j < getnumFrames; j++)       // 첫번째 배열의 값이 두번째 배열보다
            {                                            // 작으면 자리를 바꿈
                if(HistogramGain[i] < HistogramGain[j])  // 순서대로 다음의 배열과 비교하여 작은경우에
                {                                        // 자리를 바꿈
                    int temp = HistogramGain[i];
                    HistogramGain[i] = HistogramGain[j];
                    HistogramGain[j] = temp;
                }
            }
        }

        int sum = 0;
        for(int i = 0;i<num; i++) {
            sum = sum + HistogramGain[i];
        }

        int average = (int) (sum / num);

        return average;
    }

    //노이즈를 제거하는 메소드
    private int[] removeNoiseSection(int[] inputData, String WHOAMI){
        int startIndex = 0;
        int endIndex = 0;

        for(int i = 0; i< inputData.length ; i++)
        {
            if(inputData[i]>NOISE_BOUND && inputData[i+5]>NOISE_BOUND && inputData[i+10]>NOISE_BOUND) {
                startIndex = i;
                break;
            }
        }

        for(int i = inputData.length - 1; i > 5 ; i--)
        {
            if(inputData[i] < NOISE_BOUND && inputData[i-5] > NOISE_BOUND){
                endIndex = i;
                break;
            }
        }

        int arrStart, arrEnd;
        if(startIndex > 50){
            arrStart = startIndex - 50;
        }else
            arrStart = 0;

        //복사할 배열의 끝점
        if(endIndex + 50 <= inputData.length) {
            arrEnd = endIndex - startIndex + (startIndex - arrStart) + 50;//실제 그래프 길이 + 100
        }else
            arrEnd = (endIndex - startIndex) + (startIndex - arrStart) + (inputData.length - endIndex);

        int[] analysisData = new int[arrEnd];
        arraycopy(inputData, arrStart, analysisData, 0, arrEnd);

        int tempStartIndex =0, tempEndIndex = 0;
        for(int i = 0; i< analysisData.length ; i++)
        {
            if(analysisData[i]>NOISE_BOUND && analysisData[i+5]>NOISE_BOUND && analysisData[i+10]>NOISE_BOUND) {
                tempStartIndex = i;
                break;
            }
        }

        for(int i = analysisData.length - 1; i > 5 ; i--)
        {
            if(analysisData[i] < NOISE_BOUND && analysisData[i-5] > NOISE_BOUND){
                tempEndIndex = i;
                break;
            }
        }

        if(WHOAMI.equals("ORIGINAL")){
            mOriginalData.setStartIndex(tempStartIndex);
            mOriginalData.setEndIndex(tempEndIndex);
        }else{
            mRecodeData.setStartIndex(tempStartIndex);
            mRecodeData.setEndIndex(tempEndIndex);
        }

        return  analysisData;
    }

    //비교할 구간을 맞춰주는 메소드
    private void syncTwoArr(int[] originalData, int[] recodeData){
        int length = originalData.length > recodeData.length ? originalData.length : recodeData.length;
        boolean whoBigger = true;   //default값은 original값이 크다고 가정(original이 크면 true)
        int originalStartIndex = 0, recodeStartIndex = 0;

        for(int i = 0 ; i < originalData.length ; i++){
            if(originalData[i]>NOISE_BOUND && originalData[i+5]>NOISE_BOUND && originalData[i+10]>NOISE_BOUND) {
                originalStartIndex = i;
                mOriginalData.setStartIndex(i);
                break;
            }
        }
        for(int i = 0 ; i < recodeData.length ; i++){
            if(recodeData[i]>NOISE_BOUND && recodeData[i+5]>NOISE_BOUND && recodeData[i+10]>NOISE_BOUND) {
                recodeStartIndex = i;
                mRecodeData.setStartIndex(i);
                break;
            }
        }
        if(length == recodeData.length){
            whoBigger = false;
        }


        int[] tempOriginal;
        int[] tempRecode;



        if(whoBigger){
            int index = originalStartIndex > recodeStartIndex ? originalStartIndex-recodeStartIndex : recodeStartIndex-originalStartIndex;
            tempOriginal = new int[length + index];
            tempRecode = new int[length + index];

            if(originalStartIndex == recodeStartIndex){
                arraycopy(originalData,0, tempOriginal,0, originalData.length);
                arraycopy(recodeData, 0, tempRecode, 0, recodeData.length);
                for(int i = recodeData.length ; i<tempRecode.length ; i++){
                    tempRecode[i] = 0;
                }
            }else{
                for(int i = 0; i<= originalStartIndex ; i++){
                    if(originalData[originalStartIndex] < recodeData[i]){
                        index = i;
                        break;
                    }else{
                        index = i;
                    }
                }
                arraycopy(originalData, 0, tempOriginal, 0 , originalData.length);
                arraycopy(recodeData, 0, tempRecode, originalStartIndex - index, index);
                arraycopy(recodeData, index, tempRecode, originalStartIndex,recodeData.length - index);

                for(int i = 0; i<originalStartIndex - index ; i++){
                    tempRecode[i] = 0;
                }
                for(int i = originalStartIndex - index + recodeData.length ; i<tempRecode.length ; i++){
                    tempRecode[i] = 0;
                }
            }
        }else{
            int index = originalStartIndex > recodeStartIndex ? originalStartIndex-recodeStartIndex : recodeStartIndex-originalStartIndex;
            tempOriginal = new int[length + index];
            tempRecode = new int[length + index];


            if(originalStartIndex == recodeStartIndex){
                arraycopy(originalData,0, tempOriginal,0, originalData.length);
                arraycopy(recodeData, 0, tempRecode, 0, recodeData.length);
                for(int i = recodeData.length ; i<tempRecode.length ; i++){
                    tempRecode[i] = 0;
                }
            }else{
                for(int i = 0; i<= recodeStartIndex ; i++){
                    if(recodeData[recodeStartIndex] < originalData[i]){
                        index = i;
                        break;
                    }else{
                        index = i;
                    }
                }
                arraycopy(recodeData, 0, tempRecode, 0 , recodeData.length);
                arraycopy(originalData, 0, tempOriginal, recodeStartIndex - index, index);
                arraycopy(originalData, index, tempOriginal, recodeStartIndex,originalData.length - index);

                for(int i = 0; i<recodeStartIndex - index ; i++){
                    tempOriginal[i] = 0;
                }
                for(int i = recodeStartIndex - index + tempOriginal.length ; i<tempOriginal.length ; i++){
                    tempOriginal[i] = 0;
                }
            }
        }

        mOriginalData.setWaveData(tempOriginal);
        mRecodeData.setWaveData(tempRecode);
    }

    //미분하는 메소드
    private int[] findSlopeValue(int[] _input, int _windowSize){
        int[] tempData = _input;
        int[] resultData = new int[tempData.length];

        int windowSize = _windowSize;

        for(int i = windowSize ; i < tempData.length ; i++)
            resultData[i-windowSize] = (tempData[i] - tempData[i-windowSize])/windowSize * 10;

        return resultData;
    }
}
