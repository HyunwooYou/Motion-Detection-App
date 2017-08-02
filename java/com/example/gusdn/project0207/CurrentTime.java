package com.example.gusdn.project0207;

import java.util.Date;

/**
 * Created by Hyun woo on 2017-02-15.
 *
 * 현재 시간을 보여줄 수 있는 클래스.
 */
public class CurrentTime {

    /**
     * 현재 시간을 string type 으로 반환한다.
     * @return [시간 : 분 : 초]로 반환.
     */
    public String getTime(){
        Date d = new Date(); // 현재 (시, 분, 초).
        int hour = d.getHours();
        int minute = d.getMinutes();
        int second = d.getSeconds();
        String stringHour = hour + "";
        String stringMinute = minute + "";
        String stringSecond = second + "";

        if(hour < 12){
            stringHour = "0" + stringHour;
        }
        if(minute < 10){
            stringMinute = "0" + stringMinute;
        }
        if(second < 10){
            stringSecond = "0" + stringSecond;
        }
        return stringHour + " : " + stringMinute + " : " + stringSecond + "\n";
    }
}
