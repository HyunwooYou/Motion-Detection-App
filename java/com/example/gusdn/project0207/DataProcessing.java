package com.example.gusdn.project0207;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hyun woo on 2017-02-15.
 *
 * 데이터를 처리하는 클래스.
 */
public class DataProcessing {

    /**
     * teiList 에 time, energy, list 정보를 포함하는 객체를 insert 한다.
     * @param list DataXY 객체로 이루어진 리스트.
     * @return TimeEnergyInclination 정보를 가진 리스트를 반환한다.
     */
    public List<TimeEnergyInclination> insertDataXYToTeiList(List<DataXY> list){
        List<TimeEnergyInclination> list2 = new ArrayList<>();
        int myInclination = 0;

        for(int i=0; i<list.size(); i++){
            TimeEnergyInclination teiData = new TimeEnergyInclination();
            if(i != 0){
                myInclination = getInclination(
                        list.get(i-1).getX(), list.get(i-1).getY(),
                        list.get(i).getX(), list.get(i).getY()
                );
            }
            teiData.setTime(list.get(i).getX());
            teiData.setEnergy(list.get(i).getY());
            teiData.setInclination(myInclination);

            list2.add(teiData);
        }
        return list2;
    }

    /**
     * 기울기의 부호가 변하는 객체를 ChangeMomentList 에 insert 한다.
     * @param list TimeEnergyInclination 객체로 이루어진 리스트.
     * @return 기울기가 변하는 시점의 X,Y 값 포함하는 DataXY 객체들로 이루어진 리스트.
     */
    public List<DataXY> teiListToChangeMomentList(List<TimeEnergyInclination> list){
        List<DataXY> changeMomentList = new ArrayList<>();
        for(int i=0; i<list.size(); i++){
            if(i != 0){
                // 기울기의 부호가 변하는 시점을 파악하기 위한 조건문.
                if(list.get(i-1).getInclination()*list.get(i).getInclination() < 0){
                    // 기울기를 제외한 [시간,에너지] 값을 changeMomentList 의 객체로 추가한다.
                    DataXY tem = new DataXY();
                    tem.setX(list.get(i-1).getTime());
                    tem.setY(list.get(i-1).getEnergy());

                    changeMomentList.add(tem);
                }
            }
        }
        return changeMomentList;
    }

    /**
     * 진폭 값으로 이루어진 리스트를 반환한다.
     * @param list DataXY 객체로 이루어진 리스트.
     * @return 진폭 값으로 이루어진 리스트 반환.
     */
    public List<Integer> setAmplitudeList(List<DataXY> list){
        List<Integer> amplitudeList = new ArrayList<>();
        for(int i=0; i<list.size(); i++){
            if(i != 0){
                amplitudeList.add(
                        getAmplitude(list.get(i-1), list.get(i))
                );
            }
        }
        return amplitudeList;
    }

    /**
     * amplitude list 값들의 평균을 반환한다.
     * @param list amplitude list.
     * @return (int type) average.
     */
    public int getAverOfAmplitude(List<Integer> list){
        int result = 0;

        for(int i=0; i<list.size(); i++){
            result += list.get(i);
        }
        if(list.size() != 0){   // ArithmeticException : divide by zero.
            result = result/list.size();
        }

        return result / 10;
    }

    /**
     * 진폭 값을 return 한다.
     * @param object1 기울기 값이 양수에서 음수로 바뀌는 시점의 DataXY 객체.
     * @param object2 기울기 값이 음수에서 양수로 바뀌는 시점의 DataXY 객체.
     * @return (int type) first, second 객체의 Y 값 차이의 절대값.
     */
    private int getAmplitude(DataXY object1, DataXY object2){
        return Math.abs(object2.getY() - object1.getY());
    }

    /**
     * 기울기를 반환한다.
     * @param prevX : 현재 X 값에서 -10을 한 값.
     * @param prevY : 이전 에너지 값.
     * @param currX : 이전 X 값에서 +10을 한 값.
     * @param currY : 현재 에너지 값.
     * @return 기울기.
     */
    private int getInclination(int prevX, int prevY, int currX, int currY) {
        int inclination;

        if((currX - prevX) != 0){ // ArithmeticException : divide by zero.
            inclination = (int)(((float)(currY - prevY)/(float)(currX - prevX))*10);
        }else{
            inclination = 0;
        }

        return inclination;
    }

    /**
     * 표준 편차 값을 반환한다.
     * @param list 진폭 값을 가지는 리스트.
     * @param average 진폭 평균 값.
     * @return (int type) 진폭의 표준 편차.
     */
    public int getStandardDeviation(final List<Integer> list, final int average){
        float standardDeviation = 0;

        for(int i=0; i<list.size(); i++){
            standardDeviation += (list.get(i) - average) * (list.get(i) - average);
        }
        if(list.size() != 0){
            standardDeviation = standardDeviation / (list.size() - 1);
        }

        standardDeviation = (float) Math.sqrt((double)standardDeviation);
        standardDeviation = (float) Math.sqrt((double)standardDeviation);

        return (int)standardDeviation;
    }

    /**
     * 사용자 모션 결정.
     * @param energyAmplitude Energy 진폭 평균.
     * @param accXAmplitude X 축 가속도 센서 진폭 평균.
     * @param accYAmplitude Y 축 가속도 센서 진폭 평균.
     * @param accZAmplitude Z 축 가속도 센서 진폭 평균.
     * @param xStandardDev X 축 가속도 센서 진폭 표준 편차.
     * @param zStandardDev Z 축 가속도 센서 진폭 표준 편차.
     * @return (String type) 사용자 모션.
     */
    public String deterUserMotion(
            final int energyAmplitude,
            final int accXAmplitude,final int accYAmplitude,final int accZAmplitude,
            final int xStandardDev,final int zStandardDev
    ){
        String motion;

        // 자전거.
        if( (100 <= energyAmplitude && energyAmplitude <= 300) &&
                (accXAmplitude>=150 || accYAmplitude>=150 || accZAmplitude>=150)){
            motion = "bicycle";
        }
        // 휴식.
        else if(0 <= energyAmplitude && energyAmplitude <= 300){
            motion = "rest";
        }
        // 계단 오르내리기.
        else if( (300 < energyAmplitude && energyAmplitude <= 680) &&
                (Math.abs(xStandardDev - zStandardDev) < 10)){
            motion = "stair";
        }
        // 걷기.
        else if(300 < energyAmplitude && energyAmplitude <= 680){
            motion = "walk";
        }
        // 달리기.
        else if(680 < energyAmplitude && energyAmplitude <= 1300){
            motion = "run";
        }else{
            motion = "nothing";
        }
        return motion;
    }
}
