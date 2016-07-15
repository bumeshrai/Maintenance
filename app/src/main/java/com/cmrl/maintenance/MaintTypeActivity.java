package com.cmrl.maintenance;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by prasa on 11-Jul-16.
 */
public class MaintTypeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maint_type);

        Intent intent = getIntent();
        final String auth_key = intent.getStringExtra("auth_key");

        Button btScheduledMaint = (Button) findViewById(R.id.btScheduledMaint);
        Button btBreakdownMaint = (Button) findViewById(R.id.btBreakdownMaint);
        Button btAttendBreakdown = (Button) findViewById(R.id.btAttendBreakdown);

        btScheduledMaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MaintTypeActivity.this, AssetMaintActivity.class);
                intent.putExtra("auth_key", auth_key);
                startActivity(intent);
            }
        });

        btBreakdownMaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MaintTypeActivity.this,BreakdownMaintActivity.class);
                startActivity(intent);
            }
        });

        btAttendBreakdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MaintTypeActivity.this,AttendBreakdownActivity.class);
                startActivity(intent);
            }
        });
    }
}
