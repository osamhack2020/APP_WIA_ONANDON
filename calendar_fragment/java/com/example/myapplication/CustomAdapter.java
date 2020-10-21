package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
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

    private ArrayList<Vacation> mVacations;
    private ArrayList<GeneralEvent> mEvents;
    private Context mContext;
    private MaterialCalendarView mCalendarView;

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

        // 꾹 눌렀을 때 나타나는 메뉴
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            MenuItem Edit = menu.add(Menu.NONE, 1001, 2, "수정");
            MenuItem Delete = menu.add(Menu.NONE, 1002, 3, "삭제");
            MenuItem Use = menu.add(Menu.NONE, 1003, 1, "사용");
            Edit.setOnMenuItemClickListener(onEditMenu);
            Delete.setOnMenuItemClickListener(onEditMenu);
            Use.setOnMenuItemClickListener(onEditMenu);

        }

        // 메뉴 아이템을 눌렀을 때 이벤트
        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    // 수정 버튼 누를 시 이벤트
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
                        editTextType.setText(mVacations.get(apos).getType());
                        editTextName.setText(mVacations.get(apos).getName());
                        editTextPeriod.setText(Integer.toString(mVacations.get(apos).getPeriod()));

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
                                    mVacations.get(apos).setName(strName);
                                    mVacations.get(apos).setType(strType);
                                    mVacations.get(apos).setPeriod(strPeriod);
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

                    // 삭제 버튼 누를 시 이벤트
                    case 1002:

                        Vacation tmp = mVacations.get(getAdapterPosition());
                        tmp.getDates().clear();
                        mCalendarView.removeDecorator(tmp.getDecorator());
                        mVacations.remove(getAdapterPosition());
                        notifyItemRemoved(getAdapterPosition());
                        notifyItemRangeChanged(getAdapterPosition(), mVacations.size());
                        mCalendarView.invalidateDecorators();

                        break;

                    // 사용 버튼 누를 시 이벤트
                    case 1003:

                        List<CalendarDay> dayList = mCalendarView.getSelectedDates();
                        int ap = getAdapterPosition();
                        Vacation vac = mVacations.get(ap);
                        int originalPeriod = vac.getPeriod();
                        int usePeriod = dayList.size();
                        int left = originalPeriod - usePeriod;
                        boolean dupFlag = false;
                        for(int i=0; i<usePeriod; i++) {
                            for(int j=0; j<mVacations.size(); j++) {
                                if(mVacations.get(j).getDates().contains(dayList.get(i))) {
                                    dupFlag = true;
                                }
                            }
                        }
                        if(left >= 0 && usePeriod > 0 && !dupFlag) {
                            vac.setPeriod(left);
                            for(int i=0; i<usePeriod; i++) {
                                vac.getDates().add(dayList.get(i));
                            }
                            notifyItemChanged(ap);

                            mCalendarView.invalidateDecorators();
                            mCalendarView.clearSelection();
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

    public CustomAdapter(Context context, ArrayList<Vacation> list, MaterialCalendarView calendar, ArrayList<GeneralEvent> eList) {
        this.mVacations = list;
        this.mContext = context;
        this.mCalendarView = calendar;
        this.mEvents = eList;
    }

    // 각각의 리스트 아이템에 대한 설정을 하는 부분
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

        viewholder.type.setText(mVacations.get(position).getType());
        viewholder.name.setText(mVacations.get(position).getName());
        viewholder.period.setText(mVacations.get(position).getPeriod() + "일");
    }

    @Override
    public int getItemCount() {
        return (null != mVacations ? mVacations.size() : 0);
    }

}