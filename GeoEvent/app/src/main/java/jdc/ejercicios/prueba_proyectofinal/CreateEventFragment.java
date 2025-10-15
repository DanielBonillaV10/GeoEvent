package jdc.ejercicios.prueba_proyectofinal;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateEventFragment extends Fragment {

    EditText title, description;
    Button saveBtn, takePhotoBtn;
    ImageView eventImage;
    DatabaseHelper dbHelper;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_CAMERA_PERMISSION = 100;
    static final int REQUEST_LOCATION_PERMISSION = 101;

    private Uri imageUri;
    private File imageFile;

    private FusedLocationProviderClient fusedLocationClient;
    private double latitude = 0.0;
    private double longitude = 0.0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_event, container, false);

        title = view.findViewById(R.id.event_title);
        description = view.findViewById(R.id.event_description);
        saveBtn = view.findViewById(R.id.save_event_button);
        takePhotoBtn = view.findViewById(R.id.take_photo_button);
        eventImage = view.findViewById(R.id.event_image);
        dbHelper = new DatabaseHelper(getContext());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        takePhotoBtn.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            } else if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            } else {
                obtenerUbicacionYTomarFoto();
            }
        });

        saveBtn.setOnClickListener(v -> {
            String eventTitle = title.getText().toString().trim();
            String eventDescription = description.getText().toString().trim();

            if (eventTitle.isEmpty()) {
                Toast.makeText(getContext(), "El título es obligatorio", Toast.LENGTH_SHORT).show();
                return;
            }

            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            String imagePath = imageFile != null ? imageFile.getAbsolutePath() : "";

            obtenerUbicacionYGuardar(eventTitle, eventDescription, currentDate, imagePath);
        });

        return view;
    }

    private void abrirCamara() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePicture.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(getContext(), "No se encontró una app de cámara", Toast.LENGTH_SHORT).show();
        }
    }

    private void obtenerUbicacionYTomarFoto() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(), "Permiso de ubicación no concedido", Toast.LENGTH_SHORT).show();
            abrirCamara(); // Permitir tomar la foto igual
            return;
        }

        try {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                        abrirCamara();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error al obtener ubicación", Toast.LENGTH_SHORT).show();
                        abrirCamara();
                    });
        } catch (SecurityException e) {
            Toast.makeText(getContext(), "Permiso de ubicación no disponible", Toast.LENGTH_SHORT).show();
            abrirCamara();
        }
    }

    private void obtenerUbicacionYGuardar(String title, String description, String date, String imagePath) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
            Toast.makeText(getContext(), "Permiso de ubicación requerido", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        double lat = 0.0;
                        double lng = 0.0;
                        if (location != null) {
                            lat = location.getLatitude();
                            lng = location.getLongitude();
                        }

                        dbHelper.insertEvent(title, description, date, imagePath, lat, lng);
                        Toast.makeText(getContext(), "Evento guardado", Toast.LENGTH_SHORT).show();

                        limpiarUI();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error al obtener ubicación", Toast.LENGTH_SHORT).show();
                    });
        } catch (SecurityException e) {
            Toast.makeText(getContext(), "Permiso de ubicación no disponible", Toast.LENGTH_SHORT).show();
        }
    }

    private void limpiarUI() {
        title.setText("");
        description.setText("");
        eventImage.setImageResource(R.drawable.default_image);
        imageFile = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data != null ? data.getExtras() : null;
            Bitmap imageBitmap = extras != null ? (Bitmap) extras.get("data") : null;
            if (imageBitmap != null) {
                eventImage.setImageBitmap(imageBitmap);
                try {
                    imageFile = saveImageToInternalStorage(imageBitmap);
                    imageUri = FileProvider.getUriForFile(requireContext(),
                            requireContext().getPackageName() + ".fileprovider", imageFile);
                } catch (IOException e) {
                    Toast.makeText(getContext(), "Error al guardar la imagen", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private File saveImageToInternalStorage(Bitmap bitmap) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File imageFile = new File(requireContext().getFilesDir(), "IMG_" + timeStamp + ".jpg");

        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        }

        return imageFile;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
                } else {
                    obtenerUbicacionYTomarFoto();
                }
            } else {
                Toast.makeText(getContext(), "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtenerUbicacionYTomarFoto();
            } else {
                Toast.makeText(getContext(), "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
                abrirCamara(); // Toma la foto aunque no haya ubicación
            }
        }
    }
}
