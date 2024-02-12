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
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import npnlab.smart.algriculture.scheduler.databinding.ActivityMainBinding;
import npnlab.smart.algriculture.scheduler.model.PumpStationModel;
import npnlab.smart.algriculture.scheduler.network.MqttHelper;
import npnlab.smart.algriculture.scheduler.ui.dashboard.DashboardFragment;
import npnlab.smart.algriculture.scheduler.ui.home.HomeFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private static final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";


    private PumpStationModel pumpStationModel = new PumpStationModel(0);
    private PumpStationModel waterLevelStationModel = new PumpStationModel(1);

    private Gson gson = new Gson();

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
                if (topic.equals("/innovation/algriculture/AABBCCDD/pumpStatus")){
                    pumpStationModel = gson.fromJson(message.toString(), PumpStationModel.class);
                    Fragment f = getForegroundFragment();
                    if(f instanceof HomeFragment){
                        ((HomeFragment)f).updatePumpStatus(pumpStationModel);
                    }
                }else if (topic.equals("/innovation/algriculture/AABBCCDD/waterLevelStatus")) {
                    waterLevelStationModel = gson.fromJson(message.toString(), PumpStationModel.class);
                    Fragment f = getForegroundFragment();
                    if(f instanceof HomeFragment){
                        ((HomeFragment)f).updateWaterLevelStatus(waterLevelStationModel);
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
                    talkToMe("Xin chào các bạn, tôi là hệ thống trợ lý ảo nhân tạo dựa trên Chat gi pi ti");
                }else{
                    Log.d("ChatGPT", "Init fail");
                }
            }
        });

        startMQTT("innovation");

        String json = gson.toJson(pumpStationModel);
        Log.d("ChatGPT", json);
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

    public void sendDataMQTT(String topic, String value){
        MqttMessage msg = new MqttMessage();
        msg.setId(1234);
        msg.setQos(0);
        msg.setRetained(true);

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
                    //processV9(msg);
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
}