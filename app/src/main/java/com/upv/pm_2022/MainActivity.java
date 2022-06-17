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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
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
    int SiguienteID, idElem,SiguinteID2;
    CheckBox cb1,cb2;
    EditText edt1,edt2,edt3,edt4,edt5,edt6, idEdtName, idEdtDescription, idEdtBrand;
    Cursor cursor;
    final String NOMBRE_BASE_DATOS = "SuperMercado.db";
    private TextView TV1;
    private ArrayList<String> Products=new ArrayList<String>();

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    private ArrayAdapter<String> adapter;

    private ListView editListview;

    private Button BT1,BT2,BT3,BT4;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SiguienteID=0;
        SiguinteID2=0;
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

        TabHost.TabSpec spec5 = tabHost.newTabSpec("");
        spec5.setContent(R.id.tab5);
        spec5.setIndicator("Import/Export");

        tabHost.addTab(spec1);
        tabHost.addTab(spec2);
        tabHost.addTab(spec3);
        tabHost.addTab(spec4);
        tabHost.addTab(spec5);


        edt1=(EditText)findViewById(R.id.nombre);
        edt2=(EditText)findViewById(R.id.description);
        edt3=(EditText)findViewById(R.id.Brand);
        edt6=(EditText)findViewById(R.id.amount);
        edt4=(EditText)findViewById(R.id.Date);
        edt5=(EditText)findViewById(R.id.Price);
        listView = (ListView)findViewById(R.id.Listview1);

        idEdtName = (EditText) findViewById(R.id.idEdtName);
        idEdtDescription = (EditText) findViewById(R.id.idEdtDescription);
        idEdtBrand = (EditText) findViewById(R.id.idEdtBrand);

        editListview = (ListView) findViewById(R.id.idEdtLitsView);

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
        cb1=(CheckBox)findViewById(R.id.checkbox_unit);


        cb2=(CheckBox)findViewById(R.id.checkbox_kg);
        cb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cb1.isChecked()){
                    cb2.setChecked(false);
                    edt6.setEnabled(false);
                }
            }
        });
        cb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cb2.isChecked()){
                    cb1.setChecked(false);
                    edt6.setEnabled(true);
                }
            }
        });
        BT1 = (Button) findViewById (R.id.addProduct);
        BT1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub


                //Si hemos abierto correctamente la base de datos
                if(db != null)
                {
                    //Toast.makeText(MainActivity.this, "Insert" ,Toast.LENGTH_SHORT).show();
                    //Generamos los datos
                    int codigo = SiguienteID;
                    ContentValues values = new ContentValues();
                    values.put("nombre",edt1.getText().toString());
                    values.put("descripcion",edt2.getText().toString());
                    values.put("marca",edt3.getText().toString());
                    if(cb1.isChecked()){
                        values.put("tipo",cb1.getText().toString());
                        values.put("cantB",1);
                    }else if(cb2.isChecked()){
                        values.put("tipo",cb2.getText().toString());
                        values.put("cantB",edt6.getText().toString());
                    }
                    ContentValues values2=new ContentValues();
                    /*
                    int id=getIdProduct(edt1.getText().toString(),edt3.getText().toString());

                    values2.put("fecha",edt4.getText().toString());
                    values2.put("precio",Double.valueOf(edt5.getText().toString()));
                    values2.put("id_producto",id);
                    */
                    db.insert(TABLA_PRINCIPAL,null,values);

                    //ConsultaTabla_ActualizaControl();
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

        //Ver si hay contenido dentro del los productos
        if (Products != null){
            adapter=new ArrayAdapter<String>
                    (getApplicationContext(),android.R.layout.simple_list_item_1,Products);
            editListview.setAdapter(adapter);
            editListview.setScrollContainer(true);
        }
        //Funcion para poder cargar el contenido dentro del listview
        editListview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // TODO Auto-generated method stub
                String value = adapter.getItem(position);
                Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT).show();

                int pos = position +1;
                idElem = pos;

                cursor = db.rawQuery("select * from "+TABLA_PRINCIPAL+" WHERE _id ="+pos, null);

                if (cursor.getCount() != 0) {
                    if (cursor.moveToFirst()) {
                        do {
                            //C1 = cursor.getString(cursor.getColumnIndexOrThrow("_id"));

                            idEdtName.setText(cursor.getString(cursor.getColumnIndexOrThrow("nombre")));
                            idEdtDescription.setText(cursor.getString(cursor.getColumnIndexOrThrow("descripcion")));
                            idEdtBrand.setText(cursor.getString(cursor.getColumnIndexOrThrow("marca")));

                        } while (cursor.moveToNext());
                    }
                }
                cursor.close();
            }
        });

        //Boton para actualizar Producto
        BT3 = (Button) findViewById(R.id.idBtnUpdateProduct);
        BT3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                if(db != null)
                {
                    Toast.makeText(MainActivity.this, "Update" ,Toast.LENGTH_SHORT).show();
                    //Generamos los datos
                    //int codigo = SiguienteID;
                    String Args;
                    ContentValues values = new ContentValues();
                    values.put("nombre",idEdtName.getText().toString());
                    values.put("descripcion",idEdtDescription.getText().toString());
                    values.put("marca",idEdtBrand.getText().toString());

                    Args = Integer.toString(idElem);

                    db.update(TABLA_PRINCIPAL,values,"_id = ?",new String[]{Args});
                    //db.insert(TABLA_PRINCIPAL,null,values);
                    AD.setMessage("Producto actualizado");
                    AD.show();
                }

                mostrar();
                adapter=new ArrayAdapter<String>
                        (getApplicationContext(),android.R.layout.simple_list_item_1,Products);
                editListview.setAdapter(adapter);


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

    private int getIdProduct(String name, String brand) {
        String C1;
        String Fin="";
        cursor = db.rawQuery("select _id from "+TABLA_PRINCIPAL+" where nombre="+name+" and marca="+brand, null);
        Products.clear();
        if (cursor.getCount() != 0) {
            if (cursor.moveToFirst()) {
                do {
                    C1 = cursor.getString(cursor
                            .getColumnIndexOrThrow("_id"));
                    return Integer.valueOf(C1);
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        return 0;
    }


    private void mostrar() {
        String C1, C2, C3,C4,C5,C6;
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
                    C5 = cursor.getString(cursor
                            .getColumnIndexOrThrow("tipo"));
                    C6 = cursor.getString(cursor
                            .getColumnIndexOrThrow("cantB"));

                    Products.add(C1 + "-" + C2 + "-" + C3 + "-" + C4 + "-" + C5 + "-" + C6);

                } while (cursor.moveToNext());
            }
        }
        cursor.close();
    }
}
