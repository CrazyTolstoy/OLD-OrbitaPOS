package com.example.orbitapos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class MainActivity extends AppCompatActivity {

    TextView workingsTV;
    TextView resultsTV;


    String workings = "";
    String formula = "";
    String tempFormula = "";

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initTextViews();
        viewPagerAdapter = new ViewPagerAdapter(this);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager2 = findViewById(R.id.viewpager2);
        viewPager2.setAdapter(viewPagerAdapter);

        handleNfcIntent(getIntent());
        viewPager2.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                // Set the second tab as the initial tab after the layout is complete
                viewPager2.setCurrentItem(1);
                // Remove the listener to avoid unnecessary calls
                viewPager2.removeOnLayoutChangeListener(this);
            }
        });



        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
                tab.view.setBackground(getDrawable(R.drawable.tabselected));

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                tab.view.setBackground(getDrawable(R.drawable.tab));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });
    }

    private void initTextViews() {
        workingsTV = findViewById(R.id.workingsTextView);
        resultsTV = findViewById(R.id.resultTextView);
    }

    private void setWorkings(String givenValue) {
        if (givenValue.equals("C")) {
            // Clear the workings and results
            workings = "";
            formula = "";
            workingsTV.setText("");
            resultsTV.setText("");
        } else {
            // Update the workings with the new input
            workings = workings + givenValue;
            updateResult();
            formatWorkings();
        }
    }

    private void formatWorkings() {
        workingsTV.setText(workings); // Set the original workings to workingsTV
    }

    private void updateResult() {
        Double result = null;
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("rhino");
        checkForPowerOf();

        try {
            result = (double) engine.eval(formula);
        } catch (ScriptException e) {
            // Invalid expression, do nothing
        }

        if (result != null) {
            // Display the result with a dollar sign in resultsTV
            resultsTV.setText("$" + String.valueOf(result));

            // Update the formula for the next calculation
            formula = workings;
        }
    }

    private void checkForPowerOf() {
        ArrayList<Integer> indexOfPowers = new ArrayList<>();
        for (int i = 0; i < workings.length(); i++) {
            if (workings.charAt(i) == '%')
                indexOfPowers.add(i);
        }

        formula = workings;
        tempFormula = workings;
        for (Integer index : indexOfPowers) {
            changeFormula(index);
        }
        formula = tempFormula;
    }

    private void changeFormula(Integer index) {
        String numberLeft = "";
        String numberRight = "";

        // Find the last number before the percentage sign
        for (int i = index - 1; i >= 0; i--) {
            if (isNumeric(workings.charAt(i)) || workings.charAt(i) == '.') {
                numberLeft = workings.charAt(i) + numberLeft;
            } else {
                break;
            }
        }

        // Find the first number after the percentage sign
        for (int i = index + 1; i < workings.length(); i++) {
            if (isNumeric(workings.charAt(i)) || workings.charAt(i) == '.') {
                numberRight = numberRight + workings.charAt(i);
            } else {
                break;
            }
        }

        // Check if numberRight is not empty before performing the conversion
        if (!numberRight.isEmpty()) {
            // Convert percentage to a multiplication expression
            double percentage = Double.parseDouble(numberLeft) * (Double.parseDouble(numberRight) / 100.0);
            String changed = String.valueOf(percentage);

            String original = numberLeft + "%" + numberRight;
            tempFormula = tempFormula.replace(original, changed);
        }
    }



    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleNfcIntent(intent); // Handle NFC intent when a new intent is received
    }

    private void handleNfcIntent(Intent intent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Uri uri = intent.getData();
            if (uri != null) {
                // Do something with the received URL
                setWorkings(uri.toString());
            }
        }
    }

    public void submit(View view) {
        // Set a specific URL, for example:
        String url = "https://artivagency.com";


        // Prepare the NFC message
        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{
                NdefRecord.createUri(Uri.parse(url)),
                NdefRecord.createMime("application/com.example.myapplication", url.getBytes())
        });

        // Get an instance of NfcAdapter
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        // Check if NFC is enabled
        if (nfcAdapter != null && nfcAdapter.isEnabled()) {
            // Write the NdefMessage to the NFC tag
            nfcAdapter.setNdefPushMessage(ndefMessage, this);
        }

    }

    private boolean isNumeric(char c) {
        return (c <= '9' && c >= '0') || c == '.';
    }

    public void clearOnClick(View view) {
        setWorkings("C");
    }

    private int openBracketCount = 0;

    public void bracketsOnClick(View view) {
        if (openBracketCount % 2 == 0) {
            setWorkings("(");
        } else {
            setWorkings(")");
        }
        openBracketCount++;
    }

    public void powerOfOnClick(View view) {
        setWorkings("%");
    }

    public void divisionOnClick(View view) {
        setWorkings("/");
    }

    public void sevenOnClick(View view) {
        setWorkings("7");
    }

    public void eightOnClick(View view) {
        setWorkings("8");
    }

    public void nineOnClick(View view) {
        setWorkings("9");
    }

    public void timesOnClick(View view) {
        setWorkings("*");
    }

    public void fourOnClick(View view) {
        setWorkings("4");
    }

    public void fiveOnClick(View view) {
        setWorkings("5");
    }

    public void sixOnClick(View view) {
        setWorkings("6");
    }

    public void minusOnClick(View view) {
        setWorkings("-");
    }

    public void oneOnClick(View view) {
        setWorkings("1");
    }

    public void twoOnClick(View view) {
        setWorkings("2");
    }

    public void threeOnClick(View view) {
        setWorkings("3");
    }

    public void plusOnClick(View view) {
        setWorkings("+");
    }

    public void decimalOnClick(View view) {
        setWorkings(".");
    }

    public void zeroOnClick(View view) {
        setWorkings("0");
    }
}
