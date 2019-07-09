package net.simplifiedcoding;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    EditText username,password;
    Button btn_login;

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedpreferences = getSharedPreferences(Variable.PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        final String login = sharedpreferences.getString(Variable.CEK_LOGIN, null);

        username = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btn_login = findViewById(R.id.btn_login);

        if(login!=null){
            if(login.equals("1")){
                Intent i = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        }

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login(username.getText().toString(),password.getText().toString());
            }
        });
    }

    public void Login(String mUsername, String mPassword) {
        new LoginAsync().execute(
                Variable.Login,
                mUsername,
                mPassword
        );
    }

    class LoginAsync extends AsyncTask<String, String, JSONObject> {
        JSONParser jsonParser = new JSONParser();
        private final String API_URL = Variable.URL_API;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected JSONObject doInBackground(String... args) {

            try {

                HashMap<String, String> params = new HashMap<>();
                params.put(Variable.FUNCTION, args[0]);
                params.put(Variable.Username, args[1]);
                params.put(Variable.password, args[2]);
                Log.d("request", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        API_URL, "POST", params);

                if (json != null) {
                    Log.d("JSON result", json.toString());
                    return json;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(JSONObject json) {
            if (json != null) {
//                Toast.makeText(getApplicationContext(),json.toString(),Toast.LENGTH_SHORT).show();
                try {
                    JSONObject parentObject = new JSONObject(json.toString());
                    JSONObject userDetails = parentObject.getJSONObject("hasil");
                    String success = userDetails.getString("success");
                    if (success.equals("1")) {

                        editor.remove(Variable.CEK_LOGIN );

                        editor.putString(Variable.CEK_LOGIN, "1");

                        editor.commit();

                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();

                    } else {
                        AlertDialog.Builder noticeLogin = new AlertDialog.Builder(LoginActivity.this);
                        noticeLogin.setTitle("Login gagal");
                        noticeLogin.setMessage("\nSilakan periksa kembali Nomor HP dan Password anda.");
                        noticeLogin.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        noticeLogin.create();
                        noticeLogin.show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                }

            }
        }
    }
}
