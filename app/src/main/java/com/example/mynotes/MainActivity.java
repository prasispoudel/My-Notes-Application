package com.example.mynotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {

    static ArrayList<String> notes = new ArrayList<String>();
    static ArrayAdapter arrayAdapter;
    DBHelper mydb;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.addmenu,menu);
        menuInflater.inflate(R.menu.clearmenu,menu);
        menuInflater.inflate(R.menu.help,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.add_menu){
            Intent intent = new Intent(getApplicationContext(),Note_Editor.class);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.clear_menu){
            SQLiteDatabase database= mydb.getWritableDatabase();
            mydb.onUpgrade(database, 1 ,2);
            notes.clear();
            SharedPreferences settings = getApplicationContext().getSharedPreferences("com.example.mynotes", Context.MODE_PRIVATE);
            settings.edit().clear().commit();
            arrayAdapter.notifyDataSetChanged();
            new AlertDialog.Builder(MainActivity.this)
                    .setMessage("All data has Been Removed")
                     .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialogInterface, int i) {
                             Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                             startActivity(intent);
                         }
                     })
                    .show();
        }
        if(item.getItemId() == R.id.help_menu){
         Intent intent = new Intent(getApplicationContext(),Help.class);
         startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ListView listView = (ListView) findViewById(R.id.ListView);
        mydb = new DBHelper(this);
        SQLiteDatabase database= mydb.getWritableDatabase();

        mydb.onCreate(database);


        int rows = mydb.numberOfRows();
        Log.i("Rows", String.valueOf(rows));

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.mynotes", Context.MODE_PRIVATE);
        HashSet<String> set= (HashSet) sharedPreferences.getStringSet("Notes",null);
        if (set == null) {
            if(notes.size() == 0) {
                notes.add("Sample Note");
            }
        }else{
            notes = new ArrayList(set);
        }
        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,notes);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(),Note_Editor.class);
                intent.putExtra("note_id",i);
                startActivity(intent);

            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int Itemfordeletion= i;
                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Confirm Deletion")
                        .setMessage("Do you want to delete this note?")
                        .setPositiveButton("Yes,Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        notes.remove(Itemfordeletion);
                                        arrayAdapter.notifyDataSetChanged();
                                        int response = mydb.deleteNote(Itemfordeletion);
                                        Log.i("Response", "Value: "+String.valueOf(response));
                                        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.mynotes", Context.MODE_PRIVATE);
                                        HashSet<String> set = new HashSet<>(MainActivity.notes);
                                        sharedPreferences.edit().putStringSet("Notes",set).apply();

                                    }
                                }
                        )
                        .setNegativeButton("No",null)
                        .show();
                return true;
            }
        });
    }
}