package com.vehiclebooking;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BookingActivity extends AppCompatActivity {

    private EditText sourceEditText;
    private EditText destinationEditText;
    private TextView selectedDateText;
    private Button selectDateButton;
    private Button bookNowButton;
    
    private Calendar selectedCalendar;
    private NotificationHelper notificationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        initializeViews();
        setupClickListeners();
        selectedCalendar = Calendar.getInstance();
        notificationHelper = new NotificationHelper(this);
    }

    private void initializeViews() {
        sourceEditText = findViewById(R.id.et_source);
        destinationEditText = findViewById(R.id.et_destination);
        selectedDateText = findViewById(R.id.tv_selected_date);
        selectDateButton = findViewById(R.id.btn_select_date);
        bookNowButton = findViewById(R.id.btn_book_now);
    }

    private void setupClickListeners() {
        selectDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        bookNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleBookingSubmission();
            }
        });
    }

    private void showDatePicker() {
        Calendar currentCalendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    selectedCalendar.set(year, month, dayOfMonth);
                    updateSelectedDateDisplay();
                }
            },
            currentCalendar.get(Calendar.YEAR),
            currentCalendar.get(Calendar.MONTH),
            currentCalendar.get(Calendar.DAY_OF_MONTH)
        );
        
        // Set minimum date to today
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void updateSelectedDateDisplay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(selectedCalendar.getTime());
        selectedDateText.setText("Selected Date: " + formattedDate);
    }

    private void handleBookingSubmission() {
        String source = sourceEditText.getText().toString().trim();
        String destination = destinationEditText.getText().toString().trim();
        String selectedDate = selectedDateText.getText().toString();

        if (source.isEmpty() || destination.isEmpty() || selectedDate.equals("No date selected")) {
            Toast.makeText(this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        // Create booking request
        BookingRequest bookingRequest = new BookingRequest(source, destination, selectedCalendar.getTime());
        
        // Send notification to vehicle owner (you)
        notificationHelper.sendBookingNotification(bookingRequest);
        
        // Show confirmation to user
        Toast.makeText(this, getString(R.string.booking_submitted), Toast.LENGTH_LONG).show();
        
        // Clear form
        clearForm();
    }

    private void clearForm() {
        sourceEditText.setText("");
        destinationEditText.setText("");
        selectedDateText.setText("No date selected");
        selectedCalendar = Calendar.getInstance();
    }
}