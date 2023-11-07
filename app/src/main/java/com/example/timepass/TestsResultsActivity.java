package com.example.timepass;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TestsResultsActivity extends AppCompatActivity {

    private TableLayout resultsTable;
    private int totalPassed = 0;
    private int totalFailed = 0;
    private DatabaseReference databaseReference;
    private Map<String, Boolean> testResults = new HashMap<>();
    private boolean optionSelected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tests_results);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("testResults");
        optionSelected = false;

        resultsTable = findViewById(R.id.resultsTable);

        displayTestResult("Bluetooth Test", getTestResult("bluetoothTest"));
        displayTestResult("Wi-Fi Test", getTestResult("wifiTest"));
        displayTestResult("GPS Test", getTestResult("gpsTest"));
        displayTestResult("Root Status Test", getTestResult("rootTest"));
        displayTestResult("Vibration Test", getTestResult("vibrationTest"));
        displayTestResult("Accelerometer Test", getTestResult("accelerometerTest"));
        displayTestResult("Gyroscope Sensor Test", getTestResult("gyroscopeTest"));
        displayTestResult("Proximity Sensor Test", getTestResult("proximityTest"));
        displayTestResult("Speaker Test", getTestResult("SpeakerTest"));
        displayTestResult("Rear Camera Test", getTestResult("RearCameraTest"));
        displayTestResult("Front Camera Test", getTestResult("FrontCameraTest"));
        displayTestResult("Microphone Test", getTestResult("MicrophoneTest"));
        displayTotalResults();
    }

    private boolean getTestResult(String testName) {
        SharedPreferences preferences = getSharedPreferences("TestResults", MODE_PRIVATE);
        boolean result = preferences.getBoolean(testName, false);
        return result;
    }

    private void displayTestResult(String testName, boolean isPassed) {
        TableRow row = new TableRow(this);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(layoutParams);

        TextView testNameTextView = new TextView(this);
        testNameTextView.setText(testName);
        TextView resultTextView = new TextView(this);
        resultTextView.setText(isPassed ? "Passed" : "Failed");
        testResults.put(testName, isPassed);

        if (isPassed) {
            totalPassed++;
        } else {
            totalFailed++;
        }

        row.addView(testNameTextView);
        row.addView(resultTextView);
        resultsTable.addView(row);
    }

    private void displayTotalResults() {
        TextView totalPassedTextView = new TextView(this);
        totalPassedTextView.setText("Total Passed: " + totalPassed);

        TextView totalFailedTextView = new TextView(this);
        totalFailedTextView.setText("Total Failed: " + totalFailed);

        TableRow row = new TableRow(this);
        row.addView(totalPassedTextView);
        row.addView(totalFailedTextView);

        resultsTable.addView(row);
    }

    // Method to show the options dialog
    public void showOptionsDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose an Option");

        builder.setView(getLayoutInflater().inflate(R.layout.dialog_options, null));

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //Method to send the data to firebase
    public void sendToServer(View view) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String newChildKey = databaseReference.push().getKey();

        DatabaseReference childReference = databaseReference.child(newChildKey);

        for (Map.Entry<String, Boolean> entry : testResults.entrySet()) {
            String testName = entry.getKey();
            boolean isPassed = entry.getValue();

            TestResult testResult = new TestResult(testName, isPassed);

            childReference.push().setValue(testResult);
        }
        Toast.makeText(this, "Data sent to server", Toast.LENGTH_SHORT).show();
    }
    public class TestResult {
        private String testName;
        private boolean isPassed;

        public TestResult() {
        }

        public TestResult(String testName, boolean isPassed) {
            this.testName = testName;
            this.isPassed = isPassed;
        }

        public String getTestName() {
            return testName;
        }

        public boolean isPassed() {
            return isPassed;
        }
    }


    //Method to create and store data to pdf
    public void generatePDF(View view) {
        // Initialize a new document
        Document document = new Document();

        // Set the file path for the PDF
        String filePath = getExternalFilesDir(null).getAbsolutePath() + "/test_report.pdf";

        try {
            // Create a File object to represent the PDF file
            File pdfFile = new File(filePath);

            // Create necessary directories if they don't exist
            pdfFile.getParentFile().mkdirs();

            // Create a PDF file and open it for writing
            PdfWriter.getInstance(document, new FileOutputStream(pdfFile));

            // Open the document for writing
            document.open();

            // Add content to the PDF
            document.add(new Paragraph("Test Report"));
            document.add(new Paragraph("Date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date())));
            document.add(new Paragraph("\n"));

            // Add test results to the PDF
            addTestResultsToPDF(document);

            // Close the document
            document.close();

            // Show a success message
            Toast.makeText(this, "PDF report generated and saved successfully", Toast.LENGTH_SHORT).show();

            // Open the PDF using an Intent
            openPDF(pdfFile);
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "PDF report generation and saving failed", Toast.LENGTH_SHORT).show();
        }
    }
    private void addTestResultsToPDF(Document document) throws DocumentException {
        for (Map.Entry<String, Boolean> entry : testResults.entrySet()) {
            String testName = entry.getKey();
            boolean isPassed = entry.getValue();
            document.add(new Paragraph(testName + ": " + (isPassed ? "Passed" : "Failed")));
        }
    }
    private void openPDF(File pdfFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", pdfFile);
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No PDF viewer installed", Toast.LENGTH_SHORT).show();
        }
    }

    public void onBackPressed() {
        if (optionSelected) {
            super.onBackPressed();
        } else {
            showExitConfirmationDialog();
        }
    }
    private void showExitConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit Confirmation");
        builder.setMessage("Are you sure you want to exit the app?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }
}

