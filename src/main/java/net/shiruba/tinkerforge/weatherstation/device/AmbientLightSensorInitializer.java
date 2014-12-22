package net.shiruba.tinkerforge.weatherstation.device;

import com.tinkerforge.BrickletAmbientLight;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;
import net.shiruba.tinkerforge.weatherstation.listener.IlluminanceListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class AmbientLightSensorInitializer implements DeviceInitializer {
	@Autowired
	private IlluminanceListener illuminanceListener;

	@Value("${sensorCallbackPeriod}")
	private Integer sensorCallbackPeriod;

	@Override
	public void initialize(IPConnection ipConnection, String uid, int deviceIdentifier) throws TimeoutException, NotConnectedException {
		BrickletAmbientLight ambientLightSensor = new BrickletAmbientLight(uid, ipConnection);
		ambientLightSensor.setIlluminanceCallbackPeriod(sensorCallbackPeriod);
		ambientLightSensor.addIlluminanceListener(illuminanceListener);
	}
}
