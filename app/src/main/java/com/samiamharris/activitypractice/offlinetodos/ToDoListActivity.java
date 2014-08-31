package com.samiamharris.activitypractice.offlinetodos;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;


public class ToDoListActivity extends ListActivity {

    private LayoutInflater layoutInflater;
    private ParseQueryAdapter<Todo> todoListAdapter;
    public final static int EDIT_ACTIVITY_CODE = 5490;
    public final static int LOGIN_ACTIVITY_CODE = 5889;

    private Button emptyButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);

        emptyButton = (Button) findViewById(R.id.empty_button);
        emptyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ToDoListActivity.this, NewTodoActivity.class);
                startActivity(i);
            }
        });

        //set up the parse query to use in the adapter
        ParseQueryAdapter.QueryFactory<Todo> factory = new ParseQueryAdapter.QueryFactory<Todo>() {
            @Override
            public ParseQuery<Todo> create() {
                ParseQuery<Todo> query = Todo.getQuery();
                query.orderByDescending("createdAt");
                query.fromLocalDatastore();
                return query;

            }
        };

        layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        todoListAdapter = new ToDoListAdapter(this, factory);

        setListAdapter(todoListAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())){
            syncTodosToParse();
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Todo todo = (Todo) getListAdapter().getItem(position);
        openEditView(todo);
    }

    private void openEditView(Todo todo) {
        Intent i = new Intent(this, NewTodoActivity.class);
        i.putExtra("ID", todo.getUuidString());
        startActivityForResult(i, EDIT_ACTIVITY_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            if(requestCode == EDIT_ACTIVITY_CODE) {
                todoListAdapter.loadObjects();
            } else if(requestCode == LOGIN_ACTIVITY_CODE) {
                if(ParseUser.getCurrentUser().isNew()) {
                    syncTodosToParse();
                } else {
                    loadFromParse();
                }
            }
        }
    }

    private void syncTodosToParse() {
        ConnectivityManager cm = ((ConnectivityManager)
                getSystemService((Context.CONNECTIVITY_SERVICE)));
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if((ni != null) && (ni.isConnected())) {
            if(!ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
                //if we have a network connection and a current logged in user
                //sync the todos
                ParseQuery<Todo> query = Todo.getQuery();
                query.fromPin(OfflineTodoApplication.TODO_GROUP_NAME);
                query.whereEqualTo("isDraft", true);
                query.findInBackground(new FindCallback<Todo>() {
                    @Override
                    public void done(List<Todo> todos, ParseException e) {
                        if (e == null) {
                            for (final Todo todo: todos) {
                                //Set is draft flag to false before
                                //syncing to Parse
                                todo.setDraft(false);
                                todo.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if(e == null) {
                                            //Let adapter know how to update view
                                            if(!isFinishing()) {
                                                todoListAdapter.notifyDataSetChanged();
                                            }
                                        } else {
                                            //Reset the is draft flag locally to true
                                            todo.setDraft(true);
                                        }
                                    }
                                });
                            }
                        } else {
                            Log.i("TodoListActivity", "syncTodosToParse: Eroor finding pinned todos:" +
                                     e.getMessage());
                        }
                    }
                });
            } else {
                //If we have a network connection but no logged in user,
                //direct the person to log in or sign up
                ParseLoginBuilder builder = new ParseLoginBuilder(this);
                startActivityForResult(builder.build(), LOGIN_ACTIVITY_CODE);
            }
        } else {
            //if there is no connection, let the user know the synce didn't happen
            Toast.makeText(
                    getApplicationContext(),
                    "Your device appears to be offline. Some todos may not have been synced to Parse.",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void loadFromParse() {
        ParseQuery<Todo> query = Todo.getQuery();
        query.whereEqualTo("author", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<Todo>() {
            @Override
            public void done(List<Todo> todos, ParseException e) {
                if (e == null) {
                    ParseObject.pinAllInBackground((List<Todo>) todos,
                            new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        if (!isFinishing()) {
                                            todoListAdapter.loadObjects();
                                        }
                                    } else {
                                        Log.i("TodoListActivity",
                                                "Error pinning todos: " +
                                            e.getMessage());
                                    }
                                }
                            });
                } else  {
                    Log.i("TodoListActivity",
                            "loadFromParse: Error finding pinning todos: " +
                                    e.getMessage());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.to_do_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_add){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
