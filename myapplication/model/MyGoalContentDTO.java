package com.example.myapplication.model;

import java.util.HashMap;
import java.util.Map;

public class MyGoalContentDTO {
    public String explain="";
    public String title="";
    public String uid="";
    public long timestamp=0;
    public int year=0;
    public int month=0;
    public int day=0;
    public int favoriteCount = 0;
    public int commentCount = 0;
    public int isPhoto=0;
    public String imageUri="";
    public Map<String, Boolean> favorites = new HashMap<>();
}
