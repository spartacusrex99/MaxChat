package com.minima.maxchat;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.minima.maxchat.db.MaxMessage;

import java.util.ArrayList;
import java.util.Date;


public class MaxAdapter extends ArrayAdapter<MaxMessage> {

    private ArrayList<MaxMessage> dataSet;
    Context mContext;

    // View lookup cache
    static class ViewHolder {
        TextView tvName;
        TextView tvMessage;
        TextView tvDate;

        ViewHolder(View view) {
            tvName      = (TextView) view.findViewById(R.id.maxname);
            tvMessage   = (TextView) view.findViewById(R.id.maxmessage);
            tvDate = (TextView) view.findViewById(R.id.maxdate);
        }
    }

    public MaxAdapter(ArrayList<MaxMessage> data, Context context) {
        super(context, R.layout.maxmessageitem, data);
        this.dataSet = data;
        this.mContext=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        MaxMessage dataModel = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder holder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.maxmessageitem, parent, false);
            holder = new ViewHolder(convertView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(dataModel.isUnread()){
            holder.tvName.setTypeface(null, Typeface.BOLD);
        }else{
            holder.tvName.setTypeface(null, Typeface.NORMAL);
        }

        holder.tvName.setText(dataModel.getChatroom());
        holder.tvMessage.setText(dataModel.getMessage());
        holder.tvDate.setText(new Date(dataModel.getTimeMIlli()).toString());

        // Return the completed view to render on screen
        return convertView;
    }
}

