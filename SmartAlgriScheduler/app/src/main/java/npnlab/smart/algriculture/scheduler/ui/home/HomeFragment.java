package npnlab.smart.algriculture.scheduler.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.john.waveview.WaveView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import nl.bryanderidder.themedtogglebuttongroup.ThemedButton;
import nl.bryanderidder.themedtogglebuttongroup.ThemedToggleButtonGroup;
import npnlab.smart.algriculture.scheduler.MainActivity;
import npnlab.smart.algriculture.scheduler.R;
import npnlab.smart.algriculture.scheduler.databinding.FragmentHomeBinding;
import npnlab.smart.algriculture.scheduler.model.PumpModel;
import npnlab.smart.algriculture.scheduler.model.PumpStationModel;
import npnlab.smart.algriculture.scheduler.network.MqttHelper;

public class HomeFragment extends Fragment implements View.OnClickListener, OnToggledListener {

    private FragmentHomeBinding binding;
    TextView txtWaterLevel1, txtWaterLevel2, txtWaterLevel3, txtWaterLevel4;
    WaveView water1, water2, water3, water4;

    LabeledSwitch btnWater1, btnWater2, btnWater3, btnWater4, btnWater5;
    ThemedButton btnLiveCamera, btnGraph, btnAICamera;
    private CombinedChart mChart;
    private LinearLayout mAIPicture;
    private VideoView mLiveCamera;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        txtWaterLevel1 = root.findViewById(R.id.txtWaterLevel1);
        water1 = root.findViewById(R.id.water1);

        txtWaterLevel2 = root.findViewById(R.id.txtWaterLevel2);
        water2 = root.findViewById(R.id.water2);

        txtWaterLevel3 = root.findViewById(R.id.txtWaterLevel3);
        water3 = root.findViewById(R.id.water3);

        txtWaterLevel4 = root.findViewById(R.id.txtWaterLevel4);
        water4 = root.findViewById(R.id.water4);

        btnLiveCamera = root.findViewById(R.id.btn1);
        btnGraph = root.findViewById(R.id.btn2);
        btnAICamera = root.findViewById(R.id.btn3);

        btnLiveCamera.setOnClickListener(this);
        btnGraph.setOnClickListener(this);
        btnAICamera.setOnClickListener(this);

        btnWater1 = root.findViewById(R.id.btnWater1);
        btnWater2 = root.findViewById(R.id.btnWater2);
        btnWater3 = root.findViewById(R.id.btnWater3);
        btnWater4 = root.findViewById(R.id.btnWater4);
        btnWater5 = root.findViewById(R.id.btnWater5);

        btnWater1.setOnToggledListener(this);
        btnWater2.setOnToggledListener(this);
        btnWater3.setOnToggledListener(this);
        btnWater4.setOnToggledListener(this);
        btnWater5.setOnToggledListener(this);

        mChart = (CombinedChart) root.findViewById(R.id.combinedChart);
        mAIPicture = (LinearLayout) root.findViewById(R.id.aiPictures);
        mLiveCamera = (VideoView) root.findViewById(R.id.liveCameraView);


        setChart();
        return root;
    }

    private void setChart(){

        mChart.getDescription().setEnabled(false);
        mChart.setBackgroundColor(Color.WHITE);
        mChart.setDrawGridBackground(false);
        mChart.setDrawBarShadow(false);
        mChart.setHighlightFullBarEnabled(false);

        //mChart.setOnChartValueSelectedListener(this);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f);

        final List<String> xLabel = new ArrayList<>();
        xLabel.add("1");
        xLabel.add("2");
        xLabel.add("3");
        xLabel.add("4");
        xLabel.add("5");
        xLabel.add("6");
        xLabel.add("7");
        xLabel.add("8");
        xLabel.add("9");
        xLabel.add("10");
        xLabel.add("11");
        xLabel.add("12");

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);

