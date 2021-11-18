package com.trainme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.trainme.api.model.ContentEx;
import com.trainme.api.model.Cycle;
import com.trainme.databinding.ActivityPlayRoutineBinding;
import com.trainme.repository.RoutineRepository;
import com.trainme.repository.Status;
import com.trainme.ui.login.LoginActivity;

import java.util.List;

public class PlayRoutineActivity extends AppCompatActivity {

    private static final String TAG = "PlayRoutineActivity";
    private ActivityPlayRoutineBinding binding;
    private MyViewModel model;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPlayRoutineBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());


        binding.getRoot().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        model = new ViewModelProvider(this).get(MyViewModel.class);
        if(model.currentCycle==null){
            model.routineID = getIntent().getExtras().getInt("ID");
            RoutineRepository repository = ((App)getApplication()).getRoutineRepository();

            repository.getCycles(model.cycleIdx++, 1, "order", model.routineID).observe(this, rCycle -> {
                if(rCycle.getStatus() == Status.SUCCESS){
                    model.currentCycle = rCycle.getData().getContent().get(0);
                    model.cycle_isLastPage = rCycle.getData().getIsLastPage();
                    model.totalCycles = rCycle.getData().getTotalCount();
                    binding.cycleTitle.setText(model.currentCycle.getName() + " " + model.currentCycleReps + "/" + model.currentCycle.getRepetitions());
                    repository.getExercises(model.exerciseIdx, 3, "order", model.currentCycle.getId()).observe(this, rExercise -> {

                        if(rExercise.getStatus() == Status.SUCCESS){
                            model.currentExercise = rExercise.getData().getContent();
                            model.exercise_isLastPage = rExercise.getData().getIsLastPage();
                            model.totalExercises = rExercise.getData().getTotalCount();
                            // NO DETAIL VIEW
                            binding.exercisesLeft.setText(getResources().getString(R.string.ejercicios)+" " + model.exercisesDone + "/" + model.totalExercises);
                            if(model.totalExercises >0) {
                                binding.exerciseName1.setText(model.currentExercise.get(model.miniExerciseListIdx).getExercise().getName());
                                binding.exerciseReps1.setText((model.currentExerciseReps) + "/" + (model.currentExercise.get(model.miniExerciseListIdx).getRepetitions()));
                                binding.exerciseCard1.setCardBackgroundColor(getResources().getColor(R.color.teal_200));
                            }else binding.exerciseCard1.setVisibility(View.GONE);
                            if(model.totalExercises>1){
                                binding.exerciseName2.setText(model.currentExercise.get(model.miniExerciseListIdx+1).getExercise().getName());
                                binding.exerciseReps2.setText("0" + "/" + (model.currentExercise.get(model.miniExerciseListIdx + 1).getRepetitions()));
                            }else binding.exerciseCard2.setVisibility(View.GONE);
                            if(model.totalExercises>2) {
                                binding.exerciseName3.setText(model.currentExercise.get(model.miniExerciseListIdx+2).getExercise().getName());
                                binding.exerciseReps3.setText("0" + "/" + (model.currentExercise.get(model.miniExerciseListIdx + 2).getRepetitions()));
                            }else binding.exerciseCard3.setVisibility(View.GONE);

                            // DETAIL VIEW
                            binding.exerciseTitleDetailLayout.setText(model.currentExercise.get(model.miniExerciseListIdx).getExercise().getName() +" "+model.currentExerciseReps+"/"+model.currentExercise.get(model.miniExerciseListIdx).getRepetitions());
                            int minutes = model.currentExercise.get(model.miniExerciseListIdx).getDuration() / 60;
                            int seconds = (model.currentExercise.get(model.miniExerciseListIdx).getDuration() % 60);
                            binding.exerciseDurationDetailLayout.setText(minutes + ":" + seconds);
                            binding.exerciseDetailDetailLayout.setText(model.currentExercise.get(model.miniExerciseListIdx).getExercise().getDetail());
                            binding.exerciseTypeDetailLayout.setText(model.currentExercise.get(model.miniExerciseListIdx).getExercise().getType());

                            model.timeLeftMiliseconds = model.currentExercise.get(model.miniExerciseListIdx).getDuration()*1000;
                            binding.toggleExerciseView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(binding.noDetailLayout.getVisibility() == View.VISIBLE){
                                        binding.noDetailLayout.setVisibility(View.GONE);
                                        binding.detailLayout.setVisibility(View.VISIBLE);
                                    }else{
                                        binding.noDetailLayout.setVisibility(View.VISIBLE);
                                        binding.detailLayout.setVisibility(View.GONE);
                                    }
                                }
                            });
                            binding.fabPlay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(!model.timerRunning){
                                        model.timer = new CountDownTimer(model.timeLeftMiliseconds, 1000) {
                                            @Override
                                            public void onTick(long l) {
                                                model.timeLeftMiliseconds = l;
                                                updateTimer();
                                            }

                                            @Override
                                            public void onFinish() {
                                                boolean doNotStartTimer = model.exercise_isLastPage && model.miniExerciseListIdx==2 && (model.exercisesDone+1)==model.totalExercises;
                                                nextExercise();
                                                if(!doNotStartTimer) {
                                                    model.timerRunning = false;
                                                    binding.fabPlay.callOnClick();
                                                }
                                                // Hacer ruidito
                                            }
                                        };
                                        model.timer.start();
                                        model.timerRunning = true;
                                    }else {
                                        model.timer.cancel();
                                        model.timerRunning = false;
                                    }
                                }
                            });
                            binding.fabNext.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    nextExercise();
                                    if(model.timer !=null)
                                    model.timer.cancel();
                                    model.timerRunning = false;

                                }
                            });
                            binding.fabPrev.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    prevExercise();
                                    if(model.timer !=null)
                                        model.timer.cancel();
                                    model.timerRunning = false;

                                }
                            });
                            updateTimer();
                        }else {
                            // Loading
                        }
                    });
                }else {
                    // Loading
                }
            });
        }

    }

    private void prevExercise() {
        if(model.currentExerciseReps==0){
            if(model.miniExerciseListIdx==0){
                if(model.currentCycleReps==0){

                }else {
                    model.currentCycleReps--;
                    resetearCiclo();
                }
            }else{
                if(model.miniExerciseListIdx==1){
                    binding.exerciseCard2.setCardBackgroundColor(getResources().getColor(R.color.purple_200));
                }else binding.exerciseCard3.setCardBackgroundColor(getResources().getColor(R.color.purple_200));
                model.miniExerciseListIdx--;
                model.exercisesDone--;
                model.currentExerciseReps = (model.currentExercise.get(model.miniExerciseListIdx).getRepetitions())-1;
                binding.exercisesLeft.setText(getResources().getString(R.string.ejercicios)+" " + model.exercisesDone + "/" + model.totalExercises);
                if(model.miniExerciseListIdx==0) {
                    binding.exerciseName1.setText(model.currentExercise.get(model.miniExerciseListIdx).getExercise().getName());
                    binding.exerciseReps1.setText((model.currentExerciseReps) + "/" + (model.currentExercise.get(model.miniExerciseListIdx).getRepetitions()));
                    binding.exerciseCard1.setCardBackgroundColor(getResources().getColor(R.color.teal_200));
                }else if(model.miniExerciseListIdx==1){
                    binding.exerciseName2.setText(model.currentExercise.get(model.miniExerciseListIdx).getExercise().getName());
                    binding.exerciseReps2.setText((model.currentExerciseReps) + "/" + (model.currentExercise.get(model.miniExerciseListIdx).getRepetitions()));
                    binding.exerciseCard2.setCardBackgroundColor(getResources().getColor(R.color.teal_200));
                }else if(model.miniExerciseListIdx==2) {        // Creo que no llega nunca aca
                    binding.exerciseName3.setText(model.currentExercise.get(model.miniExerciseListIdx).getExercise().getName());
                    binding.exerciseReps3.setText((model.currentExerciseReps) + "/" + (model.currentExercise.get(model.miniExerciseListIdx).getRepetitions()));
                    binding.exerciseCard3.setCardBackgroundColor(getResources().getColor(R.color.teal_200));
                }
            }
        }else {
            model.currentExerciseReps--;
            updateCurrentExerciseNoDetail();

            // DETAIL VIEW
            binding.exerciseTitleDetailLayout.setText(model.currentExercise.get(model.miniExerciseListIdx).getExercise().getName() +" "+model.currentExerciseReps+"/"+model.currentExercise.get(model.miniExerciseListIdx).getRepetitions());
            model.timeLeftMiliseconds = model.currentExercise.get(model.miniExerciseListIdx).getDuration()*1000;
            updateTimer();
        }
    }

    private void updateTimer() {
        int minutes = ((int)model.timeLeftMiliseconds) / 60000;
        int seconds = ((int)model.timeLeftMiliseconds % 60000)/1000;
        String timeLeft;
        timeLeft = "" + minutes;
        timeLeft += ":";
        timeLeft+=""+seconds;
        binding.routineTimer.setText(timeLeft);
    }

    public void nextExercise(){
        if(++model.currentExerciseReps != model.currentExercise.get(model.miniExerciseListIdx).getRepetitions()){     // Quedan ejercicios del mismo tipo
            Log.d(TAG, "nextExercise: Quedan ejercicios del mismo");
            updateCurrentExerciseNoDetail();

            // DETAIL VIEW
            binding.exerciseTitleDetailLayout.setText(model.currentExercise.get(model.miniExerciseListIdx).getExercise().getName() +" "+model.currentExerciseReps+"/"+model.currentExercise.get(model.miniExerciseListIdx).getRepetitions());
            model.timeLeftMiliseconds = model.currentExercise.get(model.miniExerciseListIdx).getDuration()*1000;
            updateTimer();
        }else{
            Log.d(TAG, "nextExercise: Termina un ejercicio");
            //Termino un ejecicio
            finishCurrentExercise();
            binding.exerciseTitleDetailLayout.setText(model.currentExercise.get(model.miniExerciseListIdx).getExercise().getName() +" "+model.currentExerciseReps+"/"+model.currentExercise.get(model.miniExerciseListIdx).getRepetitions());
            if(++model.miniExerciseListIdx!=model.currentExercise.size()){       // Quedan ejercicios en la mini lista
                Log.d(TAG, "nextExercise: Quedan ejercicios en la mini lista");
                nextMiniListExercise();
            }else{      // Se acaba la minilista
                Log.d(TAG, "nextExercise: Se acabo la minilista");
                if(model.exercise_isLastPage){   // Termina ciclo
                    if(++model.currentCycleReps == model.currentCycle.getRepetitions()){    // Termino repeticion de ciclo
                        if (!model.cycle_isLastPage) {    // Quedan ciclos
                            Log.d(TAG, "nextExercise: Termina ciclo");
                            new AlertDialog.Builder(this)
                                    .setTitle(getResources().getString(R.string.finishCyclePopUp) + " " + model.currentCycle.getName() + ". " + getResources().getString(R.string.keepGoing))
                                    .setMessage(getResources().getString(R.string.cyclesLeft, model.cyclesDone+1, model.totalCycles))
                                    .setPositiveButton(R.string.goNextCycle, null).show();
                            proximoCyclo();
                        } else { // Termino rutina
                            AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                                    .setTitle(getResources().getString(R.string.finishRoutine)).setMessage(getResources().getString(R.string.rememberToReview))
                                    .setPositiveButton(R.string.goBackToDetail, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }

                                    });
                            dialog.setCancelable(false);
                            dialog.show();
                        }
                    }else { // Me quedan repeticiones de ciclos
                        resetearCiclo();
                    }
                }else {     // Renuevo minilista
                    Log.d(TAG, "nextExercise: Renuevo minilista");
                    renuevoMiniList();
                }
            }
        }
    }

    private void resetearCiclo() {
        model.exercisesDone = 0;
        model.exerciseIdx = 0;
        model.currentExerciseReps = 0;
        model.miniExerciseListIdx = 0;
        binding.cycleTitle.setText(model.currentCycle.getName() + " " + model.currentCycleReps + "/" + model.currentCycle.getRepetitions());
        ((App)getApplication()).getRoutineRepository().getExercises(model.exerciseIdx, 3, "order", model.currentCycle.getId()).observe(this, rExercise -> {

            if(rExercise.getStatus() == Status.SUCCESS){
                model.currentExercise = rExercise.getData().getContent();
                model.exercise_isLastPage = rExercise.getData().getIsLastPage();
                model.miniExerciseListIdx=0;
                model.currentExerciseReps = 0;
                // NO DETAIL VIEW
                binding.exercisesLeft.setText(getResources().getString(R.string.ejercicios)+" " + model.exercisesDone + "/" + model.totalExercises);
                if(model.currentExercise.size() >0) {
                    binding.exerciseName1.setText(model.currentExercise.get(model.miniExerciseListIdx).getExercise().getName());
                    binding.exerciseReps1.setText((model.currentExerciseReps) + "/" + (model.currentExercise.get(model.miniExerciseListIdx).getRepetitions()));
                    binding.exerciseCard1.setCardBackgroundColor(getResources().getColor(R.color.teal_200));
                    binding.exerciseCard1.setVisibility(View.VISIBLE);
                }else binding.exerciseCard1.setVisibility(View.GONE);
                if(model.currentExercise.size()>1){
                    binding.exerciseName2.setText(model.currentExercise.get(model.miniExerciseListIdx+1).getExercise().getName());
                    binding.exerciseReps2.setText("0" + "/" + (model.currentExercise.get(model.miniExerciseListIdx + 1).getRepetitions()));
                    binding.exerciseCard2.setCardBackgroundColor(getResources().getColor(R.color.purple_200));
                    binding.exerciseCard2.setVisibility(View.VISIBLE);
                }else binding.exerciseCard2.setVisibility(View.GONE);
                if(model.currentExercise.size()>2) {
                    binding.exerciseName3.setText(model.currentExercise.get(model.miniExerciseListIdx+2).getExercise().getName());
                    binding.exerciseReps3.setText("0" + "/" + (model.currentExercise.get(model.miniExerciseListIdx + 2).getRepetitions()));
                    binding.exerciseCard3.setCardBackgroundColor(getResources().getColor(R.color.purple_200));
                    binding.exerciseCard3.setVisibility(View.VISIBLE);
                }else binding.exerciseCard3.setVisibility(View.GONE);

                // DETAIL VIEW
                updateCurrentExerciseDetail();

                // Timer
                model.timeLeftMiliseconds = model.currentExercise.get(model.miniExerciseListIdx).getDuration()*1000;
                updateTimer();


            }else {
                // Loading
            }
        });
    }

    private void proximoCyclo() {
        RoutineRepository repository = ((App)getApplication()).getRoutineRepository();
        model.exercisesDone=0;
        model.currentCycleReps = 0;
        model.miniExerciseListIdx=0;
        model.currentExerciseReps=0;
        model.exerciseIdx=0;
        model.cyclesDone++;
        repository.getCycles(model.cycleIdx++, 1, "order", model.routineID).observe(this, rCycle -> {
            if(rCycle.getStatus() == Status.SUCCESS){
                model.currentCycle = rCycle.getData().getContent().get(0);
                model.cycle_isLastPage = rCycle.getData().getIsLastPage();
                model.totalCycles = rCycle.getData().getTotalCount();
                binding.cycleTitle.setText(model.currentCycle.getName() + " " + model.currentCycleReps + "/" + model.currentCycle.getRepetitions());
                repository.getExercises(model.exerciseIdx, 3, "order", model.currentCycle.getId()).observe(this, rExercise -> {

                    if(rExercise.getStatus() == Status.SUCCESS){
                        model.currentExercise = rExercise.getData().getContent();
                        model.exercise_isLastPage = rExercise.getData().getIsLastPage();
                        model.totalExercises = rExercise.getData().getTotalCount();
                        // NO DETAIL VIEW
                        binding.exercisesLeft.setText(getResources().getString(R.string.ejercicios)+" " + model.exercisesDone + "/" + model.totalExercises);
                        if(model.totalExercises >0) {
                            binding.exerciseName1.setText(model.currentExercise.get(model.miniExerciseListIdx).getExercise().getName());
                            binding.exerciseReps1.setText((model.currentExerciseReps) + "/" + (model.currentExercise.get(model.miniExerciseListIdx).getRepetitions()));
                            binding.exerciseCard1.setCardBackgroundColor(getResources().getColor(R.color.teal_200));
                            binding.exerciseCard1.setVisibility(View.VISIBLE);
                        }else binding.exerciseCard1.setVisibility(View.GONE);
                        if(model.totalExercises>1){
                            binding.exerciseName2.setText(model.currentExercise.get(model.miniExerciseListIdx+1).getExercise().getName());
                            binding.exerciseReps2.setText("0" + "/" + (model.currentExercise.get(model.miniExerciseListIdx + 1).getRepetitions()));
                            binding.exerciseCard2.setCardBackgroundColor(getResources().getColor(R.color.purple_200));
                            binding.exerciseCard2.setVisibility(View.VISIBLE);
                        }else binding.exerciseCard2.setVisibility(View.GONE);
                        if(model.totalExercises>2) {
                            binding.exerciseName3.setText(model.currentExercise.get(model.miniExerciseListIdx+2).getExercise().getName());
                            binding.exerciseReps3.setText("0" + "/" + (model.currentExercise.get(model.miniExerciseListIdx + 2).getRepetitions()));
                            binding.exerciseCard3.setCardBackgroundColor(getResources().getColor(R.color.purple_200));
                            binding.exerciseCard3.setVisibility(View.VISIBLE);
                        }else binding.exerciseCard3.setVisibility(View.GONE);

                        // DETAIL VIEW
                        binding.exerciseTitleDetailLayout.setText(model.currentExercise.get(model.miniExerciseListIdx).getExercise().getName() +" "+model.currentExerciseReps+"/"+model.currentExercise.get(model.miniExerciseListIdx).getRepetitions());
                        int minutes = model.currentExercise.get(model.miniExerciseListIdx).getDuration() / 60;
                        int seconds = (model.currentExercise.get(model.miniExerciseListIdx).getDuration() % 60);
                        binding.exerciseDurationDetailLayout.setText(minutes + ":" + seconds);
                        binding.exerciseDetailDetailLayout.setText(model.currentExercise.get(model.miniExerciseListIdx).getExercise().getDetail());
                        binding.exerciseTypeDetailLayout.setText(model.currentExercise.get(model.miniExerciseListIdx).getExercise().getType());

                        model.timeLeftMiliseconds = model.currentExercise.get(model.miniExerciseListIdx).getDuration()*1000;
                        updateTimer();
                    }else {
                        // Loading
                    }
                });
            }else {
                // Loading
            }
        });
    }

    private void renuevoMiniList() {
        ((App)getApplication()).getRoutineRepository().getExercises(++model.exerciseIdx, 3, "order", model.currentCycle.getId()).observe(this, rExercise -> {

            if(rExercise.getStatus() == Status.SUCCESS){
                model.currentExercise = rExercise.getData().getContent();
                model.exercise_isLastPage = rExercise.getData().getIsLastPage();
                model.miniExerciseListIdx=0;
                model.currentExerciseReps = 0;
                // NO DETAIL VIEW
                if(model.currentExercise.size() >0) {
                    binding.exerciseName1.setText(model.currentExercise.get(model.miniExerciseListIdx).getExercise().getName());
                    binding.exerciseReps1.setText((model.currentExerciseReps) + "/" + (model.currentExercise.get(model.miniExerciseListIdx).getRepetitions()));
                    binding.exerciseCard1.setCardBackgroundColor(getResources().getColor(R.color.teal_200));
                }else binding.exerciseCard1.setVisibility(View.GONE);
                if(model.currentExercise.size()>1){
                    binding.exerciseName2.setText(model.currentExercise.get(model.miniExerciseListIdx+1).getExercise().getName());
                    binding.exerciseReps2.setText("0" + "/" + (model.currentExercise.get(model.miniExerciseListIdx + 1).getRepetitions()));
                    binding.exerciseCard2.setCardBackgroundColor(getResources().getColor(R.color.purple_200));
                }else binding.exerciseCard2.setVisibility(View.GONE);
                if(model.currentExercise.size()>2) {
                    binding.exerciseName3.setText(model.currentExercise.get(model.miniExerciseListIdx+2).getExercise().getName());
                    binding.exerciseReps3.setText("0" + "/" + (model.currentExercise.get(model.miniExerciseListIdx + 2).getRepetitions()));
                    binding.exerciseCard3.setCardBackgroundColor(getResources().getColor(R.color.purple_200));
                }else binding.exerciseCard3.setVisibility(View.GONE);

                // DETAIL VIEW
                updateCurrentExerciseDetail();

                // Timer
                model.timeLeftMiliseconds = model.currentExercise.get(model.miniExerciseListIdx).getDuration()*1000;
                updateTimer();

            }else {
                // Loading
            }
        });
    }

    private void nextMiniListExercise() {
        model.currentExerciseReps = 0;
        model.timeLeftMiliseconds = model.currentExercise.get(model.miniExerciseListIdx).getDuration() * 1000;
        updateTimer();
        updateCurrentExerciseNoDetail();
        updateCurrentExerciseDetail();
    }

    public void updateCurrentExerciseDetail(){
        // DETAIL VIEW
        binding.exerciseTitleDetailLayout.setText(model.currentExercise.get(model.miniExerciseListIdx).getExercise().getName() +" "+model.currentExerciseReps+"/"+model.currentExercise.get(model.miniExerciseListIdx).getRepetitions());
        int minutes = model.currentExercise.get(model.miniExerciseListIdx).getDuration() / 60;
        int seconds = (model.currentExercise.get(model.miniExerciseListIdx).getDuration() % 60);
        binding.exerciseDurationDetailLayout.setText(minutes + ":" + seconds);
        binding.exerciseDetailDetailLayout.setText(model.currentExercise.get(model.miniExerciseListIdx).getExercise().getDetail());
        binding.exerciseTypeDetailLayout.setText(model.currentExercise.get(model.miniExerciseListIdx).getExercise().getType());
    }

    public void updateCurrentExerciseNoDetail(){
        binding.exercisesLeft.setText(getResources().getString(R.string.ejercicios)+" " + model.exercisesDone + "/" + model.totalExercises);
        if(model.miniExerciseListIdx==0) {
            binding.exerciseName1.setText(model.currentExercise.get(model.miniExerciseListIdx).getExercise().getName());
            binding.exerciseReps1.setText((model.currentExerciseReps) + "/" + (model.currentExercise.get(model.miniExerciseListIdx).getRepetitions()));
            binding.exerciseCard1.setCardBackgroundColor(getResources().getColor(R.color.teal_200));
        }else if(model.miniExerciseListIdx==1){
            binding.exerciseName2.setText(model.currentExercise.get(model.miniExerciseListIdx).getExercise().getName());
            binding.exerciseReps2.setText((model.currentExerciseReps) + "/" + (model.currentExercise.get(model.miniExerciseListIdx).getRepetitions()));
            binding.exerciseCard2.setCardBackgroundColor(getResources().getColor(R.color.teal_200));
        }else if(model.miniExerciseListIdx==2) {
            binding.exerciseName3.setText(model.currentExercise.get(model.miniExerciseListIdx).getExercise().getName());
            binding.exerciseReps3.setText((model.currentExerciseReps) + "/" + (model.currentExercise.get(model.miniExerciseListIdx).getRepetitions()));
            binding.exerciseCard3.setCardBackgroundColor(getResources().getColor(R.color.teal_200));
        }
    }

    public void finishCurrentExercise(){
        model.exercisesDone++;
        binding.exercisesLeft.setText(getResources().getString(R.string.ejercicios)+" " + model.exercisesDone + "/" + model.totalExercises);
        updateCurrentExerciseNoDetail();
        // Cambio color del ejercicio
        if(model.miniExerciseListIdx==0) {
            binding.exerciseCard1.setCardBackgroundColor(getResources().getColor(R.color.purple_500));
        }else if(model.miniExerciseListIdx==1){
            binding.exerciseCard2.setCardBackgroundColor(getResources().getColor(R.color.purple_500));
        }else if(model.miniExerciseListIdx==2) {
            binding.exerciseCard3.setCardBackgroundColor(getResources().getColor(R.color.purple_500));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class MyViewModel extends ViewModel{
        public long timeLeftMiliseconds;
        public boolean timerRunning = false;
        public CountDownTimer timer;
        public List<ContentEx> currentExercise;
        public Cycle currentCycle;
        public int totalCycles;
        public int cyclesDone = 0;
        public int totalExercises;
        public int exercisesDone = 0;
        public int routineID;
        public int cycleIdx=0;
        public int exerciseIdx=0;
        public int currentExerciseReps = 0;
        public int currentCycleReps = 0;
        public int miniExerciseListIdx=0;
        public boolean cycle_isLastPage;
        public boolean exercise_isLastPage;

        public MyViewModel() {
        }
    }
}