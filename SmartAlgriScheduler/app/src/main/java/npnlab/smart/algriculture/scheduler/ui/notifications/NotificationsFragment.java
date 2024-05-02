package npnlab.smart.algriculture.scheduler.ui.notifications;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import npnlab.smart.algriculture.scheduler.MainActivity;
import npnlab.smart.algriculture.scheduler.R;
import npnlab.smart.algriculture.scheduler.databinding.FragmentNotificationsBinding;

public class NotificationsFragment extends Fragment implements View.OnClickListener {

    private FragmentNotificationsBinding binding;
    ImageButton btnVoice;
    TextView txtChatGPT;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textNotifications;
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        btnVoice = (ImageButton) root.findViewById(R.id.btnVoice);
        btnVoice.setOnClickListener(this);
        txtChatGPT = (TextView) root.findViewById(R.id.txtConsole);
        txtChatGPT.setMovementMethod(new ScrollingMovementMethod());
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    OkHttpClient client = new OkHttpClient();
    private void getChatGPTAnswer(String question){

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtChatGPT.setText("Đang xử lý...");
                Log.d("ChatGPT", "Đang xử lý...");
            }
        });
        final Request request = new Request.Builder()
                .url("http://lpnserver.net:51087/test2?c=" + question)
                .build();
        try {
            //Response response = client.newCall(request).execute();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {

                }

                @Override
                public void onResponse(Response response) throws IOException {
                    String msg = response.body().string();
                    Log.d("ChatGPT", msg);
                    txtChatGPT.setText(msg);
                    ((MainActivity)getActivity()).talkToMe(msg);
                }
            });
        }catch (Exception e){}
    }


    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btnVoice){
            ((MainActivity)getActivity()).startVoiceInput();
        }
    }



    public void updateGPT(String msg) {
        if(msg.contains("nhiệt độ") && msg.contains("vườn")){
            if(((MainActivity)getActivity()).currentTemp > 0){
                ((MainActivity)getActivity()).talkToMe("Nhiệt độ của hệ thống hiện tại là " + ((MainActivity)getActivity()).currentTemp);
                try {
                    Thread.sleep(3000);
                    getChatGPTAnswer("Nhiệt độ là " + ((MainActivity)getActivity()).currentTemp + " có tốt cho cây trồng thủy canh?");
                }catch (Exception e){}
            }else{
                ((MainActivity)getActivity()).talkToMe("Thông tin nhiệt độ chưa được cập nhật");
            }
        }else if(msg.contains("độ ẩm") && msg.contains("vườn")){
            if(((MainActivity)getActivity()).currentHumidity > 0){
                ((MainActivity)getActivity()).talkToMe("Độ ẩm của hệ thống hiện tại là " + ((MainActivity)getActivity()).currentHumidity);
                try {
                    Thread.sleep(3000);
                    getChatGPTAnswer("Độ ẩm là " + ((MainActivity)getActivity()).currentHumidity + " có tốt cho cây trồng thủy canh?");
                }catch (Exception e){}
            }else{
                ((MainActivity)getActivity()).talkToMe("Thông tin độ ẩm chưa được cập nhật");
            }
        }else if(msg.contains("ph") && msg.contains("vườn")){
            if(((MainActivity)getActivity()).currentPh > 0){
                ((MainActivity)getActivity()).talkToMe("Nồng độ ph của hệ thống hiện tại là " + ((MainActivity)getActivity()).currentPh);
                try {
                    Thread.sleep(3000);
                    getChatGPTAnswer("Nồng độ ph là " + ((MainActivity)getActivity()).currentPh + " có tốt cho cây trồng thủy canh?");
                }catch (Exception e){}
            }else{
                ((MainActivity)getActivity()).talkToMe("Thông tin ph chưa được cập nhật");
            }
        }else if(msg.contains("tds") && msg.contains("vườn")){
            if(((MainActivity)getActivity()).currentTDS > 0){
                ((MainActivity)getActivity()).talkToMe("Nồng độ tds của hệ thống hiện tại là " + ((MainActivity)getActivity()).currentTDS);
                try {
                    Thread.sleep(3000);
                    getChatGPTAnswer("Nồng độ tds là " + ((MainActivity)getActivity()).currentTDS + " có tốt cho cây trồng thủy canh?");
                }catch (Exception e){}
            }else{
                ((MainActivity)getActivity()).talkToMe("Thông tin tds chưa được cập nhật");
            }
        }else if(msg.contains("tình trạng hiện tại") && (msg.contains("vườn") || msg.contains("hệ thống"))){
            String ans = "";
            if(((MainActivity)getActivity()).currentTemp > 0){
                ans += "Nhiệt độ của hệ thống hiện tại là " + ((MainActivity)getActivity()).currentTemp;
            }

            if(((MainActivity)getActivity()).currentHumidity > 0){
                ans  += "  Độ ẩm là " + ((MainActivity)getActivity()).currentHumidity;

            }
            if(((MainActivity)getActivity()).currentPh > 0){
                ans += "  Nồng độ ph là " + ((MainActivity)getActivity()).currentPh;

            }
            if(((MainActivity)getActivity()).currentTDS > 0){
                ans += "   Nồng độ tds của hệ thống hiện tại là " + ((MainActivity)getActivity()).currentTDS;

            }
            if (ans.length() > 0){
                ((MainActivity)getActivity()).talkToMe(ans.replaceAll(Pattern.quote("."), ","));
            }else {
                ((MainActivity)getActivity()).talkToMe("Hệ thống chưa được cập nhật");
            }

        }
        else {
            getChatGPTAnswer(msg);
        }
    }




}