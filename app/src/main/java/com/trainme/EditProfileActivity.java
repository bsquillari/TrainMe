package com.trainme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.trainme.api.model.Error;
import com.trainme.api.model.User;
import com.trainme.databinding.ActivityEditProfileBinding;
import com.trainme.repository.Resource;
import com.trainme.repository.Status;

import java.util.Calendar;
import java.util.Date;

public class EditProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ActivityEditProfileBinding binding;
    private LifecycleOwner lifecycleOwner;
    private String gender;
    private long birth;
    private DatePickerDialog datePickerDialog;

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        gender = parent.getItemAtPosition(pos).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        gender = "other";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.editTextGender.setAdapter(adapter);


        binding.editTextGender.setOnItemSelectedListener(this);
        App app = (App) getApplication();
        app.getUserRepository().getCurrentUser().observe(this, r -> {
            if (r.getStatus() == Status.SUCCESS) {
                User user = r.getData();
                binding.editTextFirstName.setText(user.getFirstName());
                binding.editTextLastName.setText(user.getLastName());
                binding.editTextPhone.setText(user.getPhone());
                binding.editTextGender.setSelection(adapter.getPosition(user.getGender()));
                binding.editTextLinkToPicture.setText(user.getAvatarUrl());
            }else{
                defaultHandler(r);
            }
        });


        lifecycleOwner = this;
        binding.editProfileButton.setOnClickListener(l -> {
//            new AlertDialog.Builder(getApplicationContext()).setTitle(R.string.confirmChanges).setPositiveButton(R.string.yes, (dialog, which) -> {
            User u = new User(binding.editTextFirstName.getText().toString(),
                    binding.editTextLastName.getText().toString(), gender,
                    birth, binding.editTextPhone.getText().toString(),
                    binding.editTextLinkToPicture.getText().toString(), null);

            app.getUserRepository().editProfile(u).observe(lifecycleOwner, r -> {
                Log.d("Logout", r.getStatus().toString());
                if (r.getStatus() == Status.SUCCESS) {
                    Log.d("editProfile", getString(R.string.success));
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.editProfileConfirm), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }else{
                    defaultHandler(r);
                }
            });
        });

        initDatePicker();
//        binding.datePickerButton = findViewById(R.id.datePickerButton);
        binding.datePickerButton.setText(getTodaysDate());
//        );
//        setContentView(R.layout.activity_edit_profile);
    }
    private String getTodaysDate()
    {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }
    private void initDatePicker()
    {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                month = month + 1;
                Date newDate = new Date(year, month, day);
                birth = newDate.getTime();
                String date = makeDateString(day, month, year);
                binding.datePickerButton.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        //datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

    }

    private String makeDateString(int day, int month, int year)
    {
        return getMonthFormat(month) + " " + day + " " + year;
    }
    private void defaultHandler(Resource<User> r) {
        switch (r.getStatus()) {
            case LOADING:

                break;
            case ERROR:
                Error error = r.getError();
                String message;

                message = getApplicationContext().getResources().getString(R.string.checkConnection);
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                Log.d("RoutineAdapter", "defaultHandler: " + message);
                break;
        }
    }
    private String getMonthFormat(int month)
    {
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";

        //default should never happen
        return "JAN";
    }

    public void openDatePicker(View view)
    {
        datePickerDialog.show();
    }
}