package net.shiruba.tinkerforge.weatherstation.device;

import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

public interface DeviceInitializer {
	void initialize(IPConnection ipConnection, String uid, int deviceIdentifier) throws TimeoutException, NotConnectedException;
}
