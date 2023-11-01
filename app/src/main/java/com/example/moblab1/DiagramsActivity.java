package com.example.moblab1;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;

public class DiagramsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagrams);
        Intent intent = getIntent();

        List<BarEntry> entries1 = new ArrayList<>();
        List<BarEntry> entries2 = new ArrayList<>();

        int size = intent.getIntExtra("size",0);
        for (int i = 0; i < size; i++) {
            double d1 = intent.getDoubleExtra("matrix1row" + i,0);
            double d2 = intent.getDoubleExtra("matrix2row" + i,0);
            entries1.add(new BarEntry(i+1,(float) d1));
            entries2.add(new BarEntry(i+1,(float) d2));
        }

        Button backButton = findViewById(R.id.backDiagramsButton);

        backButton.setOnClickListener(v -> {
            finish();
        });

        BarChart barChart1 = findViewById(R.id.barChart1);
        BarChart barChart2 = findViewById(R.id.barChart2);

        BarDataSet dataSet1 = new BarDataSet(entries1, "Matrix1");
        BarDataSet dataSet2 = new BarDataSet(entries2, "Matrix2");
        dataSet1.setColor(Color.rgb(0, 155, 0));
        dataSet2.setColor(Color.rgb(0, 0, 155));

        BarData barData1 = new BarData(dataSet1);
        BarData barData2 = new BarData(dataSet2);
        barChart2.setData(barData2);
        barChart1.setData(barData1);

        barChart1.getDescription().setEnabled(false);
        barChart1.setFitBars(true);
        barChart1.animateY(1000);
        barChart1.invalidate();
        barChart2.getDescription().setEnabled(false);
        barChart2.setFitBars(true);
        barChart2.animateY(1000);
        barChart2.invalidate();
    }
}
