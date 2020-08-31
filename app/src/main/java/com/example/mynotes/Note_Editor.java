package com.example.mynotes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;

public class Note_Editor extends AppCompatActivity {
    int note_id;
    DBHelper mydb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note__editor);
        mydb = new DBHelper(this);

        final EditText editText = (EditText) findViewById(R.id.editTextTextMultiLine);
        Intent intent = getIntent();
        note_id = intent.getIntExtra("note_id", -1);
        if (note_id != -1) {
            editText.setText(MainActivity.notes.get(note_id));
        } else {
            MainActivity.notes.add("");
            note_id = MainActivity.notes.size() - 1;
            MainActivity.arrayAdapter.notifyDataSetChanged();
        }
        editText.addTextChangedListener(new TextWatcher() {
            @Override

            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                MainActivity.notes.set(note_id, String.valueOf(charSequence));
                MainActivity.arrayAdapter.notifyDataSetChanged();
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.mynotes", Context.MODE_PRIVATE);
                HashSet<String> set = new HashSet<>(MainActivity.notes);
                sharedPreferences.edit().putStringSet("Notes", set).apply();


            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(mydb.getData(note_id) == null){
                    mydb.insertNotes(MainActivity.notes.get(note_id));
                }else{
                    mydb.updateNote(note_id,MainActivity.notes.get(note_id));
                }
            }
        });
    }
}
