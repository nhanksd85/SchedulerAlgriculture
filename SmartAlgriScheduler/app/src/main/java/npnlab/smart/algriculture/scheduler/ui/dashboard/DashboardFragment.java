package npnlab.smart.algriculture.scheduler.ui.dashboard;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.leanback.widget.VerticalGridView;
import androidx.lifecycle.ViewModelProvider;
import npnlab.smart.algriculture.scheduler.MainActivity;
import npnlab.smart.algriculture.scheduler.R;
import npnlab.smart.algriculture.scheduler.databinding.FragmentDashboardBinding;
import npnlab.smart.algriculture.scheduler.menu.SchedulerAdapter;
import npnlab.smart.algriculture.scheduler.model.SchedulerModel;

public class DashboardFragment extends Fragment implements SchedulerAdapter.IRecyclerViewItemClickListener
                                    , View.OnClickListener{

    private FragmentDashboardBinding binding;

    VerticalGridView mGridView;
    ImageButton btnAddScheduler;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDashboard;
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        mGridView = (VerticalGridView)root.findViewById(R.id.verticalListChannel);

        btnAddScheduler = (ImageButton)root.findViewById(R.id.btn_add_scheduler);
        btnAddScheduler.setOnClickListener(this);


        mGridView.setAdapter(((MainActivity)getActivity()).mSchedulerAdapter);
        mGridView.invalidate();

        ((MainActivity)getActivity()).mSchedulerAdapter.setOnItemClickListener(this::onItemClick);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onItemClick(int position) {
        //Log.d("SmartAlgri", position + " " + mScheduler.get(position).getSchedulerName());
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity)getActivity()).UpdateSchedulerList();

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_add_scheduler){
            ((MainActivity)getActivity()).AddSchedulerList();
        }
    }
}