package com.example.stopbell;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class BusStopAdapter extends ArrayAdapter<MainActivity.BusStop> {
    public BusStopAdapter(Context context, List<MainActivity.BusStop> busStops) {
        super(context, 0, busStops);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 데이터 아이템을 위한 뷰를 얻거나 재사용합니다.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.bus_stop_item, parent, false);
        }

        // 현재 정류장 아이템을 가져옵니다.
        MainActivity.BusStop busStop = getItem(position);

        // 뷰 요소를 찾아 데이터를 바인딩합니다.
        TextView busStopName = convertView.findViewById(R.id.busStopName);
        busStopName.setText(busStop.getName());

        // 다른 뷰 요소를 업데이트합니다...

        return convertView;
    }
}
