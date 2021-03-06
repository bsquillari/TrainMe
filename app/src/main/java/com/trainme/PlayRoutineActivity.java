package com.trainme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.trainme.api.model.ContentEx;
import com.trainme.api.model.Cycle;
import com.trainme.api.model.Error;
import com.trainme.api.model.Exercise;
import com.trainme.api.model.PagedList;
import com.trainme.api.model.Routine;
import com.trainme.databinding.ActivityPlayRoutineBinding;
import com.trainme.repository.Resource;
import com.trainme.repository.RoutineRepository;
import com.trainme.repository.Status;

import java.util.List;
import java.util.ListIterator;

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

            repository.getCycles(0, 1, "order", model.routineID).observe(this, rAuxCycle -> {
                if(rAuxCycle.getStatus() == Status.SUCCESS){
                    if(rAuxCycle.getData().getTotalCount()==0){
                        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                                .setTitle(getResources().getString(R.string.noCycles))
                                .setPositiveButton(R.string.goBackToDetail, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }

                                }).setCancelable(false);
                        dialog.show();
                    }
                    repository.getCycles(0, rAuxCycle.getData().getTotalCount(), "order", model.routineID).observe(this, rCycle -> {
                        if(rCycle.getStatus() == Status.SUCCESS){
                        model.totalCycles = rCycle.getData().getTotalCount();
                        model.cycles = rCycle.getData().getContent();
                        model.cycleIterator = model.cycles.listIterator();
                        model.currentCycle = model.cycleIterator.next();
                        binding.cycleTitle.setText(new StringBuilder().append(model.currentCycle.getName()).append(" ").append(getResources().getString(R.string.barra, model.currentCycleReps, model.currentCycle.getRepetitions())).toString());
                        repository.getExercises(0, 1, "order", model.currentCycle.getId()).observe(this, rAuxExercise -> {

                            if (rAuxExercise.getStatus() == Status.SUCCESS) {
                                if(rAuxExercise.getData().getTotalCount()==0){
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                                            .setTitle(getResources().getString(R.string.noExercises, model.currentCycle.getName())).setMessage(getResources().getString(R.string.noExercisesMsg))
                                            .setPositiveButton(R.string.goBackToDetail, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    finish();
                                                }

                                            }).setCancelable(false);
                                    dialog.show();
                                }
                                repository.getExercises(0, rAuxExercise.getData().getTotalCount(), "order", model.currentCycle.getId()).observe(this, rExercise -> {
                                    if (rExercise.getStatus() == Status.SUCCESS) {
                                        model.Exercises = rExercise.getData().getContent();
//                                    model.exercise_isLastPage = rExercise.getData().getIsLastPage();
                                        model.totalExercises = rExercise.getData().getTotalCount();
                                        model.exercisesIterator = model.Exercises.listIterator();
                                        model.currentExercise = model.exercisesIterator.next();
                                        // NO DETAIL VIEW
                                        binding.exercisesLeft.setText(new StringBuilder().append(getResources().getString(R.string.ejercicios)).append(" ").append(getResources().getString(R.string.barra, model.exercisesDone, model.totalExercises)).toString());
                                        SharedPreferences settings = getSharedPreferences("UserPreferences", 0);
                                        if(settings!=null){
                                            boolean value = settings.getBoolean("DetailExerciseView", false);
                                            if(value){
                                                binding.detailLayout.setVisibility(View.VISIBLE);
                                                binding.noDetailLayout.setVisibility(View.INVISIBLE);
                                            }else{
                                                binding.noDetailLayout.setVisibility(View.VISIBLE);
                                                binding.detailLayout.setVisibility(View.INVISIBLE);
                                            }
                                        }
                                        binding.exerciseCard1.setVisibility(View.INVISIBLE);
                                        if (model.totalExercises > 0) {
                                            int idxAux = model.exercisesIterator.nextIndex();
                                            binding.exerciseName2.setText(model.currentExercise.getExercise().getName());
                                            binding.exerciseReps2.setText(getResources().getString(R.string.barra, 0, model.currentExercise.getRepetitions()));
                                            TypedValue typedValue = new TypedValue();
                                            getTheme().resolveAttribute(R.attr.colorSecondary, typedValue, true);
                                            binding.exerciseCard2.setCardBackgroundColor(typedValue.data);
                                            if (model.totalExercises > 1) {
                                                binding.exerciseName3.setText(model.Exercises.get(idxAux).getExercise().getName());
                                                binding.exerciseReps3.setText(getResources().getString(R.string.barra, 0, model.Exercises.get(idxAux).getRepetitions()));
                                            } else binding.exerciseCard3.setVisibility(View.GONE);
                                        } else {
                                            // TODO NO TIENE EJERCICIOS
                                        }


                                        // DETAIL VIEW
                                        binding.exerciseTitleDetailLayout.setText(model.currentExercise.getExercise().getName() + " " + model.currentExerciseReps + "/" + model.currentExercise.getRepetitions());
                                        int minutes = model.currentExercise.getDuration() / 60;
                                        int seconds = (model.currentExercise.getDuration() % 60);
                                        binding.exerciseDurationDetailLayout.setText(minutes + ":" + seconds);
                                        binding.exerciseDetailDetailLayout.setText(model.currentExercise.getExercise().getDetail());
                                        binding.exerciseTypeDetailLayout.setText(model.currentExercise.getExercise().getType());

                                        model.timeLeftMiliseconds = model.currentExercise.getDuration() * 1000;
                                        binding.toggleExerciseView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                if (binding.noDetailLayout.getVisibility() == View.VISIBLE) {
                                                    binding.noDetailLayout.setVisibility(View.GONE);
                                                    binding.detailLayout.setVisibility(View.VISIBLE);
                                                } else {
                                                    binding.noDetailLayout.setVisibility(View.VISIBLE);
                                                    binding.detailLayout.setVisibility(View.GONE);
                                                }
                                            }
                                        });
                                        binding.fabPlay.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                if (!model.timerRunning) {
                                                    model.timer = new CountDownTimer(model.timeLeftMiliseconds, 1000) {
                                                        @Override
                                                        public void onTick(long l) {
                                                            model.timeLeftMiliseconds = l;
                                                            updateTimer();
                                                        }

                                                        @Override
                                                        public void onFinish() {
                                                            boolean startTimer = !(!model.exercisesIterator.hasNext() && !model.cycleIterator.hasNext() && (model.currentExerciseReps+1)==model.currentExercise.getRepetitions() && (model.currentCycleReps+1)==model.currentCycle.getRepetitions());
                                                            nextExercise();
                                                            if (startTimer) {
                                                                if(model.timer!=null)
                                                                    model.timer.cancel();
                                                                model.timerRunning = false;
                                                                binding.fabPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24));
                                                                binding.fabPlay.callOnClick();
                                                            }else {
                                                                model.timerRunning = false;
                                                                binding.fabPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24));
                                                            }
                                                            playSound();

                                                        }
                                                    };
                                                    model.timer.start();
                                                    model.timerRunning = true;
                                                    binding.fabPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_pause_24));
                                                } else {
                                                    model.timer.cancel();
                                                    model.timerRunning = false;
                                                    binding.fabPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24));
                                                }
                                            }
                                        });
                                        binding.fabNext.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                nextExercise();
                                                if (model.timer != null)
                                                    model.timer.cancel();
                                                model.timerRunning = false;
                                                binding.fabPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24));


                                            }
                                        });
                                        binding.fabStop.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                if(model.timerRunning) {
                                                    if(model.timer!=null)
                                                        model.timer.cancel();
                                                    model.timerRunning = false;
                                                }
                                                confirmStop();
                                            }
                                        });
                                        binding.fabPrev.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                prevExercise();
                                                resetTimer(model.currentExercise.getDuration()*1000);
                                                if (model.timer != null)
                                                    model.timer.cancel();
                                                model.timerRunning = false;
                                                binding.fabPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24));
                                            }
                                        });
                                        updateTimer();
                                    } else {
                                        // loading
                                        defaultHandler(rExercise);
                                    }
                                });
                            } else {
                                // Loading
                                defaultHandler(rAuxExercise);
                            }
                        });
                    }else{
                        // Loading
                            defaultHandlerCycle(rCycle);
                    }
                });
            }else{
                // Loading
                    defaultHandlerCycle(rAuxCycle);
            }
        });

    }else{

            if(!model.cycleIterator.hasNext() && model.currentCycleReps==model.currentCycle.getRepetitions()){
                finishRoutine();
            }
            // Card 1
            if(model.exercisesIterator.nextIndex()==1){
                binding.exerciseCard1.setVisibility(View.INVISIBLE);
            }else {
                int auxIdx = model.exercisesIterator.previousIndex();
                binding.exerciseReps1.setText(getResources().getString(R.string.barra, model.Exercises.get(auxIdx).getRepetitions(), model.Exercises.get(auxIdx).getRepetitions()));
                binding.exerciseName1.setText(model.Exercises.get(auxIdx).getExercise().getName());
            }
            // Card 2
            binding.exerciseName2.setText(model.currentExercise.getExercise().getName());
            binding.exerciseReps2.setText(getResources().getString(R.string.barra, model.currentExerciseReps, model.currentExercise.getRepetitions()));
            // Card 3
            if(!model.exercisesIterator.hasNext()){
                binding.exerciseCard3.setVisibility(View.INVISIBLE);
            }else {
                int auxIdx = model.exercisesIterator.nextIndex();
                binding.exerciseReps3.setText(getResources().getString(R.string.barra, 0, model.Exercises.get(auxIdx).getRepetitions()));
                binding.exerciseName3.setText(model.Exercises.get(auxIdx).getExercise().getName());
            }
            // Card de detail
            binding.exerciseTitleDetailLayout.setText(model.currentExercise.getExercise().getName() + " " + model.currentExerciseReps + "/" + model.currentExercise.getRepetitions());
            int minutes = model.currentExercise.getDuration() / 60;
            int seconds = (model.currentExercise.getDuration() % 60);
            binding.exerciseDurationDetailLayout.setText(minutes + ":" + seconds);
            binding.exerciseDetailDetailLayout.setText(model.currentExercise.getExercise().getDetail());
            binding.exerciseTypeDetailLayout.setText(model.currentExercise.getExercise().getType());
            // Exercises left
            binding.exercisesLeft.setText(new StringBuilder().append(getResources().getString(R.string.ejercicios)).append(" ").append(getResources().getString(R.string.barra, model.exercisesDone, model.totalExercises)).toString());
            // Title routine
            binding.cycleTitle.setText(new StringBuilder().append(model.currentCycle.getName()).append(" ").append(getResources().getString(R.string.barra, model.currentCycleReps, model.currentCycle.getRepetitions())).toString());
            // Botones
            binding.toggleExerciseView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (binding.noDetailLayout.getVisibility() == View.VISIBLE) {
                        binding.noDetailLayout.setVisibility(View.GONE);
                        binding.detailLayout.setVisibility(View.VISIBLE);
                    } else {
                        binding.noDetailLayout.setVisibility(View.VISIBLE);
                        binding.detailLayout.setVisibility(View.GONE);
                    }
                }
            });
            binding.fabPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!model.timerRunning) {
                        model.timer = new CountDownTimer(model.timeLeftMiliseconds, 1000) {
                            @Override
                            public void onTick(long l) {
                                model.timeLeftMiliseconds = l;
                                updateTimer();
                            }

                            @Override
                            public void onFinish() {
                                boolean startTimer = !(!model.exercisesIterator.hasNext() && !model.cycleIterator.hasNext() && (model.currentExerciseReps+1)==model.currentExercise.getRepetitions() && (model.currentCycleReps+1)==model.currentCycle.getRepetitions());
                                nextExercise();
                                if (startTimer) {
                                    if(model.timer!=null)
                                        model.timer.cancel();
                                    model.timerRunning = false;
                                    binding.fabPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24));
                                    binding.fabPlay.callOnClick();
                                }else {
                                    model.timerRunning = false;
                                    binding.fabPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24));
                                }
                                playSound();

                            }
                        };
                        model.timer.start();
                        model.timerRunning = true;
                        binding.fabPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_pause_24));
                    } else {
                        model.timer.cancel();
                        model.timerRunning = false;
                        binding.fabPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24));
                    }
                }
            });
            binding.fabNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    nextExercise();
                    if (model.timer != null)
                        model.timer.cancel();
                    model.timerRunning = false;
                    binding.fabPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24));


                }
            });
            binding.fabStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(model.timerRunning){
                        if(model.timer!=null)
                            model.timer.cancel();
                        model.timerRunning = false;
                    }
                    confirmStop();
                }
            });
            binding.fabPrev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    prevExercise();
                    resetTimer(model.currentExercise.getDuration()*1000);
                    if (model.timer != null)
                        model.timer.cancel();
                    model.timerRunning = false;
                    binding.fabPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24));
                }
            });
            if(model.timerRunning) {
                model.timer.cancel();
                model.timer = new CountDownTimer(model.timeLeftMiliseconds, 1000) {
                    @Override
                    public void onTick(long l) {
                        model.timeLeftMiliseconds = l;
                        updateTimer();
                    }

                    @Override
                    public void onFinish() {
                        boolean startTimer = !(!model.exercisesIterator.hasNext() && !model.cycleIterator.hasNext() && (model.currentExerciseReps+1)==model.currentExercise.getRepetitions() && (model.currentCycleReps+1)==model.currentCycle.getRepetitions());
                        nextExercise();
                        if (startTimer) {
                            if (model.timer != null)
                                model.timer.cancel();
                            model.timerRunning = false;
                            binding.fabPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24));
                            binding.fabPlay.callOnClick();
                        } else {
                            model.timerRunning = false;
                            binding.fabPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24));
                        }
                        playSound();

                    }
                };
                model.timer.start();
                binding.fabPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_pause_24));
            } else binding.fabPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24));
            // timer
