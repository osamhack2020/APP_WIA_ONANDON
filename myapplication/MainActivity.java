package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.myapplication.BottomNavigation.DashboardFragment;
import com.example.myapplication.BottomNavigation.HomeFragment;
import com.example.myapplication.BottomNavigation.NotificationFragment;
import com.example.myapplication.BottomNavigation.PlanFragment;
import com.example.myapplication.model.MyToken;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity {

    private long backKeyPressedTime = 0;
    private Toast toast;
    String permission_list [] = {Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navigation_home);

        ItemSelectedListener listener = new ItemSelectedListener();
        navView.setOnNavigationItemSelectedListener(listener);

        checkPermission();
        registerPushToken();

        HomeFragment homeFragment = new HomeFragment();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction tran = manager.beginTransaction();
        tran.replace(R.id.main_content, homeFragment);
        tran.commit();
    }

    @Override
    public void onBackPressed(){
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

    public void registerPushToken(){
        String pushToken = FirebaseInstanceId.getInstance().getToken();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        MyToken myToken = new MyToken();
        myToken.pushtoken=pushToken;
        FirebaseFirestore.getInstance().collection("pushtokens").document(uid).set(myToken);
    }
}