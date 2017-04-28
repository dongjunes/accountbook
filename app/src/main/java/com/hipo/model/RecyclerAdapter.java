package com.hipo.model;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hipo.callback.ShareEventAdapterToFragment;
import com.hipo.lookie.R;
import com.hipo.model.pojo.ListVo;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dongjune on 2017-04-27.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private Context context;
    private List<ListVo> list;
    private int itemLayout;
    private FragmentManager fm;
    private ShareEventAdapterToFragment shareEvent;

    public RecyclerAdapter(Context context, List<ListVo> list, int itemLayout, ShareEventAdapterToFragment shareEvent) {
        this.context = context;
        this.list = list;
        this.itemLayout = itemLayout;
        this.shareEvent = shareEvent;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, null);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        ListVo listVo = list.get(position);
        String date = listVo.getDay();
        String times[] = parsingDate(date, listVo);
        holder.ymText.setText(times[0]);
        holder.dText.setText(times[1]);
        holder.moneyText.setText(listVo.getMoney());
        if (listVo.getOperations().equals("-")) {
            holder.moneyText.setTextColor(Color.parseColor("#cc0303"));
        } else {
            holder.moneyText.setTextColor(Color.parseColor("#5cd1e5"));
        }
        holder.paidText.setText(listVo.getPaid());
        holder.nameText.setText(listVo.getName());
        holder.categoryText.setText(listVo.getCategory());
        holder.listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareEvent.event(v, list.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView ymText;
        TextView dText;
        TextView categoryText;
        TextView nameText;
        TextView paidText;
        TextView moneyText;
        LinearLayout listItem;

        public ViewHolder(View itemView) {
            super(itemView);
            ymText = (TextView) itemView.findViewById(R.id.ymtxt);
            dText = (TextView) itemView.findViewById(R.id.dtxt);
            categoryText = (TextView) itemView.findViewById(R.id.category);
            nameText = (TextView) itemView.findViewById(R.id.name);
            paidText = (TextView) itemView.findViewById(R.id.paid);
            moneyText = (TextView) itemView.findViewById(R.id.money);
            listItem = (LinearLayout) itemView.findViewById(R.id.list_item);
            /*itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("click","Click");
                }
            });*/
        }
    }

    public String[] parsingDate(String data, ListVo listVo) {
        String date[] = new String[3];
        Pattern p = null;
        Matcher m;
        for (int i = 0; i < date.length; i++) {
            switch (i) {
                case 0:
                    p = Pattern.compile("(.*?)-[0-9][0-9] ");//년월
                    break;
                case 1:
                    p = Pattern.compile("-[0-9][0-9]-(.*?) ");//일
                    break;
                case 2:
                    p = Pattern.compile("-[0-9][0-9] (.*?):[0-9][0-9].[0-9]$");//시간
            }
            m = p.matcher(data);
            if (m.find()) {
                date[i] = m.group(1);
            }
        }
        listVo.setDate_ym(date[0]);
        listVo.setDate_day(date[1]);
        listVo.setTime(date[2]);
        return date;
    }

}
