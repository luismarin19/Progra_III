package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Agre_Vehiculos extends AppCompatActivity {
    BD_Veiculos miDB;
    String accion = "Nuevo";
    String idVehiculo = "0";
    ImageView imgFotoProducto;
    //Galeria
    ImageView imgGaleriaProducto;

    String urlCompletaImg;
    Button btnVehiculos;
    Intent takePictureIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agre__vehiculos);
        imgFotoProducto = (ImageView)findViewById(R.id.imgPhotoProducto);
        //galeria
        imgGaleriaProducto = (ImageView)findViewById(R.id.imgGaleriaProducto);

        btnVehiculos = (Button) findViewById(R.id.btnMostrarVehiculos);
        btnVehiculos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarListaVehiculos();
            }
        });
        guardarDatosVehiculos();
        mostrarDatosVehiculos();
        tomarFotoProducto();
        TomarGaleriaProducto();
    }

    //Galeria
    void TomarGaleriaProducto(){
        imgGaleriaProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentGaleri = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentGaleri,100);
            }
        });
    }

    void  tomarFotoProducto(){
        imgFotoProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    //guardando la imagen
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    }catch (Exception ex){}
                    if (photoFile != null) {
                        try {
                            Uri photoURI = FileProvider.getUriForFile(Agre_Vehiculos.this, "com.example.myapplication.fileprovider", photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(takePictureIntent, 1);
                        }catch (Exception ex){
                            Toast.makeText(getApplicationContext(), "Error En La Toma De Foto: "+ ex.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 1 && resultCode == RESULT_OK) {
                Bitmap imageBitmap = BitmapFactory.decodeFile(urlCompletaImg);
                imgFotoProducto.setImageBitmap(imageBitmap);
            }
            //Galeria
            else if(requestCode==100 && resultCode==RESULT_OK){
                Bitmap imageBitmap = BitmapFactory.decodeFile(urlCompletaImg);
                imgGaleriaProducto.setImageURI(data.getData());
            }
        }catch (Exception ex){
            Toast.makeText(getApplicationContext(), "Error: "+ ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private File createImageFile() throws IOException {
        // Aqui se crea un nombre de archivo de imagen
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "imagen_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if( storageDir.exists()==false ){
            storageDir.mkdirs();
        }
        File image = File.createTempFile(
                imageFileName,  /* prefijo */
                ".jpg",         /* sufijo */
                storageDir      /* directorio */

        );
        // Guarda un archivo, ruta para usar con las intenciones VISTA DE ACCION
        urlCompletaImg = image.getAbsolutePath();
        return image;
    }

    void  guardarDatosVehiculos(){

        btnVehiculos = (Button)findViewById(R.id.btnGuardarVehiculos);
        btnVehiculos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextView tempval = (TextView)findViewById(R.id.txtMarca);
                String marca = tempval.getText().toString();

                tempval = (TextView)findViewById(R.id.txtModelo);
                String modelo = tempval.getText().toString();

                tempval = (TextView)findViewById(R.id.txtYear);
                String year = tempval.getText().toString();

                tempval = (TextView)findViewById(R.id.txtNum_Motor);
                String numeromodelo = tempval.getText().toString();

                tempval = (TextView)findViewById(R.id.txtNum_Chasis);
                String numerochasis = tempval.getText().toString();

                if(!marca.isEmpty() && !modelo.isEmpty() && !year.isEmpty() && !numeromodelo.isEmpty() && !numerochasis.isEmpty()){

                    String[] data = {idVehiculo, marca, modelo, year, numeromodelo, numerochasis, urlCompletaImg};

                    miDB = new BD_Veiculos(getApplicationContext(), "", null, 1);
                    miDB.mantenimientoVehiculos(accion, data);

                    Toast.makeText(getApplicationContext(),"Se ha insertado un producto con exito", Toast.LENGTH_SHORT).show();
                    mostrarListaVehiculos();
                }
                else {
                    Toast.makeText(getApplicationContext(), "ERROR: Ingrese los datos", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnVehiculos = (Button)findViewById(R.id.btnMostrarVehiculos);
        btnVehiculos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarListaVehiculos();
            }
        });
        mostrarDatosVehiculos();
    }

    void mostrarListaVehiculos(){
        Intent mostrarVehiculos = new Intent( Agre_Vehiculos.this, MainActivity.class);
        startActivity(mostrarVehiculos);
    }

    void mostrarDatosVehiculos(){
        try {
            Bundle recibirParametros = getIntent().getExtras();
            accion = recibirParametros.getString("accion");


            if(accion.equals("Modificar")){
                String[] dataVehiculo= recibirParametros.getStringArray("dataVehiculo");

                idVehiculo= dataVehiculo[0];

                TextView tempval = (TextView)findViewById(R.id.txtMarca);
                tempval.setText(dataVehiculo[1]);

                tempval = (TextView)findViewById(R.id.txtModelo);
                tempval.setText(dataVehiculo[2]);

                tempval = (TextView)findViewById(R.id.txtYear);
                tempval.setText(dataVehiculo[3]);

                tempval = (TextView)findViewById(R.id.txtNum_Motor);
                tempval.setText(dataVehiculo[4]);

                tempval = (TextView)findViewById(R.id.txtNum_Chasis);
                tempval.setText(dataVehiculo[5]);

                urlCompletaImg = dataVehiculo[6];
                Bitmap imageBitmap = BitmapFactory.decodeFile(urlCompletaImg);
                imgFotoProducto.setImageBitmap(imageBitmap);
                imgGaleriaProducto.setImageBitmap(imageBitmap);
            }

        }catch (Exception ex){

        }
    }
}