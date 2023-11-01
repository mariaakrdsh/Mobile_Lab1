package com.example.moblab1;

import android.os.Bundle;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class CardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        Button backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> {
            finish();
        });
    }
}
