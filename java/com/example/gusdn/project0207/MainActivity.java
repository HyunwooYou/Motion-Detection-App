package com.example.gusdn.project0207;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Hyun woo on 2017-02-06.
 *
 * 사용자의 모션을 인식하는 클래스.
 */
public class MainActivity extends Activity implements SensorEventListener {

    // 10초 count 를 시각화하는 TextView.
    TextView progress = null;
    // 진폭 평균 측정을 위해 사용되는 TextView.
    TextView rest = null, walking = null, run = null;
    TextView bicycle = null;
    TextView stair = null;
    // 측정을 시작, 종료를 컨트롤하는 버튼.
    Button startButton = null;
    Button stopButton= null;
    Button resetButton= null;

    // x,y,z 가속도 센서 값.
    float accXValue,accYValue,accZValue;

    // 센서와 관련된 선언
    private SensorManager mSensorManager;
    private Sensor accSensor;

    List<DataXY> teXYList = new ArrayList<>();// [Time, Energy]
    List<DataXY> txXYList = new ArrayList<>();// [Time, X acceleration]
    List<DataXY> tyXYList = new ArrayList<>();// [Time, Y acceleration]
    List<DataXY> tzXYList = new ArrayList<>();// [Time, Z acceleration]

    // Timer 설정
    private Timer timer = null;
    private int deterTimerStart = 0; // 타이머 시작 순간을 판단하는 변수.

    // 현재 X,Y 값
    private int curX = 0; // 0.1초당 10만큼 증가하는 시간단위.
    private int curY = 0; // 현재 에너지 값.

    // Timer operation.
    private TimerTask taskForData; // [X = 시간, Y = 에너지 값]를 teXYList 에 insert.
    private TimerTask taskForMainGoal; // Time,Energy,Inclination 값을 가지는 객체 List 생성.
    private TimerTask showProgress;// 사용자 모션 인식 진행 상황을 시각화.

