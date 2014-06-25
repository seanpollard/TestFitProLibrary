package com.ifit.sparkydevapp.sparkydevapp.connecting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.ifit.sparky.fecp.communication.CommType;
import com.ifit.sparkydevapp.sparkydevapp.ItemListActivity;
import com.ifit.sparkydevapp.sparkydevapp.R;

public class ConnectionActivity extends Activity implements View.OnClickListener{

    private Button connectButton;
    private Button wifiConnectButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        //assign gui elements
        this.connectButton = (Button) findViewById(R.id.buttonConnect);
        this.wifiConnectButton = (Button) findViewById(R.id.buttonWifiConnect);
        connectButton.setOnClickListener(this);
        wifiConnectButton.setOnClickListener(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.connection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        //connect to the fecp controller
        if(v == this.connectButton)
        {
            //start the other intent, if the connection failed come back to here
            Intent itemListIntent = new Intent(getApplicationContext(), ItemListActivity.class);

            itemListIntent.putExtra("commInterface", CommType.USB.ordinal());

            startActivity(itemListIntent);
            finish();
        }
        else if(v == this.wifiConnectButton)
        {
            //Switch to the TCP connection activity
            Intent wifiConnect = new Intent(v.getContext(), WifiConnectActivity.class);
            startActivity(wifiConnect);
            finish();
        }
    }
}
