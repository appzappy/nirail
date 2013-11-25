package AppZappy.NIRailAndBus.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import AppZappy.NIRailAndBus.R;

public class FragmentedDashboard extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}