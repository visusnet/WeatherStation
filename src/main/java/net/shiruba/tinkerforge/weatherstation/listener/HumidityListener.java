package net.shiruba.tinkerforge.weatherstation.listener;

import com.tinkerforge.BrickletHumidity;
import net.shiruba.tinkerforge.weatherstation.data.HumidityMeasurement;
import net.shiruba.tinkerforge.weatherstation.data.HumidityMeasurementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HumidityListener implements BrickletHumidity.HumidityListener {

    @Autowired
    private HumidityMeasurementRepository humidityMeasurementRepository;

    @Override
    public void humidity(int humidity) {
        humidityMeasurementRepository.save(new HumidityMeasurement(humidity));
    }
}
