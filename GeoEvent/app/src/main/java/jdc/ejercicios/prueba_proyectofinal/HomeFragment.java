package jdc.ejercicios.prueba_proyectofinal;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HomeFragment extends Fragment {

    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private SensorEventListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        if (proximitySensor != null) {
            listener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    if (event.values[0] < proximitySensor.getMaximumRange()) {
                        // Sensor detecta cercanía
                        // Aquí podrías realizar alguna acción si lo deseas
                    } else {
                        // Sensor sin objeto cercano
                        // Aquí podrías realizar otra acción si lo deseas
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {}
            };

            sensorManager.registerListener(listener, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sensorManager != null && listener != null) {
            sensorManager.unregisterListener(listener);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sensorManager != null && listener != null) {
            sensorManager.unregisterListener(listener);
        }
    }
}