    /************************ 생 명 주 기 ***********************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 초기 설정.
        declareSensorVariable();
        findViewId();
        adaptClickEvent();
    }

    /**
     * 리스너 등록
     */
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, accSensor,SensorManager.SENSOR_DELAY_FASTEST);
    }

    /**
     * 리스너 해제.
     */
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
    /*************************************************************/


    /************************ V i e w 설정 ************************/

    /**
     * Layout 에 있는 id를 찾는 findViewById를 사용.
     */
    private void findViewId(){
        progress = (TextView)findViewById(R.id.progress);

        rest = (TextView)findViewById(R.id.rest);
        bicycle = (TextView)findViewById(R.id.bicycle);
        walking = (TextView)findViewById(R.id.walking);
        run = (TextView)findViewById(R.id.run);
        stair = (TextView)findViewById(R.id.stair);

        startButton = (Button)findViewById(R.id.startButton);
        stopButton = (Button)findViewById(R.id.stopButton);
        resetButton = (Button)findViewById(R.id.resetButton);
    }

    /**
     * 클릭 이벤트를 적용한다.
     */
    private void adaptClickEvent(){
        startButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                testStart();
            }
        });
        stopButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                if(timer != null) timer.cancel();
                timer = null;
                deterTimerStart = 0;

                reset();
            }
        });
        resetButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                if(timer != null) timer.cancel();
                timer = null;
                deterTimerStart = 0;

                resetView();
                reset();
            }
        });
    }

    /**
     * 사용자의 모션을 UI 상에서 업데이트 한다.
     * @param motion (String type) 모션.
     */
    private void addUserMotion(final String motion){

        new Thread(new Runnable() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String tem;
                    CurrentTime currentTime = new CurrentTime();

                    if(deterTimerStart == 1){
                        switch (motion) {
                            case "rest":
                                tem = rest.getText().toString() + currentTime.getTime();
                                rest.setText(tem);
                                break;
                            case "walk":
                                tem = walking.getText().toString() + currentTime.getTime();
                                walking.setText(tem);
                                break;
                            case "run":
                                tem = run.getText().toString() + currentTime.getTime();
                                run.setText(tem);
                                break;
                            case "stair":
                                tem = stair.getText().toString() + currentTime.getTime();
                                stair.setText(tem);
                                break;
                            case "bicycle":
                                tem = stair.getText().toString() + currentTime.getTime();
                                bicycle.setText(tem);
                                break;
                        }
                    }else{
                        deterTimerStart = 1;
                    }
                }
            });
            }
        }).start();
    }

    /**
     * View 를 초기화면으로 되돌린다.
     */
    private void resetView(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rest.setText("");
                        bicycle.setText("");
                        walking.setText("");
                        run.setText("");
                        stair.setText("");
                    }
                });
            }
        }).start();
    }

    /**
     * View 를 초기화면으로 되돌린다
     */
    private void showProgressInView(){
        showProgress = new TimerTask() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String tem;
                                tem = progress.getText().toString() + "|";
                                progress.setText(tem);
                            }
                        });
                    }
                }).start();
            }
        };
    }

    /**
     * Progress TextView 를 초기화한다.
     */
    private void resetProgressTV(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progress.setText("");
                    }
                });
            }
        }).start();
    }
    /**************************************************************/


    /************************사용자 모션 인식***********************/

    /**
     * 실제 테스트를 진행한다.
     */
    private void testStart() {

        timer = new Timer();

        showProgressInView();
        insertDataToXYList();

        coreMethod(teXYList, txXYList, tyXYList, tzXYList);

        // 0.155초 간격으로 설정.
        timer.scheduleAtFixedRate(showProgress, 1000, 155);
        // 0.05초 간격으로 설정.
        timer.scheduleAtFixedRate(taskForData, 1000, 50);
        // 10초 간격으로 설정.
        timer.scheduleAtFixedRate(taskForMainGoal, 1000, 10000);
    }

    /**
     * teXYList 안에 X,Y 값을 포함하는 dataXY 객체를 insert 한다.
     */
    private void insertDataToXYList(){
        taskForData = new TimerTask() {
            @Override
            public void run() {
                DataXY teData = new DataXY(); // [time, energy]
                DataXY txData = new DataXY(); // [time, x acceleration]
                DataXY tyData = new DataXY(); // [time, y acceleration]
                DataXY tzData = new DataXY(); // [time, z acceleration]

                // X 값 증가.
                curX = curX + 1;

                /*현재 X,Y값을 대입한다.*/
                teData.setX(curX); teData.setY(curY);
                txData.setX(curX); txData.setY((int)accXValue);
                tyData.setX(curX); tyData.setY((int)accYValue);
                tzData.setX(curX); tzData.setY((int)accZValue);

                teXYList.add(teXYList.size(),teData);
                txXYList.add(txXYList.size(),txData);
                tyXYList.add(tyXYList.size(),tyData);
                tzXYList.add(tzXYList.size(),tzData);
            }
        };
    }

    /**
     * 데이터를 가공하고 사용자 motion 인식을 한다.
     * @param teXYList [time,energy] 값으로 이루어진 리스트.
     * @param txXYList [time,x acceleration] 값으로 이루어진 리스트.
     * @param tyXYList [time,y acceleration] 값으로 이루어진 리스트.
     * @param tzXYList [time,z acceleration] 값으로 이루어진 리스트.
     */
    private void coreMethod(
            final List<DataXY> teXYList,
            final List<DataXY> txXYList,final List<DataXY> tyXYList,final List<DataXY> tzXYList
    ){
        taskForMainGoal = new TimerTask() {
        @Override
        public void run() {
            DataProcessing dataProcessing = new DataProcessing();

            List<TimeEnergyInclination> teiList = dataProcessing.insertDataXYToTeiList(teXYList);
            List<TimeEnergyInclination> txiList = dataProcessing.insertDataXYToTeiList(txXYList);
            List<TimeEnergyInclination> tyiList = dataProcessing.insertDataXYToTeiList(tyXYList);
            List<TimeEnergyInclination> tziList = dataProcessing.insertDataXYToTeiList(tzXYList);

            List<DataXY> changeMomentEnergyList = dataProcessing.teiListToChangeMomentList(teiList);
            List<DataXY> changeMomentXList = dataProcessing.teiListToChangeMomentList(txiList);
            List<DataXY> changeMomentYList = dataProcessing.teiListToChangeMomentList(tyiList);
            List<DataXY> changeMomentZList = dataProcessing.teiListToChangeMomentList(tziList);

            List<Integer> energyAmplitudeList = dataProcessing.setAmplitudeList(changeMomentEnergyList);
            List<Integer> xAmplitudeList = dataProcessing.setAmplitudeList(changeMomentXList);
            List<Integer> yAmplitudeList = dataProcessing.setAmplitudeList(changeMomentYList);
            List<Integer> zAmplitudeList = dataProcessing.setAmplitudeList(changeMomentZList);

            int averOfAmplitude = dataProcessing.getAverOfAmplitude(energyAmplitudeList);
            int averOfAccXAmplitude = dataProcessing.getAverOfAmplitude(xAmplitudeList);
            int averOfAccYAmplitude = dataProcessing.getAverOfAmplitude(yAmplitudeList);
            int averOfAccZAmplitude = dataProcessing.getAverOfAmplitude(zAmplitudeList);

            int standardDevOfAccXAmp = dataProcessing.getStandardDeviation(xAmplitudeList, averOfAccXAmplitude);
            int standardDevOfAccZAmp = dataProcessing.getStandardDeviation(zAmplitudeList, averOfAccZAmplitude);

            // 사용자 모션을 결정지을 데이터 확보 및 결정.
            String motion = dataProcessing.deterUserMotion(
                    // 에너지값
                    averOfAmplitude,
                    // X 가속도, Y 가속도, Z 가속도.
                    averOfAccXAmplitude, averOfAccYAmplitude, averOfAccZAmplitude,
                    // X 표준편차, Z 표준편차.
                    standardDevOfAccXAmp, standardDevOfAccZAmp
            );
            addUserMotion(motion);

            // reset curX, curY.
            reset();
        }
        };
    }

    /**
     * Progress TextView, teList,txList,tyList,tzList, curX, curY 초기화.
     */
    private void reset(){
        resetProgressTV();

        // DataXY 객체로 이루어진 리스트 clear.
        teXYList.clear();
        txXYList.clear();
        tyXYList.clear();
        tzXYList.clear();
        
        // 현재 X,Y 값
        curX = 0; // 0.1초당 10만큼 증가하는 시간단위.
        curY = 0; // 현재 에너지 값.
    }
    /***************************************************************/


    /*********************센서와 관련된 부분 시작*********************/

    /**
     * Sensor 측정에 필요한 variable 선언.
     */
    private void declareSensorVariable(){
        //센서 매니저 얻기
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //엑셀러로미터 센서(가속)
        accSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    /**
     * 정확도에 대한 메소드 호출. (사용안함)
     * @param sensor 사용안함
     * @param accuracy 사용안함
     */
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * 센서값 얻어오기. x,y,z 가속도 센서를 통한 에너지 값 도출.
     * @param event 센서값을 얻어올 수 있는 변수.
     */
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // 기존 단위에 100을 곱해서 단위를 증가시킨다. (세밀화를 위함)
            accXValue = (int) (event.values[0]*1000);
            accYValue = (int) (event.values[1]*1000);
            accZValue = (int) (event.values[2]*1000);
        }

        // 에너지 값을 도출하는 expression.
        curY = (int)Math.sqrt((accXValue*accXValue) + (accYValue*accYValue) + (accZValue * accZValue));
    }
    /****************************************************************/
}






