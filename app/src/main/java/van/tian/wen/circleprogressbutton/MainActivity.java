package van.tian.wen.circleprogressbutton;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CircleProgressButton circleProgressButton = (CircleProgressButton) findViewById(R.id.circleProgressButton);

        circleProgressButton.setText("文字");

    }
}
