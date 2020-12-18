package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    BD_Veiculos miDB;
    Cursor misVehiculos;
    Vehiculos vehiculo;
    ArrayList<Vehiculos> stringArrayList = new ArrayList<Vehiculos>();
    ArrayList<Vehiculos> copyStringArrayList = new ArrayList<Vehiculos>();
    ListView lvsVehiculos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FloatingActionButton btnAgregarVehiculos = (FloatingActionButton)findViewById(R.id.btnAgregarProductos);
        btnAgregarVehiculos.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                agregarVehiculo("Nuevo", new String[]{});
            }
        });
        obtenerDatosVehiculo();
        buscarProductos();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_productos, menu);

        AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo)menuInfo;
        misVehiculos.moveToPosition((adapterContextMenuInfo.position));
        menu.setHeaderTitle(misVehiculos.getString(1));
    }

    void buscarProductos(){
        final TextView tempVal = (TextView)findViewById(R.id.etBuscarProducto);
        tempVal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    stringArrayList.clear();
                    if (tempVal.getText().toString().trim().length() < 1) { //aqui no hay texto para mostrar
                        stringArrayList.addAll(copyStringArrayList);
                    } else { // Aqui hacemos la busqueda
                        for (Vehiculos am : copyStringArrayList) {
                            String nombre = am.getMarca();
                            if (nombre.toLowerCase().contains(tempVal.getText().toString().trim().toLowerCase())) {
                                stringArrayList.add(am);
                            }
                        }
                    }
                    adapterImagen adaptadorImg = new adapterImagen(getApplicationContext(), stringArrayList);
                    lvsVehiculos.setAdapter(adaptadorImg);
                }catch (Exception ex){
                    Toast.makeText(getApplicationContext(), "Error: "+ ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.mnxAgregar:
                agregarVehiculo("Nuevo", new String[]{});
                return true;

            case R.id.mnxModificar:
                String[] dataVehiculo = {
                        misVehiculos.getString(0), // Para el id vehiculo
                        misVehiculos.getString(1), // Para el marca
                        misVehiculos.getString(2), // para el modelo
                        misVehiculos.getString(3), // Para la year
                        misVehiculos.getString(4), // numeromotor
                        misVehiculos.getString(5), // numerochasis
                        misVehiculos.getString(6) // Para la URL
                };
                agregarVehiculo("Modificar", dataVehiculo);
                return true;

            case R.id.mnxEliminar:
                AlertDialog eliminarProduct =  eliminarProductos();
                eliminarProduct.show();
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    AlertDialog eliminarProductos(){
        AlertDialog.Builder confirmacion = new AlertDialog.Builder(MainActivity.this);
        confirmacion.setTitle(misVehiculos.getString(1));
        confirmacion.setMessage("Esta seguro de eliminar el Vehiculo?");
        confirmacion.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                miDB.mantenimientoVehiculos("Eliminar",new String[]{misVehiculos.getString(0)});
                obtenerDatosVehiculo();
                Toast.makeText(getApplicationContext(), "Producto eliminado exitosamente.",Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
            }
        });
        confirmacion.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getApplicationContext(), "Sea eliminado el producto.",Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
            }
        });
        return confirmacion.create();
    }

    void obtenerDatosVehiculo(){
        miDB = new BD_Veiculos(getApplicationContext(), "", null, 1);
        misVehiculos = miDB.mantenimientoVehiculos( "Consultar", null);

        if(misVehiculos.moveToFirst()){ // si hay registros en la base de datos que mostrar
            mostrarDatoVehiculos();
        }else{
            Toast.makeText(getApplicationContext(),"No hay Productos que mostrar", Toast.LENGTH_SHORT).show();

            agregarVehiculo("Nuevo", new String[]{});
        }
    }

    void agregarVehiculo(String accion, String[] dataVehiculo){
        Bundle enviarParametros = new Bundle();
        enviarParametros.putString("accion", accion);
        enviarParametros.putStringArray("dataVehiculo", dataVehiculo);
        Intent agregarVehiculos = new Intent(MainActivity.this, Agre_Vehiculos.class);
        agregarVehiculos.putExtras(enviarParametros);
        startActivity(agregarVehiculos);
    }

    void mostrarDatoVehiculos() {
        stringArrayList.clear();
        lvsVehiculos = (ListView)findViewById(R.id.lvsVehiculos);
        do {
            vehiculo = new Vehiculos(misVehiculos.getString(0), misVehiculos.getString(1),misVehiculos.getString(2), misVehiculos.getString(3), misVehiculos.getString(4),  misVehiculos.getString(5), misVehiculos.getString(6));
            stringArrayList.add(vehiculo);
        } while (misVehiculos.moveToNext());

        adapterImagen adaptadorImg = new adapterImagen(getApplicationContext(), stringArrayList);
        lvsVehiculos.setAdapter(adaptadorImg);

        copyStringArrayList.clear(); // Para hacer una limpieza de la lista
        copyStringArrayList.addAll(stringArrayList); //Para crear una copia de la lista de productos

        registerForContextMenu(lvsVehiculos);
    }
}

class Vehiculos{
    String id;
    String marca;
    String modelo;
    String year;
    String numeromotor;
    String numerochasis;
    String urlImg;

    public Vehiculos(String id, String marca, String modelo, String year, String numeromotor, String numerochasis, String urlImg) {
        this.id = id;
        this.marca = marca;
        this.modelo = modelo;
        this.year = year;
        this.numeromotor = numeromotor;
        this.numerochasis = numerochasis;
        this.urlImg = urlImg;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getNumeromotor() {
        return numeromotor;
    }

    public void setNumeromotor(String numeromotor) {
        this.numeromotor = numeromotor;
    }

    public String getNumerochasis() {
        return numerochasis;
    }

    public void setNumerochasis(String numerochasis) {
        this.numerochasis = numerochasis;
    }

    public String getUrlImg() {
        return urlImg;
    }

    public void setUrlImg(String urlImg) {
        this.urlImg = urlImg;
    }
}
