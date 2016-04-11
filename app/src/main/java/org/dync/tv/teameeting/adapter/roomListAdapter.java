package org.dync.tv.teameeting.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.dync.tv.teameeting.R;
import org.dync.tv.teameeting.bean.MeetingListEntity;

import java.util.List;

/**
 * Created by Xiao_Bailong on 2016/4/5.
 */
public class RoomListAdapter extends BaseAdapter {

    private List<MeetingListEntity> meetingLists;
    private Context context;

    public RoomListAdapter(List<MeetingListEntity> meetingLists, Context context) {
        this.meetingLists = meetingLists;
        this.context = context;
    }

    @Override
    public int getCount() {
        return meetingLists.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MeetingListEntity meetingListEntity = meetingLists.get(position);
        View view = LayoutInflater.from(context).inflate(R.layout.item_room_list,null);
        TextView txtRoomNum = (TextView) view.findViewById(R.id.txt_room_num);
        TextView txtRoomName = (TextView) view.findViewById(R.id.txt_room_name);
        txtRoomNum.setText(meetingListEntity.getMeetingid());
        txtRoomName.setText(meetingListEntity.getMeetname());
        return view;
    }
}
