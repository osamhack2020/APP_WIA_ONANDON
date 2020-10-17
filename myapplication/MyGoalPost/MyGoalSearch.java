package com.example.myapplication.MyGoalPost;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.example.myapplication.R;
import com.example.myapplication.SearchIntro;
import com.example.myapplication.model.TagDTO;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class MyGoalSearch extends AppCompatActivity {

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    Toolbar toolbar;
    SearchView searchView;
    ListView tagList;

    ArrayList<String> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_goal_search);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        firestore.collection("tag").document("tag").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        TagDTO tagDTO =documentSnapshot.toObject(TagDTO.class);
                        dataList=tagDTO.tag;

                        tagList = (ListView)findViewById(R.id.tag_list);
                        tagList.setTextFilterEnabled(false);
                        tagList.setVisibility(View.GONE);

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MyGoalSearch.this, android.R.layout.simple_expandable_list_item_1, dataList);
                        tagList.setAdapter(adapter);

                        tagList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                searchView.setQuery(dataList.get(i), false);
                            }
                        });

                        SearchIntro searchIntro = new SearchIntro();
                        FragmentManager manager = getSupportFragmentManager();
                        FragmentTransaction tran = manager.beginTransaction();
                        tran.replace(R.id.search_content, searchIntro);
                        tran.commit();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_goal_search_menu, menu);

        MenuItem search = menu.findItem(R.id.real_search);
        searchView = (SearchView) search.getActionView();
        searchView.setQueryHint("태그를 검색하세요.");
        searchView.setIconified(false);

        SearchListener listener = new SearchListener();
        searchView.setOnQueryTextListener(listener);

        return true;
    }

    class SearchListener implements SearchView.OnQueryTextListener{

        @Override
        public boolean onQueryTextSubmit(String query) {
            tagList.setVisibility(View.GONE);
            MyGoalPostIng myGoalPostIng = new MyGoalPostIng();
            Bundle bundle = new Bundle(2);
            bundle.putInt("isSearch", 1);
            bundle.putString("search", query);
            myGoalPostIng.setArguments(bundle);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction tran = manager.beginTransaction();
            tran.replace(R.id.search_content, myGoalPostIng);
            tran.commit();

            searchView.clearFocus();
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            ArrayAdapter<String> ca = (ArrayAdapter<String>) tagList.getAdapter();

            //검색어의 길이가 0일 때 필터를 해제한다.
            if(newText.length() == 0){
                ca.getFilter().filter(null);
                tagList.setVisibility(View.GONE);
            }
            else{
                ca.getFilter().filter(newText);
                tagList.setVisibility(View.VISIBLE);
            }
            return true;
        }
    }
}