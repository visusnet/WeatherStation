package net.shiruba.tinkerforge.weatherstation.device;

import com.tinkerforge.BrickletHumidity;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;
import net.shiruba.tinkerforge.weatherstation.listener.HumidityListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class HumiditySensorInitializer implements DeviceInitializer {
	@Autowired
	private HumidityListener humidityListener;

	@Value("${sensorCallbackPeriod}")
	private Integer sensorCallbackPeriod;

	@Override
	public void initialize(IPConnection ipConnection, String uid, int deviceIdentifier) throws TimeoutException, NotConnectedException {
		BrickletHumidity humiditySensor = new BrickletHumidity(uid, ipConnection);
		humiditySensor.setHumidityCallbackPeriod(sensorCallbackPeriod);
		humiditySensor.addHumidityListener(humidityListener);
	}
}
