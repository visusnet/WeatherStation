package net.shiruba.tinkerforge.weatherstation;

import com.tinkerforge.*;
import net.shiruba.tinkerforge.weatherstation.device.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WeatherStation {
	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	@Value("${host}")
	private String host;

	@Value("${port}")
	private Integer port;

	private final Map<Integer, DeviceInitializer> deviceInitializers = new ConcurrentHashMap<>();

	private final List<DeviceDestroyer> deviceDestroyers = new ArrayList<>();

	private IPConnection ipConnection;

	public WeatherStation() {
		DisplayManager displayManager = new DisplayManager();

		deviceInitializers.put(BrickletLCD20x4.DEVICE_IDENTIFIER, displayManager);
		deviceInitializers.put(BrickletAmbientLight.DEVICE_IDENTIFIER, new AmbientLightSensorInitializer());
		deviceInitializers.put(BrickletHumidity.DEVICE_IDENTIFIER, new HumiditySensorInitializer());
		deviceInitializers.put(BrickletBarometer.DEVICE_IDENTIFIER, new AirPressureInitializer());

		deviceDestroyers.add(displayManager);
	}

	@Scheduled(fixedRateString = "${reconnectRate}")
	public void initialize() {
		initializeConnection();
		initializeHardware();
	}

	private void initializeConnection() {
		prepareConnection();

		logger.trace("Initializing connection...");
		try {
			if (isDisconnected()) {
				connect();
			}
		} catch (IOException e) {
			logger.error("Error while connection to {} via port {}.", host, port, e);
		} catch (AlreadyConnectedException e) {
			logger.trace("Already connected.");
		}
		logger.trace("Initialized connection.");
	}

	private void initializeHardware() {
		logger.trace("Initializing hardware...");
		logger.trace("Enumerating hardware...");
		try {
			ipConnection.enumerate();
		} catch (NotConnectedException e) {
			logger.error("Not connected to host.", e);
		}
	}

	private void prepareConnection() {
		if (ipConnection == null) {
			ipConnection = new IPConnection();
			ipConnection.setAutoReconnect(true);
		}
	}

	private void connect() throws IOException, AlreadyConnectedException {
		ipConnection.connect(host, port);
		ipConnection.addConnectedListener(this::logConnectionEstablished);
		ipConnection.addDisconnectedListener(disconnectReasonCode -> deviceDestroyers.forEach(DeviceDestroyer::destroy));
		ipConnection.addEnumerateListener(new DeviceEnumerationListener());
	}

	private void logConnectionEstablished(short connectReasonCode) {
		switch (connectReasonCode) {
			case IPConnection.CONNECT_REASON_REQUEST:
				logger.trace("Connection established upon user request.");
				break;
			case IPConnectionBase.CONNECT_REASON_AUTO_RECONNECT:
				logger.trace("Connection established by auto reconnect.");
				break;
		}
	}

	private boolean isDisconnected() {
		return isConnectionStateDisconnected(ipConnection.getConnectionState());
	}

	private boolean isConnectionStateDisconnected(short connectionState) {
		return connectionState == IPConnection.CONNECTION_STATE_DISCONNECTED;
	}

	private DeviceInitializer getDeviceInitializer(int deviceIdentifier) {
		return deviceInitializers.containsKey(deviceIdentifier) ? deviceInitializers.get(deviceIdentifier) : null;
	}

	private class DeviceEnumerationListener implements IPConnection.EnumerateListener {
		@Override
		public void enumerate(String uid, String connectedUid, char position, short[] hardwareVersion, short[] firmwareVersion, int deviceIdentifier, short enumerationType) {
			if (isConnectionStateDisconnected(enumerationType)) {
				return;
			}

			DeviceInitializer deviceInitializer = getDeviceInitializer(deviceIdentifier);
			if (deviceInitializer != null) {
				try {
					deviceInitializer.initialize(ipConnection, uid, deviceIdentifier);
				} catch (TimeoutException | NotConnectedException e) {
					logger.error("An error occurred while initializing the hardware.", e);
				}
			}
		}
	}
}
