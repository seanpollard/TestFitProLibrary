package com.ifit.sparkydevapp.sparkydevapp.connecting;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ifit.sparky.fecp.communication.CommType;
import com.ifit.sparkydevapp.sparkydevapp.ItemListActivity;
import com.ifit.sparkydevapp.sparkydevapp.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WifiConnectActivity extends Activity implements View.OnClickListener, View.OnKeyListener {

    private Button connectButton;
    private EditText ipAddressEditText;
    private EditText portEditText;
    private boolean mValidPort;
    private boolean mValidIpAddress;

    private static final Pattern IP_ADDRESS
            = Pattern.compile(
            "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
                    + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
                    + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
                    + "|[1-9][0-9]|[0-9]))");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_connect);

        //assign gui elements
        this.connectButton = (Button) findViewById(R.id.buttonWifiConnect);
        this.portEditText = (EditText)findViewById(R.id.portEditText);
        this.ipAddressEditText = (EditText)findViewById(R.id.ipAddressEditText);
        this.ipAddressEditText.setText("192.168.0.4");
        this.portEditText.setText("8090");
        this.portEditText.setOnKeyListener(this);
        this.ipAddressEditText.setOnKeyListener(this);
        this.connectButton.setEnabled(true);
        this.mValidIpAddress = true;
        this.mValidPort = true;
        connectButton.setOnClickListener(this);
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
            if(this.mValidPort && this.mValidIpAddress)
            {
                //start the other intent, if the connection failed come back to here
                Intent itemListIntent = new Intent(v.getContext(), ItemListActivity.class);
                Bundle bundleParameters = new Bundle();
                try {
                    bundleParameters.putInt("commInterface", CommType.TCP.ordinal());
                    bundleParameters.putInt("port", Integer.parseInt(this.portEditText.getText().toString()));
                    bundleParameters.putString("ipAddress", this.ipAddressEditText.getText().toString());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }catch (NullPointerException e) {
                    e.printStackTrace();
                }
                itemListIntent.putExtras(bundleParameters);
                startActivity(itemListIntent);
                finish();
            }


        }
    }

    /**
     * Called when a hardware key is dispatched to a view. This allows listeners to
     * get a chance to respond before the target view.
     * <p>Key presses in software keyboards will generally NOT trigger this method,
     * although some may elect to do so in some situations. Do not assume a
     * software input method has to be key-based; even if it is, it may use key presses
     * in a different way than you expect, so there is no way to reliably catch soft
     * input key presses.
     *
     * @param v       The view the key has been dispatched to.
     * @param keyCode The code for the physical key that was pressed
     * @param event   The KeyEvent object containing full information about
     *                the event.
     * @return True if the listener has consumed the event, false otherwise.
     */
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        try {
            if(v == this.portEditText && this.portEditText != null && !this.portEditText.getText().toString().isEmpty())
            {
                //check for valid format
                if(Integer.parseInt(this.portEditText.getText().toString()) < Short.MAX_VALUE)
                {
                    this.portEditText.setTextColor(Color.parseColor("#000000"));
                    this.mValidPort = true;
                }
                else
                {
                    this.portEditText.setTextColor(Color.parseColor("#FF0000"));
                }
            }
            else if(v == this.ipAddressEditText)
            {
                //check if number

                Matcher matcher = IP_ADDRESS.matcher(ipAddressEditText.getText().toString());
                if (matcher.matches()) {
                    // ip is valid
                    this.ipAddressEditText.setTextColor(Color.parseColor("#000000"));
                    this.mValidIpAddress = true;
                }
                else
                {
                    this.ipAddressEditText.setTextColor(Color.parseColor("#FF0000"));
                }

            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        this.connectButton.setEnabled((this.mValidIpAddress && this.mValidPort));

        return false;
    }
}
