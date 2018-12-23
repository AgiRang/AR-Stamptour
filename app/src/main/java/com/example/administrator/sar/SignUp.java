package com.example.administrator.sar;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {
    private FirebaseAuth mAuth;


    //private EditText SignUp_Email;
    //private EditText SignUp_Password;
    TextInputLayout textInputLayout1;
    TextInputLayout textInputLayout2;
    TextInputLayout textInputLayout3;
    TextInputLayout textInputLayout4;
    AppCompatEditText SignUp_Email;
    AppCompatEditText SignUp_Password;
    AppCompatEditText SignUp_Password_Confirm;
    AppCompatEditText SignUp_Name;

    //FirebaseDatabase database;
    //DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_layout);
        mAuth = FirebaseAuth.getInstance();

        //SignUp_Email = (EditText) findViewById(R.id.SignUp_Email);
        //SignUp_Password = (EditText) findViewById(R.id.SignUp_Password);
        Button oKBtn = (Button) findViewById(R.id.SignUp_Ok);
        Button cancelBtn = (Button) findViewById(R.id.SignUp_Cancel);

        textInputLayout1 = (TextInputLayout) findViewById(R.id.textInputLayout1);
        textInputLayout2 = (TextInputLayout) findViewById(R.id.textInputLayout2);
        textInputLayout3 = (TextInputLayout) findViewById(R.id.textInputLayout3);
        textInputLayout4 = (TextInputLayout) findViewById(R.id.textInputLayout4);

        SignUp_Email = (AppCompatEditText) findViewById(R.id.SignUp_Email);
        SignUp_Password = (AppCompatEditText) findViewById(R.id.SignUp_Password);
        SignUp_Password_Confirm = (AppCompatEditText) findViewById(R.id.SignUp_Password_Confirm);
        SignUp_Name = (AppCompatEditText) findViewById(R.id.SignUp_Name);


        SignUp_Email.addTextChangedListener(idTextWatcher);
        SignUp_Password.addTextChangedListener(pwTextWatcher);
        SignUp_Password_Confirm.addTextChangedListener(pwconTextWatcher);
        SignUp_Name.addTextChangedListener(nameTextWatcher);

        textInputLayout1.setCounterEnabled(true);
        textInputLayout2.setCounterEnabled(true);
        textInputLayout3.setCounterEnabled(true);
        textInputLayout4.setCounterEnabled(true);


        textInputLayout1.setErrorEnabled(true);
        textInputLayout2.setErrorEnabled(true);
        textInputLayout3.setErrorEnabled(true);
        textInputLayout4.setErrorEnabled(true);

        textInputLayout1.setCounterMaxLength(50);
        textInputLayout2.setCounterMaxLength(20);
        textInputLayout3.setCounterMaxLength(20);
        textInputLayout4.setCounterMaxLength(20);
/*
        database = FirebaseDatabase.getInstance();
        Log.d("SignUP",""+database);
        databaseReference = database.getReference();
        Log.d("SignUP",""+databaseReference);
*/
        oKBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser(SignUp_Email.getText().toString(), SignUp_Password.getText().toString(), SignUp_Password_Confirm.getText().toString(), SignUp_Name.getText().toString());

            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private void createUser(final String email, final String password, String passwordconfirm, final String name) {
        if(email.length() == 0){
            Toast.makeText(SignUp.this,"Email 입력하세요.",Toast.LENGTH_SHORT).show();
            SignUp_Email.requestFocus();
            return;
        }else if(!(checkEmail(email))){
            Toast.makeText(SignUp.this,"Email형식에 맞게 입력하세요.",Toast.LENGTH_SHORT).show();
            SignUp_Email.setText("");
            SignUp_Email.requestFocus();
            return;
        }
        if(password.length() == 0 ){
            Toast.makeText(SignUp.this,"password 입력하세요.",Toast.LENGTH_SHORT).show();
            SignUp_Password.requestFocus();
            return;
        }else if(password.length() < 6 || password.length() >20){
            Toast.makeText(SignUp.this,"password 6자리이상 20자리 이하로 입력하세요.",Toast.LENGTH_SHORT).show();
            SignUp_Password.setText("");
            SignUp_Password.requestFocus();
            return;
        }
        if(passwordconfirm.length() == 0){
            Toast.makeText(SignUp.this,"passwordconfirm 입력하세요.",Toast.LENGTH_SHORT).show();
            SignUp_Password_Confirm.requestFocus();
            return;
        }if(!(password.equals(passwordconfirm))){
            Toast.makeText(SignUp.this,"비밀번호가 일치하지 않습니다.",Toast.LENGTH_SHORT).show();
            SignUp_Password.setText("");
            SignUp_Password_Confirm.setText("");
            SignUp_Password.requestFocus();
            return;
        }if(name.length() == 0){
            Toast.makeText(SignUp.this,"name 입력하세요.",Toast.LENGTH_SHORT).show();
            SignUp_Name.requestFocus();
            return;
        }else if( name.length() >20){
            Toast.makeText(SignUp.this,"name 20자리 이하로 입력하세요.",Toast.LENGTH_SHORT).show();
            SignUp_Name.setText("");
            SignUp_Name.requestFocus();
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(SignUp.this, "회원가입 성공", Toast.LENGTH_LONG).show();
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignUp.this, "회원가입 실패", Toast.LENGTH_LONG).show();
                        }

                        // ...
                    }
                });
    }

    public boolean checkEmail(String email){
        String emailRegex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
        return Pattern.matches(emailRegex, email);
    }

    TextWatcher idTextWatcher = new TextWatcher()
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
            if(s.length()>textInputLayout1.getCounterMaxLength()) {
                textInputLayout1.setError("error");
            }
            else
            {
                textInputLayout1.setError(null);
            }
        }
    };

    TextWatcher pwTextWatcher = new TextWatcher()
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
            if(s.length()<6 || s.length() > textInputLayout2.getCounterMaxLength())
            {
                textInputLayout2.setError("error");
            }
            else
            {
                textInputLayout2.setError(null);
            }
        }
    };

    TextWatcher pwconTextWatcher = new TextWatcher()
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
            if(s.length()<6 || s.length() > textInputLayout3.getCounterMaxLength()) {
                textInputLayout3.setError("error");
            }
            else
            {
                textInputLayout3.setError(null);
            }
        }
    };
    TextWatcher nameTextWatcher = new TextWatcher()
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
            if(s.length()>textInputLayout4.getCounterMaxLength()) {
                textInputLayout4.setError("error");
            }
            else
            {
                textInputLayout4.setError(null);
            }
        }
    };
}
