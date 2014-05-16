package com.xiazhiri.TypeIn;

import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Administrator on 5.14 014.
 */
public class FragPoint extends Fragment {
    ActivityMain activity;
    View view;

    public FragPoint(ActivityMain activity) {
        this.activity = activity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_point,container,false);
        this.view = view;
        ActionCallback callback = new ActionCallback();
        activity.startActionMode(callback);
        return view;
    }


    class ActionCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            activity.getMenuInflater().inflate(R.menu.action_point,menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.cancel:
                    activity.upPanelLayout.collapsePane();
                    mode.finish();
                    break;
                case R.id.send:
                    ArrayList<Location> locations = new ArrayList<Location>();
                    locations.add(activity.pubLocation);
                    String discription = ((TextView)view.findViewById(R.id.discription)).getText().toString();
                    ProcessItem processItem = new ProcessItem("点",locations,discription);
                    activity.processListAdapter.add(processItem);
                    activity.upPanelLayout.collapsePane();
                    mode.finish();
                    break;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    }

}