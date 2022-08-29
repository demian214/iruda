package com.jica.iruda.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.jica.iruda.databinding.FragmentTimePickerBinding;

import java.time.LocalTime;

public class TimePickerFragment extends DialogFragment {


    private FragmentTimePickerBinding binding;

    public TimePickerFragment() {
        // Required empty public constructor
        Log.d("TAG" , "TimePickerFragment()");
    }

    public static TimePickerFragment newInstance() {
        TimePickerFragment fragment = new TimePickerFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTimePickerBinding.inflate(inflater, container, false);

        // DialogFragment의 View뒤에 배경색 주기
        getDialog().getWindow().setDimAmount(0.3f);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setListeners();
    }

    private void setListeners(){
        binding.buttonCancel.setOnClickListener(view -> onDestroyView());
        binding.buttonSubmit.setOnClickListener(view -> {
            LocalTime pickTieme = LocalTime.of(binding.timePicker.getHour(), binding.timePicker.getMinute());
            setAlramTime setAlramTime = (setAlramTime) getActivity();
            if(setAlramTime != null) setAlramTime.setAlramTime(pickTieme);
            dismiss();
            onDestroyView();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public interface setAlramTime{
        void setAlramTime(LocalTime localTime);
    }


}