package menum.menum.MainScreenPackage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import menum.menum.R;

public class Notifications extends AppCompatActivity {

    private TextView tvDeleteAll;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recNewNotification;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        init();
    }

    private void init() {
        tvDeleteAll=findViewById(R.id.tvDeleteAll);
        linearLayoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recNewNotification=findViewById(R.id.recNewNotification);
        recNewNotification.setLayoutManager(linearLayoutManager);
    }
}
