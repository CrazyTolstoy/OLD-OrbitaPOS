package com.example.orbitapos;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Calculator extends Fragment {

    TextView workingsTV;
    TextView resultsTV;

    String workings = "";
    String formula = "";
    String tempFormula = "";

    boolean leftBracket = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("CalculatorFragment", "onCreateView called");

        // Inflate the fragment layout

        return inflater.inflate(R.layout.fragment_calculator, container, false);
    }



}
