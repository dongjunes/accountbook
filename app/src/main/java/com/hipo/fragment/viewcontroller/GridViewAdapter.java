package com.hipo.fragment.viewcontroller;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hipo.lookie.R;

import java.util.Calendar;
import java.util.List;

/**
 * Created by dongjune on 2017-05-01.
 */

public class GridViewAdapter extends BaseAdapter {

    private final List<String> list;

    private final LayoutInflater inflater;

    public GridViewAdapter(Context context, List<String> list) {
        this.list = list;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.calender_item, parent, false);
            holder = new ViewHolder();
            holder.itemGridView = (TextView) convertView.findViewById(R.id.item_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.itemGridView.setText("" + getItem(position));
        Calendar calendar = Calendar.getInstance();
        Integer today = calendar.get(Calendar.DAY_OF_MONTH);
        String sToday = String.valueOf(today);


        if (sToday.equals(getItem(position))) { //오늘 day 텍스트 컬러 변경

            String mColor = "#0000FF";
            holder.itemGridView.setTextColor(Color.parseColor(mColor));

        }
        return convertView;
    }

    private class ViewHolder {
        TextView itemGridView;
    }
}