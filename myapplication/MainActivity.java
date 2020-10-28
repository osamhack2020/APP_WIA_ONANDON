package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaDrm;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.BottomNavigation.DashboardFragment;
import com.example.myapplication.BottomNavigation.HomeFragment;
import com.example.myapplication.BottomNavigation.NotificationFragment;
import com.example.myapplication.BottomNavigation.PlanFragment;
import com.example.myapplication.model.AlarmDTO;
import com.example.myapplication.model.MyToken;
import com.example.myapplication.model.UserDTO;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity {

    private long backKeyPressedTime = 0;
    private Toast toast;

    FirebaseAuth auth;
    FirebaseFirestore firestore;

    TextView titleInfo;
    ImageView alarm;

    // 사용자가 앱을 통해 앨범에 접근할 수 있도록 권한 설정
    String permission_list [] = {Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        titleInfo = (TextView)findViewById(R.id.title_info);
        alarm = (ImageView)findViewById(R.id.alarm);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navigation_home);

        ItemSelectedListener listener = new ItemSelectedListener();
        navView.setOnNavigationItemSelectedListener(listener);

        // 사용자에게 권한 허가를 받는 함수
        checkPermission();

        // 푸시알림을 위해 사용자의 토큰을 서버에 저장
        registerPushToken();

        firestore.collection("UserInfo").document(auth.getCurrentUser().getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        UserDTO userDTO = documentSnapshot.toObject(UserDTO.class);
                        titleInfo.setText(userDTO.army+" "+userDTO.budae);
                    }
                });

        // 처음 MainActivity로 이동했을 때, HomeFragment가 보이게 한다.
        HomeFragment homeFragment = new HomeFragment();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction tran = manager.beginTransaction();
        tran.replace(R.id.main_content, homeFragment);
        tran.commit();
    }

    @Override
    public void onBackPressed(){

        // 버튼을 2초 이내, 두번 눌러야 앱이 종료
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finish();
            toast.cancel();
        }
    }

    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener{

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            int id = menuItem.getItemId();

            // BottomNavigationView의 하단 버튼을 누를 때 마다 화면 이동을 지정
            switch(id){
                case R.id.navigation_home :
                    HomeFragment homeFragment = new HomeFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_content, homeFragment)
                            .commit();
                    return true;
                case R.id.navigation_plan :
                    PlanFragment planFragment = new PlanFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_content, planFragment)
                            .commit();
                    return true;
                case R.id.navigation_dashboard :
                    DashboardFragment dashboardFragment = new DashboardFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_content, dashboardFragment)
                            .commit();
                    return true;
                case R.id.navigation_notifications :
                    NotificationFragment notificationFragment = new NotificationFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_content, notificationFragment)
                            .commit();
                    return true;
            }
            return false;
        }
    }

    // 사용자에게 권한 허가를 받는 함수
    public void checkPermission(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return;
        }
        else{
            for(String permission : permission_list) {
                int chk = checkCallingOrSelfPermission(permission);
                if (chk == PackageManager.PERMISSION_DENIED) {
                    requestPermissions(permission_list, 0);
                }
            }
        }
    }

    // 푸시알림을 위해 사용자의 토큰을 서버에 저장
    public void registerPushToken(){
        String pushToken = FirebaseInstanceId.getInstance().getToken();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        MyToken myToken = new MyToken();
        myToken.pushtoken=pushToken;
        FirebaseFirestore.getInstance().collection("pushtokens").document(uid).set(myToken);
    }
}