package com.example.webshopgyakorlas;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String LOG_TAG = RegisterActivity.class.getName();
    private static final String PREF_KEY = RegisterActivity.class.getPackage().toString();
    private static final int SECRET_KEY = 2846;

    EditText userNameEditText;
    EditText userEmailEditText;
    EditText userPasswordEditText;
    EditText userPassword2EditText;
    EditText userPhoneEditText;
    Spinner spinner;
    EditText userAddressEditText;
    RadioGroup accountTypeGroup;

    private SharedPreferences preferences;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        int secret_key = getIntent().getIntExtra("SECRET_KEY", 0);

        if (secret_key != 2846){
            finish();
        }

        userNameEditText = findViewById(R.id.userNameEditText);
        userEmailEditText = findViewById(R.id.userEmailEditText);
        userPasswordEditText = findViewById(R.id.userPasswordEditText);
        userPassword2EditText = findViewById(R.id.userPassword2EditText);
        userPhoneEditText = findViewById(R.id.userPhoneEditText);
        spinner = findViewById(R.id.phoneSpinner);
        accountTypeGroup = findViewById(R.id.accountTypeGroup);
        accountTypeGroup.check(R.id.buyerRadioButton);

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        String userName = preferences.getString("userName", "");
        String password = preferences.getString("password", "");

        spinner.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.phone_modes,
                android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        userNameEditText.setText(userName);
        userPasswordEditText.setText(password);

        mAuth = FirebaseAuth.getInstance();

        Log.i(LOG_TAG, "onCreate");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void register(View view) {
        String userName = userNameEditText.getText().toString();
        String email = userEmailEditText.getText().toString();
        String password = userPasswordEditText.getText().toString();
        String password2 = userPassword2EditText.getText().toString();

        if (!password.equals(password2)) {
            Log.e(LOG_TAG, "A két jelszó nem egyezik meg");
            return;
        }

        String phoneNumber = userPhoneEditText.getText().toString();
        String phoneType = spinner.getSelectedItem().toString();
        String address = userAddressEditText.getText().toString();

        int chekckedID = accountTypeGroup.getCheckedRadioButtonId();
        RadioButton radioButton = accountTypeGroup.findViewById(chekckedID);
        String accountType = radioButton.getText().toString();

        Log.i(LOG_TAG, "Regisztrált: "+ userName + ", email: " + email);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Log.d(LOG_TAG, "User registered succesfully");
                    startShopping();
                }else {
                    Log.d(LOG_TAG, "User wasn't created succesfully");
                    Toast.makeText(RegisterActivity
                            .this,"User wasn't created succesfully: " + task
                            .getException()
                            .getMessage(),
                            Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    public void cancel(View view) {
        finish();
    }

    private void  startShopping(/* registered user data*/){
        Intent intent = new Intent(this, ShopListActivity.class);

        intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String selectedItem = parent.getItemAtPosition(position).toString();
        Log.i(LOG_TAG, selectedItem);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}