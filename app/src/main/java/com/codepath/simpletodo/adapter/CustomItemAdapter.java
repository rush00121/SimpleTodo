package com.codepath.simpletodo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.codepath.simpletodo.R;
import com.codepath.simpletodo.model.Item;

import java.util.List;

/**
 * Created by rushabh on 4/5/15.
 */
public class CustomItemAdapter extends ArrayAdapter<Item> {

    private static List<Item> items;

    private LayoutInflater mInflater;

    public CustomItemAdapter(Context context, int resource, List<Item> objects) {
        super(context, resource, objects);
        items = objects;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Item getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.custom_layout, null);
            holder = new ViewHolder();
            holder.txtItemString = (TextView) convertView.findViewById(R.id.itemString);
            holder.txtItemDueDate = (TextView) convertView.findViewById(R.id.dueDate);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtItemString.setText(items.get(position).task);
        holder.txtItemDueDate.setText(items.get(position).date);
        return convertView;
    }


    static class ViewHolder {
        TextView txtItemString;
        TextView txtItemDueDate;
    }
}
