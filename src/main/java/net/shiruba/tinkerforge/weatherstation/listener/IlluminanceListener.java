package net.shiruba.tinkerforge.weatherstation.listener;

import com.tinkerforge.BrickletAmbientLight;
import net.shiruba.tinkerforge.weatherstation.data.IlluminanceMeasurement;
import net.shiruba.tinkerforge.weatherstation.data.IlluminanceMeasurementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IlluminanceListener implements BrickletAmbientLight.IlluminanceListener {

    @Autowired
    private IlluminanceMeasurementRepository illuminanceMeasurementRepository;

    @Override
    public void illuminance(int illuminance) {
        illuminanceMeasurementRepository.save(new IlluminanceMeasurement(illuminance));
    }
}
