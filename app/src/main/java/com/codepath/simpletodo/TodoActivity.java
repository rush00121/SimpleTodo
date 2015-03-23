package com.codepath.simpletodo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by rushabh on 3/22/15.
 */
public class TodoActivity extends Activity {

    private ArrayList<String> todoItems;
    private ArrayAdapter<String> todoAdapter;
    private ListView lvItems ;
    private EditText newItem ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);
        lvItems = (ListView) findViewById(R.id.lvItems);
        newItem = (EditText) findViewById(R.id.etItem);
        todoItems = new ArrayList<>();
        populateArrayItems();
        todoAdapter = new ArrayAdapter<>(getBaseContext(),android.R.layout.simple_list_item_1 ,todoItems);
        lvItems.setAdapter(todoAdapter);
        setupAllListeners();
    }

    private void setupAllListeners() {
        lvItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        todoItems.remove(position);
                        writeData();
                        todoAdapter.notifyDataSetChanged();
                        return true;
                    }
                }
        );

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent editIntent = new Intent(TodoActivity.this, EditActivity.class);
                editIntent.putExtra("text", todoItems.get(position));
                editIntent.putExtra("position",position);
                startActivityForResult(editIntent,1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String editedString = data.getStringExtra("editedText");
        int position = data.getIntExtra("position",0);
        todoItems.set(position,editedString);
        writeData();
        todoAdapter.notifyDataSetChanged();
    }

    private void populateArrayItems() {
        readData();
    }

    public void onAddedItem(View view){
        String itemText =  newItem.getText().toString();
        todoAdapter.add(itemText);
        writeData();
        newItem.getText().clear();
    }

    private void readData(){
        File currentDir = getFilesDir();
        File todoFile = new File(currentDir,"todo.txt");
        try{
            todoItems = new ArrayList<>(FileUtils.readLines(todoFile));
        }catch (IOException e){
            todoItems = new ArrayList<>();
        }
    }

    private void writeData(){
        File currentDir = getFilesDir();
        File todoFile = new File(currentDir,"todo.txt");
        try {
            FileUtils.writeLines(todoFile, todoItems);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }
}
