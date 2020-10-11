package com.example.myapplication.model;

import java.util.HashMap;
import java.util.Map;

public class ClubDTO {
    public String name="";
    public String explain="";
    public String period="";
    public String represent="";
    public String number="";
    public String manager="";
    public String imageUri="";
    public Map<String, String> kind = new HashMap<>();
    public int questionCount=0;
    public long timestamp=0;
    public int isPhoto=0;
}
