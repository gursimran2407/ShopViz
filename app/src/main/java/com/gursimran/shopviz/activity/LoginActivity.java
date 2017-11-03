package com.gursimran.shopviz.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.gursimran.shopviz.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener
{
    EditText etEmail;
    EditText etPassword;

    TextView txtvForgotPassword;
    TextView txtvRegisterUser;
    Button btnLogin;

    FirebaseAuth firebaseAuth;
    ProgressDialog pd;
    Toolbar toolbar;
    private String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("ShopViz");
        //getSupportActionBar().setIcon(R.mipmap.onlineshopviz);

        firebaseAuth = FirebaseAuth.getInstance();


        etEmail=(EditText) findViewById(R.id.editTextEmail);
        etPassword=(EditText) findViewById(R.id.editTextPassword);

        txtvForgotPassword=(TextView) findViewById(R.id.textViewForgotPassword);
        txtvRegisterUser=(TextView) findViewById(R.id.textViewRegisterUser);
        txtvRegisterUser.setPaintFlags(txtvRegisterUser.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        btnLogin=(Button) findViewById(R.id.email_sign_in_button);

        btnLogin.getBackground().setAlpha(64);

        pd = new ProgressDialog(this);

        btnLogin.setOnClickListener(this);
        txtvRegisterUser.setOnClickListener(this);


    }

    @Override
    public void onClick(View view)
    {
        int id=view.getId();

        switch (id)
        {
            case R.id.email_sign_in_button : loginUser();
                break;

            case R.id.textViewRegisterUser:
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        }

    }

    void loginUser()
    {
        if(etEmail.getText().toString().trim().equals(""))
        {
            etEmail.setError("Please Enter Email");
        }

        else if(etPassword.getText().toString().trim().equals(""))
        {
            etPassword.setError("Please Enter Password");
        }

        //|| (etEmail.getText().toString().trim().matches("[0-9]+") && etEmail.getText().toString().trim().length()==10)
        else if(!etEmail.getText().toString().trim().contains("@") || !etEmail.getText().toString().trim().contains("."))
        {
            etEmail.setError("Invalid Email");
        }

        else
        {
            pd.setMessage("Signing you in.");
            pd.show();

            email = etEmail.getText().toString().trim();
            password = etPassword.getText().toString().trim();

            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete( Task<AuthResult> task)
                {
                    if(task.isSuccessful()) {
                        pd.dismiss();
                        Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    }
                    else
                    {
                        pd.dismiss();
                        Toast.makeText(LoginActivity.this, "Invalid Details", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }


    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

    }

}
