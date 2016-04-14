package org.dync.tv.teameeting.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.dync.tv.teameeting.R;
import org.dync.tv.teameeting.bean.MeetingListEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xiao_Bailong on 2016/4/5.
 */
public class RoomListAdapter extends BaseAdapter {

    private List<MeetingListEntity> meetingLists;
    private Context context;
    private boolean hasFocus;

    public RoomListAdapter(List<MeetingListEntity> meetingLists, Context context) {
        this.meetingLists = meetingLists;
        this.context = context;
    }

    public void hasFocus(boolean hasFocus){
        this.hasFocus = hasFocus;
        notifyDataSetChanged();
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
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MeetingListEntity meetingListEntity = meetingLists.get(position);
        HolderView holderView = null;
        if (convertView == null) {
            holderView = new HolderView();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_room_list, null);
            holderView.txtRoomNum = (TextView) convertView.findViewById(R.id.txt_room_num);
            holderView.txtRoomName = (TextView) convertView.findViewById(R.id.txt_room_name);
            holderView.txtRoomNum.setText(meetingListEntity.getMeetingid());
            holderView.txtRoomName.setText(meetingListEntity.getMeetname());

            convertView.setTag(holderView);
        } else {
            holderView = (HolderView) convertView.getTag();
        }

        if (hasFocus) {
            convertView.setBackgroundResource(R.drawable.selector_item);
            Log.e("BaseAdapter", "getView得到焦点====" + position);
        } else {
            convertView.setBackgroundResource(R.drawable.button_default);
            Log.e("BaseAdapter", "getView失去焦点====" + position);
        }

        return convertView;
    }

    private class HolderView {
        public TextView txtRoomNum;
        public TextView txtRoomName;
    }
}
