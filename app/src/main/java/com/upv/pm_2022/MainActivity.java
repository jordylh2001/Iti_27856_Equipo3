package com.upv.pm_2022;

import static android.widget.AdapterView.*;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    final String TABLA_PRINCIPAL = "Productos";
    final String TABLA_SECUNDARIA = "Precios";
    final String TABLA_TERCIARIA= "Tickets";
    AgendaSqlite usdbh;
    AlertDialog.Builder ADX;
    AlertDialog AD;
    SQLiteDatabase db;
    int SiguienteID, idElem, SiguinteID2,idTicket;
    CheckBox cb1, cb2;
    EditText edt1, edt2, edt3, edt4, edt5, edt6,edt7,edt8, idEdtName, idEdtDescription, idEdtBrand;
    Cursor cursor,cursor2;
    final String NOMBRE_BASE_DATOS = "SuperMercado.db";
    private TextView TV1,TV2;
    private ArrayList<String> Products = new ArrayList<String>();

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    private ArrayAdapter<String> adapter;

    private ListView editListview,listView;

    private Button BT1, BT2, BT3, BT4, BT5,BT6,BT7,dateButton,dateButton2;
    private DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SiguienteID = 0;
        SiguinteID2 = 0;
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

        edt1 = (EditText) findViewById(R.id.nombre);
        edt2 = (EditText) findViewById(R.id.description);
        edt3 = (EditText) findViewById(R.id.Brand);
        edt6 = (EditText) findViewById(R.id.amount);
        edt5 = (EditText) findViewById(R.id.Price);
        edt8= (EditText) findViewById(R.id.Quantity);

        listView = (ListView) findViewById(R.id.Listview1);
        TV1=(TextView)findViewById(R.id.ActualTicket);
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
        cb1 = (CheckBox) findViewById(R.id.checkbox_unit);
        initDatePicker();
        dateButton = findViewById(R.id.datePickerButton);
        dateButton.setText(getTodaysDate());
        cb2 = (CheckBox) findViewById(R.id.checkbox_kg);
        cb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cb1.isChecked()) {
                    cb2.setChecked(false);
                    edt6.setEnabled(false);
                }
            }
        });
        cb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cb2.isChecked()) {
                    cb1.setChecked(false);
                    edt6.setEnabled(true);
                }
            }
        });
        dateButton2 = findViewById(R.id.datePickerButton2);
        dateButton2.setText(getTodaysDate());

        getIdTicket();

        //Boton Add product to DB
        BT1 = (Button) findViewById(R.id.addProduct);
        BT1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub


                //Si hemos abierto correctamente la base de datos
                if (db != null) {
                    if(verficiacion(edt1.getText().toString(), edt3.getText().toString())==false) {
                        //Toast.makeText(MainActivity.this, "Insert" ,Toast.LENGTH_SHORT).show();
                        //Generamos los datos
                        int codigo = SiguienteID;
                        ContentValues values = new ContentValues();
                        values.put("nombre", edt1.getText().toString());
                        values.put("descripcion", edt2.getText().toString());
                        values.put("marca", edt3.getText().toString());
                        if (cb1.isChecked()) {
                            values.put("tipo", cb1.getText().toString());
                            values.put("cantB", 1);
                        } else if (cb2.isChecked()) {
                            values.put("tipo", cb2.getText().toString());
                            values.put("cantB", edt6.getText().toString());
                        }
                        db.insert(TABLA_PRINCIPAL, null, values);
                    }
                    ContentValues values2 = new ContentValues();
                    int id = getIdProduct(edt1.getText().toString(), edt3.getText().toString());

                    values2.put("fecha", getDate(dateButton.getText().toString()));
                    values2.put("precio", Double.valueOf(edt5.getText().toString()));
                    values2.put("id_producto", id);
                    db.insert(TABLA_SECUNDARIA, null, values2);

                    //ConsultaTabla_ActualizaControl();
                    AD.setMessage(values2.toString());
                    AD.show();
                    AD.setMessage("Insertando un producto");
                    AD.show();
                }

            }
        });
        //Boton Add product to ticket
        BT7=(Button)findViewById(R.id.addTicket) ;
        BT7.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                if (db != null) {
                    ContentValues values = new ContentValues();
                    values.put("_id",idTicket);
                    values.put("fecha",edt7.getText().toString());
                    values.put("cuantity",edt8.getText().toString());
                    int id = getIdProduct(edt1.getText().toString(), edt3.getText().toString());
                    values.put("id_producto",id);


                    Toast.makeText(MainActivity.this, values.toString(), Toast.LENGTH_SHORT).show();
                    db.insert(TABLA_TERCIARIA, null, values);

                }

            }
        });

        //Boton New Ticket
        BT6=(Button)findViewById(R.id.newTicket);
        BT6.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                idTicket+=1;
                TV1.setText("Ticket Actual: "+idTicket);
            }
        });

        //Boton Show Products
        BT2 = (Button) findViewById(R.id.loadProducts);
        BT2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                //Si hemos abierto correctamente la base de datos
                if (db != null) {
                    mostrar();
                    adapter = new ArrayAdapter<String>
                            (getApplicationContext(), android.R.layout.simple_list_item_1, Products);
                    listView.setAdapter(adapter);
                }

            }
        });
        //Ver si hay contenido dentro del los productos
        if (Products != null) {
            adapter = new ArrayAdapter<String>
                    (getApplicationContext(), android.R.layout.simple_list_item_1, Products);
            editListview.setAdapter(adapter);
            editListview.setScrollContainer(true);
        }
        //Funcion para poder cargar el contenido dentro del listview
        editListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // TODO Auto-generated method stub
                String value = adapter.getItem(position);
                Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT).show();


                String[] split = value.split("/");
                idElem = Integer.valueOf(split[0]);
                System.out.println("Done");
                cursor = db.rawQuery("select * from " + TABLA_PRINCIPAL + " WHERE _id =" + idElem, null);

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

        //Boton Update Product
        BT3 = (Button) findViewById(R.id.idBtnUpdateProduct);
        BT3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                if (db != null) {
                    Toast.makeText(MainActivity.this, "Update", Toast.LENGTH_SHORT).show();
                    //Generamos los datos
                    //int codigo = SiguienteID;
                    String Args;
                    ContentValues values = new ContentValues();
                    values.put("nombre", idEdtName.getText().toString());
                    values.put("descripcion", idEdtDescription.getText().toString());
                    values.put("marca", idEdtBrand.getText().toString());

                    Args = Integer.toString(idElem);

                    db.update(TABLA_PRINCIPAL, values, "_id = ?", new String[]{Args});
                    //db.insert(TABLA_PRINCIPAL,null,values);
                    AD.setMessage("Producto actualizado");
                    AD.show();
                }
                mostrar();
                adapter = new ArrayAdapter<String>
                        (getApplicationContext(), android.R.layout.simple_list_item_1, Products);
                editListview.setAdapter(adapter);


            }
        });

        TV2=(TextView)findViewById(R.id.Tv2);
        //Boton Analyze expenses
        BT5 = (Button) findViewById(R.id.Graficar);
        BT5.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                mostrarTickets();
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

    private String getDate(String date) {
        String fecha="";
        String[] split=date.split(" ");
        fecha=split[2];
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";
        switch (split[0]){
            case "JAN":
                fecha+="01";
                break;
            case "FEB":
                break;
            case "MAR":
                break;
            case "APR":
                break;
            case "MAY":
                break;
            case "JUN":
                break;
            case "JUL":
                break;
            case "AUG":
                break;
            case "SEP":
                break;
            case "OCT":
                break;
            case "NOV":
                break;
            case "DEC":
                break;
            default:

        }
        return fecha;
    }

    private void mostrarTickets() {
        String C1, C2, C3, C4;
        cursor = db.rawQuery("select * from " + TABLA_TERCIARIA, null);
        if (cursor.getCount() != 0) {
            if (cursor.moveToFirst()) {
                do {
                    C1 = cursor.getString(cursor
                            .getColumnIndexOrThrow("_id"));
                    C2 = cursor.getString(cursor
                            .getColumnIndexOrThrow("fecha"));
                    C3 = cursor.getString(cursor
                            .getColumnIndexOrThrow("cuantity"));
                    C4 = cursor.getString(cursor
                            .getColumnIndexOrThrow("id_producto"));
                    TV2.append(C1 + "-" + C2 + "-" + C3 + "-" + C4 +"\n" );
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
    }


    private String getTodaysDate()
    {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    private void initDatePicker()
    {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                month = month + 1;
                String date = makeDateString(day, month, year);
                dateButton.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        //datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

    }

    private String makeDateString(int day, int month, int year)
    {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month)
    {
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";

        //default should never happen
        return "JAN";
    }

    public void openDatePicker(View view)
    {
        datePickerDialog.show();
    }

    private void getIdTicket() {
        cursor = db.rawQuery("select * from " + TABLA_TERCIARIA , null);
        if (cursor.getCount() != 0) {
            idTicket=cursor.getCount()+1;
        }else{
            idTicket=1;
        }
        TV1.setText("Ticket actual: "+idTicket);
        cursor.close();
    }

    private boolean verficiacion(String name, String brand) {
        boolean aux=false;
        String C1;
        String Fin = "";
        cursor = db.rawQuery("select * from " + TABLA_PRINCIPAL + " where nombre='" + name + "' and marca='" + brand + "'", null);
        Products.clear();
        if (cursor.getCount() != 0) {
            aux=true;
            return aux;
        }
        cursor.close();
        return aux;
    }

    private int getIdProduct(String name, String brand) {
        String C1;
        String Fin = "";
        cursor = db.rawQuery("select * from " + TABLA_PRINCIPAL + " where nombre='" + name + "' and marca='" + brand + "'", null);
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
        String C1, C2, C3, C4,C6,C7;
        cursor = db.rawQuery("select * from " + TABLA_PRINCIPAL, null);
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
                    C4 = cursor.getString(cursor
                            .getColumnIndexOrThrow("marca"));
                    TV2.append(C1 + "-" + C2 + "-" + C3 + "-" + C4 +"\n" );
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
    }
}