//        xAxis.setValueFormatter(new IAxisValueFormatter() {
//            @Override
//            public String getFormattedValue(float value, AxisBase axis) {
//                return xLabel.get((int) value % xLabel.size());
//            }
//        });

        CombinedData data = new CombinedData();
        LineData lineDatas = new LineData();
        lineDatas.addDataSet((ILineDataSet) dataChart());
        lineDatas.addDataSet((ILineDataSet) dataChart2());
        data.setData(lineDatas);

        xAxis.setAxisMaximum(data.getXMax() + 0.25f);

        mChart.setData(data);
        mChart.invalidate();
    }

    private static DataSet dataChart2() {

        LineData d = new LineData();
        int[] data = new int[] { 2, 1, 1, 2, 2, 2, 1, 2, 2, 1, 2, 13 };

        ArrayList<Entry> entries = new ArrayList<Entry>();

        for (int index = 0; index < 12; index++) {
            entries.add(new Entry(index, data[index]));
        }

        LineDataSet set = new LineDataSet(entries, "Request Ots approved");
        set.setColor(Color.GREEN);
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.GREEN);
        set.setCircleRadius(5f);
        set.setFillColor(Color.GREEN);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.GREEN);

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        d.addDataSet(set);

        return set;
    }

    private static DataSet dataChart() {

        LineData d = new LineData();
        int[] data = new int[] { 1, 2, 2, 1, 1, 1, 2, 1, 1, 2, 1, 9 };

        ArrayList<Entry> entries = new ArrayList<Entry>();

        for (int index = 0; index < 12; index++) {
            entries.add(new Entry(index, data[index]));
        }

        LineDataSet set = new LineDataSet(entries, "Request Ots approved");
        set.setColor(Color.GREEN);
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.GREEN);
        set.setCircleRadius(5f);
        set.setFillColor(Color.GREEN);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.GREEN);

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        d.addDataSet(set);

        return set;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn1:
                Log.d("ChatGPT", "LIVE CAMERA");
                mChart.setVisibility(View.GONE);
                mAIPicture.setVisibility(View.GONE);
                mLiveCamera.setVisibility(View.VISIBLE);
                break;
            case R.id.btn2:
                Log.d("ChatGPT", "GRAPH");
                mChart.setVisibility(View.VISIBLE);
                mAIPicture.setVisibility(View.GONE);
                mLiveCamera.setVisibility(View.GONE);
                break;
            case R.id.btn3:
                Log.d("ChatGPT", "AI CAMERA");
                mChart.setVisibility(View.GONE);
                mAIPicture.setVisibility(View.VISIBLE);
                mLiveCamera.setVisibility(View.GONE);
                break;
        }
    }

    private static void addDataSet(PieChart pieChart) {
        ArrayList<PieEntry> yEntrys = new ArrayList<>();
        ArrayList<String> xEntrys = new ArrayList<>();
        float[] yData = { 25, 40, 70 };
        String[] xData = { "January", "February", "January" };

        for (int i = 0; i < yData.length;i++){
            yEntrys.add(new PieEntry(yData[i],i));
        }
        for (int i = 0; i < xData.length;i++){
            xEntrys.add(xData[i]);
        }

        PieDataSet pieDataSet=new PieDataSet(yEntrys,"Employee Sales");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);

        ArrayList<Integer> colors=new ArrayList<>();
        colors.add(Color.GRAY);
        colors.add(Color.BLUE);
        colors.add(Color.RED);

        pieDataSet.setColors(colors);

        Legend legend=pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);

        //legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);

        PieData pieData=new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }
    @Override
    public void onResume() {
        super.onResume();

        updateWaterLevelStatus(((MainActivity)getActivity()).getWaterLevelStatus());
        updatePumpStatus(((MainActivity)getActivity()).getPumpStatus());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onSwitched (ToggleableView toggleableView, boolean isOn) {

        switch (toggleableView.getId()){
            case R.id.btnWater1:
                Log.d("ChatGPT", "Toggle 1 " + isOn);
                ((MainActivity)getActivity()).updatePumpStatus(0, isOn);
                break;
            case R.id.btnWater2:
                Log.d("ChatGPT", "Toggle 2 " + isOn);
                ((MainActivity)getActivity()).updatePumpStatus(1, isOn);
                break;
            case R.id.btnWater3:
                Log.d("ChatGPT", "Toggle 3 " + isOn);
                ((MainActivity)getActivity()).updatePumpStatus(2, isOn);
                break;
            case R.id.btnWater4:
                Log.d("ChatGPT", "Toggle 4 " + isOn);
                ((MainActivity)getActivity()).updatePumpStatus(3, isOn);
                break;
            case R.id.btnWater5:
                Log.d("ChatGPT", "Toggle 5 " + isOn);
                ((MainActivity)getActivity()).updatePumpStatus(4, isOn);
                break;
        }
    }

    public void updatePumpStatus(PumpStationModel pumpStation) {
        Log.d("SmartAlgri", "Update pump status from main activity");
        for(PumpModel aPump : pumpStation.getSensors()){
            if(aPump.getSensorID().equals("pump_0001")){
                btnWater1.setOn(aPump.getSensorValue() == 1);
            }else if (aPump.getSensorID().equals("pump_0002")){
                btnWater2.setOn(aPump.getSensorValue() == 1);
            }else if (aPump.getSensorID().equals("pump_0003")){
                btnWater3.setOn(aPump.getSensorValue() == 1);
            }else if (aPump.getSensorID().equals("pump_0004")){
                btnWater4.setOn(aPump.getSensorValue() == 1);
            }else if (aPump.getSensorID().equals("pump_0005")){
                btnWater5.setOn(aPump.getSensorValue() == 1);
            }
        }
    }

    public void updateWaterLevelStatus(PumpStationModel waterLevelStation) {
        Log.d("SmartAlgri", "Update water level status from main activity");
        for(PumpModel aPump : waterLevelStation.getSensors()){
            if(aPump.getSensorID().equals("water_level_0001")) {
                water1.setProgress(aPump.getSensorValue());
                txtWaterLevel1.setText(aPump.getSensorValue() + "%");
            }else if(aPump.getSensorID().equals("water_level_0002")) {
                water2.setProgress(aPump.getSensorValue());
                txtWaterLevel2.setText(aPump.getSensorValue() + "%");
            }else if(aPump.getSensorID().equals("water_level_0003")) {
                water3.setProgress(aPump.getSensorValue());
                txtWaterLevel3.setText(aPump.getSensorValue()  + "%");
            }else if(aPump.getSensorID().equals("water_level_0004")) {
                water4.setProgress(aPump.getSensorValue());
                txtWaterLevel4.setText(aPump.getSensorValue() + "%");
            }
        }
    }


}