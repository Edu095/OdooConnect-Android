package com.example.eduard.odooconnect;

// Connector Android-Odoo: https://github.com/gturri/aXMLRPC/tree/master

// Adaptado de: https://github.com/zikzakmedia/android-openerp

// tutorial thread AsyncTask:
// http://www.sgoliver.net/blog/tareas-en-segundo-plano-en-android-i-thread-y-asynctask/
// https://github.com/sgolivernet/curso-android-src/tree/master/android-asynctask


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    AlertDialog.Builder msg;
    private String msgResult = "";
    ProgressDialog pDialog;

    private EditText domain, portt, dataBase, user, pass;
    ConnectionData cd = new ConnectionData();
    Boolean inp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        msg = new AlertDialog.Builder(this);
        pDialog = new ProgressDialog(this);

        domain = (EditText) findViewById(R.id.editTextDomain);
        portt = (EditText) findViewById(R.id.editTextPort);
        dataBase = (EditText) findViewById(R.id.editTextDB);
        user = (EditText) findViewById(R.id.editTextUser);
        pass = (EditText) findViewById(R.id.editTextPass);

        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Connecting...");
        pDialog.setCancelable(true);

    }

    public void executeGetData(View view) {
        Intent i = new Intent(this, OdooGetData.class);
        i.putExtra("ConnData", cd);
        startActivity(i);
    }

    public void connectServer(View view) {

        ConnectionOdoo tarea;
        tarea = new ConnectionOdoo();
        tarea.execute();

    }

    protected boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0)
            return false;
        return true;
    }

    private class ConnectionOdoo extends AsyncTask<Void, String, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {

            // Try connection to server

            if (inp) {
                try {
                    Boolean ocT = OdooConnect.testConnection(cd.getUrl(), cd.getPort(),
                            cd.getDb(), cd.getUsername(), cd.getPassword());
                    if (ocT) {
                        return true;
                    } else {
                        msgResult = "Connection error";
                    }
                } catch (Exception ex) {
                    // Any other exception
                    msgResult = "Error: " + ex;
                }
            }
            return false;
        }

        @Override
        protected void onProgressUpdate(String... values) {
        }

        @Override
        protected void onPreExecute() {

            // Before connect check all data required

            if (isEmpty(domain)) {
                msgResult = "Domain cannot be empty.";
            } else if (isEmpty(dataBase)) {
                msgResult = "Data Base cannot be empty.";
            } else if (isEmpty(user)) {
                msgResult = "User cannot be empty.";
            } else if (isEmpty(pass)) {
                msgResult = "Passwors cannot be empty.";
            } else {
                cd.setUrl(domain.getText().toString());
                if (!portt.getText().toString().equals("")) {
                    cd.setPort(Integer.parseInt(portt.getText().toString()));
                } else {
                    cd.setPort(8069);
                }
                cd.setDb(dataBase.getText().toString());
                cd.setUsername(user.getText().toString());
                cd.setPassword(pass.getText().toString());
                inp = true;

                pDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        ConnectionOdoo.this.cancel(true);
                    }
                });
                pDialog.show();
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            pDialog.dismiss();
            if (result) {
                executeGetData(null); // if connection works call OdooGetData class
            } else {
                msg.setMessage(msgResult);
                msg.show();
                msgResult = "";
            }
        }

        @Override
        protected void onCancelled() {
            Toast.makeText(MainActivity.this, "Connection cancelled", Toast.LENGTH_SHORT).show();
        }
    }
}

