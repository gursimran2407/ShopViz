package com.gursimran.shopviz.activity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
//import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
//import android.widget.Spinner;
import android.widget.Toast;
import com.gursimran.shopviz.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.gursimran.shopviz.modal.User;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener
{
    EditText etName, etEmail, etMobileNo, etPassword;
    RadioButton rbMale, rbFemale;
    RadioGroup rgGender;
    //Spinner spCity;
    Button btnRegister;
    //ArrayAdapter<String> adapterCity;

    ProgressDialog pd;
    FirebaseAuth firebaseAuth;
    String email, password;
    String name, mobile, gender;
    User user;
    Toolbar toolbar;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("ShopViz");
        getSupportActionBar().setIcon(R.mipmap.onlineshopviz);

        /*ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.web_hi_res_512);

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
*/

        etName = findViewById(R.id.editTextName);
        etEmail = findViewById(R.id.editTextEmail);
        etMobileNo = findViewById(R.id.editTextMobileNo);
        etPassword = findViewById(R.id.editTextPassword);
        //etAddress = () findViewById(R.id.editTextAddress);

        rgGender = findViewById(R.id.RadioGroupGender);
        rbMale = findViewById(R.id.radioButtonMale);
        rbFemale = findViewById(R.id.radioButtonFemale);

//        spCity = (Spinner) findViewById(R.id.spinnerCity);
//        adapterCity=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item);
//        adapterCity.add("Select City");
//        adapterCity.add("Ambala");
//        adapterCity.add("Amritsar");
//        adapterCity.add("Bathinda");
//        adapterCity.add("Jalandhar");
//        adapterCity.add("Ludhiana");
//        adapterCity.add("Kapurthala");
//        adapterCity.add("Patiala");
//        adapterCity.add(" Phagwara");
//        spCity.setAdapter(adapterCity);

        pd = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();


        btnRegister = findViewById(R.id.buttonRegister);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        int id = view.getId();

        switch (id)
        {
            case R.id.buttonRegister:
                AuthUser();
                break;
        }

    }

    void AuthUser() {
        if (etName.getText().toString().trim().equals("")) {
            etName.setError("Please Enter Your Name");
        } else if (etEmail.getText().toString().trim().equals("")) {
            etEmail.setError("Please Enter Email");
        }

        else if(etMobileNo.getText().toString().equals(""))
        {
            etMobileNo.setError("Please Enter Mobile No.");
        }


        else if (etPassword.getText().toString().equals("")) {
            etPassword.setError("Please Enter Password");
        } else if (!etEmail.getText().toString().trim().contains("@") || !etEmail.getText().toString().trim().contains(".")) {
            etEmail.setError("Please Enter Valid Email");
        }

        else if(etMobileNo.getText().toString().trim().length()<6 || etMobileNo.getText().toString().trim().length()>13)
        {
            etMobileNo.setError("Please Enter Valid Mobile Number");
        }

        else if (etPassword.getText().toString().trim().length() < 6)
        {
            etPassword.setError("Password Must Have 6 or More Characters");
        }
        else
        {
            pd.setMessage("Signing You Up..");
            pd.show();

            email = etEmail.getText().toString().trim();
            password = etPassword.getText().toString().trim();

            name = etName.getText().toString().trim();
            mobile = etMobileNo.getText().toString().trim();

            int rgid = rgGender.getCheckedRadioButtonId();
            if (rgid == R.id.radioButtonMale) {
                gender = rbMale.getText().toString();
            } else {
                gender = rbFemale.getText().toString();
            }
            //  address = etAddress.getText().toString().trim();
            //city = spCity.getSelectedItem().toString();

            user = new User(name, email, password, mobile, gender);

            //Toast.makeText(this, "name "+name + "email "+ email+" mobile "+mobile+" pass "+password+" gender "+ gender+" add ", Toast.LENGTH_LONG).show();


            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    //FirebaseUser user = firebaseAuth.getCurrentUser();

                    if (task.isSuccessful())
                    {
                        registerUser(task.getResult().getUser());
                        Toast.makeText(RegisterActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration Failed. Please try again", Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }

    }


    void registerUser(FirebaseUser fbuser)
    {
        databaseReference.child("EndUsers").child(fbuser.getUid()).setValue(user);

    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

    }

}
