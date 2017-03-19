package me.yogeshwardan.mqttsubscriber;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private EditText mEditText;
    private TextView mtextview;
    private Button savenotebutton1;
    private SharedPreferences savednotes;
    private SharedPreferences savednotes1;
    private ImageView mImage;

    private EditText editText1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mtextview = (TextView) findViewById(R.id.textBus);
        mImage = (ImageView) findViewById(R.id.imageView);
        //savenotebutton1 = (Button) findViewById(R.id.button);
        editText1 = (EditText) findViewById(R.id.editText);
        //savednotes = getSharedPreferences("notes", MODE_PRIVATE);
        //savednotes1 = getSharedPreferences("notes1", MODE_PRIVATE);

//        editText1.setText(savednotes.getString("tag", "Default Value")); //add this line

       // savenotebutton1.setOnClickListener(saveButtonListener);

        //MQTTConnect options : setting version to MQTT 3.1.1
        MqttConnectOptions options = new MqttConnectOptions();
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
        options.setUserName("cat");
        options.setPassword("cat".toCharArray());

        //Below code binds MainActivity to Paho Android Service via provided MqttAndroidClient
        // client interface
        //Todo : Check why it wasn't connecting to test.mosquitto.org. Isn't that a public broker.
        //Todo : .check why client.subscribe was throwing NullPointerException  even on doing subToken.waitForCompletion()  for Async                  connection estabishment. and why it worked on subscribing from within client.connect’s onSuccess(). SO
        String clientId = MqttClient.generateClientId();
        final MqttAndroidClient client =
                new MqttAndroidClient(this.getApplicationContext(), "tcp://m10.cloudmqtt.com:14062",
                        clientId);


        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "onSuccess");
                    Toast.makeText(MainActivity.this, "Connection successful", Toast.LENGTH_SHORT).show();
                    //Subscribing to a topic door/status on broker.hivemq.com
//                    client.setCallback(MainActivity.this);
                    final String topic = "GPS";
                    int qos = 1;
                    try {
                        IMqttToken subToken = client.subscribe(topic, qos);
                        subToken.setActionCallback(new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {
                                // successfully subscribed
                                Toast.makeText(MainActivity.this, "Successfully subscribed to: " + topic, Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onFailure(IMqttToken asyncActionToken,
                                                  Throwable exception) {
                                // The subscription could not be performed, maybe the user was not
                                // authorized to subscribe on the specified topic e.g. using wildcards
                                Toast.makeText(MainActivity.this, "Couldn't subscribe to: " + topic, Toast.LENGTH_SHORT).show();

                            }
                        });
                    } catch (MqttException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "onFailure");
                    Toast.makeText(MainActivity.this, "Connection failed", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }


        Button btn = (Button) findViewById(R.id.button2);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable cause) {

                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {


                        Log.d("GPS", message.toString());


                        EditText et;
                        final TextView tv;


                        et = (EditText) findViewById(R.id.editText);
                        String ett;
                        ett = "51C45338";
                        tv = (TextView) findViewById(R.id.tv1);
                        String mes = message.toString();
                        final String[] split = mes.split(",");
                       // if (split[0].equals(editText1.getText().toString())) {
                        tv.append( "\n"+"Bus Id" + " : "+split[0] + "\n");
                        tv.append( "latitude" + " : "+ split[2] + "\n");
                        tv.append( "longtitude" + " : "+ split[1] +  "\n");
                        tv.append( "date-time" + " : "+ split[3] +  "\n");
                            Toast.makeText(MainActivity.this, "Topic: " + topic + "\nMessage: " + split[1] + "," + split[2], Toast.LENGTH_LONG).show();
                       // }
                        Log.d("asd", "đã bấm");
                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {

                    }
                });
            }
        });
    }

    private void makeTag(String tag) {
        String or = savednotes.getString(tag, null);
        SharedPreferences.Editor preferencesEditor = savednotes.edit();
        preferencesEditor.putString("tag", tag); //change this line to this
        preferencesEditor.commit();
        String orr = savednotes1.getString(tag, null);
        SharedPreferences.Editor preferencesEditor1 = savednotes.edit();
        preferencesEditor1.putString("tag", tag); //change this line to this
        preferencesEditor1.commit();
    }


//    public void messageArrived(final String topic, MqttMessage message) throws Exception {
//    }

    public View.OnClickListener saveButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (editText1.getText().length() > 0) {
                makeTag(editText1.getText().toString());

                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(editText1.getWindowToken(), 0);

            }
        }
    };

}
