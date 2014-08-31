package com.samiamharris.activitypractice.offlinetodos;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseQueryAdapter;

import org.w3c.dom.Text;

import java.util.zip.Inflater;

/**
 * Created by SamMyxer on 8/29/14.
 */
public class ToDoListAdapter extends ParseQueryAdapter<Todo> {

    public ToDoListAdapter(Context context, QueryFactory<Todo> queryFactory) {
        super(context, queryFactory);
    }


    @Override
    public View getItemView(Todo todo, View v, ViewGroup parent) {
        ViewHolder holder;

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(v == null) {
            v = inflater.inflate(R.layout.list_item_todo, parent, false);
            holder = new ViewHolder();
            holder.toDoTitle = (TextView) v.findViewById(R.id.todo_title);
            v.setTag(holder);

        } else {
            holder = (ViewHolder) v.getTag();
        }
        TextView todoTitle = holder.toDoTitle;
        todoTitle.setText(todo.getTitle());
        if (todo.isDraft()) {
            todoTitle.setTypeface(null, Typeface.ITALIC);
        } else {
            todoTitle.setTypeface(null, Typeface.NORMAL);
        }

        return v;
    }

    private static class ViewHolder {
        TextView toDoTitle;
    }
}
