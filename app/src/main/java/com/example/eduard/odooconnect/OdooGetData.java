package com.example.eduard.odooconnect;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Edu on 16/5/17.
 */

public class OdooGetData extends AppCompatActivity {

    ConnectionData cd; // class to save login data

    AlertDialog.Builder msg;
    private String msgResult = "";

    protected String url,   // = "192.168.1.228",
            db,             // = "odoodb2",
            username,       // = "odoo",
            password;       // = "odoodb";
    protected Integer port; // = 8069;

    int act = 0;

    private String[] spin;
    Spinner s, sC, sD;
    ArrayList<String> listD = new ArrayList<String>();

    EditText name, phone;
    Boolean inpCreate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.odoo_get_data);

        cd = (ConnectionData) getIntent().getSerializableExtra("ConnData");

        msg = new AlertDialog.Builder(this);

        this.spin = new String[]{"Person", "", "Is Company"};
        s = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, spin);
        s.setAdapter(adapter);

        this.spin = new String[]{"Individual", "", "Company"};
        sC = (Spinner) findViewById(R.id.spinnerCreate);
        ArrayAdapter<String> adapterC = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, spin);
        sC.setAdapter(adapterC);

        sD = (Spinner) findViewById(R.id.spinnerDelete);

        name = (EditText) findViewById(R.id.editTextNameC);
        phone = (EditText) findViewById(R.id.editTextPhoneC);

        getData();

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    protected boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0)
            return false;
        return true;
    }

    private void getData() {
        url = cd.getUrl();
        db = cd.getDb();
        username = cd.getUsername();
        password = cd.getPassword();
        port = cd.getPort();
    }

    //Button actions

    public void list(View view) {
        act = 1;
        ConnectionOdoo tarea;
        tarea = new ConnectionOdoo();
        tarea.execute();
    }

    public void create(View view) {
        act = 2;
        ConnectionOdoo tarea;
        tarea = new ConnectionOdoo();
        tarea.execute();
    }

    public void delete(View view) {
        act = 3;
        ConnectionOdoo tarea;
        tarea = new ConnectionOdoo();
        tarea.execute();
    }


    private class ConnectionOdoo extends AsyncTask<Void, ArrayList<String>, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {

            OdooConnect oc = OdooConnect.connect(url, port, db, username, password);

            List<HashMap<String, Object>> data = oc.search_read("res.partner", new Object[]{
                    new Object[]{new Object[]{"customer", "=", true}}}, "name");

            // get data to fill spin partner list
            for (int i = 0; i < data.size(); ++i) {
                if (data.get(i).get("id").toString().length()>1) {
                    listD.add("Id: " + data.get(i).get("id").toString() + " - " + data.get(i).get("name").toString());
                }else{
                    listD.add("Id: 0" + data.get(i).get("id").toString() + " - " + data.get(i).get("name").toString());
                }
            }

            switch (act) {
                case 1:
                    listTask();
                    break;
                case 2:
                    if (inpCreate) { createTask(); }
                    break;
                case 3:
                    deleteTask();
                    break;
                case 4:
                    break;
                case 5:
                    break;
            }
            return true;
        }

        @Override
        protected void onProgressUpdate(ArrayList<String>... values) {

        }

        @Override
        protected void onPreExecute() {

            if (act == 2 && isEmpty(name)) {
                msgResult = "Name cannot be empty.";
                inpCreate = false;
            } else if (act == 2) {
                inpCreate = true;
            }

        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                msg.setMessage(msgResult);
                msg.show();
                msgResult = "";
            }
            upDlist();
        }

        @Override
        protected void onCancelled() {
            Toast.makeText(OdooGetData.this, "Connection cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    private void upDlist() {

        //TODO: clear spinner
        
        sD.setAdapter(null);
        sD.notifyAll();
        ArrayAdapter<String> adapterD = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, listD);
        sD.setAdapter(adapterD);
    }

    private Object[] filterList() {
        String list = s.getSelectedItem().toString();
        if (list.equals("Is Company")) {
            Object[] filter = {new Object[]{
                    new Object[]{"customer", "=", true},
                    new Object[]{"is_company", "=", true}}};
            return filter;
        }
        Object[] filter = {new Object[]{
                new Object[]{"customer", "=", true},
                new Object[]{"is_company", "=", false}}};
        return filter;
    }

    private void listTask() {
        try {
            OdooConnect oc = OdooConnect.connect(url, port, db, username, password);

            Object[] param = filterList();
            // Count id's
            Integer ids = oc.search_count("res.partner", param);
            msgResult += "Num. of customers: " + ids.toString() + "\n";

            // List records
            List<HashMap<String, Object>> data = oc.search_read("res.partner", param, "name");

            for (int i = 0; i < data.size(); ++i) {
                msgResult += "\n" + data.get(i).get("name");
            }

        } catch (Exception ex) {
            msgResult = "Error: " + ex;
        }
    }

    private void createTask() {
        try {
            OdooConnect oc = OdooConnect.connect(url, port, db, username, password);

            String list = sC.getSelectedItem().toString();
            Boolean iscomp;
            if (list.equals("Individual")) {
                iscomp = true;
            } else {
                iscomp = false;
            }
            final Boolean isComp = iscomp;
            final String n = name.getText().toString();
            final String p = phone.getText().toString();

            // Create record
            @SuppressWarnings("unchecked")
            Integer idC = oc.create("res.partner", new HashMap() {{
                put("name", n);
                put("phone", p);
                put("is_company", isComp);
            }});
            msgResult += "Id of customer created: " + idC.toString();

        } catch (Exception ex) {
            msgResult = "Error: " + ex;
        }
    }


    private void deleteTask() {
        try {
            OdooConnect oc = OdooConnect.connect(url, port, db, username, password);

            String list = sD.getSelectedItem().toString();
            if (!list.isEmpty()) {
                int id = Integer.parseInt(list.substring(4, 6));

                // Delete record
                Boolean idD = oc.unlink("res.partner", new Object[]{id});
                msgResult += "Customer deleted: " + idD.toString();
            }
        } catch (Exception ex) {
            msgResult = "Error: " + ex;
        }
    }

    /*
    // Write record
    @SuppressWarnings("unchecked")
    Boolean idW = oc.write("openacademy.course", new Object[]{3}, new HashMap() {{
        put("name", "Android 1");
    }});
    */
}
