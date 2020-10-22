package edu.byuh.cis.cs203.slide.edu.byuh.cis.cs203.slide.ui;

import android.os.Bundle;
import android.app.Activity;

import edu.byuh.cis.cs203.slide.edu.byuh.cis.cs203.slide.ui.MyView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyView mv = new MyView(this);
        setContentView(mv);

    }
}
