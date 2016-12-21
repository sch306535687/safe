package sun.ch.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sun.ch.safe.R;

/**
 * Created by sunch on 2016/12/21.
 */
public class LockFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.activity_lock, null);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
