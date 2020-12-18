package com.example.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class BD_Veiculos extends SQLiteOpenHelper {

    static String nameDB = "vehiculos";

    static String tblVehiculos = "CREATE TABLE Vehiculos(idvehiculo integer primary key autoincrement, marca text, modelo text, year int, numeromotor text, numerochasis text, url text)";

    public BD_Veiculos(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, nameDB, factory, version); // nameDB = La creacion de la base de datos en SQLite
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(tblVehiculos);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    //Creamos una estructura switch para los diferentes procesos que tendra
    public Cursor mantenimientoVehiculos(String accion, String[] data){
        SQLiteDatabase sqLiteDatabaseReadable = getReadableDatabase();
        SQLiteDatabase sqLiteDatabasewritable = getWritableDatabase();
        Cursor cursor = null;
        switch (accion){
            case "Consultar":
                cursor = sqLiteDatabaseReadable.rawQuery("SELECT * FROM vehiculos ORDER BY marca ASC", null);
                break;
            case "Nuevo":
                sqLiteDatabasewritable.execSQL("INSERT INTO vehiculos(marca,modelo,year,numeromotor,numerochasis, url) VALUES('"+ data[1] +"','"+data[2]+"','"+data[3]+"','"+data[4]+"','"+data[5]+"','"+data[6]+"')");
                break;
            case "Modificar":
                sqLiteDatabasewritable.execSQL("UPDATE vehiculos SET marca='"+ data[1] +"', modelo='"+data[2]+"', year='"+data[3]+"', numeromotor='"+data[4]+"', numerochasis='"+data[5]+"', url='"+data[6]+"' WHERE idvehiculo='"+data[0]+"'");
                break;
            case "Eliminar":
                sqLiteDatabasewritable.execSQL("DELETE FROM vehiculos WHERE idvehiculo='"+ data[0] +"'");
                break;
            default:
                break;
        }
        return cursor;
    }
}

