package com.codepath.simpletodo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.codepath.simpletodo.R;
import com.codepath.simpletodo.model.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rushabh on 3/22/15.
 */
public class TodoActivity extends Activity {

    private ArrayList<Item> todoItems;
    private ArrayAdapter<Item> todoAdapter;
    private ListView lvItems ;
    private EditText newItem ;
    private long maxRemoteId;

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
                editIntent.putExtra("text", todoItems.get(position).data);
                editIntent.putExtra("position",position);
                editIntent.putExtra("remote_id",todoItems.get(position).remoteId);
                startActivityForResult(editIntent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String editedString = data.getStringExtra("editedText");
        int position = data.getIntExtra("position", 0);
        Item updatedItem = new Item();
        updatedItem.data = editedString;
        updatedItem.remoteId = data.getLongExtra("remote_id", 0);
        todoItems.set(position, updatedItem);
        updateItemIntoDB(editedString, updatedItem.remoteId);
        todoAdapter.notifyDataSetChanged();
    }

    private void populateArrayItems() {
        readDataFromDb();
    }

    public void onAddedItem(View view){
        String itemText =  newItem.getText().toString();

        Item newlyCreatedItem = new Item();
        newlyCreatedItem.data = itemText;
        newlyCreatedItem.remoteId = ++maxRemoteId;
        todoAdapter.add(newlyCreatedItem);
        insertDataIntoDB(newlyCreatedItem);
        newItem.getText().clear();
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

    private void insertDataIntoDB(Item newItem){
        newItem.save();
    }

    private void updateItemIntoDB(String data,long position){
        List<Item> items = new Select().from(Item.class).where("remote_id=?",position).execute();
        if(items!=null && items.size()>0){
            Item item = items.get(0);
            item.data =data;
            item.save();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }
}
