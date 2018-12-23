package com.example.administrator.sar;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.regex.Pattern;

public class Login extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 10;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    TextInputLayout login_textInputLayout1;
    TextInputLayout login_textInputLayout2;
    AppCompatEditText Email;
    AppCompatEditText Password;
    String TAG = "LoginActivity- ";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        Log.d(TAG, "onCreate");

        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Button LoginBtn = (Button) findViewById(R.id.Login);
        Button SignUpBtn = (Button) findViewById(R.id.SignUp);
        SignInButton LoginGoogleBtn = (SignInButton) findViewById(R.id.Login_google);

        login_textInputLayout1 = (TextInputLayout) findViewById(R.id.login_textInputLayout1);
        login_textInputLayout2 = (TextInputLayout) findViewById(R.id.login_textInputLayout2);
        Email = (AppCompatEditText) findViewById(R.id.Email);
        Password = (AppCompatEditText) findViewById(R.id.Password);

        Email.addTextChangedListener(EmailTextWatcher);
        Password.addTextChangedListener(PasswordTextWatcher);

        login_textInputLayout1.setCounterEnabled(true);
        login_textInputLayout2.setCounterEnabled(true);


        login_textInputLayout1.setErrorEnabled(true);
        login_textInputLayout2.setErrorEnabled(true);

        login_textInputLayout1.setCounterMaxLength(50);
        login_textInputLayout2.setCounterMaxLength(20);

        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailLogin(Email.getText().toString(), Password.getText().toString());
            }
        });

        SignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        });

        LoginGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                Log.d(TAG, "mAuthStateListener: " + user);
                if (user != null) {
                    IntentMainActivity();
                } else {

                }

            }
        };

//        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult");
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
                //IntentMainActivity();
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately;
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        Log.d(TAG, "firebaseAuthWithGoogle");
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(Login.this, "구글아이디가 생성되었습니다.", Toast.LENGTH_LONG).show();
                        } else {
                            Log.d(TAG, "firebaseAuthWithGoogle: 실패?");
                            // If sign in fails, display a message to the user.

                        }
                        // ...
                    }
                });
    }

    private void emailLogin(String email, String password) {
        Log.d(TAG, "emailLogin");
        if(email.length() == 0){
            Toast.makeText(Login.this,"Email 입력하세요.",Toast.LENGTH_SHORT).show();
            Email.requestFocus();
            return;
        }else if(!(checkEmail(email))){
            Toast.makeText(Login.this,"Email형식에 맞게 입력하세요.",Toast.LENGTH_SHORT).show();
            Email.setText("");
            Email.requestFocus();
            return;
        }
        if(password.length() == 0 ){
            Toast.makeText(Login.this,"password 입력하세요.",Toast.LENGTH_SHORT).show();
            Password.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            Toast.makeText(Login.this, "로그인 완료", Toast.LENGTH_SHORT).show();
                            //IntentMainActivity();
                        } else {
                            Log.d(TAG, "emailLogin 실패");
                            Toast.makeText(Login.this,"Email 또는 password를 확인하세요",Toast.LENGTH_SHORT).show();
                            // If sign in fails, display a message to the user.
                        }

                        // ...
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        mAuth.removeAuthStateListener(mAuthStateListener);
        //mAuth.addAuthStateListener(mAuthStateListener);
    }

    private void IntentMainActivity(){
        Intent intent = new Intent(Login.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    TextWatcher EmailTextWatcher = new TextWatcher()
    {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {

        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {

        }
        @Override
        public void afterTextChanged(Editable s)
        {
            if(s.length()>login_textInputLayout1.getCounterMaxLength()) {
                login_textInputLayout1.setError("error");
            }
            else
            {
                login_textInputLayout1.setError(null);
            }
        }
    };

    TextWatcher PasswordTextWatcher = new TextWatcher()
    {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {

        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {

        }
        @Override
        public void afterTextChanged(Editable s)
        {
            if(s.length()<6 || s.length() > login_textInputLayout2.getCounterMaxLength())
            {
                login_textInputLayout2.setError("error");
            }
            else
            {
                login_textInputLayout2.setError(null);
            }
        }
    };

    public boolean checkEmail(String email){
        String emailRegex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
        return Pattern.matches(emailRegex, email);
    }
}
