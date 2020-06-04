package com.example.erchihin522;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {
    private EditText loginEdTxt;
    private EditText passwordEdTxt;
    private Button loginBtn;
    private Button registrationBtn;
    private CheckBox chBoxExternalStorage;
    private SharedPreferences chBoxChoice;
    private static final String PREF_STORAGE_MODE = "PREF_STORAGE_MODE";
    private static final String PREF_EXTERNAL_STORAGE = "PREF_EXTERNAL_STORAGE";
    private static final String FILE_LOGIN = "login.txt";
    private static final String FILE_PASSWORD = "password.txt";
    private static final String FILE_SAVE = "save.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialView();
        chBoxChoice = getSharedPreferences(PREF_STORAGE_MODE, MODE_PRIVATE);


        chBoxExternalStorage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = chBoxChoice.edit();
                editor.putBoolean(PREF_EXTERNAL_STORAGE, chBoxExternalStorage.isChecked());
                editor.apply();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chBoxChoice.getBoolean(PREF_EXTERNAL_STORAGE, false)) {
                    readExternalStorage();
                } else {
                    readInnerStorage();
                }
            }
        });

        registrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chBoxChoice.getBoolean(PREF_EXTERNAL_STORAGE, false)) {
                    writeExternalStorage();
                } else {
                    writeInnerStorage();
                }
            }


        });
    }

    public void writeExternalStorage() {
        if (isInputFieldEmpty()) {
            Toast.makeText(MainActivity.this, R.string.error_input_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isExternalStorageWritable()) {
            Toast.makeText(this, "No access", Toast.LENGTH_SHORT).show();
            return;
        }

        File saveData = new File(getApplicationContext()
                .getExternalFilesDir(null), FILE_SAVE);

        String enterLogin = loginEdTxt.getText().toString();
        String enterPassword = passwordEdTxt.getText().toString();

        try (FileWriter writer = new FileWriter(saveData, false)) {
            writer.append(enterLogin + "\n" + enterPassword);
            Toast.makeText(MainActivity.this, R.string.toast_registration_ok, Toast.LENGTH_SHORT).show();
            loginEdTxt.setText("");
            passwordEdTxt.setText("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readExternalStorage() {
        if (!isExternalStorageWritable()) {
            Toast.makeText(this, "No access", Toast.LENGTH_SHORT).show();
            return;
        }

        File saveData = new File(getApplicationContext()
                .getExternalFilesDir(null), "save.txt");

        try (FileReader reader = new FileReader(saveData);
             BufferedReader br = new BufferedReader(reader)) {
            if (isInputFieldEmpty()) {
                Toast.makeText(MainActivity.this, R.string.error_input_empty, Toast.LENGTH_SHORT).show();
            } else {
                String enterLogin = loginEdTxt.getText().toString();
                String enterPassword = passwordEdTxt.getText().toString();
                String userLogin = br.readLine();
                String userPassword = br.readLine();

                if (userLogin == null || userPassword == null) {
                    Toast.makeText(MainActivity.this, R.string.toast_not_registered, Toast.LENGTH_SHORT).show();
                } else {
                    if (userLogin.equals(enterLogin) && userPassword.equals(enterPassword)) {
                        Toast.makeText(MainActivity.this, R.string.toast_enter_ok, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, R.string.toast_enter_failed, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readInnerStorage() {
        File loginFile = new File(getFilesDir(), FILE_LOGIN);
        File passwordFile = new File(getFilesDir(), FILE_PASSWORD);

        if (!loginFile.exists() || !passwordFile.exists()) {
            Toast.makeText(MainActivity.this, R.string.toast_not_registered, Toast.LENGTH_SHORT).show();
            return;
        }

        try (FileInputStream loginInputStream = new FileInputStream(loginFile);
             FileInputStream passwordInputStream = new FileInputStream(passwordFile);

             InputStreamReader loginStreamReader = new InputStreamReader(loginInputStream);
             InputStreamReader passwordStreamReader = new InputStreamReader(passwordInputStream);

             BufferedReader loginReader = new BufferedReader(loginStreamReader);
             BufferedReader passwordReader = new BufferedReader(passwordStreamReader)) {

            if (isInputFieldEmpty()) {
                Toast.makeText(MainActivity.this, R.string.error_input_empty, Toast.LENGTH_SHORT).show();
            } else {
                String enterLogin = loginEdTxt.getText().toString();
                String enterPassword = passwordEdTxt.getText().toString();

                String userLogin = loginReader.readLine();
                String userPassword = passwordReader.readLine();
                if (userLogin == null || userPassword == null) {
                    Toast.makeText(MainActivity.this, R.string.toast_not_registered, Toast.LENGTH_SHORT).show();
                } else {
                    if (userLogin.equals(enterLogin) && userPassword.equals(enterPassword)) {
                        Toast.makeText(MainActivity.this, R.string.toast_enter_ok, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, R.string.toast_enter_failed, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeInnerStorage() {
        if (isInputFieldEmpty()) {
            Toast.makeText(MainActivity.this, R.string.error_input_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        try (FileOutputStream loginOutputStream = openFileOutput(FILE_LOGIN, MODE_PRIVATE);
             FileOutputStream passwordOutputStream = openFileOutput(FILE_PASSWORD, MODE_PRIVATE);

             OutputStreamWriter loginOutputStreamWriter = new OutputStreamWriter(loginOutputStream);
             OutputStreamWriter passwordOutputStreamWriter = new OutputStreamWriter(passwordOutputStream);

             BufferedWriter loginBw = new BufferedWriter(loginOutputStreamWriter);
             BufferedWriter passwordBw = new BufferedWriter(passwordOutputStreamWriter)) {

            String login = loginEdTxt.getText().toString();
            String password = passwordEdTxt.getText().toString();

            loginBw.write(login);
            passwordBw.write(password);

            Toast.makeText(MainActivity.this, R.string.toast_registration_ok, Toast.LENGTH_SHORT).show();
            loginEdTxt.setText("");
            passwordEdTxt.setText("");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean isInputFieldEmpty() {
        if (loginEdTxt.getText().toString().equals("") || passwordEdTxt.getText().toString().equals("")) {
            return true;
        }
        return false;
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public void initialView() {
        loginEdTxt = findViewById(R.id.edtxt_login);
        passwordEdTxt = findViewById(R.id.edtxt_password);
        loginBtn = findViewById(R.id.btn_login);
        registrationBtn = findViewById(R.id.btn_registration);
        chBoxExternalStorage = findViewById(R.id.chbox_external_storage);
    }
}