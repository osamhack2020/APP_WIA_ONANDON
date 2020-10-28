package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myapplication.MyGoalPost.MyGoalPostIng;
import com.example.myapplication.MyGoalPost.MyGoalSearch;
import com.example.myapplication.model.TagDTO;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class PostSearch extends AppCompatActivity {

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    Toolbar toolbar;
    SearchView searchView;
    ListView tagList;

    String documentUid;
    String name;
    String manager;

    FrameLayout searchContent;

    ArrayList<String> dataList;
    int exist = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_search);

        dataList = new ArrayList<>();

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        searchContent = (FrameLayout)findViewById(R.id.search_content);
        // searchContent.setVisibility(View.GONE);

        Intent intent = getIntent();
        documentUid = intent.getStringExtra("documentUid");
        name = intent.getStringExtra("name");
        manager = intent.getStringExtra("manager");

        firestore.collection(documentUid+"_tag").document("tag").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        if(documentSnapshot.exists()) {
                            TagDTO tagDTO = documentSnapshot.toObject(TagDTO.class);
                            dataList = tagDTO.tag;

                            tagList = (ListView) findViewById(R.id.tag_list);
                            tagList.setTextFilterEnabled(false);
                            tagList.setVisibility(View.GONE);

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(PostSearch.this, android.R.layout.simple_expandable_list_item_1, dataList);
                            tagList.setAdapter(adapter);

                            tagList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    TextView click = (TextView)view;
                                    searchView.setQuery(click.getText().toString(), false);
                                }
                            });
                        }

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
            if(dataList.size() != 0){
                tagList.setVisibility(View.GONE);
            }
            searchContent.setVisibility(View.VISIBLE);
            PostList postList = new PostList();
            Bundle bundle = new Bundle(5);
            bundle.putString("name", name);
            bundle.putString("documentUid", documentUid);
            bundle.putString("manager", manager);
            bundle.putInt("isSearch", 1);
            bundle.putString("search", query);
            postList.setArguments(bundle);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction tran = manager.beginTransaction();
            tran.replace(R.id.search_content, postList);
            tran.commit();

            searchView.clearFocus();
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if(dataList.size() != 0) {
                ArrayAdapter<String> ca = (ArrayAdapter<String>) tagList.getAdapter();
                searchContent.setVisibility(View.GONE);

                //검색어의 길이가 0일 때 필터를 해제한다.
                if (newText.length() == 0) {
                    ca.getFilter().filter(null);
                    tagList.setVisibility(View.GONE);
                } else {
                    ca.getFilter().filter(newText);
                    tagList.setVisibility(View.VISIBLE);
                }
                return true;
            }
            return true;
        }
    }
}