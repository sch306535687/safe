package sun.ch.safe;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by sunch on 2016/12/22.
 */
public class LockActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lock_activity);
    }
}
