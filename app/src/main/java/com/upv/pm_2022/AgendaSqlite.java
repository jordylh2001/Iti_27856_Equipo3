package com.upv.pm_2022;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class AgendaSqlite extends SQLiteOpenHelper {

    //
    //private static String DB_PATH = "/data/data/YOUR_PACKAGE/databases/";
//	private static String DB_PATH = "/sdcard/";
//    private static String DB_NAME = "Experimento01";

    //Sentencia SQL para crear la tabla de Usuarios
    String sqlCreate   = "CREATE TABLE Productos       (_id INTEGER PRIMARY KEY, nombre TEXT,descripcion TEXT,marca TEXT,tipo TEXT,cantB REAL)";
    String sqlCreate2  = "CREATE TABLE Precios     (_id INTEGER PRIMARY KEY, fecha TEXT, precio REAL, id_producto REFERENCES Productos(_id))";
    String sqlCreate3  = "CREATE TABLE Tickets     (_id INTEGER PRIMARY KEY, fecha TEXT,cuantity REAL, id_producto REFERENCES Productos(_id))";

    public AgendaSqlite(Context context, String name, CursorFactory factory,int version) {
        super(context, name, factory, version);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Se ejecuta la sentencia SQL de creaci�n de la tabla
        db.execSQL(sqlCreate);
        db.execSQL(sqlCreate2);
        db.execSQL(sqlCreate3);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //eliminamos la version anterior de la tabla
        db.execSQL("DROP TABLE IF EXISTS Productos");
        db.execSQL("DROP TABLE IF EXISTS Precios");
        db.execSQL("DROP TABLE IF EXISTS Tickets");

        //aqu� creamos la nueva versi�n de la tabla
        db.execSQL(sqlCreate);
        db.execSQL(sqlCreate2);
        db.execSQL(sqlCreate3);

    }

}
