package com.example.pioneerball;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RulesActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);

        Button backBtn = findViewById(R.id.btn_back_rules);
        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(RulesActivity.this, MainMenuActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
