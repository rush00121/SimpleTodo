package com.codepath.simpletodo.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.codepath.simpletodo.R;
import com.codepath.simpletodo.adapter.CustomItemAdapter;
import com.codepath.simpletodo.model.Item;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by rushabh on 3/22/15.
 */
public class TodoActivity extends Activity implements View.OnFocusChangeListener {

    private ArrayList<Item> todoItems;
    private CustomItemAdapter todoAdapter;
    private ListView lvItems ;
    private EditText newItem ;
    private long maxRemoteId;
    private EditText dateText;
    private SimpleDateFormat dateFormat;
    private DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);
        lvItems = (ListView) findViewById(R.id.lvItems);
        newItem = (EditText) findViewById(R.id.etItem);
        dateText = (EditText) findViewById(R.id.dateText);
        dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        todoItems = new ArrayList<>();
        populateArrayItems();
        todoAdapter = new CustomItemAdapter(getBaseContext(),android.R.layout.simple_list_item_1 ,todoItems);
        lvItems.setAdapter(todoAdapter);
        setupAllListeners();
    }

    private void setupAllListeners() {
        lvItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        Item item = todoItems.get(position);
                        todoItems.remove(position);
                        new Delete().from(Item.class).where("remote_id = ?", item.remoteId).execute();
                       // writeData();
                        todoAdapter.notifyDataSetChanged();
                        return true;
                    }
                }
        );

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent editIntent = new Intent(TodoActivity.this, EditActivity.class);
                editIntent.putExtra("text", todoItems.get(position).task);
                editIntent.putExtra("position",position);
                editIntent.putExtra("remote_id",todoItems.get(position).remoteId);
                editIntent.putExtra("dueDate",todoItems.get(position).date);
                startActivityForResult(editIntent, 1);
            }
        });


        dateText.setOnFocusChangeListener(this);
        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this,new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(Calendar.YEAR,year);
                newDate.set(Calendar.MONTH,monthOfYear);
                newDate.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                dateText.setText(dateFormat.format(newDate.getTime()));
            }
        },newCalendar.get(Calendar.YEAR),newCalendar.get(Calendar.MONTH),newCalendar.get(Calendar.DAY_OF_MONTH));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data!=null) {
            String editedString = data.getStringExtra("editedText");
            int position = data.getIntExtra("position", 0);
            Item updatedItem = new Item();
            updatedItem.task = editedString;
            updatedItem.remoteId = data.getLongExtra("remote_id", 0);
            updatedItem.date = data.getStringExtra("dueDate");
            todoItems.set(position, updatedItem);
            saveDataToDB(updatedItem);
            todoAdapter.notifyDataSetChanged();
        }
    }

    private void populateArrayItems() {
        readDataFromDb();
    }

    public void onAddedItem(View view){
        String itemText =  newItem.getText().toString();

        Item newlyCreatedItem = new Item();
        newlyCreatedItem.task = itemText;
        newlyCreatedItem.date = dateText.getText().toString();
        newlyCreatedItem.remoteId = ++maxRemoteId;
        todoAdapter.add(newlyCreatedItem);
        saveDataToDB(newlyCreatedItem);
        newItem.getText().clear();
        dateText.getText().clear();
        newItem.requestFocus();
    }

    private void readDataFromDb(){
        List<Item> items = new Select().from(Item.class).execute();
        for(Item item :items){
            todoItems.add(item);
            if(item.remoteId>maxRemoteId){
                maxRemoteId=item.remoteId;
            }
        }
    }

    private void saveDataToDB(Item item){
       item.save();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(v==dateText && hasFocus) {
            datePickerDialog.show();
        }
    }
}
