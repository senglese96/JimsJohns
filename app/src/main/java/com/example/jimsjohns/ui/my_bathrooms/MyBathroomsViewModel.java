package com.example.jimsjohns.ui.my_bathrooms;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyBathroomsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public MyBathroomsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}