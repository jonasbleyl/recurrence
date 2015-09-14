package com.bleyl.recurrence.models;

public class Icon {
    private int mId;
    private String mName;
    private int mUseFrequency;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getUseFrequency() {
        return mUseFrequency;
    }

    public void setUseFrequency(int useFrequency) {
        mUseFrequency = useFrequency;
    }
}