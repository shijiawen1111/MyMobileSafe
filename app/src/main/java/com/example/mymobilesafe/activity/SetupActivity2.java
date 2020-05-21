package com.example.mymobilesafe.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.mymobilesafe.R;

public class SetupActivity2 extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);
    }

    public void clickPre(View view) {
        Intent intent = new Intent(SetupActivity2.this, SetupActivity1.class);
        startActivity(intent);
    }

    public void clickNext(View view) {
        Intent intent = new Intent(SetupActivity2.this, SetupActivity3.class);
        startActivity(intent);
    }
}
