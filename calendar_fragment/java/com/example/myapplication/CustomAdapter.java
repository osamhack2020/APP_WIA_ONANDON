package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

    private ArrayList<Vacation> mList;
    private Context mContext;
    private MaterialCalendarView mCalendar;

    public class CustomViewHolder extends RecyclerView.ViewHolder
            implements View.OnCreateContextMenuListener {
        protected TextView type;
        protected TextView name;
        protected TextView period;

        public CustomViewHolder(View view) {
            super(view);
            this.type = (TextView) view.findViewById(R.id.type_listitem);
            this.name = (TextView) view.findViewById(R.id.name_listitem);
            this.period = (TextView) view.findViewById(R.id.period_listitem);
            view.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            MenuItem Edit = menu.add(Menu.NONE, 1001, 2, "수정");
            MenuItem Delete = menu.add(Menu.NONE, 1002, 3, "삭제");
            MenuItem Use = menu.add(Menu.NONE, 1003, 1, "사용");
            Edit.setOnMenuItemClickListener(onEditMenu);
            Delete.setOnMenuItemClickListener(onEditMenu);
            Use.setOnMenuItemClickListener(onEditMenu);

        }

        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case 1001:

                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                        View view = LayoutInflater.from(mContext)
                                .inflate(R.layout.edit_vacation, null, false);
                        builder.setView(view);
                        final Button buttonSubmit = (Button) view.findViewById(R.id.button_dialog_submit);
                        final EditText editTextType = (EditText) view.findViewById(R.id.edittext_dialog_type);
                        final EditText editTextName = (EditText) view.findViewById(R.id.edittext_dialog_name);
                        final EditText editTextPeriod = (EditText) view.findViewById(R.id.edittext_dialog_period);

                        final int apos = getAdapterPosition();
                        editTextType.setText(mList.get(apos).getType());
                        editTextName.setText(mList.get(apos).getName());
                        editTextPeriod.setText(Integer.toString(mList.get(apos).getPeriod()));

                        final AlertDialog dialog = builder.create();
                        buttonSubmit.setOnClickListener(new View.OnClickListener() {

                            public void onClick(View v) {
                                String strType = editTextType.getText().toString();
                                String strName = editTextName.getText().toString();
                                int strPeriod = 0;
                                try {
                                    strPeriod = Integer.parseInt(editTextPeriod.getText().toString().replaceAll("[^0-9]", ""));
                                } catch (Exception e) {
                                    strPeriod = 0;
                                }

                                if (strPeriod > 0) {
                                    mList.get(apos).setName(strName);
                                    mList.get(apos).setType(strType);
                                    mList.get(apos).setPeriod(strPeriod);
                                    notifyItemChanged(apos);
                                }
                                else {
                                    Toast.makeText(mContext, "0일 이하는 등록이 불가능합니다", Toast.LENGTH_LONG).show();
                                }

                                dialog.dismiss();
                            }
                        });

                        dialog.show();

                        break;

                    case 1002:

                        mList.remove(getAdapterPosition());
                        notifyItemRemoved(getAdapterPosition());
                        notifyItemRangeChanged(getAdapterPosition(), mList.size());

                        break;

                    case 1003:

                        List<CalendarDay> dayList = mCalendar.getSelectedDates();
                        int ap = getAdapterPosition();
                        Vacation vac = mList.get(ap);
                        int originalPeriod = vac.getPeriod();
                        int usePeriod = dayList.size();
                        int left = originalPeriod - usePeriod;
                        boolean dupFlag = false;
                        for(int i=0; i<usePeriod; i++) {
                            for(int j=0; j<mList.size(); j++) {
                                if(mList.get(j).getDates().contains(dayList.get(i))) {
                                    dupFlag = true;
                                }
                            }
                        }
                        if(left > 0 && usePeriod > 0 && !dupFlag) {
                            vac.setPeriod(left);
                            for(int i=0; i<usePeriod; i++) {
                                vac.getDates().add(dayList.get(i));
                            }
                            notifyItemChanged(ap);

                            mCalendar.invalidateDecorators();
                            mCalendar.clearSelection();
                        }
                        else if(left == 0 && usePeriod > 0 && !dupFlag) {
                            vac.setPeriod(left);
                            for(int i=0; i<usePeriod; i++) {
                                vac.getDates().add(dayList.get(i));
                            }
                            notifyItemChanged(ap);
                            //mList.remove(ap);
                            //notifyItemRemoved(getAdapterPosition());
                            //notifyItemRangeChanged(getAdapterPosition(), mList.size());
                            mCalendar.invalidateDecorators();
                            mCalendar.clearSelection();
                        }
                        else {
                            if(dupFlag) {
                                Toast.makeText(mContext, "휴가 일정이 겹칩니다", Toast.LENGTH_LONG).show();
                            }
                            else if(left < 0) {
                                Toast.makeText(mContext, "휴가 일수가 선택한 일수보다 작습니다", Toast.LENGTH_LONG).show();
                            }
                            else if(usePeriod <= 0) {
                                Toast.makeText(mContext, "0일 이하는 사용할 수 없습니다", Toast.LENGTH_LONG).show();
                            }
                        }

                        break;

                }
                return true;
            }
        };
    }

    public CustomAdapter(Context context, ArrayList<Vacation> list, MaterialCalendarView calendar) {
        this.mList = list;
        this.mContext = context;
        this.mCalendar = calendar;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_item, viewGroup, false);

        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {

        viewholder.type.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        viewholder.name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        viewholder.period.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);

        viewholder.type.setGravity(Gravity.CENTER);
        viewholder.name.setGravity(Gravity.CENTER);
        viewholder.period.setGravity(Gravity.CENTER);

        viewholder.type.setText(mList.get(position).getType());
        viewholder.name.setText(mList.get(position).getName());
        viewholder.period.setText(mList.get(position).getPeriod() + "일");
    }

    @Override
    public int getItemCount() {
        return (null != mList ? mList.size() : 0);
    }

}