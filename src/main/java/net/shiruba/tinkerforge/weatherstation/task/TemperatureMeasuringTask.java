package net.shiruba.tinkerforge.weatherstation.task;

import com.tinkerforge.BrickletBarometer;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;
import net.shiruba.tinkerforge.weatherstation.data.TemperatureMeasurement;
import net.shiruba.tinkerforge.weatherstation.data.TemperatureMeasurementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TemperatureMeasuringTask {

	@Autowired
	private TemperatureMeasurementRepository temperatureMeasurementRepository;

	private BrickletBarometer airPressureSensor;

	@Scheduled(fixedRateString = "${sensorCallbackPeriod}")
	public void measureTemperature() throws TimeoutException, NotConnectedException {
		if (airPressureSensor == null) {
			return;
		}
		TemperatureMeasurement temperatureMeasurement = new TemperatureMeasurement(airPressureSensor.getChipTemperature());
		temperatureMeasurementRepository.save(temperatureMeasurement);
	}

	public void setAirPressureSensor(BrickletBarometer airPressureSensor) {
		this.airPressureSensor = airPressureSensor;
	}
}
