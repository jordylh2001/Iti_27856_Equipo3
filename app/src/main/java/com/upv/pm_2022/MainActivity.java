package com.upv.pm_2022;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.opencsv.CSVWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ID_READ_PERMISSION = 100;
    private static final int REQUEST_ID_WRITE_PERMISSION = 200;

    final String TABLA_PRINCIPAL = "Productos";
    final String TABLA_SECUNDARIA = "Precios";
    final String TABLA_TERCIARIA = "Tickets";
    public String actualfilepath = "";
    AgendaSqlite usdbh;
    AlertDialog.Builder ADX;
    AlertDialog AD;
    SQLiteDatabase db;
    int SiguienteID, idElem, SiguinteID2, idTicket = 0, SiguinteID3, importaux = 0;
    CheckBox cb1, cb2,cb3,cb4;
    EditText edt1, edt2, edt3, edt4, edt5, edt6, edt7, edt8, idEdtName, idEdtDescription, idEdtBrand,idEdAmount,idEdPrice;
    Cursor cursor, cursor2;
    final String NOMBRE_BASE_DATOS = "SuperMercado.db";
    private TextView TV1, TV2;
    private ArrayList<String> Products = new ArrayList<String>();

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    private ArrayAdapter<String> adapter;

    private ListView editListview, listView;
    private int request_code = 1, FILE_SELECT_CODE = 101;
    private String TAG = "mainactivity",fechaElem;

    private Button BT1, BT2, BT3, BT4, BT5, BT6, BT7, dateButton, dateButton2,dateButton3, BT8, BT9, BT10, BT11, BT12, BT13;
    private DatePickerDialog datePickerDialog,datePickerDialog2,datePickerDialog3;

    static String startingDir = Environment.getExternalStorageDirectory().toString();
    String Ruta;
    InputStream inputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        askPermissionOnly();

        SiguienteID = 0;
        SiguinteID2 = 0;
        SiguinteID3 = 0;
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
        edt8 = (EditText) findViewById(R.id.Quantity);

        listView = (ListView) findViewById(R.id.Listview1);
        TV1 = (TextView) findViewById(R.id.ActualTicket);
        idEdtName = (EditText) findViewById(R.id.idEdtName);
        idEdtDescription = (EditText) findViewById(R.id.idEdtDescription);
        idEdtBrand = (EditText) findViewById(R.id.idEdtBrand);
        idEdAmount = (EditText) findViewById(R.id.idEdtAmount);
        idEdPrice = (EditText) findViewById(R.id.idEdtPrice);
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
        initDatePicker2();
        initDatePicker3();
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
        cb3 = (CheckBox) findViewById(R.id.checkbox_unit2);
        cb3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cb3.isChecked()) {
                    cb4.setChecked(false);
                    idEdAmount.setText("");
                    idEdAmount.setEnabled(false);
                }
            }
        });
        cb4 = (CheckBox) findViewById(R.id.checkbox_kg2);
        cb4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cb4.isChecked()) {
                    cb3.setChecked(false);
                    idEdAmount.setEnabled(true);
                }
            }
        });
        dateButton2 = findViewById(R.id.datePickerButton2);
        dateButton2.setText(getTodaysDate());

        dateButton3 = findViewById(R.id.datePickerButton3);
        dateButton3.setText(getTodaysDate());


        //Boton Add product to DB
        BT1 = (Button) findViewById(R.id.addProduct);
        BT1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub


                //Si hemos abierto correctamente la base de datos
                if (db != null) {
                    if (verficiacion(edt1.getText().toString(), edt3.getText().toString()) == false) {
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
        BT7 = (Button) findViewById(R.id.addTicket);
        BT7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (db != null) {
                    ContentValues values = new ContentValues();
                    values.put("numTicket", idTicket);
                    values.put("fecha", getDate(dateButton2.getText().toString()));
                    values.put("cuantity", edt8.getText().toString());
                    int id = getIdProduct(edt1.getText().toString(), edt3.getText().toString());
                    values.put("id_producto", id);


                    Toast.makeText(MainActivity.this, values.toString(), Toast.LENGTH_SHORT).show();
                    db.insert(TABLA_TERCIARIA, null, values);

                }

            }
        });

        //Boton New Ticket
        BT6 = (Button) findViewById(R.id.newTicket);
        BT6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                idTicket += 1;
                TV1.setText("Ticket actual: " + idTicket);
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
                fechaElem=split[6];
                System.out.println("Done");
                cursor = db.rawQuery("select * from " + TABLA_PRINCIPAL + " WHERE _id =" + idElem, null);

                if (cursor.getCount() != 0) {
                    if (cursor.moveToFirst()) {
                        do {
                            //C1 = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
                            String name,desc,brand,C2,amount,fecha,price;
                            int month,day,year;

                            name=cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
                            desc=cursor.getString(cursor.getColumnIndexOrThrow("descripcion"));
                            brand=cursor.getString(cursor.getColumnIndexOrThrow("marca"));
                            C2=cursor.getString(cursor.getColumnIndexOrThrow("tipo"));
                            amount=cursor.getString(cursor.getColumnIndexOrThrow("cantB"));
                            fecha=split[6];
                            String[] split2=fecha.split("-");
                            year=Integer.valueOf(split2[0]);
                            month=Integer.valueOf(split2[1]);
                            day=Integer.valueOf(split2[2]);
                            price=split[7];
                            idEdtName.setText(name);
                            idEdtDescription.setText(desc);
                            idEdtBrand.setText(brand);

                            if(C2.equals("Unit")){
                                cb3.setChecked(true);
                            }else if(C2.equals("Kg")){
                                cb4.setChecked(true);
                                idEdAmount.setText(amount);
                            }
                            idEdPrice.setText(price.replace("$",""));
                            dateButton3.setText(makeDateString(day,month,year));

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
                    //Toast.makeText(MainActivity.this, "Update", Toast.LENGTH_SHORT).show();
                    //Generamos los datos
                    //int codigo = SiguienteID;
                    String Args;
                    ContentValues values = new ContentValues();
                    values.put("nombre", idEdtName.getText().toString());
                    values.put("descripcion", idEdtDescription.getText().toString());
                    values.put("marca", idEdtBrand.getText().toString());
                    if(cb3.isChecked()){
                        values.put("tipo",cb3.getText().toString());
                        values.put("cantB", 1);
                    }else if(cb4.isChecked()){
                        values.put("tipo",cb4.getText().toString());
                        values.put("cantB", idEdAmount.getText().toString());
                    }
                    Args = Integer.toString(idElem);
                    db.update(TABLA_PRINCIPAL, values, "_id = ?", new String[]{Args});
                    ContentValues values2 = new ContentValues();
                    values2.put("fecha",getDate(dateButton3.getText().toString()));
                    values2.put("precio",idEdPrice.getText().toString());
                    db.update(TABLA_SECUNDARIA,values2,  "_id = ? and fecha = ?", new String[]{Args, fechaElem});

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

        TV2 = (TextView) findViewById(R.id.Tv2);
        //Boton Analyze expenses
        BT5 = (Button) findViewById(R.id.Graficar);
        BT5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mostrarTickets();
            }
        });

        //Boton Export Productos
        BT8 = (Button) findViewById(R.id.exportProducto);
        BT8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                File exportDir = new File(Environment.getExternalStorageDirectory(), "");
                if (!exportDir.exists()) {
                    exportDir.mkdirs();
                }

                File file = new File(exportDir, "Productos.csv");
                try {
                    file.createNewFile();
                    CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
                    Cursor curCSV = db.rawQuery("select * from " + TABLA_PRINCIPAL, null);
                    csvWrite.writeNext(curCSV.getColumnNames());
                    while (curCSV.moveToNext()) {
                        //Which column you want to exprort
                        String arrStr[] = {curCSV.getString(0), curCSV.getString(1), curCSV.getString(2), curCSV.getString(3),
                                curCSV.getString(4), curCSV.getString(5)};
                        csvWrite.writeNext(arrStr);
                    }
                    csvWrite.close();
                    curCSV.close();
                    Toast.makeText(getApplicationContext(), "Se exporto Productos exitosamente", Toast.LENGTH_SHORT).show();
                } catch (Exception sqlEx) {
                    Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
                }

            }
        });

        //Boton Export Precios
        BT9 = (Button) findViewById(R.id.exportPrices);
        BT9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                File exportDir = new File(Environment.getExternalStorageDirectory(), "");
                if (!exportDir.exists()) {
                    exportDir.mkdirs();
                }

                File file = new File(exportDir, "Precios.csv");
                try {
                    file.createNewFile();
                    CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
                    Cursor curCSV = db.rawQuery("select * from " + TABLA_SECUNDARIA, null);
                    csvWrite.writeNext(curCSV.getColumnNames());
                    while (curCSV.moveToNext()) {
                        //Which column you want to exprort
                        String arrStr[] = {curCSV.getString(0), curCSV.getString(1), curCSV.getString(2),
                                curCSV.getString(3)};
                        csvWrite.writeNext(arrStr);
                    }
                    csvWrite.close();
                    curCSV.close();
                    Toast.makeText(getApplicationContext(), "Se exporto Precios exitosamente", Toast.LENGTH_SHORT).show();
                } catch (Exception sqlEx) {
                    Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
                }

            }
        });

        //Boton Exporta Tickets
        BT10 = (Button) findViewById(R.id.exportTicekts);
        BT10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                File exportDir = new File(Environment.getExternalStorageDirectory(), "");
                if (!exportDir.exists()) {
                    exportDir.mkdirs();
                }

                File file = new File(exportDir, "Tickets.csv");
                try {
                    file.createNewFile();
                    CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
                    Cursor curCSV = db.rawQuery("select * from " + TABLA_TERCIARIA, null);
                    csvWrite.writeNext(curCSV.getColumnNames());
                    while (curCSV.moveToNext()) {
                        //Which column you want to exprort
                        String arrStr[] = {curCSV.getString(0), curCSV.getString(1), curCSV.getString(2),
                                curCSV.getString(3),curCSV.getString(4)};
                        csvWrite.writeNext(arrStr);
                    }
                    csvWrite.close();
                    curCSV.close();
                    Toast.makeText(getApplicationContext(), "Se exporto Tickets exitosamente", Toast.LENGTH_SHORT).show();
                } catch (Exception sqlEx) {
                    Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
                }

            }
        });

        //Boton Importar Productos
        BT11 = (Button) findViewById(R.id.importProducto);
        BT11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                importaux = 1;
                showFileChooser();


                //Toast.makeText(getApplicationContext(), "Se exporto Tickets exitosamente", Toast.LENGTH_SHORT).show();

            }
        });

        //Boton Importar Productos
        BT12 = (Button) findViewById(R.id.importPrices);
        BT12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                importaux = 2;
                showFileChooser();


                //Toast.makeText(getApplicationContext(), "Se exporto Tickets exitosamente", Toast.LENGTH_SHORT).show();

            }
        });

        //Boton Importar Productos
        BT13 = (Button) findViewById(R.id.importTicekts);
        BT13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                importaux = 3;
                showFileChooser();


                //Toast.makeText(getApplicationContext(), "Se exporto Tickets exitosamente", Toast.LENGTH_SHORT).show();

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

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
        } catch (Exception e) {
            Log.e(TAG, " choose file error " + e.toString());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String fullerror = "";
        if (requestCode == FILE_SELECT_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    InputStream stream = null;
                    String tempID = "", id = "";
                    Uri uri = data.getData();
                    //Toast.makeText(this, uri.getPath(), Toast.LENGTH_SHORT).show();
                    BufferedReader br;
                    //System.out.println(uri.getPath());
                    br = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(uri)));
                    //WHAT TODO ? Is this creates new file with
                    //the name NewFileName on internal app storage?
                    String line = null;
                    int aux = 0;
                    while ((line = br.readLine()) != null) {
                        if (aux != 0) {
                            if (importaux == 1) {
                                importProduct(line);
                            }
                            if (importaux == 2) {
                                importPrices(line);
                            }
                            if (importaux == 3) {
                                importTicket(line);
                            }
                        } else {
                            aux += 1;
                        }
                    }
                    br.close();
                } catch (Exception e) {
                    Log.e(TAG, " read errro " + e.toString());
                }
            }
        }
    }

    private void importTicket(String line) {
        String[] split = line.split(",");
        //System.out.println(split[1]+" - "+split[2]+" - "+split[3]+"\n");
        if (db != null) {
            ContentValues values2 = new ContentValues();
            int id = Integer.parseInt(((split[4].trim()).replace('"', ' ')).replace(" ", ""));
            values2.put("numTicket", ((split[1].trim()).replace('"', ' ')).replace(" ", ""));
            values2.put("fecha", ((split[2].trim()).replace('"', ' ')).replace(" ", ""));
            values2.put("cuantity", ((split[3].trim()).replace('"', ' ')).replace(" ", ""));
            values2.put("id_producto", id);
            db.insert(TABLA_TERCIARIA, null, values2);

            /*


             */
            //Toast.makeText(this, split.toString(), Toast.LENGTH_SHORT).show();
            AD.setMessage("Tabla importada");
            AD.show();
        }
    }

    private void importPrices(String line) {
        String[] split = line.split(",");
        //System.out.println(split[1]+" - "+split[2]+" - "+split[3]+"\n");
        if (db != null) {
            ContentValues values2 = new ContentValues();
            int id = Integer.parseInt(((split[3].trim()).replace('"', ' ')).replace(" ", ""));
            System.out.println(id);
            values2.put("fecha", ((split[1].trim()).replace('"', ' ')).replace(" ", ""));
            values2.put("precio", ((split[2].trim()).replace('"', ' ')).replace(" ", ""));
            values2.put("id_producto", id);

            db.insert(TABLA_SECUNDARIA, null, values2);

            /*


             */
            //Toast.makeText(this, split.toString(), Toast.LENGTH_SHORT).show();
            AD.setMessage("Tabla importada");
            AD.show();
        }
    }

    private void importProduct(String line) {
        if (db != null) {
            String[] split = line.split(",");
            ContentValues values = new ContentValues();
            System.out.println(split[1] + " - " + split[2] + " - " + split[3] + " - " + split[4] + " - " + split[5] + "\n");
            values.put("nombre", ((split[1].trim()).replace('"', ' ')).replace(" ", ""));
            values.put("descripcion", ((split[2].trim()).replace('"', ' ')).replace(" ", ""));
            values.put("marca",((split[3].trim()).replace('"', ' ')).replace(" ", ""));
            values.put("tipo", ((split[4].trim()).replace('"', ' ')).replace(" ", ""));
            values.put("cantB", ((split[5].trim()).replace('"', ' ')).replace(" ", ""));
            //Toast.makeText(this, split.toString(), Toast.LENGTH_SHORT).show();
            db.insert(TABLA_PRINCIPAL, null, values);
            AD.setMessage("Tabla importada");
            AD.show();
        }
    }

    private String getDate(String date) {
        String fecha = "";
        String[] split = date.split(" ");
        fecha = split[2];
        switch (split[0]) {
            case "JAN":
                fecha += "-01";
                break;
            case "FEB":
                fecha += "-02";
                break;
            case "MAR":
                fecha += "-03";
                break;
            case "APR":
                fecha += "-04";
                break;
            case "MAY":
                fecha += "-05";
                break;
            case "JUN":
                fecha += "-06";
                break;
            case "JUL":
                fecha += "-07";
                break;
            case "AUG":
                fecha += "-08";
                break;
            case "SEP":
                fecha += "-09";
                break;
            case "OCT":
                fecha += "-10";
                break;
            case "NOV":
                fecha += "-11";
                break;
            case "DEC":
                fecha += "-12";
                break;
            default:

        }
        fecha += "-" + split[1];
        return fecha;
    }

    private void mostrarTickets() {
        String C1, C2, C3, C4, C5;
        cursor = db.rawQuery("select * from " + TABLA_TERCIARIA, null);
        TV2.setText("");
        if (cursor.getCount() != 0) {
            if (cursor.moveToFirst()) {
                do {
                    C1 = cursor.getString(cursor
                            .getColumnIndexOrThrow("_id"));
                    C2 = cursor.getString(cursor
                            .getColumnIndexOrThrow("numTicket"));
                    C3 = cursor.getString(cursor
                            .getColumnIndexOrThrow("fecha"));
                    C4 = cursor.getString(cursor
                            .getColumnIndexOrThrow("cuantity"));
                    C5 = cursor.getString(cursor
                            .getColumnIndexOrThrow("id_producto"));
                    TV2.append(C1 + "-" + C2 + "-" + C3 + "-" + C4 + "-" + C5 + "\n");
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
    }


    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
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

    private void initDatePicker2() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker2, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(day, month, year);
                dateButton2.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog2 = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        //datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

    }

    private void initDatePicker3() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker2, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(day, month, year);
                dateButton3.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog3 = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        //datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

    }

    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month) {
        if (month == 1)
            return "JAN";
        if (month == 2)
            return "FEB";
        if (month == 3)
            return "MAR";
        if (month == 4)
            return "APR";
        if (month == 5)
            return "MAY";
        if (month == 6)
            return "JUN";
        if (month == 7)
            return "JUL";
        if (month == 8)
            return "AUG";
        if (month == 9)
            return "SEP";
        if (month == 10)
            return "OCT";
        if (month == 11)
            return "NOV";
        if (month == 12)
            return "DEC";

        //default should never happen
        return "JAN";
    }

    public void openDatePicker(View view) {
        datePickerDialog.show();
    }

    public void openDatePicker2(View view) {
        datePickerDialog2.show();
    }
    public void openDatePicker3(View view) {
        datePickerDialog3.show();
    }

    private void getIdTicket() {
        cursor = db.rawQuery("select * from " + TABLA_TERCIARIA, null);
        if (cursor.getCount() != 0) {
            idTicket = cursor.getCount() + 1;
        } else {
            idTicket = 1;
        }
        TV1.setText("Ticket actual: " + idTicket);
        cursor.close();
    }

    private boolean verficiacion(String name, String brand) {
        boolean aux = false;
        String C1;
        String Fin = "";
        cursor = db.rawQuery("select * from " + TABLA_PRINCIPAL + " where nombre='" + name + "' and marca='" + brand + "'", null);
        Products.clear();
        if (cursor.getCount() != 0) {
            aux = true;
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
        String C1, C2, C3, C4, C5, C6, C7, C8,aux;
        String Fin = "";
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
                    C5 = cursor.getString(cursor
                            .getColumnIndexOrThrow("tipo"));
                    C6 = cursor.getString(cursor
                            .getColumnIndexOrThrow("cantB"));
                    //Toast.makeText(this, "llego a la tabla prueba1", Toast.LENGTH_SHORT).show();
                    cursor2 = db.rawQuery("select * from " + TABLA_SECUNDARIA , null);
                    if (cursor2.getCount() != 0) {
                        if (cursor2.moveToFirst()) {
                            do {
                                //Toast.makeText(this, "llego a la tabla 2", Toast.LENGTH_SHORT).show();
                                /*
                                aux = cursor2.getString(cursor2
                                        .getColumnIndexOrThrow("_id"));
                                 */

                                C7 = cursor2.getString(cursor2
                                        .getColumnIndexOrThrow("fecha"));
                                C8 = cursor2.getString(cursor2
                                        .getColumnIndexOrThrow("precio"));

                                aux = cursor2.getString(cursor2
                                        .getColumnIndexOrThrow("id_producto"));
                                //Toast.makeText(this, "C1="+ C1 +" Aux="+aux , Toast.LENGTH_SHORT).show();
                                if(aux.equals(C1)){
                                    Products.add(C1 + "/" + C2 + "/" + C3 + "/" + C4  + "/" + C5 + "/" + C6 + "/" + C7 + "/" + C8+"$");

                                }
                            } while (cursor2.moveToNext());
                        }
                    }
                    cursor2.close();
                    //Products.add(C1 + "-" + C2 + "-" + C3 + "-" + C4 + "-" + C5 + "-" + C6 + "-"  );
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
    }


    private void askPermissionOnly() {
        this.askPermission(REQUEST_ID_WRITE_PERMISSION,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        this.askPermission(REQUEST_ID_READ_PERMISSION,
                android.Manifest.permission.READ_EXTERNAL_STORAGE);

    }


    // With Android Level >= 23, you have to ask the user
    // for permission with device (For example read/write data on the device).
    private boolean askPermission(int requestId, String permissionName) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {

            // Check if we have permission
            int permission = ActivityCompat.checkSelfPermission(this, permissionName);


            if (permission != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                // If don't have permission so prompt the user.
                this.requestPermissions(
                        new String[]{permissionName},
                        requestId
                );
                return false;
            }
        }
        return true;
    }

    // When you have the request results
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //
        // Note: If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0) {
            switch (requestCode) {
                case REQUEST_ID_READ_PERMISSION: {
                    if (grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getApplicationContext(), "Permission Lectura Concedido!", Toast.LENGTH_SHORT).show();
                    }
                }
                case REQUEST_ID_WRITE_PERMISSION: {
                    if (grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getApplicationContext(), "Permission Escritura Concedido!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), "Permission Cancelled!", Toast.LENGTH_SHORT).show();
        }
    }

}
