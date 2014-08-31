package com.samiamharris.activitypractice.offlinetodos;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Created by SamMyxer on 8/29/14.
 */
public class NewTodoActivity extends Activity {

    private TextView textView;
    private EditText todoText;
    private Button saveButton;
    private Button deleteButton;
    private String todoId = null;
    private Todo todo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newtodo);


        //Fetch the todo data from le intent
        if(getIntent().hasExtra("ID")) {
            todoId = getIntent().getExtras().getString("ID");
        }


        if (todoId == null) {
            ParseUser debugging = ParseUser.getCurrentUser();

            todo = new Todo();
            todo.setUuidString();
        } else {
            ParseQuery<Todo> query = Todo.getQuery();
            query.fromLocalDatastore();
            query.whereEqualTo("uuid", todoId);
            query.getFirstInBackground(new GetCallback<Todo>() {

                @Override
                public void done(Todo object, ParseException e) {
                    if (!isFinishing()) {
                        todo = object;
                        todoText.setText(todo.getTitle());
                        deleteButton.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        todoText = (EditText) findViewById(R.id.todo_text);
        saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                todo.setTitle(todoText.getText().toString());
                todo.setDraft(true);
                todo.setAuthor(ParseUser.getCurrentUser());
                todo.pinInBackground(OfflineTodoApplication.TODO_GROUP_NAME,
                        new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(isFinishing()) {
                                    return;
                                }
                                if(e == null) {
                                    setResult(Activity.RESULT_OK);
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(),
                                            "Error saving: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show();                                }
                            }
                        });

            }
        });

        deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                todo.deleteEventually();
                setResult(Activity.RESULT_OK);
                finish();
            }
        });
    }


}
