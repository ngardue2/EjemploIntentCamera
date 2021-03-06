package com.example.usuario.ejemplointentcamera;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    static int VENGO_DE_LA_CAMARA = 1;
    static int VENGO_DE_LA_CAMARA_CON_FICHERO = 2;
    static final int PEDI_PERMISOS_PARA_ESCRIBIR = 3;
    Button captura;
    Button captura2;
    ImageView imageViewfoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        captura = (Button) findViewById(R.id.captura);
        captura2 = (Button) findViewById(R.id.button2);
        imageViewfoto = (ImageView)findViewById(R.id.imageView);

        captura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent haceFoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if(haceFoto.resolveActivity(getPackageManager())!=null){

                    startActivityForResult(haceFoto,VENGO_DE_LA_CAMARA);

                }else{

                    Toast.makeText(MainActivity.this, "Necesito un programa de hacer fotos.", Toast.LENGTH_SHORT).show();

                }
            }
        });

        captura2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pedirPermisoDeEscriturayHagoFoto();

            }
        });
    }
    void hacerFoto(){
        Intent haceFoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(haceFoto.resolveActivity(getPackageManager())!=null){

            File  ficheroFoto = null;
            try {
                ficheroFoto = crearFicheroDeImagen();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(ficheroFoto!=null){
                //cuando haga la foto la guarde en ese fichero
                haceFoto.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(ficheroFoto));

            }

            startActivityForResult(haceFoto,VENGO_DE_LA_CAMARA_CON_FICHERO);

        }else{

            Toast.makeText(MainActivity.this, "Necesito un programa de hacer fotos.", Toast.LENGTH_SHORT).show();

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if((requestCode == VENGO_DE_LA_CAMARA) && (resultCode == RESULT_OK)){

            Bundle extras = data.getExtras();

            Bitmap foto = (Bitmap) extras.get("data");

            imageViewfoto.setImageBitmap(foto);
        }

    }



    void pedirPermisoDeEscriturayHagoFoto(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE )!=PackageManager.PERMISSION_GRANTED){

            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){

            }else{

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PEDI_PERMISOS_PARA_ESCRIBIR);
            }

        }else{
             hacerFoto();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PEDI_PERMISOS_PARA_ESCRIBIR:
            {
               if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                   hacerFoto();
               }
               else{
                   Toast.makeText(this, "Sin permisos, no funciona.", Toast.LENGTH_SHORT).show();
               }
               return;
            }

        }
    }

    File crearFicheroDeImagen() throws IOException {
        String fecha = new SimpleDateFormat("yyMMdd_HHmmss").format(new Date());
        String nombreFichero = "MisFotos_"+fecha;
        File carpetaDeFotos = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(fecha,".jpg",carpetaDeFotos);
        return image;

    }
}
