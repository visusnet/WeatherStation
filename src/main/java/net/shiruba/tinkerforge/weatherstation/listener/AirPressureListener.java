package net.shiruba.tinkerforge.weatherstation.listener;

import com.tinkerforge.BrickletBarometer;
import net.shiruba.tinkerforge.weatherstation.data.AirPressureMeasurement;
import net.shiruba.tinkerforge.weatherstation.data.AirPressureMeasurementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AirPressureListener implements BrickletBarometer.AirPressureListener {

    @Autowired
    private AirPressureMeasurementRepository airPressureMeasurementRepository;

    @Override
    public void airPressure(int airPressure) {
        airPressureMeasurementRepository.save(new AirPressureMeasurement(airPressure));
    }
}
