package com.example.myapplication;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateLongClickListener;

import org.threeten.bp.DayOfWeek;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Vacation> mArrayList;
    private CustomAdapter mAdapter;
    private MaterialCalendarView mCalendarView;
    private Vacation[] userVacationData = {new Vacation("연가", "연가", 24), new Vacation("위로", "신병위로", 4), new Vacation("위로", "수료식", 1)};
    private int count = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_calendar);

        //ImageButton mAddButton = (ImageButton) findViewById(R.id.button_add_vacation);

        mCalendarView = (MaterialCalendarView) findViewById(R.id.calendarView);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mArrayList = new ArrayList<>();
        for(Vacation vac : userVacationData) {
            mArrayList.add(0, vac);
            mCalendarView.addDecorator(vac.getDecorator());
        }

        mCalendarView.state().edit()
                .setFirstDayOfWeek(DayOfWeek.WEDNESDAY)
                .setMinimumDate(CalendarDay.from(2000, 1, 1))
                .setMaximumDate(CalendarDay.from(2030, 12, 31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();
        //Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG);

        mCalendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_RANGE);
        //mCalendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_MULTIPLE);
        mCalendarView.setSelectionColor(Color.parseColor("#B2D0FF"));
        mCalendarView.setOnDateLongClickListener(new OnDateLongClickListener() {
            @Override
            public void onDateLongClick(@NonNull final MaterialCalendarView widget, @NonNull final CalendarDay date) {
                final ArrayList<Vacation> selectedVacation = new ArrayList<>();
                for(int i=0; i<mArrayList.size(); i++) {
                    if(mArrayList.get(i).getDates().contains(date)) {
                        selectedVacation.add(mArrayList.get(i));
                    }
                }
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                final LinearLayout layout = new LinearLayout(MainActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                ArrayList<Button> buttons = new ArrayList<>();
                for(int i=0; i<selectedVacation.size(); i++) {
                    Button btn = new Button(MainActivity.this);
                    btn.setText(selectedVacation.get(i).getName());
                    btn.setBackgroundColor(Color.TRANSPARENT);
                    final int finalI = i;
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setMessage("삭제하시겠습니까?");
                            builder.setNegativeButton("아니오",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    });
                            builder.setPositiveButton("예",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            layout.removeView(v);
                                            selectedVacation.get(finalI).getDates().remove(date);
                                            selectedVacation.get(finalI).setPeriod(selectedVacation.get(finalI).getPeriod() + 1);
                                            mCalendarView.invalidateDecorators();
                                            mAdapter.notifyDataSetChanged();
                                        }
                                    });
                            builder.show();

                        }
                    });
                    layout.addView(btn);
                    buttons.add(btn);
                }
                Button addButton = new Button(MainActivity.this);
                addButton.setText("일정 추가");
                layout.addView(addButton);
                dialogBuilder.setView(layout);
                AlertDialog dialog = dialogBuilder.create();
                dialog.show();
            }
        });

        mAdapter = new CustomAdapter(this, mArrayList, mCalendarView);
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        Button buttonInsert = (Button)findViewById(R.id.button_insert);
        buttonInsert.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                View view = LayoutInflater.from(MainActivity.this)
                        .inflate(R.layout.edit_vacation, null, false);
                builder.setView(view);

                final Button buttonSubmit = (Button) view.findViewById(R.id.button_dialog_submit);
                final EditText editTextType = (EditText) view.findViewById(R.id.edittext_dialog_type);
                final EditText editTextName = (EditText) view.findViewById(R.id.edittext_dialog_name);
                final EditText editTextPeriod = (EditText) view.findViewById(R.id.edittext_dialog_period);

                buttonSubmit.setText("추가");

                final AlertDialog dialog = builder.create();

                buttonSubmit.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {

                        String strType = editTextType.getText().toString();
                        String strName = editTextName.getText().toString();
                        int strPeriod = 0;
                        try {
                            strPeriod = Integer.parseInt(editTextPeriod.getText().toString().replaceAll("[^0-9]", ""));
                        } catch(Exception e) {
                            strPeriod = 0;
                        }

                        if(!strType.equals("") && !strName.equals("") && strPeriod > 0) {
                            Vacation vac = new Vacation(strType, strName, strPeriod);
                            mCalendarView.addDecorator(vac.getDecorator());
                            mArrayList.add(0, vac);
                            mAdapter.notifyItemInserted(0);
                            //mAdapter.notifyDataSetChanged();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "0일 이하는 등록이 불가능합니다", Toast.LENGTH_LONG).show();
                        }

                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

    }

}