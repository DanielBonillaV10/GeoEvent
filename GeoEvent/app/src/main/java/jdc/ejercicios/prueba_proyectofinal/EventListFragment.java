package jdc.ejercicios.prueba_proyectofinal;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

public class EventListFragment extends Fragment {

    ListView listView;
    DatabaseHelper dbHelper;
    ArrayList<String> events;
    ArrayList<Event> eventObjects;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);

        listView = view.findViewById(R.id.event_list);
        dbHelper = new DatabaseHelper(getContext());
        events = new ArrayList<>();
        eventObjects = new ArrayList<>();

        Cursor cursor = dbHelper.getAllEvents();
        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex("id");
            int titleIndex = cursor.getColumnIndex("title");
            int descriptionIndex = cursor.getColumnIndex("description");
            int dateIndex = cursor.getColumnIndex("date");
            int imageIndex = cursor.getColumnIndex("image_uri");
            int latitudeIndex = cursor.getColumnIndex("latitude");
            int longitudeIndex = cursor.getColumnIndex("longitude");

            do {
                int id = cursor.getInt(idIndex);
                String title = cursor.getString(titleIndex);
                String description = cursor.getString(descriptionIndex);
                String date = cursor.getString(dateIndex);
                String imagePath = cursor.getString(imageIndex);
                double latitude = cursor.getDouble(latitudeIndex);
                double longitude = cursor.getDouble(longitudeIndex);

                events.add(title);
                eventObjects.add(new Event(id, title, description, date, imagePath, latitude, longitude));
            } while (cursor.moveToNext());

            cursor.close();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, events);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            Event selectedEvent = eventObjects.get(position);

            EventDetailFragment detailFragment = new EventDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("event", selectedEvent);
            detailFragment.setArguments(bundle);

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, detailFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }
}
