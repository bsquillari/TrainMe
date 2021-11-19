package com.trainme.ui.login;

import android.app.Activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.trainme.App;
import com.trainme.DetailRoutineActivity;
import com.trainme.MainActivity;
import com.trainme.R;
import com.trainme.api.model.Credentials;
import com.trainme.api.model.Error;
import com.trainme.databinding.ActivityLoginBinding;
import com.trainme.repository.Resource;
import com.trainme.repository.Status;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "UI";
    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final ProgressBar loadingProgressBar = binding.loading;

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                Credentials credentials = new Credentials(binding.username.getText().toString(), binding.password.getText().toString());
                App app = ((App) getApplication());
                app.getUserRepository().login(credentials).observe(this, r -> {
                    Log.d("Login", r.getStatus().toString());
                    if (r.getStatus() == Status.SUCCESS) {
                        Log.d(TAG, getString(R.string.success));
                        binding.loading.setVisibility(View.GONE);
                        app.getPreferences().setAuthToken(r.getData().getToken());
                        updateUiWithUser(new LoggedInUserView(binding.username.getText().toString()));
                    }else {
                        defaultResourceHandler(r);
                    }
                });
            }
            return false;
        });


        binding.login.setOnClickListener(v -> {
            Credentials credentials = new Credentials(binding.username.getText().toString(), binding.password.getText().toString());
            App app = ((App) getApplication());
            app.getUserRepository().login(credentials).observe(this, r -> {
                Log.d("Login", r.getStatus().toString());
                if (r.getStatus() == Status.SUCCESS) {
                    Log.d(TAG, getString(R.string.success));
                    binding.loading.setVisibility(View.GONE);
                    app.getPreferences().setAuthToken(r.getData().getToken());
                    updateUiWithUser(new LoggedInUserView(binding.username.getText().toString()));
                } else {
                    defaultResourceHandler(r);
                }
            });
        });
    }

    private void defaultResourceHandler(Resource<?> resource) {
        switch (resource.getStatus()) {
            case LOADING:
                Log.d(TAG, getString(R.string.loading));
                binding.loading.setVisibility(View.VISIBLE);
                break;
            case ERROR:
                binding.loading.setVisibility(View.GONE);
                Error error = resource.getError();
                String message = getString(R.string.error) + ": " + error.getDescription();
                Snackbar snack = Snackbar.make(binding.container, message, Snackbar.LENGTH_LONG).setBackgroundTint(getResources().getColor(R.color.error)).setDuration(20 * 1000);
                View view = snack.getView();
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
                params.gravity = Gravity.TOP;
                view.setLayoutParams(params);
                snack.setAction("DISMISS", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
                snack.show();
                Log.d(TAG, message);
                break;
        }
    }

    private void updateUiWithUser(LoggedInUserView model) {
//        String welcome = getString(R.string.welcome) + model.getDisplayName();
        String welcome = "Welcome!";
        // TODO : initiate successful logged in experience

        Intent intent;

        if(getIntent().getAction().equals(Intent.ACTION_VIEW)) {
            intent = new Intent(this, DetailRoutineActivity.class);

            intent.putExtra("ID", Integer.parseInt(getIntent().getData().getQueryParameter("id")));
            intent.putExtra("Name", getIntent().getData().getQueryParameter("name"));
            intent.putExtra("Detail", getIntent().getData().getQueryParameter("detail"));
            intent.putExtra("Difficulty", getIntent().getData().getQueryParameter("difficulty"));
            intent.putExtra("Score", Integer.parseInt(getIntent().getData().getQueryParameter("score")));
            intent.putExtra("ColorPill", Integer.parseInt(getIntent().getData().getQueryParameter("color_pill")));
        } else {
            intent = new Intent(this, MainActivity.class);
        }

        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
        startActivity(intent);
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}