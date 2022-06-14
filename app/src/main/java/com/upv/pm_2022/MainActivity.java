package com.upv.pm_2022;

import static android.widget.AdapterView.*;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    final String TABLA_PRINCIPAL = "Productos";
    final String TABLA_SECUNDARIA= "Precios";
    //final String TABLA_TERCIARIA= "Precios";
    AgendaSqlite usdbh;
    AlertDialog.Builder ADX;
    AlertDialog AD;
    SQLiteDatabase db;
    int SiguienteID;
    EditText edt1,edt2,edt3,edt4,edt5;
    Cursor cursor;
    final String NOMBRE_BASE_DATOS = "SuperMercado.db";
    private TextView TV1;
    private ArrayList<String> Products=new ArrayList<String>();

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    private ArrayAdapter<String> adapter;

    private Button BT1,BT2,BT3,BT4;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SiguienteID=0;
        //cargar();
        // Importante: Esto va antes de instanciar controles dentro de cada pestaña
        // Agregar las pestañas---
        Resources res = getResources();
        TabHost tabHost = (TabHost) findViewById(R.id.tabhost);
        tabHost.setup();

        TabHost.TabSpec spec1 = tabHost.newTabSpec("");
        spec1.setContent(R.id.tab1);
        spec1.setIndicator("Add");


        TabHost.TabSpec spec2 = tabHost.newTabSpec("");
        spec2.setContent(R.id.tab2);
        spec2.setIndicator("Show");

        TabHost.TabSpec spec3 = tabHost.newTabSpec("");
        spec3.setContent(R.id.tab3);
        spec3.setIndicator("Edit");

        TabHost.TabSpec spec4 = tabHost.newTabSpec("");
        spec4.setContent(R.id.tab4);
        spec4.setIndicator("Ticket");

        tabHost.addTab(spec1);
        tabHost.addTab(spec2);
        tabHost.addTab(spec3);
        tabHost.addTab(spec4);

        edt1=(EditText)findViewById(R.id.nombre);
        edt2=(EditText)findViewById(R.id.description);
        edt3=(EditText)findViewById(R.id.Brand);
        edt4=(EditText)findViewById(R.id.Date);
        edt5=(EditText)findViewById(R.id.Price);
        TV1=(TextView)findViewById(R.id.TV2);
        listView = (ListView)findViewById(R.id.Listview1);
        ADX = new AlertDialog.Builder(this);
        AD = ADX.create();
        String ArchivoDB = NOMBRE_BASE_DATOS;
        // Guarda el archivo de labase de datos en el directorio RAIZ (o cualquier ruta de usuario)
        //String ArchivoDB = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+NOMBRE_BASE_DATOS;

        usdbh = new AgendaSqlite(this, ArchivoDB, null, 1);
        db = usdbh.getWritableDatabase();
        /*
        adapter=new ArrayAdapter<String>
                (getApplicationContext(),android.R.layout.simple_list_item_1,Products);
        listView.setAdapter(adapter );

         */
        BT1 = (Button) findViewById (R.id.addProduct);
        BT1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub


                //Si hemos abierto correctamente la base de datos
                if(db != null)
                {
                    Toast.makeText(MainActivity.this, "Insert" ,Toast.LENGTH_SHORT).show();
                    //Generamos los datos
                    int codigo = SiguienteID;
                    ContentValues values = new ContentValues();
                    values.put("nombre",edt1.getText().toString());
                    values.put("descripcion",edt2.getText().toString());
                    values.put("marca",edt3.getText().toString());
                    db.insert(TABLA_PRINCIPAL,null,values);
                    ConsultaTabla_ActualizaControl();
                    AD.setMessage("Insertando un producto");
                    AD.show();
                }

            }
        });
        BT2 = (Button) findViewById (R.id.loadProducts);
        BT2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                //Si hemos abierto correctamente la base de datos
                if(db != null){
                    mostrar();
                    adapter=new ArrayAdapter<String>
                            (getApplicationContext(),android.R.layout.simple_list_item_1,Products);
                    listView.setAdapter(adapter );
                }

            }
        });
        /*
        // Creamos el método OnItem
        listView.setOnClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = adapterView.getItemAtPosition(i).toString();
                Log.d(,"onItemClick: Has clickeado en: " + name);
            }
        });
         */



        //adapter.notifyDataSetChanged();

    }

    private void cargar() {
        String C1, C2, C3,C4;
        String Fin="";
        cursor = db.rawQuery("select * from "+TABLA_PRINCIPAL, null);

        if (cursor.getCount() != 0) {
            if (cursor.moveToFirst()) {
                do {
                    C1 = cursor.getString(cursor
                            .getColumnIndexOrThrow("_id"));

                    C2 = cursor.getString(cursor
                            .getColumnIndexOrThrow("nombre"));
                    C3 = cursor.getString(cursor
                            .getColumnIndexOrThrow("descripcion"));
                    C4 = cursor.getString(cursor
                            .getColumnIndexOrThrow("marca"));

                    Products.add(C1 + "-" + C2 + "-" + C3 + "-" + C4);

                } while (cursor.moveToNext());
            }
        }
        cursor.close();
    }

    private void mostrar() {
        String C1, C2, C3,C4;
        String Fin="";
        cursor = db.rawQuery("select * from "+TABLA_PRINCIPAL, null);
        Products.clear();
        if (cursor.getCount() != 0) {
            if (cursor.moveToFirst()) {
                do {
                    C1 = cursor.getString(cursor
                            .getColumnIndexOrThrow("_id"));

                    C2 = cursor.getString(cursor
                            .getColumnIndexOrThrow("nombre"));
                    C3 = cursor.getString(cursor
                            .getColumnIndexOrThrow("descripcion"));
                    C4 = cursor.getString(cursor
                            .getColumnIndexOrThrow("marca"));

                    Products.add(C1 + "-" + C2 + "-" + C3 + "-" + C4);

                } while (cursor.moveToNext());
            }
        }
        cursor.close();
    }

    void ConsultaTabla_ActualizaControl ()
    {
        String C1, C2, C3,C4;
        String Fin="";
        cursor = db.rawQuery("select * from "+TABLA_PRINCIPAL, null);

        if (cursor.getCount() != 0) {
            if (cursor.moveToFirst()) {
                do {
                    C1 = cursor.getString(cursor
                            .getColumnIndexOrThrow("_id"));

                    C2 = cursor.getString(cursor
                            .getColumnIndexOrThrow("nombre"));
                    C3 = cursor.getString(cursor
                            .getColumnIndexOrThrow("descripcion"));
                    C4 = cursor.getString(cursor
                            .getColumnIndexOrThrow("marca"));

                    Fin += C1 + "-" + C2 + "-" + C3 + "-" + C4 + "\n";

                } while (cursor.moveToNext());
            }
            TV1.setText(Fin);
        }
        cursor.close();
    }
}