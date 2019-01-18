/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.ghost;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;


public class GhostActivity extends AppCompatActivity {
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("words.txt");
            dictionary = new SimpleDictionary(inputStream);
            //String test = dictionary.getAnyWordStartingWith("cab");
            //Log.d("Vinty", "the test string is :" + test);
        } catch (IOException e) {
            Toast toast = Toast.makeText(this, "Could not load dictionary", Toast.LENGTH_LONG);
            toast.show();
        }
        Button challenge = (Button)findViewById(R.id.button_challenge);
        challenge.setOnClickListener(challengeListener);
        Button restart = (Button) findViewById(R.id.button_restart);
        restart.setOnClickListener(restartListener);
        onStart(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     * @param view
     * @return true
     */
    public boolean onStart(View view) {
        userTurn = random.nextBoolean();
        TextView text = (TextView) findViewById(R.id.ghostText);
        text.setText("");
        TextView label = (TextView) findViewById(R.id.gameStatus);
        if (userTurn) {
            label.setText(USER_TURN);
        } else {
            label.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }

    private Button.OnClickListener restartListener = new Button.OnClickListener(){

        @Override
        public void onClick(View v) {
            onStart(v);
        }
    };

    private Button.OnClickListener challengeListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView window = findViewById(R.id.ghostText);
            String uiWindow = window.getText().toString();
            TextView status = (TextView) findViewById(R.id.gameStatus);
            if(dictionary.isWord(uiWindow) && uiWindow.length() >= 4){
                status.setText("You win!");
            }
            else if (dictionary.getAnyWordStartingWith(uiWindow) != null){
                status.setText("You lose! The word could be " + dictionary.getAnyWordStartingWith(uiWindow));
            }
            else{
                status.setText("You win!");
            }
        }
    };


    private void computerTurn() {
        TextView window = findViewById(R.id.ghostText);
        String uiWindow = window.getText().toString();
        TextView label = (TextView) findViewById(R.id.gameStatus);
        if(uiWindow.length() >= 4 && dictionary.isWord(uiWindow)){
            label.setText("You lose!"); // declaring computer's victory!
            userTurn = false;
        }
        else if(dictionary.getAnyWordStartingWith(uiWindow) == null){
            label.setText("You lose!");
            userTurn = false;
        }
        else {
            String addedFragment = dictionary.getAnyWordStartingWith(uiWindow).substring(0,uiWindow.length() +1 );//.substring(0, uiWindow.length());
            //Log.d("computer", addedFragment);
            window.setText(addedFragment);
            userTurn = true;
            label.setText(USER_TURN);
        }
    }

    /**
     * Handler for user key presses.
     * @param keyCode
     * @param event
     * @return whether the key stroke was handled.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode > 54 || keyCode < 29){
            return super.onKeyUp(keyCode, event);
        }
        TextView window = findViewById(R.id.ghostText);
        String uiWindow = window.getText().toString();
        char ch = (char)event.getUnicodeChar();
        uiWindow = uiWindow + ch;
        window.setText(uiWindow);
        if(dictionary.isWord(uiWindow)){
            TextView label = (TextView) findViewById(R.id.gameStatus);
            label.setText("You lose!");
            return super.onKeyUp(keyCode, event);
        }
        computerTurn();
        return super.onKeyUp(keyCode, event);
    }
}
