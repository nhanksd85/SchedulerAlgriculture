package npnlab.smart.algriculture.scheduler.menu;

import android.app.TimePickerDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;

import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;

import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

import androidx.recyclerview.widget.RecyclerView;
import npnlab.smart.algriculture.scheduler.R;
import npnlab.smart.algriculture.scheduler.model.SchedulerModel;

public class SchedulerAdapter extends RecyclerView.Adapter<SchedulerAdapter.MyViewHolder>{
    private Context context;
    private List<SchedulerModel> data;
    private IRecyclerViewItemClickListener mItemClickListener;
    private IRecyclerViewItemSelectedChangeListener mItemSelectedChangeListener;
    private int selectedIndex = -1;
    private int previousSelectedIndex = 0;
    RecyclerView recyclerView;

    public void setSelectedItem(int position) {
        selectedIndex = position;
        notifyDataSetChanged();

        RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
        Log.d("NPNTV", "Refresh focus " + selectedIndex);
        if (selectedIndex >= 0 && selectedIndex < getItemCount()) {

            //notifyItemChanged(previousSelectedIndex);

            //notifyItemChanged(selectedIndex);
            lm.scrollToPosition(selectedIndex);
            //recyclerView.getChildAt(selectedIndex).setSelected(true);
        }
    }

    public SchedulerAdapter(Context context, List<SchedulerModel> mData) {
        this.context = context;
        this.data = mData;
    }

    public void setOnItemClickListener(IRecyclerViewItemClickListener clickListener) {
        this.mItemClickListener = clickListener;
    }

    public void setOnItemSelectedChangeListener(IRecyclerViewItemSelectedChangeListener mItemSelectedChangeListener) {
        this.mItemSelectedChangeListener = mItemSelectedChangeListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_scheduler, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {


        holder.txtSchedulerName.setText(data.get(position).getSchedulerName());
        holder.txtSchedulerStart.setText(data.get(position).getStartTime());
        holder.txtSchedulerStop.setText(data.get(position).getStopTime());

        int minute = data.get(position).getCycle();
        if(minute % 60 == 0){
            int hour = minute / 60;
            holder.txtSchedulerCycle.setText(hour + " giờ");
        }else{
            holder.txtSchedulerCycle.setText(minute + " phút");
        }

        holder.btnActive.setOn(data.get(position).isActive());
        holder.itemView.setSelected(position == selectedIndex);
        holder.txtFlow1.setText(data.get(position).getFlow1() + " ml");
        holder.txtFlow2.setText(data.get(position).getFlow2() + " ml");
        holder.txtFlow3.setText(data.get(position).getFlow3() + " ml");


        holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                holder.itemView.setSelected(hasFocus);
                if (hasFocus && mItemSelectedChangeListener != null) {
                    mItemSelectedChangeListener.onItemSelectedChange(position);
                }

            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.requestFocus();
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(position);
                    selectedIndex = position;
                }
            }
        });
        holder.txtSchedulerStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog picker;
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                selectedIndex = position;
                picker = new TimePickerDialog(context,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                holder.txtSchedulerStart.setText(sHour + ":" + sMinute);
                                data.get(position).setStartTime(sHour + ":" + sMinute);
                            }
                        }, hour, minutes, true);
                picker.show();
            }
        });
        holder.txtSchedulerStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog picker;
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                // time picker dialog
                selectedIndex = position;
                picker = new TimePickerDialog(context,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                holder.txtSchedulerStop.setText(sHour + ":" + sMinute);
                                data.get(position).setStopTime(sHour + ":" + sMinute);
                            }
                        }, hour, minutes, true);
                picker.show();
            }
        });

        holder.txtSchedulerCycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(context, holder.txtSchedulerCycle);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.cycle_popup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        selectedIndex = position;

                        holder.txtSchedulerCycle.setText(item.getTitle());
                        if(item.getTitle().toString().contains("giờ")){
                            int hour = Integer.parseInt(item.getTitle().toString().replaceAll(Pattern.quote(" giờ"),""));
                            data.get(position).setCycle(hour * 60);
                        }else{
                            int minute = Integer.parseInt(item.getTitle().toString().replaceAll(Pattern.quote(" phút"),""));
                            data.get(position).setCycle(minute);
                        }

                        return true;
                    }
                });

                popup.show(); //showing popup menu
            }
        });

        holder.btnActive.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                data.get(position).setActive(isOn);
                selectedIndex = position;
            }
        });

        holder.btnDelte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data.remove(position);
                notifyDataSetChanged();
            }
        });

        holder.txtFlow1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(context, holder.txtFlow1);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.flow_popup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        holder.txtFlow1.setText(item.getTitle());
                        data.get(position).setFlow1(Integer.parseInt(item.getTitle().toString().replaceAll(Pattern.quote(" ml"),"")));
                        return true;
                    }
                });

                popup.show(); //showing popup menu
            }
        });
        holder.txtFlow2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(context, holder.txtFlow2);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.flow_popup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        holder.txtFlow2.setText(item.getTitle());
                        data.get(position).setFlow2(Integer.parseInt(item.getTitle().toString().replaceAll(Pattern.quote(" ml"),"")));

                        return true;
                    }
                });

                popup.show(); //showing popup menu
            }
        });
        holder.txtFlow3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(context, holder.txtFlow3);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.flow_popup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        holder.txtFlow3.setText(item.getTitle());
                        data.get(position).setFlow3(Integer.parseInt(item.getTitle().toString().replaceAll(Pattern.quote(" ml"),"")));

                        return true;
                    }
                });

                popup.show(); //showing popup menu
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtSchedulerName;
        TextView txtSchedulerStart, txtSchedulerStop;
        TextView txtSchedulerCycle;
        TextView txtFlow1, txtFlow2, txtFlow3;
        LabeledSwitch btnActive;
        ImageView btnDelte;

        //TextView txtChannelName;
        public MyViewHolder(View itemView) {
            super(itemView);
            txtSchedulerName = (TextView) itemView.findViewById(R.id.txtSchedulerName);
            txtSchedulerStart = (TextView) itemView.findViewById(R.id.txtSchedulerStart);
            txtSchedulerStop = (TextView) itemView.findViewById(R.id.txtSchedulerStop);
            txtSchedulerCycle = (TextView) itemView.findViewById(R.id.txtSchedulerCycle);

            txtFlow1 = (TextView) itemView.findViewById(R.id.txtFlow1);
            txtFlow2 = (TextView) itemView.findViewById(R.id.txtFlow2);
            txtFlow3 = (TextView) itemView.findViewById(R.id.txtFlow3);

            btnActive = (LabeledSwitch) itemView.findViewById(R.id.btnActive);

            btnDelte = (ImageView) itemView.findViewById(R.id.btnDelete);


        }

    }


    public interface IRecyclerViewItemClickListener {
        void onItemClick(int position);
    }
    public interface IRecyclerViewItemSelectedChangeListener {
        void onItemSelectedChange(int position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }
}
