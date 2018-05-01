package edu.msu.lizheng9.findmyfriends;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public boolean userCloud = false;
    public void onGoClicked(final View view){
       final String android_id = Settings.Secure.getString(getContentResolver(),
               Settings.Secure.ANDROID_ID);

       final EditText editUsername = findViewById(R.id.editUsername);
       final String username = editUsername.getText().toString();
       Thread t = new Thread(new Runnable() {
           @Override
           public void run() {
               Cloud cloud = new Cloud();
               userCloud = cloud.User(username,android_id);

               view.post(new Runnable() {
                   @Override
                   public void run() {
                       if (!userCloud){
                           Toast.makeText(view.getContext(),
                                   R.string.user_fail,
                                   Toast.LENGTH_SHORT).show();
                       }
                       else{
                           Toast.makeText(view.getContext(),
                                   R.string.user_success,
                                   Toast.LENGTH_SHORT).show();
                       }
                   }
               });
           }
       });
       t.start();
       try { t.join(3000); } catch (InterruptedException e) { e.printStackTrace(); }

        if(userCloud){
            Intent intent = new Intent(this,MapsActivity.class);
            intent.putExtra("username",username);
            intent.putExtra("device",android_id);
            startActivity(intent);
        }

    }

}
