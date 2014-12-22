package net.shiruba.tinkerforge.weatherstation.device;

import com.tinkerforge.BrickletBarometer;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;
import net.shiruba.tinkerforge.weatherstation.listener.AirPressureListener;
import net.shiruba.tinkerforge.weatherstation.task.TemperatureMeasuringTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class AirPressureInitializer implements DeviceInitializer {
	@Autowired
	private TemperatureMeasuringTask temperatureMeasuringTask;

	@Autowired
	private AirPressureListener airPressureListener;

	@Value("${sensorCallbackPeriod}")
	private Integer sensorCallbackPeriod;

	@Override
	public void initialize(IPConnection ipConnection, String uid, int deviceIdentifier) throws TimeoutException, NotConnectedException {
		BrickletBarometer airPressureSensor = new BrickletBarometer(uid, ipConnection);
		airPressureSensor.setAirPressureCallbackPeriod(sensorCallbackPeriod);
		airPressureSensor.addAirPressureListener(airPressureListener);
		temperatureMeasuringTask.setAirPressureSensor(airPressureSensor);
	}
}
