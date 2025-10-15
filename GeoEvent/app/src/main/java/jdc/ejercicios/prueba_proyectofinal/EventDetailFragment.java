package jdc.ejercicios.prueba_proyectofinal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;

public class EventDetailFragment extends Fragment implements OnMapReadyCallback {

    private TextView titleText, descText, dateText;
    private ImageView eventImageView;
    private Button deleteButton;

    private DatabaseHelper dbHelper;
    private Event currentEvent;

    private GoogleMap googleMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        titleText = view.findViewById(R.id.textTitle);
        descText = view.findViewById(R.id.textDescription);
        dateText = view.findViewById(R.id.textDate);
        eventImageView = view.findViewById(R.id.detail_event_image);
        deleteButton = view.findViewById(R.id.delete_event_button);

        dbHelper = new DatabaseHelper(requireContext());

        Bundle args = getArguments();
        if (args != null) {
            currentEvent = (Event) args.getSerializable("event");

            if (currentEvent != null) {
                titleText.setText(currentEvent.getTitle());
                descText.setText(currentEvent.getDescription());
                dateText.setText(currentEvent.getDate());

                String imageUri = currentEvent.getImageUri();
                if (imageUri != null && !imageUri.isEmpty()) {
                    Glide.with(requireContext())
                            .load(new File(imageUri))
                            .placeholder(R.drawable.default_image)
                            .error(R.drawable.default_image)
                            .into(eventImageView);
                } else {
                    eventImageView.setImageResource(R.drawable.default_image);
                }
            }
        }

        // Configurar botón eliminar
        deleteButton.setOnClickListener(v -> {
            if (currentEvent != null) {
                int rowsDeleted = dbHelper.deleteEvent(currentEvent.getId());
                if (rowsDeleted > 0) {
                    Toast.makeText(getContext(), "Evento eliminado", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                } else {
                    Toast.makeText(getContext(), "Error al eliminar evento", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Inicializar el mapa después de que la vista está creada
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map_container);

        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.map_container, mapFragment)
                    .commitNow();  // commitNow() asegura que el fragmento se añade inmediatamente
        }

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;

        if (currentEvent != null) {
            double lat = currentEvent.getLatitude();
            double lng = currentEvent.getLongitude();

            if (lat != 0 && lng != 0) {
                LatLng location = new LatLng(lat, lng);
                googleMap.addMarker(new MarkerOptions()
                        .position(location)
                        .title(currentEvent.getTitle()));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f));
            }
        }
    }
}
