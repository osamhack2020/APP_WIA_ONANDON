package com.example.myapplication.model;

import java.util.HashMap;
import java.util.Map;

public class BoardDTO {
    public String manager="";
    public String name="";
    public String explain="";
    public int isClip=0;
    public Map<String, Boolean> clip = new HashMap<>();
    public long timestamp=0;
}