//            if(!model.timerRunning)
                updateTimer();
        }
    }

    private void defaultHandlerCycle(Resource<PagedList<Cycle>> r) {
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

    private void confirmStop() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.quitRoutineQuestion)).setMessage(getResources().getString(R.string.progressLost))
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                }).setNegativeButton(R.string.no, null);
        dialog.show();
    }

    private void playSound() {
        MediaPlayer mp = MediaPlayer.create(this, R.raw.sound);
        mp.start();
    }

    private void prevExercise() {
        if(!(!model.exercisesIterator.hasNext() && model.currentExerciseReps>0))
        binding.exerciseCard3.setVisibility(View.VISIBLE);
        if(model.currentExerciseReps>0){
            Log.d(TAG, "prevExercise: Hay reps en el ejercicio");
            model.currentExerciseReps--;
            binding.exerciseTitleDetailLayout.setText(model.currentExercise.getExercise().getName() + " " + model.currentExerciseReps + "/" + model.currentExercise.getRepetitions());
            binding.exerciseReps2.setText(getResources().getString(R.string.barra, model.currentExerciseReps, model.currentExercise.getRepetitions()));
        }else{
            model.currentExercise = model.exercisesIterator.previous();
            int aux = --model.exercisesDone;
            if(model.exercisesIterator.hasPrevious()){
                Log.d(TAG, "prevExercise: No es el ultimo ej");
                model.currentExercise = model.exercisesIterator.previous();
                boolean auxBool = !model.exercisesIterator.hasPrevious();
                NextExerciseNoCycleJump();
                model.exercisesDone = aux;
                model.currentExerciseReps = model.currentExercise.getRepetitions()-1;
                binding.exerciseReps2.setText(getResources().getString(R.string.barra, model.currentExerciseReps, model.currentExercise.getRepetitions()));
                binding.exerciseTitleDetailLayout.setText(model.currentExercise.getExercise().getName() + " " + model.currentExerciseReps + "/" + model.currentExercise.getRepetitions());
                binding.exercisesLeft.setText(new StringBuilder().append(getResources().getString(R.string.ejercicios)).append(" ").append(getResources().getString(R.string.barra, model.exercisesDone, model.totalExercises)).toString());
                if(auxBool) {
                    binding.exerciseCard1.setVisibility(View.INVISIBLE);
                }else {
                    binding.exerciseName1.setText(model.Exercises.get(model.exercisesIterator.previousIndex()-1).getExercise().getName());
                    binding.exerciseReps1.setText(getResources().getString(R.string.barra, model.Exercises.get(model.exercisesIterator.previousIndex()-1).getRepetitions(), model.Exercises.get(model.exercisesIterator.previousIndex()).getRepetitions()));
                }
            }else{  // Es el primero del ciclo
                model.exercisesIterator.next();
                model.exercisesDone++;
                if(model.currentCycleReps>0){
                    model.exercisesDone=0;
                    binding.cycleTitle.setText(new StringBuilder().append(model.currentCycle.getName()).append(" ").append(getResources().getString(R.string.barra, --model.currentCycleReps, model.currentCycle.getRepetitions())).toString());
                    while(model.exercisesIterator.hasNext())
                        nextExercise();
                    while(model.currentExerciseReps!=model.currentExercise.getRepetitions()-1)
                        nextExercise();

                }else{
                    if (model.cycleIterator.hasPrevious()) {
                        model.currentCycle = model.cycleIterator.previous();
                        if (model.cycleIterator.hasPrevious()) {
                            Log.d(TAG, "prevExercise: Vuelvo un ciclo");
                            model.cyclesDone--;
                            model.cyclesDone--;
                            model.currentCycle = model.cycleIterator.previous();
                            model.currentCycle = model.cycleIterator.next();
                            model.cyclesDone++;
                            model.currentCycleReps = 0;
                            model.currentExerciseReps = 0;
                            model.exercisesDone=0;
                            binding.cycleTitle.setText(new StringBuilder().append(model.currentCycle.getName()).append(" ").append(getResources().getString(R.string.barra, model.currentCycleReps, model.currentCycle.getRepetitions())).toString());
                            RoutineRepository repository = ((App)getApplication()).getRoutineRepository();
                            repository.getExercises(0, 1, "order", model.currentCycle.getId()).observe(this, rAuxExercise -> {

                                if (rAuxExercise.getStatus() == Status.SUCCESS) {
                                    repository.getExercises(0, rAuxExercise.getData().getTotalCount(), "order", model.currentCycle.getId()).observe(this, rExercise -> {
                                        if (rExercise.getStatus() == Status.SUCCESS) {
                                            model.Exercises = rExercise.getData().getContent();
//                                    model.exercise_isLastPage = rExercise.getData().getIsLastPage();
                                            model.totalExercises = rExercise.getData().getTotalCount();
                                            model.exercisesIterator = model.Exercises.listIterator();
                                            model.currentExercise = model.exercisesIterator.next();
                                            // NO DETAIL VIEW
                                            binding.exercisesLeft.setText(new StringBuilder().append(getResources().getString(R.string.ejercicios)).append(getResources().getString(R.string.barra, model.exercisesDone, model.totalExercises)).toString());
                                            binding.exerciseCard1.setVisibility(View.INVISIBLE);
                                            binding.exerciseCard3.setVisibility(View.VISIBLE);
                                            if (model.totalExercises > 0) {
                                                int idxAux = model.exercisesIterator.nextIndex();
                                                binding.exerciseName2.setText(model.currentExercise.getExercise().getName());
                                                binding.exerciseReps2.setText(getResources().getString(R.string.barra, 0, model.currentExercise.getRepetitions()));
                                                TypedValue typedValue = new TypedValue();
                                                getTheme().resolveAttribute(R.attr.colorSecondary, typedValue, true);
                                                binding.exerciseCard2.setCardBackgroundColor(typedValue.data);
                                                if (model.totalExercises > 1) {
                                                    binding.exerciseName3.setText(model.Exercises.get(idxAux).getExercise().getName());
                                                    binding.exerciseReps3.setText(getResources().getString(R.string.barra, 0, model.Exercises.get(idxAux).getRepetitions()));
                                                } else binding.exerciseCard3.setVisibility(View.GONE);
                                            } else {
                                                // TODO NO TIENE EJERCICIOS
                                            }


                                            // DETAIL VIEW
                                            binding.exerciseTitleDetailLayout.setText(model.currentExercise.getExercise().getName() + " " + model.currentExerciseReps + "/" + model.currentExercise.getRepetitions());
                                            int minutes = model.currentExercise.getDuration() / 60;
                                            int seconds = (model.currentExercise.getDuration() % 60);
                                            binding.exerciseDurationDetailLayout.setText(minutes + ":" + seconds);
                                            binding.exerciseDetailDetailLayout.setText(model.currentExercise.getExercise().getDetail());
                                            binding.exerciseTypeDetailLayout.setText(model.currentExercise.getExercise().getType());

                                            model.timeLeftMiliseconds = model.currentExercise.getDuration() * 1000;
                                            updateTimer();
                                            for (int i = 0; i < model.currentCycle.getRepetitions(); i++) {
                                                while(model.exercisesIterator.hasNext())
                                                    nextExercise();
                                                while(model.currentExerciseReps!=model.currentExercise.getRepetitions()-1)
                                                    nextExercise();
                                                if(i!=(model.currentCycle.getRepetitions()-1)){
                                                    nextExercise();
                                                }
                                            }
                                        } else {
                                            // loading
                                            defaultHandler(rExercise);
                                        }
                                    });
                                } else {
                                    // Loading
                                    defaultHandler(rAuxExercise);
                                }
                            });
                        } else {
                            Log.d(TAG, "prevExercise: Estoy en el primer ciclo y no puedo retroceder mas");
                            model.cycleIterator.next();
                        }
                    }
                }
            }
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

    public void resetTimer(int miliseconds){
        model.timeLeftMiliseconds = miliseconds;
        updateTimer();
    }

    public void nextExercise(){
        if(++model.currentExerciseReps != model.currentExercise.getRepetitions()){     // Quedan ejercicios del mismo tipo
            Log.d(TAG, "nextExercise: Quedan ejercicios del mismo");
            binding.exerciseReps2.setText(getResources().getString(R.string.barra, model.currentExerciseReps, model.currentExercise.getRepetitions()));
            binding.exerciseTitleDetailLayout.setText(model.currentExercise.getExercise().getName() + " " + model.currentExerciseReps + "/" + model.currentExercise.getRepetitions());
            resetTimer(model.currentExercise.getDuration()*1000);
        }else{
            if(model.exercisesIterator.hasNext()){
                NextExerciseNoCycleJump();
            }else {
                if(++model.currentCycleReps==model.currentCycle.getRepetitions()){
                    if(model.cycleIterator.hasNext())
                    cycleJump(true);
                    else finishRoutine();
                }else {
                    binding.cycleTitle.setText(new StringBuilder().append(model.currentCycle.getName()).append(" ").append(getResources().getString(R.string.barra, model.currentCycleReps, model.currentCycle.getRepetitions())).toString());
                    ContentEx aux;
                    while(model.exercisesIterator.hasPrevious())
                        aux = model.exercisesIterator.previous();
                    model.currentExercise = model.exercisesIterator.next();
                    // NO DETAIL VIEW
                    model.exercisesDone = 0;
                    binding.exercisesLeft.setText(new StringBuilder().append(getResources().getString(R.string.ejercicios)).append(" ").append(getResources().getString(R.string.barra, model.exercisesDone, model.totalExercises)).toString());
                    binding.exerciseCard1.setVisibility(View.INVISIBLE);
                    binding.exerciseCard3.setVisibility(View.VISIBLE);
                    model.currentExerciseReps = 0;

                    if (model.totalExercises > 0) {
                        int idxAux = model.exercisesIterator.nextIndex();
                        binding.exerciseName2.setText(model.currentExercise.getExercise().getName());
                        binding.exerciseReps2.setText(getResources().getString(R.string.barra, 0, model.currentExercise.getRepetitions()));
                        TypedValue typedValue = new TypedValue();
                        getTheme().resolveAttribute(R.attr.colorSecondary, typedValue, true);
                        binding.exerciseCard2.setCardBackgroundColor(typedValue.data);
                        if (model.totalExercises > 1) {
                            binding.exerciseName3.setText(model.Exercises.get(idxAux).getExercise().getName());
                            binding.exerciseReps3.setText(getResources().getString(R.string.barra, 0, model.Exercises.get(idxAux).getRepetitions()));
                        } else binding.exerciseCard3.setVisibility(View.GONE);
                    } else {
                        // TODO NO TIENE EJERCICIOS
                    }


                    // DETAIL VIEW
                    binding.exerciseTitleDetailLayout.setText(model.currentExercise.getExercise().getName() + " " + model.currentExerciseReps + "/" + model.currentExercise.getRepetitions());
                    int minutes = model.currentExercise.getDuration() / 60;
                    int seconds = (model.currentExercise.getDuration() % 60);
                    binding.exerciseDurationDetailLayout.setText(minutes + ":" + seconds);
                    binding.exerciseDetailDetailLayout.setText(model.currentExercise.getExercise().getDetail());
                    binding.exerciseTypeDetailLayout.setText(model.currentExercise.getExercise().getType());

                    resetTimer(model.currentExercise.getDuration() * 1000);
                }
            }
        }
    }

    private void finishRoutine() {
        binding.cycleTitle.setText(new StringBuilder().append(model.currentCycle.getName()).append(" ").append(getResources().getString(R.string.barra, model.currentCycleReps, model.currentCycle.getRepetitions())).toString());
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

    private void defaultHandler(Resource<PagedList<ContentEx>> r) {
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

    private void cycleJump(boolean popUp) {

        if(popUp){

            Snackbar snack = Snackbar.make(binding.getRoot(), getResources().getString(R.string.finishCyclePopUp) + " " + model.currentCycle.getName() + ". " + getResources().getString(R.string.keepGoing) + "\n" +getResources().getString(R.string.cyclesLeft, model.cyclesDone + 1, model.totalCycles) , Snackbar.LENGTH_LONG).setBackgroundTint(getResources().getColor(R.color.teal_200)).setDuration(10 * 1000);
            snack.setTextColor(getResources().getColor(R.color.black)).show();
        }
        model.currentCycle = model.cycleIterator.next();
        model.cyclesDone++;
        model.currentCycleReps = 0;
        model.currentExerciseReps = 0;
        model.exercisesDone=0;
        binding.cycleTitle.setText(new StringBuilder().append(model.currentCycle.getName()).append(" ").append(getResources().getString(R.string.barra, model.currentCycleReps, model.currentCycle.getRepetitions())).toString());
        RoutineRepository repository = ((App)getApplication()).getRoutineRepository();
        repository.getExercises(0, 1, "order", model.currentCycle.getId()).observe(this, rAuxExercise -> {

            if (rAuxExercise.getStatus() == Status.SUCCESS) {
                if(rAuxExercise.getData().getTotalCount()==0){
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                            .setTitle(getResources().getString(R.string.noExercises, model.currentCycle.getName())).setMessage(getResources().getString(R.string.noExercisesMsg))
                            .setPositiveButton(R.string.goBackToDetail, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }

                            }).setCancelable(false);
                    dialog.show();
                }
                repository.getExercises(0, rAuxExercise.getData().getTotalCount(), "order", model.currentCycle.getId()).observe(this, rExercise -> {
                    if (rExercise.getStatus() == Status.SUCCESS) {
                        model.Exercises = rExercise.getData().getContent();
//                                    model.exercise_isLastPage = rExercise.getData().getIsLastPage();
                        model.totalExercises = rExercise.getData().getTotalCount();
                        model.exercisesIterator = model.Exercises.listIterator();
                        model.currentExercise = model.exercisesIterator.next();
                        // NO DETAIL VIEW
                        binding.exercisesLeft.setText(new StringBuilder().append(getResources().getString(R.string.ejercicios)).append(" ").append(getResources().getString(R.string.barra, model.exercisesDone, model.totalExercises)).toString());
                        binding.exerciseCard1.setVisibility(View.INVISIBLE);
                        binding.exerciseCard3.setVisibility(View.VISIBLE);
                        if (model.totalExercises > 0) {
                            int idxAux = model.exercisesIterator.nextIndex();
                            binding.exerciseName2.setText(model.currentExercise.getExercise().getName());
                            binding.exerciseReps2.setText(getResources().getString(R.string.barra, 0, model.currentExercise.getRepetitions()));
                            TypedValue typedValue = new TypedValue();
                            getTheme().resolveAttribute(R.attr.colorSecondary, typedValue, true);
                            binding.exerciseCard2.setCardBackgroundColor(typedValue.data);
                            if (model.totalExercises > 1) {
                                binding.exerciseName3.setText(model.Exercises.get(idxAux).getExercise().getName());
                                binding.exerciseReps3.setText(getResources().getString(R.string.barra, 0, model.Exercises.get(idxAux).getRepetitions()));
                            } else binding.exerciseCard3.setVisibility(View.GONE);
                        } else {
                            // TODO NO TIENE EJERCICIOS
                        }


                        // DETAIL VIEW
                        binding.exerciseTitleDetailLayout.setText(model.currentExercise.getExercise().getName() + " " + model.currentExerciseReps + "/" + model.currentExercise.getRepetitions());
                        int minutes = model.currentExercise.getDuration() / 60;
                        int seconds = (model.currentExercise.getDuration() % 60);
                        binding.exerciseDurationDetailLayout.setText(minutes + ":" + seconds);
                        binding.exerciseDetailDetailLayout.setText(model.currentExercise.getExercise().getDetail());
                        binding.exerciseTypeDetailLayout.setText(model.currentExercise.getExercise().getType());

                        model.timeLeftMiliseconds = model.currentExercise.getDuration() * 1000;
                        if(model.timer!=null)
                            model.timer.cancel();
                        if(model.timerRunning) {
                            model.timer = new CountDownTimer(model.timeLeftMiliseconds, 1000) {
                                @Override
                                public void onTick(long l) {
                                    model.timeLeftMiliseconds = l;
                                    updateTimer();
                                }

                                @Override
                                public void onFinish() {
                                    boolean startTimer = !(!model.exercisesIterator.hasNext() && !model.cycleIterator.hasNext() && (model.currentExerciseReps+1)==model.currentExercise.getRepetitions() && (model.currentCycleReps+1)==model.currentCycle.getRepetitions());
                                    nextExercise();
                                    if (startTimer) {
                                        if (model.timer != null)
                                            model.timer.cancel();
                                        model.timerRunning = false;
                                        binding.fabPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24));
                                        binding.fabPlay.callOnClick();
                                    } else {
                                        model.timerRunning = false;
                                        binding.fabPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24));
                                    }
                                    playSound();

                                }
                            };
                            model.timer.start();
                            binding.fabPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_pause_24));
                        } else binding.fabPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24));
                        updateTimer();
                    } else {
                        // loading
                        defaultHandler(rExercise);
                    }
                });
            } else {
                // Loading
                defaultHandler(rAuxExercise);
            }
        });
    }

    public void NextExerciseNoCycleJump(){
        model.exercisesDone++;
        binding.exerciseCard1.setVisibility(View.VISIBLE);
        binding.exercisesLeft.setText(new StringBuilder().append(getResources().getString(R.string.ejercicios)).append(" ").append(getResources().getString(R.string.barra, model.exercisesDone, model.totalExercises)).toString());
        binding.exerciseName1.setText(model.currentExercise.getExercise().getName());
        binding.exerciseReps1.setText(getResources().getString(R.string.barra, model.currentExerciseReps, model.currentExercise.getRepetitions()));
        binding.exerciseCard1.setCardBackgroundColor(getResources().getColor(R.color.purple_500));
        model.currentExercise = model.exercisesIterator.next();
        model.currentExerciseReps = 0;
        binding.exerciseName2.setText(model.currentExercise.getExercise().getName());
        binding.exerciseReps2.setText(getResources().getString(R.string.barra, model.currentExerciseReps, model.currentExercise.getRepetitions()));
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorSecondary, typedValue, true);
        binding.exerciseCard2.setCardBackgroundColor(typedValue.data);
        if(model.exercisesIterator.hasNext()){
            int idxAux = model.exercisesIterator.nextIndex();
            binding.exerciseName3.setText(model.Exercises.get(idxAux).getExercise().getName());
            binding.exerciseReps3.setText(getResources().getString(R.string.barra, 0, model.Exercises.get(idxAux).getRepetitions()));
        }else binding.exerciseCard3.setVisibility(View.INVISIBLE);

        binding.exerciseTitleDetailLayout.setText(model.currentExercise.getExercise().getName() + " " + model.currentExerciseReps + "/" + model.currentExercise.getRepetitions());
        int minutes = model.currentExercise.getDuration() / 60;
        int seconds = (model.currentExercise.getDuration() % 60);
        binding.exerciseDurationDetailLayout.setText(minutes + ":" + seconds);
        binding.exerciseDetailDetailLayout.setText(model.currentExercise.getExercise().getDetail());
        binding.exerciseTypeDetailLayout.setText(model.currentExercise.getExercise().getType());
        resetTimer(model.currentExercise.getDuration()*1000);
    }


    @Override
    public void onBackPressed() {
        binding.fabStop.callOnClick();
        if(!binding.fabStop.hasOnClickListeners())
        finish();
//        super.onBackPressed();
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
        public ListIterator<ContentEx> exercisesIterator;
        public ContentEx currentExercise;
        public List<ContentEx> Exercises;

        public List<Cycle> cycles;
        public ListIterator<Cycle> cycleIterator;
        public Cycle currentCycle;
        public int totalCycles;
        public int cyclesDone = 0;
        public int totalExercises;
        public int exercisesDone = 0;
        public int routineID;
        public int currentExerciseReps = 0;
        public int currentCycleReps = 0;

        public MyViewModel() {
        }
    }
}