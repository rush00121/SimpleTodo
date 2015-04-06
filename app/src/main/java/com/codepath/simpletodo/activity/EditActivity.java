package com.codepath.simpletodo.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.codepath.simpletodo.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class EditActivity extends ActionBarActivity implements View.OnFocusChangeListener {

    private String todoItem ;
    private int position;
    private long remote_id;
    private String dueDate ;
    private EditText text;
    private EditText dateText;
    private SimpleDateFormat dateFormat;
    private DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        setContentView(R.layout.activity_edit);
        todoItem = getIntent().getStringExtra("text");
        position = getIntent().getIntExtra("position", 0);
        remote_id = getIntent().getLongExtra("remote_id", 0);
        dueDate = getIntent().getStringExtra("dueDate");
        text = (EditText) findViewById(R.id.editText);
        dateText = (EditText) findViewById(R.id.dateText);
        text.getText().append(todoItem);
        if(dueDate!=null) {
            dateText.getText().append(dueDate);
        }
        setDateTimeField();
    }

    private void setDateTimeField() {
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

    public void onEditItem(View view){
        onSubmit(view);

    }

    public void onSubmit(View view){
        Intent data = getIntent();
        data.putExtra("editedText",text.getText().toString());
        data.putExtra("position",position);
        data.putExtra("remote_id",remote_id);
        data.putExtra("dueDate",dateText.getText().toString());
        setResult(RESULT_OK, data);
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar task clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        datePickerDialog.show();
    }
}
