package com.example.myapplication.BottomNavigation;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;

public class PlanFragment extends Fragment {

    // 휴가 객체를 모아둔 리스트
    private ArrayList<Vacation> mVacations;
    // 일정 리스트 (기능 추가 예정)
    private ArrayList<GeneralEvent> mEvents;
    // 휴가 RecyclerView를 관리하기 위한 CustomAdapter
    private CustomAdapter mAdapter;
    // 휴가 캘린더를 위한 MaterialCalendarView
    private MaterialCalendarView mCalendarView;
    // 미리 등록된 더미 데이터
    private Vacation[] userVacationData = {new Vacation("연가", "연가", 24, 0), new Vacation("위로", "신병위로", 4, 0), new Vacation("위로", "수료식", 1, 0)};

    private AlertDialog listDialog = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        
        mCalendarView = (MaterialCalendarView) findViewById(R.id.calendarView);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        // 휴가 리스트 초기화
        mVacations = new ArrayList<>();
        readFile();

        // 이벤트 리스트 초기화 (사용하지 않음)
        mEvents = new ArrayList<>();

        // 캘린더뷰 설정
        mCalendarView.state().edit()
                .setFirstDayOfWeek(DayOfWeek.WEDNESDAY)
                .setMinimumDate(CalendarDay.from(2000, 1, 1))
                .setMaximumDate(CalendarDay.from(2030, 12, 31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        mCalendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_RANGE);
        mCalendarView.setSelectionColor(Color.parseColor("#B2D0FF"));

        // 캘린더를 꾹 눌렀을때, 해당 날짜에 등록된 휴가를 AlertDialog로 보여줌
        // 다이얼로그의 휴가를 누르면, 삭제 다이얼로그가 뜸
        mCalendarView.setOnDateLongClickListener(new OnDateLongClickListener() {
            @Override
            public void onDateLongClick(@NonNull final MaterialCalendarView widget, @NonNull final CalendarDay date) {
                // 선택된 날짜 정의
                List<CalendarDay> dayList;
                if(widget.getSelectedDates().size() == 0) {
                    dayList = new ArrayList<>();
                    dayList.add(date);
                    widget.setDateSelected(date, true);
                }
                else {
                    dayList = mCalendarView.getSelectedDates();
                }

                // 선택된 날짜에 존재하는 휴가 리스트
                final ArrayList<Vacation> selectedVacation = new ArrayList<>();

                // 선택된 날짜에 존재하는 일정 리스트 (사용하지 않음)
                final ArrayList<GeneralEvent> selectedEvent = new ArrayList<>();

                for(int i=0; i<mVacations.size(); i++) {
                    for(int j=0; j<dayList.size(); j++) {
                        if (mVacations.get(i).getDates().contains(dayList.get(j)) && !selectedVacation.contains(mVacations.get(i))) {
                            selectedVacation.add(mVacations.get(i));
                        }
                    }
                }
                for(int i=0; i<mEvents.size(); i++) {
                    for(int j=0; j<dayList.size(); j++) {
                        if (mEvents.get(i).getDates().contains(dayList.get(j)) && !selectedEvent.contains(mEvents.get(i))) {
                            selectedEvent.add(mEvents.get(i));
                        }
                    }
                }
                if(selectedVacation.size() == 0)
                    return;

                // 해당 휴가 리스트 AlertDialog로 표시
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                final LinearLayout layout = new LinearLayout(getActivity());
                layout.setOrientation(LinearLayout.VERTICAL);
                for(int i=0; i<selectedVacation.size(); i++) {
                    Button btn = new Button(getActivity());
                    btn.setText(selectedVacation.get(i).getName());
                    btn.setBackgroundColor(Color.TRANSPARENT);
                    // 리스트 요소 누를 시 삭제 팝업창
                    final int finalI = i;
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage("삭제하시겠습니까?");
                            builder.setNegativeButton("아니오",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    });
                            builder.setPositiveButton("예",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            if(listDialog != null) {
                                                if(listDialog.isShowing())
                                                    listDialog.dismiss();
                                            }
                                            List<CalendarDay> dayList = mCalendarView.getSelectedDates();
                                            for(int i=0; i<dayList.size(); i++) {
                                                if(selectedVacation.get(finalI).getDates().contains(dayList.get(i))) {
                                                    selectedVacation.get(finalI).getDates().remove(dayList.get(i));
                                                    selectedVacation.get(finalI).setPeriod(selectedVacation.get(finalI).getPeriod() + 1);
                                                }
                                            }
                                            mCalendarView.invalidateDecorators();
                                            mCalendarView.clearSelection();
                                            mAdapter.notifyDataSetChanged();
                                            saveFile();
                                        }
                                    });
                            builder.show();

                        }
                    });
                    layout.addView(btn);
                }
                dialogBuilder.setView(layout);
                listDialog = dialogBuilder.create();
                listDialog.show();
            }
        });

        // RecyclerView 요소 관리를 위한 CustomAdapter 설정
        mAdapter = new CustomAdapter(getActivity(), mVacations, mCalendarView, mEvents);
        mRecyclerView.setAdapter(mAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        // 휴가 추가 버튼
        Button buttonInsert = (Button)findViewById(R.id.button_insert);
        buttonInsert.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                View view = LayoutInflater.from(getActivity())
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

                        // EditText에서 숫자 추출
                        int strPeriod = 0;
                        try {
                            strPeriod = Integer.parseInt(editTextPeriod.getText().toString().replaceAll("[^0-9]", ""));
                        } catch(Exception e) {
                            strPeriod = 0;
                        }

                        if(!strType.equals("") && !strName.equals("") && strPeriod > 0) {
                            Vacation vac = new Vacation(strType, strName, strPeriod, 0);
                            mCalendarView.addDecorator(vac.getDecorator());
                            mVacations.add(0, vac);
                            mAdapter.notifyItemInserted(0);
                            saveFile();
                            //mAdapter.notifyDataSetChanged();
                        }
                        else {
                            Toast.makeText(getActivity(), "0일 이하는 등록이 불가능합니다", Toast.LENGTH_LONG).show();
                        }

                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        return view;
    }

}