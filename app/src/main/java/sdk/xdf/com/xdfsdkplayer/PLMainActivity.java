package sdk.xdf.com.xdfsdkplayer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gensee.player.MainActivity;

public class PLMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plmain);
        startActivity(new Intent().setClass(PLMainActivity.this, MainActivity.class));
        /*TextView hl = (TextView) findViewById(R.id.hl);
        hl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent().setClass(PLMainActivity.this, MainActivity.class));
            }
        });*/
    }
}
