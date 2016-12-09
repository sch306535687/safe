package sun.ch.safe;

import android.app.Application;
import android.content.Context;
import android.test.ApplicationTestCase;

import java.util.Set;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    private Context context;
    public ApplicationTest() {
        super(Application.class);
    }

    @Override
    protected void setUp() throws Exception {
        this.context = getContext();
        super.setUp();
    }
}