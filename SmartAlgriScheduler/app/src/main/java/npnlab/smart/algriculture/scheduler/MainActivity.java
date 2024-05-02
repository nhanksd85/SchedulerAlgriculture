package npnlab.smart.algriculture.scheduler;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import npnlab.smart.algriculture.scheduler.databinding.ActivityMainBinding;
import npnlab.smart.algriculture.scheduler.menu.SchedulerAdapter;
import npnlab.smart.algriculture.scheduler.model.ImageStationModel;
import npnlab.smart.algriculture.scheduler.model.PumpStationModel;
import npnlab.smart.algriculture.scheduler.model.SchedulerModel;
import npnlab.smart.algriculture.scheduler.network.MqttHelper;
import npnlab.smart.algriculture.scheduler.ui.dashboard.DashboardFragment;
import npnlab.smart.algriculture.scheduler.ui.home.HomeFragment;
import npnlab.smart.algriculture.scheduler.ui.notifications.NotificationsFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private static final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";


    private PumpStationModel pumpStationModel = new PumpStationModel(0);
    private PumpStationModel waterLevelStationModel = new PumpStationModel(1);
    private ImageStationModel imageStationModel = new ImageStationModel();
    private List<SchedulerModel> mScheduler;
    public SchedulerAdapter mSchedulerAdapter;
    private Gson gson = new Gson();

    public double currentTemp = 0;
    public double currentHumidity = 0;
    public double currentPh = 0;
    public double currentTDS = 0;

    private static String getRandomString(final int sizeOfRandomString)
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        sb.append('I');
        sb.append('Y');
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }
    MqttHelper mqttHelper;
    public void startMQTT(String username){
        mqttHelper = new MqttHelper(this, username, getRandomString(50));
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("SmartAlgri", topic + "  : " + message.toString());
                if (topic.equals("/innovation/algriculture/AABBCCDD/pumpStatus")) {
                    pumpStationModel = gson.fromJson(message.toString(), PumpStationModel.class);
                    Fragment f = getForegroundFragment();
                    if (f instanceof HomeFragment) {
                        ((HomeFragment) f).updatePumpStatus(pumpStationModel);
                    }
                } else if (topic.equals("/innovation/algriculture/AABBCCDD/waterLevelStatus")) {
                    waterLevelStationModel = gson.fromJson(message.toString(), PumpStationModel.class);
                    Fragment f = getForegroundFragment();
                    if (f instanceof HomeFragment) {
                        ((HomeFragment) f).updateWaterLevelStatus(waterLevelStationModel);
                    }
                } else if (topic.equals("/innovation/algriculture/AABBCCDD/imagePictures")) {

                    Fragment f = getForegroundFragment();
                    imageStationModel = gson.fromJson(message.toString(), ImageStationModel.class);
                    if (f instanceof HomeFragment) {
                        ((HomeFragment) f).updateFragmentImage(imageStationModel);
                    }
                } else if (topic.equals("/innovation/algriculture/AABBCCDD/water_sensor")){
                    try{
                        JSONObject jsonObj = new JSONObject(message.toString());
                        Fragment f = getForegroundFragment();
                        if (f instanceof HomeFragment) {
                            ((HomeFragment) f).updateWaterLevelStatus(jsonObj);
                        }
                    }
                    catch (Exception e) {}
                } else if(topic.equals("/innovation/algriculture/AABBCCDD/epcb_sensor")){
                    try{
                        Log.d("Scheduler", "Received from EPCB");
                        JSONArray jsonObj = new JSONObject(message.toString()).getJSONArray("sensors");
                        Fragment f = getForegroundFragment();
                        if (f instanceof HomeFragment) {
                            ((HomeFragment) f).updateWaterEPCB(jsonObj);
                        }
                    }
                    catch (Exception e) {}
                } else if(topic.equals("/innovation/algriculture/AABBCCDD/aiResult")){
                    try{

                        JSONArray jsonObj = new JSONArray(message.toString());
                        Fragment f = getForegroundFragment();
                        if (f instanceof HomeFragment) {
                            //Log.d("Scheduler", jsonObj.toString());
                            ((HomeFragment) f).updateAIColor(jsonObj);
                        }
                    }
                    catch (Exception e) {
                    }
                }
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//        WindowManager.LayoutParams.FLAG_FULLSCREEN);


        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        niceTTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int initStatus) {
                if (initStatus == TextToSpeech.SUCCESS) {
                    niceTTS.setLanguage(Locale.forLanguageTag("VI"));
                    //talkToMe("Xin chào các bạn, tôi là hệ thống trợ lý ảo nhân tạo dựa trên Chat gi pi ti");
                }else{
                    Log.d("ChatGPT", "Init fail");
                }
            }
        });

        startMQTT("innovation");

        String jsonScheduler = "";
        try{
            jsonScheduler = Utilities.loadKey(this,"Scheduler");
        }catch (Exception e){
            jsonScheduler = "[{\"cycle\":15,\"flow1\":20,\"flow2\":20,\"flow3\":20,\"schedulerName\":\"LỊCH TƯỚI 1\",\"startTime\":\"7:15\",\"stopTime\":\"7:45\"},{\"cycle\":15,\"flow1\":20,\"flow2\":20,\"flow3\":20,\"schedulerName\":\"LỊCH TƯỚI 2\",\"startTime\":\"9:15\",\"stopTime\":\"9:45\"}]";
        }

        SchedulerModel[] mSchedulerDataBase = gson.fromJson(jsonScheduler, SchedulerModel[].class);
        mScheduler = new ArrayList<>();
        for(int i = 0; i < mSchedulerDataBase.length; i++){
            mScheduler.add(mSchedulerDataBase[i]);
        }
        mSchedulerAdapter = new SchedulerAdapter(this, mScheduler);
        Log.d("ChatGPT", "Scheduler size " + mScheduler.size());
        Log.d("ChatGPT", jsonScheduler);
        final int[] counter_sensor = {0};
        Timer aTimer = new Timer();
        TimerTask aTask = new TimerTask() {
            @Override
            public void run() {
                DFARun();
                timerRun();
                counter_sensor[0]++;
                if(counter_sensor[0] >= 15){
                    counter_sensor[0] = 0;
                    sendRandomValue();
                }
            }
        };
        aTimer.schedule(aTask, 20000, 1000);


    }

    private void sendRandomValue(){
        String json = "{\n" +
                "\t\"station_id\":\"water_0001\",\n" +
                "\t\"sensors\": [\n" +
                "\t\t{\n" +
                "\t\t\t\"sensor_id\":\"ec_0001\",\n" +
                "\t\t\t\"sensor_value\": xxxxxx\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"sensor_id\":\"ph_0001\",\n" +
                "\t\t\t\"sensor_value\": yyyyyy\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"sensor_id\":\"ORP_0001\",\n" +
                "\t\t\t\"sensor_value\": zzzzzz\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"sensor_id\":\"TEMP_0001\",\n" +
                "\t\t\t\"sensor_value\": wwwwww\n" +
                "\t\t}\n" +
                "\t]\n" +
                "}";
        double orp = new Random().nextInt(20) + 150;
        double temp = (new Random().nextInt(10) + 250)/10.0;
        double ph = (new Random().nextInt(10) + 60)/10.0;
        double ec = (new Random().nextInt(5) + 12)/10.0;
        json = json.replaceAll(Pattern.quote("xxxxxx"), (ec + "").replaceAll(Pattern.quote(","),"."));
        json = json.replaceAll(Pattern.quote("yyyyyy"), (ph + "").replaceAll(Pattern.quote(","),"."));
        json = json.replaceAll(Pattern.quote("zzzzzz"), (orp + "").replaceAll(Pattern.quote(","),"."));
        json = json.replaceAll(Pattern.quote("wwwwww"), (temp + "").replaceAll(Pattern.quote(","),"."));


        sendDataMQTT("/innovation/algriculture/AABBCCDD/epcb_sensor", json, false);
        Log.d("Scheduler", "publish random " + json);
    }


    int[] timerCounter = new int[8];
    int[] timerFlag = new int[8];

    public void timerRun(){
        for(int i = 0; i < 8; i++){
            if(timerCounter[i] > 0){
                timerCounter[i]--;
                if(timerCounter[i] <=0) timerFlag[i] = 1;
            }
        }
    }

    public void setTimer(int index, int value){
        timerFlag[index] = 0;
        timerCounter[index] = value;
        if (value == 0) timerFlag[index] = 1;
    }

    int[] status = new int[8];
    int ratio = 2;
    public void DFARun(){
        Date currentTime = Calendar.getInstance().getTime();

        for(int i = 0; i < mScheduler.size(); i++){
            String[] startTime = mScheduler.get(i).getStartTime().split(Pattern.quote(":"));
            String[] stopTime = mScheduler.get(i).getStopTime().split(Pattern.quote(":"));

            switch (status[i]){
                case 0: //Check the activated scheduler
                    if(mScheduler.get(i).isActive() == true){
                        Log.d("Scheduler",  "The scheduler " + i + " is ACTIVE at " + mScheduler.get(i).getStartTime() );
                        Log.d("Scheduler", "Current time is " + currentTime.getHours() + "***" + currentTime.getMinutes());
                        status[i] = 1;
                    }
                    break;
                case 1: //Waiting to the starting time
                    int startTiming = Integer.parseInt(startTime[0]) * 60 + Integer.parseInt(startTime[1]);

                    int currTiming = currentTime.getHours() * 60 + currentTime.getMinutes();
                    if(startTiming - currTiming < 2
                            && Integer.parseInt(startTime[0]) == currentTime.getHours()
                            && startTiming - currTiming > 0){
                        talkToMe("Còn một phút nữa hệ thống sẽ bắt đầu kích hoạt");
                        Log.d("Scheduler", currTiming + "  " + startTiming);
                        status[i] = 2;
                        setTimer(0, 60);
                    }
                    break;
                case 2: //Start the irragation process

                    //if(Integer.parseInt(startTime[0]) == currentTime.getHours() && Integer.parseInt(startTime[1]) == currentTime.getMinutes()){
                    if(timerFlag[0] == 1){
                        if(mScheduler.get(i).getFlow1()/ratio > 0) {

                            talkToMe("Máy châm phân 1 hoạt động");
                            updatePumpStatus(0, true);
                            status[i] = 3;
                            int time_out = mScheduler.get(i).getFlow1() / ratio;
                            setTimer(0, time_out);
                        }else{
                            status[i] = 4;
                            setTimer(0, 2);
                        }

                    }
                    break;
                case 3:
                    if(timerFlag[0] == 1){
                        talkToMe("Máy châm phân 1 kết thúc");
                        updatePumpStatus(0, false);
                        status[i] = 4;
                        setTimer(0, 4);
                    }
                    break;
                case 4: //Turn on irrigation 2
                    if(timerFlag[0] == 1){
                        if(mScheduler.get(i).getFlow2()/ratio > 0) {
                            talkToMe("Máy châm phân 2 bắt đầu");
                            updatePumpStatus(1, true);
                            status[i] = 5;
                            int time_out = mScheduler.get(i).getFlow2() / ratio;
                            setTimer(0, time_out);
                        }else{
                            status[i] = 6;
                            setTimer(0, 2);
                        }
                    }
                    break;
                case 5: //Turn off irrigation 2
                    if(timerFlag[0] == 1){
                        talkToMe("Máy châm phân 2 kết thúc.");

                        updatePumpStatus(1, false);
                        status[i] = 6;
                        setTimer(0, 4);
                    }
                    break;
                case 6: //Turn on irrigation 3
                    if(timerFlag[0] == 1){
                        if(mScheduler.get(i).getFlow3()/ratio > 0) {
                            talkToMe("Máy châm phân 3 bắt đầu.");
                            updatePumpStatus(2, true);
                            status[i] = 7;
                            int time_out = mScheduler.get(i).getFlow3() / ratio;
                            setTimer(0, time_out);
                        }else{
                            setTimer(0, 2);
                            status[i] = 8;
                        }
                    }
                    break;
                case 7: //Turn of irrigation 3
                    if(timerFlag[0] == 1){
                        talkToMe("Máy châm phân 3 kết thúc.");

                        updatePumpStatus(2, false);
                        status[i] = 8;
                        setTimer(0, 4);
                    }
                    break;
                case 8:
                    if(timerFlag[0] == 1){
                        talkToMe("Bơm xoay vòng hoạt động");
                        updatePumpStatus(4, true);
                        setTimer(0, 20);
                        status[i] = 9;
                    }
                    break;
                case 9:
                    if(timerFlag[0] == 1){

                        int stopTiming = Integer.parseInt(stopTime[0]) * 60 + Integer.parseInt(stopTime[1]);

                        int currentTiming = currentTime.getHours() * 60 + currentTime.getMinutes();
                        if(stopTiming - currentTiming < 2) {
                            updatePumpStatus(4, false);
                            talkToMe("Lịch tưới tiêu kết thúc. Lịch tưới tiêu sẽ được kích hoạt lại lúc " + startTime[0] + " giờ " + startTime[1] + " phút");
                            status[i] = 0;
                        }else {

                            int postDelay = mScheduler.get(i).getCycle();
                            talkToMe("Lịch tưới tiêu kết thúc. Lịch tưới sẽ được lặp lại sau " + postDelay + " phút nữa!");
                            updatePumpStatus(4, false);
                            status[i] = 10;
                            setTimer(0, postDelay * 60);
                        }
                    }
                    break;
                case 10:
                    if(timerFlag[0] == 1){
                        status[i] = 2;
                        talkToMe("Lịch tưới tiêu chuẩn bị kích hoạt trong 10 giây nữa");
                        setTimer(0, 10);
                    }
                    break;
                default:
                    break;
            }
        }
    }
    public Fragment getForegroundFragment(){
        Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        return navHostFragment == null ? null : navHostFragment.getChildFragmentManager().getFragments().get(0);

    }

    public void updatePumpStatus(int index, boolean value){
        if(index < 0 || index > 4)
            return;
        pumpStationModel.getSensors().get(index).setSensorValue((value == true) ? 1:0);


        String json = gson.toJson(pumpStationModel);
        sendDataMQTT("/innovation/algriculture/AABBCCDD/pumpStatus", json);
    }

    public void sendDataMQTT(String topic, String value, boolean... ret){
        MqttMessage msg = new MqttMessage();
        boolean retain_msg  = false;
        if(ret.length == 1)
        {
            retain_msg = ret[0];  // Overrided Value
        }
        msg.setId(1234);
        msg.setQos(0);
        msg.setRetained(retain_msg);

        byte[] b = value.getBytes(Charset.forName("UTF-8"));
        msg.setPayload(b);

        try {
            mqttHelper.mqttAndroidClient.publish(topic, msg);
        }catch (MqttException e){
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //delayedHide(100);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }




    private static final int REQ_CODE_SPEECH_INPUT = 100;
    public void startVoiceInput() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        //intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,"vi-VN");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Xin mời nói...");
        //intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
//        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
//                "iot.bku.bkiot.chatgpt");
        try {
            //isProcessingSearch = true;
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
            //layoutHeader.setVisibility(View.INVISIBLE);
        } catch (ActivityNotFoundException a) {
            //isProcessingSearch = false;
            //layoutHeader.setVisibility(View.VISIBLE);
        }

    }



    private int DATA_CHECKING = 0;
    private TextToSpeech niceTTS;

    public void talkToMe(final String sentence) {

        Log.d("ChatGPT", "Talk to me " + sentence);
        String speakWords = sentence;
        niceTTS.speak(speakWords, TextToSpeech.QUEUE_FLUSH, null);
    }


    public void processV9(String msg){
        Fragment f = getForegroundFragment();
        if (f instanceof NotificationsFragment) {
            ((NotificationsFragment) f).updateGPT(msg);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //do they have the data
        if(requestCode == REQ_CODE_SPEECH_INPUT){
            Log.d("ChatGPT", requestCode + "****" +resultCode );
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                Log.d("ChatGPT", result.get(0));

                if (result.size() > 0) {
                    String msg = result.get(0).toLowerCase().trim();
                    processV9(msg);
                }
            }else{
                //isProcessingSearch = false;
                Log.d("mqtt", "You are here");
                if(data != null){
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Log.d("ChatGPT", result.get(0));
                }
            }
        }
    }




    public PumpStationModel getPumpStatus() {
        return pumpStationModel;
    }

    public PumpStationModel getWaterLevelStatus(){
        return waterLevelStationModel;
    }
    public ImageStationModel getFragmentImages(){
        return imageStationModel;
    }

    public void UpdateSchedulerList(){
        Gson gson = new Gson();
        String json = gson.toJson(mScheduler);
        Utilities.saveKey(this,"Scheduler", json);
    }

    public void AddSchedulerList() {
        SchedulerModel mItem = new SchedulerModel("LỊCH TƯỚI " + (mScheduler.size() + 1));
        mScheduler.add(mItem);
        mSchedulerAdapter.notifyDataSetChanged();
    }
}