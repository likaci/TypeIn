package com.xiazhiri.TypeIn;

import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import java.util.ArrayList;

/**
 * Created by Administrator on 5.14 014.
 */
public class FragLine extends Fragment {
    ArrayList<String> pointList;
    ListView linePointListView;
    ArrayAdapter<String> adapter;
    LinePointListAdapter listAdapter;
    ActivityMain activity;
    View view;

    public FragLine(ActivityMain activity) {
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_line,container,false);
        linePointListView = (ListView)view.findViewById(R.id.linePointList);
        pointList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(view.getContext(),android.R.layout.simple_list_item_1,pointList);
        listAdapter = new LinePointListAdapter(view.getContext(),R.layout.item_point,new ArrayList<Location>());
        linePointListView.setAdapter(listAdapter);

        ActionCallback callback = new ActionCallback();
        activity.startActionMode(callback).setTitle("线测量");

        return view;
    }

    class ActionCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            activity.getMenuInflater().inflate(R.menu.action_line,menu);
            if (activity.gpsEnable == true) {
                menu.findItem(R.id.gpsSwithcer).setIcon(R.drawable.ic_menu_gpson_dark);
            }
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.add:
                    if (activity.pubLocation == null) {
                        Toast.makeText(getView().getContext(),"请先打开定位",Toast.LENGTH_SHORT).show();
                        break;
                    }
                    listAdapter.add(activity.pubLocation);
                    listAdapter.notifyDataSetChanged();
                    break;
                case R.id.cancel:
                    activity.upPanelLayout.collapsePane();
                    mode.finish();
                    break;
                case R.id.send:
                    activity.upPanelLayout.collapsePane();
                    String discription = ((TextView)view.findViewById(R.id.discription)).getText().toString();
                    ProcessItem processItem = new ProcessItem("线",pointList,discription);
                    activity.processListAdapter.add(processItem);
                    mode.finish();
                    break;
                case R.id.gpsSwithcer:
                    activity.toggleGps(item);
                    break;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            activity.upPanelLayout.collapsePane();
        }
    }

    class LinePointListAdapter extends BaseAdapter {
        LayoutInflater inflater;
        //Context context;
        int resID;
        ArrayList<Location> locationList;

        LinePointListAdapter(Context context, int resID, ArrayList<Location> locationList) {
            this.inflater = LayoutInflater.from(context);
            this.resID = resID;
            this.locationList = locationList;
        }

        @Override
        public int getCount() {
            return locationList.size();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_point,null);
            }
            TextView tv = ((TextView) convertView.findViewById(R.id.pointNum));
            tv.setText( position + "");
            ((TextView) convertView.findViewById(R.id.pointCoord)).setText(locationList.get(position).getLatitude() + "," + locationList.get(position).getLongitude());

            convertView.findViewById(R.id.itemDel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    locationList.remove(position);
                    listAdapter.notifyDataSetChanged();
                }
            });
            return convertView;
        }

        public void add(Location location) {
            locationList.add(location);
        }
    }
}