package com.trainme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;

import com.trainme.api.model.Review;
import com.trainme.databinding.ActivityDetailRoutineBinding;
import com.trainme.databinding.ActivityRateActiviyBinding;
import com.trainme.repository.Status;

public class RateActiviy extends AppCompatActivity {

    ActivityRateActiviyBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityRateActiviyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());





       }

       public void ClickSubmit(View view){
           RatingBar ratingBar=binding.rating;
           EditText editText=binding.editTextReview;
           App api= (App) getApplication();
           Double score= Double.valueOf(ratingBar.getRating());
           int routineId=getIntent().getExtras().getInt("ID");

           //llamado a la Api
           binding.textView.setText("You rated "+ String.valueOf(score));
           Review review=new Review(score,editText.getText().toString() );
           api.getRoutineRepository().addReview(routineId,review).observe(this,
                   r -> {
                       if(r.getStatus()== Status.SUCCESS)
                           Log.d("RATE", "added review");
                       else if (r.getStatus() == Status.ERROR)
                           Log.d("RATE", "rate Error");

                   });
           onBackPressed();
       }
}