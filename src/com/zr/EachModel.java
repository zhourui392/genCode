package com.zr;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhour on 2017/1/21.
 */
public class EachModel {
    private String modelName;
    private List<String> fileds = new ArrayList<>();

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    private boolean isFourFileds(String field){
        if (field.toLowerCase().equals("creationtime")){
            return true;
        }
        if (field.toLowerCase().equals("createdtime")){
            return true;
        }
        if (field.toLowerCase().equals("updatedtime")){
            return true;
        }
        if (field.toLowerCase().equals("createdby")){
            return true;
        }
        if (field.toLowerCase().equals("updatedby")){
            return true;
        }
        return false;
    }

    public void addFiled(String filed) {
        if (!isFourFileds(filed)){
            fileds.add(filed);
        }
    }

    @Override
    public String toString() {
        StringBuilder filedsSb = new StringBuilder();
        fileds.forEach(s -> filedsSb.append(s+","));
        return "modelName:"+modelName+",fields:"+filedsSb;
    }

    public String getModelName() {
        return modelName;
    }

    public List<String> getFileds() {
        return fileds;
    }

    public void setFileds(List<String> fileds) {
        this.fileds = fileds;
    }
}
