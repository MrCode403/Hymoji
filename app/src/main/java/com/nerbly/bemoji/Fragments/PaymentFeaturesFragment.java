package com.nerbly.bemoji.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.nerbly.bemoji.R;


public class PaymentFeaturesFragment extends Fragment {


    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle _savedInstanceState) {
        View _view = inflater.inflate(R.layout.payment_features, container, false);
        initialize(_view);
        initializeLogic();
        return _view;
    }


    private void initialize(View view) {

    }


    private void initializeLogic() {

    }
}
