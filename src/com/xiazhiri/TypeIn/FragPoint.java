package com.xiazhiri.TypeIn;

import android.app.Fragment;
import android.os.Bundle;
import android.view.*;

/**
 * Created by Administrator on 5.14 014.
 */
public class FragPoint extends Fragment {
    ActivityMain activity;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_point,container,false);
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
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    }

}