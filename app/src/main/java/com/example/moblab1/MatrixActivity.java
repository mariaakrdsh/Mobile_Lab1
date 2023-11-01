package com.example.moblab1;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;

public class MatrixActivity extends AppCompatActivity {


    private Spinner operatorSpinner;
    private Spinner sizeSpinner;
    private EditText[][] matrix1 = new EditText[4][4];
    private EditText[][] matrix2 = new EditText[4][4];
    private TextView resultTextView;
    private String selectedSize;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matrix);
        initializeViews();
        setListeners();
        restoreSavedValues();
    }

    private void initializeViews() {
        File directory = Environment.getExternalStorageDirectory();
        file = new File(directory,"data");
        if (!file.exists()) {
            try {
                if (Build.VERSION.SDK_INT >= 30){
                    if (!Environment.isExternalStorageManager()){
                        Intent getpermission = new Intent();
                        getpermission.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                        startActivity(getpermission);
                    }
                }
                file.createNewFile();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        initializeMatrix();
        operatorSpinner = findViewById(R.id.spinnerOperator);
        sizeSpinner = findViewById(R.id.spinnerSize);
        resultTextView = findViewById(R.id.resultTextView);
        resultTextView.setTextSize(14);
        resultTextView.setTextColor(getResources().getColor(R.color.gray));
        resultTextView = findViewById(R.id.resultTextView);
    }



    private void initializeMatrix() {
        matrix1[0][0] = findViewById(R.id.editText11);
        matrix1[0][1] = findViewById(R.id.editText12);
        matrix1[0][2] = findViewById(R.id.editText13);
        matrix1[0][3] = findViewById(R.id.editText14);
        matrix1[1][0] = findViewById(R.id.editText21);
        matrix1[1][1] = findViewById(R.id.editText22);
        matrix1[1][2] = findViewById(R.id.editText23);
        matrix1[1][3] = findViewById(R.id.editText24);
        matrix1[2][0] = findViewById(R.id.editText31);
        matrix1[2][1] = findViewById(R.id.editText32);
        matrix1[2][2] = findViewById(R.id.editText33);
        matrix1[2][3] = findViewById(R.id.editText34);
        matrix1[3][0] = findViewById(R.id.editText41);
        matrix1[3][1] = findViewById(R.id.editText42);
        matrix1[3][2] = findViewById(R.id.editText43);
        matrix1[3][3] = findViewById(R.id.editText44);
        matrix2[0][0] = findViewById(R.id.editText11matrix2);
        matrix2[0][1] = findViewById(R.id.editText12matrix2);
        matrix2[0][2] = findViewById(R.id.editText13matrix2);
        matrix2[0][3] = findViewById(R.id.editText14matrix2);
        matrix2[1][0] = findViewById(R.id.editText21matrix2);
        matrix2[1][1] = findViewById(R.id.editText22matrix2);
        matrix2[1][2] = findViewById(R.id.editText23matrix2);
        matrix2[1][3] = findViewById(R.id.editText24matrix2);
        matrix2[2][0] = findViewById(R.id.editText31matrix2);
        matrix2[2][1] = findViewById(R.id.editText32matrix2);
        matrix2[2][2] = findViewById(R.id.editText33matrix2);
        matrix2[2][3] = findViewById(R.id.editText34matrix2);
        matrix2[3][0] = findViewById(R.id.editText41matrix2);
        matrix2[3][1] = findViewById(R.id.editText42matrix2);
        matrix2[3][2] = findViewById(R.id.editText43matrix2);
        matrix2[3][3] = findViewById(R.id.editText44matrix2);
    }

    private void setListeners() {
        sizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSize = sizeSpinner.getSelectedItem().toString();
                updateMatrixSize();
                saveData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                saveData();
            }
        });
        operatorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                saveData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button determinantMatrix1Button = findViewById(R.id.determinantButton1);
        determinantMatrix1Button.setOnClickListener(v -> findDeterminantInterface(matrix1));
        Button determinantMatrix2Button = findViewById(R.id.determinantButton2);
        determinantMatrix2Button.setOnClickListener(v -> findDeterminantInterface(matrix2));


        Button reverseMatrix1Button = findViewById(R.id.reverseButton1);
        reverseMatrix1Button.setOnClickListener(v -> findInverseInterface(matrix1));
        Button reverseMatrix2Button = findViewById(R.id.reverseButton2);
        reverseMatrix2Button.setOnClickListener(v -> findInverseInterface(matrix2));

        Button calculateButton = findViewById(R.id.calculateButton);
        calculateButton.setOnClickListener(v -> calculateMatrix());

        Button diagramsButton = findViewById(R.id.diagramsButton);
        diagramsButton.setOnClickListener(v -> {
            saveData();
            Intent intent = new Intent(MatrixActivity.this, DiagramsActivity.class);
            int size = getSize();
            intent.putExtra("size", size);
            for (int i = 0; i < size; i++) {
                double s1 = 0;
                double s2 = 0;
                for (int j = 0; j < size; j++) {
                    s1 += getNum(matrix1,i,j);
                    s2 += getNum(matrix2,i,j);
                }
                intent.putExtra("matrix1row" + i,s1);
                intent.putExtra("matrix2row" + i,s2);
            }
            startActivity(intent);
        });

        Button navigateButton = findViewById(R.id.navigateButton);
        navigateButton.setOnClickListener(v -> {
            saveData();
            Intent intent = new Intent(MatrixActivity.this, CardActivity.class);
            startActivity(intent);
        });
    }

    public void findInverseInterface(EditText[][] matrix){
        int n = getSize();
        try {
            String res = findInverse(matrix, n);
            resultTextView.setText(String.valueOf(res));
            saveData();
        }
        catch (Exception e){
            resultTextView.setText("wrong input");
        }
    }

    public String findInverse(EditText[][] matrix, int n) {
        double[][] augmentedMatrix = new double[n][2 * n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                augmentedMatrix[i][j] = getNum(matrix,i,j);
                augmentedMatrix[i][j + n] = (i == j) ? 1 : 0;
            }
        }

        for (int i = 0; i < n; i++) {
            double divisor = augmentedMatrix[i][i];
            for (int j = 0; j < 2 * n; j++) {
                augmentedMatrix[i][j] /= divisor;
            }

            for (int k = 0; k < n; k++) {
                if (k != i) {
                    double factor = augmentedMatrix[k][i];
                    for (int j = 0; j < 2 * n; j++) {
                        augmentedMatrix[k][j] -= factor * augmentedMatrix[i][j];
                    }
                }
            }
        }

        double[][] inverseMatrix = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                inverseMatrix[i][j] = augmentedMatrix[i][j + n];
            }
        }

        String res = matrixToString(n, inverseMatrix);

        return res;
    }

   private static String matrixToString(int n, double[][] matrix) {
       int[] maxWidths = new int[n];

       for (int j = 0; j < n; j++) {
           for (int i = 0; i < n; i++) {
               String formattedValue = String.format("%.2f", matrix[i][j]).replaceAll("\\.?0*$", "");
               int width = formattedValue.trim().length();
               if (width > maxWidths[j]) {
                   maxWidths[j] = width;
               }
           }
       }

       StringBuilder res = new StringBuilder();

       for (int i = 0; i < n; i++) {
           String result = "";
           for (int j = 0; j < n; j++) {
               String num = String.format("%.2f", matrix[i][j]).trim().replaceAll("\\.?0*$", "");
               for (int k = num.length(); k <= maxWidths[j]; k++) {
                   num += " ";
               }
               result += num;
           }
           result += "\n";
           res.append(result);
       }
       return res.toString();
   }



    @SuppressLint("SetTextI18n")
    private void findDeterminantInterface(EditText[][] matrix) {
        int n = getSize();
        try {
        double res = findDeterminant(matrix, n);
        resultTextView.setText(String.valueOf(res));
        saveData();
        }
        catch (Exception e){
            resultTextView.setText("wrong input");
        }
    }

    private int getSize() {
        int n = ((ArrayAdapter<String>) sizeSpinner.getAdapter()).getPosition(sizeSpinner.getSelectedItem().toString())+1;
        return n;
    }

    private double findDeterminant(EditText[][] matrix, int n) {

        if (n == 1) {
            return getNum(matrix,0,0);
        }

        double determinant = 0;

        for (int i = 0; i < n; i++) {
            int sign = (i % 2 == 0) ? 1 : -1;

            EditText[][] minor = getMinor(matrix, 0, i);

            double minorDeterminant = findDeterminant(minor, n-1);
            double num = getNum(matrix, 0, i);
            determinant += sign * num * minorDeterminant;
        }

        return determinant;
    }

    private static double getNum(EditText[][] matrix, int i, int j) {
        double num = matrix[i][j].getText().toString().isEmpty()? 0 : Double.parseDouble(matrix[i][j].getText().toString());
        return num;
    }

    public static EditText[][] getMinor(EditText[][] matrix, int rowToRemove, int colToRemove) {
        int n = matrix.length;
        EditText[][] minor = new EditText[n - 1][n - 1];
        int minorRow = 0;
        int minorCol;

        for (int i = 0; i < n; i++) {
            if (i == rowToRemove) {
                continue;
            }

            minorCol = 0;
            for (int j = 0; j < n; j++) {
                if (j == colToRemove) {
                    continue;
                }
                minor[minorRow][minorCol] = matrix[i][j];
                minorCol++;
            }

            minorRow++;
        }

        return minor;
    }
    private void restoreSavedValues() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            int savedOperator = Integer.parseInt(br.readLine());
            operatorSpinner.setSelection(savedOperator);

            int savedSize = Integer.parseInt(br.readLine());
            sizeSpinner.setSelection(savedSize);

            for (int i = 0; i <= savedSize; i++) {
                for (int j = 0; j <= savedSize; j++) {
                    matrix1[i][j].setText(br.readLine());
                }
            }

            for (int i = 0; i <= savedSize; i++) {
                for (int j = 0; j <= savedSize; j++) {
                    matrix2[i][j].setText(br.readLine());
                }
            }

            br.close();
        } catch (Exception e) {

        }
    }


    private void saveData() {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos);

            osw.write(((ArrayAdapter<String>) operatorSpinner.getAdapter()).getPosition(operatorSpinner.getSelectedItem().toString()) + "\n");
            int size = getSize()-1;
            osw.write( size + "\n");

            for (int i = 0; i <= size; i++) {
                for (int j = 0; j <= size; j++) {
                    osw.write(matrix1[i][j].getText() + "\n");
                }
            }

            for (int i = 0; i <= size; i++) {
                for (int j = 0; j <= size; j++) {
                    osw.write(matrix2[i][j].getText() + "\n");
                }
            }

            osw.flush();
            osw.close();
            fos.close();
        } catch (Exception e) {
        }
    }

    private void calculateMatrix() {
        int n = getSize();
        String operator = operatorSpinner.getSelectedItem().toString();
        String res = "";
        try {
            double[][] result = new double[n][n];
            switch (operator) {
                case "+":
                    result = addMatrices();
                    break;
                case "-":
                    result = subtractMatrices();
                    break;
                case "*":
                    result = multiplyMatrices();
                    break;
            }
            saveData();
            res = matrixToString(n,result);
        } catch (Exception e) {
            res = "wrong input";
            System.out.println(e.toString());
        }
        resultTextView.setText(res);
    }


    private void updateMatrixSize() {
        int size = getSize();

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if(i < size && j < size) {
                    matrix1[i][j].setVisibility(View.VISIBLE);
                    matrix2[i][j].setVisibility(View.VISIBLE);
                }
                else {
                    matrix1[i][j].setVisibility(View.INVISIBLE);
                    matrix1[i][j].setText("");
                    matrix2[i][j].setVisibility(View.INVISIBLE);
                    matrix1[i][j].setText("");
                }
            }
        }
    }

    // Method to add two matrices
    private double[][] addMatrices() {
        int size = getSize();
    
        // Initialize the result matrix
        double[][] resultMatrix = new double[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                double num1 = getNum(matrix1, i, j);
                double num2 = getNum(matrix2, i, j);
                resultMatrix[i][j] = num1 + num2;
            }
        }

        return resultMatrix;
    }

    private double[][] subtractMatrices() {
        int size = getSize();

        double[][] resultMatrix = new double[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                double num1 = getNum(matrix1, i, j);
                double num2 = getNum(matrix2, i, j);
                resultMatrix[i][j] = num1 - num2;
            }
        }

        return resultMatrix;
    }

    private double[][] multiplyMatrices() {
        int size = getSize();

        double[][] resultMatrix = new double[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                resultMatrix[i][j] = 0;
                for (int k = 0; k < size; k++) {
                    double num1 = getNum(matrix1, i, k);
                    double num2 = getNum(matrix2, k, j);
                    resultMatrix[i][j] += num1 * num2;
                }
            }
        }

        return resultMatrix;
    }

}
