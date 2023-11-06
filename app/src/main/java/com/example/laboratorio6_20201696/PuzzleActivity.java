package com.example.laboratorio6_20201696;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PuzzleActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private DocumentReference juegoRef;
    private Map<String, Map<String, Object>> piezas; // HashMap para las piezas
    private int filaCasillaVacia;
    private int columnaCasillaVacia;
    private int valorAleatorio;
    private GridLayout tablero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);
        FirebaseApp.initializeApp(this); // Agrega esta línea
        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();
        juegoRef = db.collection("Juegos").document("tu_id_de_juego");

        // Inicializar el HashMap para las piezas
        piezas = new HashMap<>();
        // Agrega las piezas al HashMap, por ejemplo:
        piezas.put("Pieza_1", new HashMap<String, Object>());
        piezas.get("Pieza_1").put("fila", 0);
        piezas.get("Pieza_1").put("columna", 0);
        piezas.get("Pieza_1").put("imagen", "pieza1.jpg");
        // Repite estos pasos para cada pieza
        tablero = findViewById(R.id.tablero_juego);


        // Solicitar al usuario subir una imagen
        Button botonSolicitarImagen = findViewById(R.id.boton_subir_imagen);
        botonSolicitarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                solicitarImagen();
            }
        });
    }

    private static final int PICK_IMAGE_REQUEST = 1; // Código de solicitud para la selección de imagen

    private void solicitarImagen() {
        // Implementa aquí la lógica para solicitar al usuario subir una imagen
        // Por ejemplo, usando un Intent para abrir la galería y seleccionar una imagen
        // Una vez seleccionada la imagen, puedes cargarla en el tablero
        // Crear un Intent para abrir la galería de imágenes
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Obtener la URI de la imagen seleccionada
            Uri imagenUri = data.getData();

            // Cargar la imagen en el tablero
            cargarImagenEnTablero(imagenUri);
        }
    }

    private void cargarImagenEnTablero(Uri imagenUri) {
        // Obtener el GridLayout del tablero
        filaCasillaVacia = tablero.getRowCount() - 1;
        columnaCasillaVacia = tablero.getColumnCount() - 1;
        Random random = new Random();
        valorAleatorio = random.nextInt(3) + 3; // Mueve esto a nivel de clase
        // Cambiar el número de columnas y filas del GridLayout
        tablero.setColumnCount(valorAleatorio);
        tablero.setRowCount(valorAleatorio);
        // Cargar la imagen usando Glide
        Glide.with(this)
                .load(imagenUri)
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        Bitmap imagenBitmap = ((BitmapDrawable) resource).getBitmap();
                        List<Bitmap> piezas = dividirImagenEnPiezas(imagenBitmap, valorAleatorio, valorAleatorio);
                        // Desordenar las piezas aleatoriamente
                        Collections.shuffle(piezas);

                        // Distribuir las piezas en el tablero
                        distribuirPiezasEnTablero(piezas, tablero);


                        // Mostrar el botón "Comenzar Juego"
                        Button botonComenzarJuego = findViewById(R.id.boton_comenzar_juego);
                        botonComenzarJuego.setVisibility(View.VISIBLE);

// Obtener todas las vistas (piezas) del tablero
                        for (int i = 0; i < tablero.getChildCount(); i++) {
                            View pieza = tablero.getChildAt(i);
                            pieza.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String nombrePieza = obtenerNombreDePiezaDesdeImageView((ImageView) v);
                                    moverPieza(nombrePieza);
                                }
                            });
                        }

                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // No es necesario implementar este método en este caso
                    }
                });
    }
    public void onComenzarJuegoClick(View view) {
        desordenarTableroAleatoriamente(tablero, valorAleatorio);
        guardarEstadoEnFirebase();
    }
    private String obtenerNombreDePiezaDesdeImageView(ImageView imageView) {
        return (String) imageView.getTag();
    }

    private void desordenarTableroAleatoriamente(GridLayout tablero, int random) {
        List<View> vistas = new ArrayList<>();

        // Obtener todas las vistas (piezas) del tablero
        for (int i = 0; i < tablero.getChildCount(); i++) {
            vistas.add(tablero.getChildAt(i));
        }

        // Desordenar las vistas aleatoriamente
        Collections.shuffle(vistas);

        // Limpiar el tablero
        tablero.removeAllViews();

        // Agregar las vistas desordenadas al tablero
        for (View vista : vistas) {
            tablero.addView(vista);
        }

        // Asegurarse de que la última casilla esté vacía
        int filaUltimaCasilla = tablero.getRowCount() - 1;
        int columnaUltimaCasilla = tablero.getColumnCount() - 1;

        View ultimaCasilla = tablero.getChildAt(filaUltimaCasilla * random + columnaUltimaCasilla);

        if (!estaCasillaVacia(ultimaCasilla)) {
            // Si la última casilla no está vacía, entonces la intercambiamos con la casilla vacía
            intercambiarCasillaConUltimaVacia(tablero, random);
        }

        // En este punto, todas las piezas están desordenadas y la última casilla está vacía
        // Puedes mostrar las piezas en el tablero
    }
    private boolean estaCasillaVacia(View casilla) {
        // Verificar si el View es un ImageView
        if (casilla instanceof ImageView) {
            ImageView imageView = (ImageView) casilla;

            // Verificar si el ImageView no tiene una imagen asignada
            return imageView.getDrawable() == null;
        }

        // Si no es un ImageView, retornar falso
        return false;
    }
    private void intercambiarCasillaConUltimaVacia(GridLayout tablero, int random) {
        int filaUltimaCasilla = tablero.getRowCount() - 1;
        int columnaUltimaCasilla = tablero.getColumnCount() - 1;

        View ultimaCasilla = tablero.getChildAt(filaUltimaCasilla * random + columnaUltimaCasilla);
        View casillaVacia = tablero.getChildAt(tablero.getChildCount() - 1);

        if (ultimaCasilla != null && casillaVacia != null) {
            // Verificar si la última casilla no está vacía
            if (!estaCasillaVacia(ultimaCasilla)) {
                // Si la última casilla no está vacía, entonces la intercambiamos con la casilla vacía
                tablero.removeViewAt(tablero.getChildCount() - 1);
                tablero.addView(ultimaCasilla);
                tablero.removeViewAt(filaUltimaCasilla * random + columnaUltimaCasilla);
                tablero.addView(casillaVacia, filaUltimaCasilla * random + columnaUltimaCasilla);
            }
        }
    }



    private List<Bitmap> dividirImagenEnPiezas(Bitmap imagen, int filas, int columnas) {
        List<Bitmap> piezas = new ArrayList<>();

        int anchoPieza = imagen.getWidth() / columnas;
        int altoPieza = imagen.getHeight() / filas;

        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                int x = j * anchoPieza;
                int y = i * altoPieza;
                Bitmap pieza = Bitmap.createBitmap(imagen, x, y, anchoPieza, altoPieza);
                piezas.add(pieza);
            }
        }

        return piezas;
    }



    private void distribuirPiezasEnTablero(List<Bitmap> piezas, GridLayout tablero) {
        int columnas = tablero.getColumnCount();
        int filas = tablero.getRowCount();

        int anchoPieza = tablero.getWidth() / columnas;
        int altoPieza = tablero.getHeight() / filas;

        int tamañoPieza = Math.min(anchoPieza, altoPieza); // Usamos el tamaño mínimo para asegurarnos de que las piezas quepan en el tablero

        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                ImageView imageView = new ImageView(this);
                imageView.setLayoutParams(new ViewGroup.LayoutParams(tamañoPieza, tamañoPieza));

                // Configurar la imagen de la pieza
                imageView.setImageBitmap(piezas.get(i * columnas + j));

                // Asignar un nombre (tag) a la pieza
                String nombrePieza = "Pieza_" + (i * columnas + j + 1);
                imageView.setTag(nombrePieza);

                // Añadir el ImageView al GridLayout en la posición (i, j)
                GridLayout.Spec rowSpec = GridLayout.spec(i);
                GridLayout.Spec colSpec = GridLayout.spec(j);
                GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams(rowSpec, colSpec);
                tablero.addView(imageView, layoutParams);
            }
        }
    }



    private int getFilaDePieza(String nombrePieza) {
        return (int) piezas.get(nombrePieza).get("fila");
    }

    private int getColumnaDePieza(String nombrePieza) {
        return (int) piezas.get(nombrePieza).get("columna");
    }

    private boolean puedeMover(String nombrePieza) {
        int filaPieza = getFilaDePieza(nombrePieza);
        int columnaPieza = getColumnaDePieza(nombrePieza);

        return (Math.abs(filaPieza - filaCasillaVacia) == 1 && columnaPieza == columnaCasillaVacia) ||
                (Math.abs(columnaPieza - columnaCasillaVacia) == 1 && filaPieza == filaCasillaVacia);
    }

    private void moverPieza(String nombrePieza) {
        if (puedeMover(nombrePieza)) {
            // Intercambiar la posición de la pieza con la casilla vacía
            piezas.get(nombrePieza).put("fila", filaCasillaVacia);
            piezas.get(nombrePieza).put("columna", columnaCasillaVacia);

            filaCasillaVacia = getFilaDePieza(nombrePieza);
            columnaCasillaVacia = getColumnaDePieza(nombrePieza);
        }
    }

    private void guardarEstadoEnFirebase() {
        // Obtener el estado actual del juego y guardar en Firestore
        Map<String, Object> estado = new HashMap<>();
        estado.put("PiezasMovidas", 0); // Inicialmente no se han movido piezas
        // Aquí añade la lógica para obtener el estado actual de las piezas y guardar en estado
        // ...
        juegoRef.set(estado)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Estado guardado exitosamente
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error al guardar el estado
                    }
                });
    }
}
