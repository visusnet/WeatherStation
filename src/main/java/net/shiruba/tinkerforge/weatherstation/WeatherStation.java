package net.shiruba.tinkerforge.weatherstation;

import com.tinkerforge.*;
import net.shiruba.tinkerforge.weatherstation.display.DisplayHolder;
import net.shiruba.tinkerforge.weatherstation.display.DisplayNotYetAvailableException;
import net.shiruba.tinkerforge.weatherstation.display.DisplayService;
import net.shiruba.tinkerforge.weatherstation.task.TemperatureMeasuringTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;


@Configuration
@ComponentScan
@EnableAutoConfiguration
@EnableScheduling
public class WeatherStation implements IPConnection.EnumerateListener, IPConnection.DisconnectedListener, IPConnection.ConnectedListener {
    public static final int SENSOR_CALLBACK_PERIOD = 1000;

    private static final Logger logger = LoggerFactory.getLogger(WeatherStation.class);
    private static final String HOST = "localhost";
    private static final Integer PORT = 4223;

    private IPConnection ipConnection;

    @Autowired
    private BrickletAmbientLight.IlluminanceListener illuminanceListener;

    @Autowired
    private BrickletHumidity.HumidityListener humidityListener;

    @Autowired
    private BrickletBarometer.AirPressureListener airPressureListener;

    @Autowired
    private BrickletLCD20x4.ButtonReleasedListener buttonReleasedListener;

    @Autowired
    private DisplayHolder displayHolder;

    @Autowired
    private TemperatureMeasuringTask temperatureMeasuringTask;

    public static void main(String[] args) {
        SpringApplication.run(WeatherStation.class, args);
    }

    @Scheduled(fixedRate = 3000)
    private void initialize() {
        prepareConnection();
        initializeConnection();
        initializeHardware();
    }

    private void prepareConnection() {
        if (ipConnection == null) {
            ipConnection = new IPConnection();
            ipConnection.setAutoReconnect(true);
        }
    }

    private void initializeConnection() {
        logger.trace("Initializing connection...");
        try {
            if (ipConnection.getConnectionState() == IPConnection.CONNECTION_STATE_DISCONNECTED) {
                ipConnection.connect(HOST, PORT);
                ipConnection.addConnectedListener(this);
                ipConnection.addEnumerateListener(this);
                ipConnection.addDisconnectedListener(this);
            }
        } catch (IOException e) {
            logger.error("Error while connection to {} via port {}.", HOST, PORT, e);
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

    @Override
    public void connected(short connectReasonCode) {
        switch (connectReasonCode) {
            case IPConnection.CONNECT_REASON_REQUEST:
                logger.trace("Connection established upon user request.");
                break;
            case IPConnectionBase.CONNECT_REASON_AUTO_RECONNECT:
                logger.trace("Connection established by auto reconnect.");
                break;
            default:
                break;
        }
    }

    @Override
    public void enumerate(String uid, String connectedUid, char position, short[] hardwareVersion, short[] firmwareVersion, int deviceIdentifier, short enumerationType) {
        if (enumerationType == IPConnection.ENUMERATION_TYPE_DISCONNECTED) {
            return;
        }

        try {
            initializeDisplay(uid, deviceIdentifier);
            initializeAmbientLightSensor(uid, deviceIdentifier);
            initializeHumiditySensor(uid, deviceIdentifier);
            initializeAirPressurSensor(uid, deviceIdentifier);
        } catch (TimeoutException | NotConnectedException e) {
            logger.error("An error occured while initializing the hardware.", e);
        }
    }

    @Override
    public void disconnected(short disconnectReason) {
        try {
            BrickletLCD20x4 display = displayHolder.getDisplay();
            display.clearDisplay();
            display.backlightOff();
        } catch (TimeoutException | NotConnectedException e) {
            logger.error("Encountered exception: ", e);
        } catch (DisplayNotYetAvailableException e) {
            logger.error("Cannot clear display and disable the backlight, because the display is not available.");
        }
    }

    private void initializeDisplay(String uid, int deviceIdentifier) throws TimeoutException, NotConnectedException {
        if (isDisplay(deviceIdentifier)) {
            BrickletLCD20x4 display = new BrickletLCD20x4(uid, ipConnection);
            display.addButtonReleasedListener(buttonReleasedListener);

            short[][] customCharacters = getCustomCharacters();
            for (short i = 0; i < customCharacters.length; i++) {
                display.setCustomCharacter(i, customCharacters[i]);
            }

            displayHolder.setDisplay(display);
        }
    }

    private void initializeAmbientLightSensor(String uid, int deviceIdentifier) throws TimeoutException, NotConnectedException {
        if (isAmbientLightSensor(deviceIdentifier)) {
            BrickletAmbientLight ambientLightSensor = new BrickletAmbientLight(uid, ipConnection);
            ambientLightSensor.setIlluminanceCallbackPeriod(SENSOR_CALLBACK_PERIOD);
            ambientLightSensor.addIlluminanceListener(illuminanceListener);
        }
    }

    private void initializeHumiditySensor(String uid, int deviceIdentifier) throws TimeoutException, NotConnectedException {
        if (isHumiditySensor(deviceIdentifier)) {
            BrickletHumidity humiditySensor = new BrickletHumidity(uid, ipConnection);
            humiditySensor.setHumidityCallbackPeriod(SENSOR_CALLBACK_PERIOD);
            humiditySensor.addHumidityListener(humidityListener);
        }
    }

    private void initializeAirPressurSensor(String uid, int deviceIdentifier) throws TimeoutException, NotConnectedException {
        if (isAirPressureSensor(deviceIdentifier)) {
            BrickletBarometer airPressureSensor = new BrickletBarometer(uid, ipConnection);
            airPressureSensor.setAirPressureCallbackPeriod(SENSOR_CALLBACK_PERIOD);
            airPressureSensor.addAirPressureListener(airPressureListener);

            temperatureMeasuringTask.setAirPressureSensor(airPressureSensor);
        }
    }

    private boolean isDisplay(int deviceIdentifier) {
        return deviceIdentifier == BrickletLCD20x4.DEVICE_IDENTIFIER;
    }

    private boolean isAmbientLightSensor(int deviceIdentifier) {
        return deviceIdentifier == BrickletAmbientLight.DEVICE_IDENTIFIER;
    }

    private boolean isHumiditySensor(int deviceIdentifier) {
        return deviceIdentifier == BrickletHumidity.DEVICE_IDENTIFIER;
    }

    private boolean isAirPressureSensor(int deviceIdentifier) {
        return deviceIdentifier == BrickletBarometer.DEVICE_IDENTIFIER;
    }

    private short[][] getCustomCharacters() {
        short[][] customCharacters = new short[8][];
            customCharacters[0] = new short[]{0b00000000, 0b00000000, 0b00000000, 0b00000000, 0b00000000, 0b00000000, 0b00000000, 0b11111111};
            customCharacters[1] = new short[]{0b00000000, 0b00000000, 0b00000000, 0b00000000, 0b00000000, 0b00000000, 0b11111111, 0b11111111};
            customCharacters[2] = new short[]{0b00000000, 0b00000000, 0b00000000, 0b00000000, 0b00000000, 0b11111111, 0b11111111, 0b11111111};
            customCharacters[3] = new short[]{0b00000000, 0b00000000, 0b00000000, 0b00000000, 0b11111111, 0b11111111, 0b11111111, 0b11111111};
            customCharacters[4] = new short[]{0b00000000, 0b00000000, 0b00000000, 0b11111111, 0b11111111, 0b11111111, 0b11111111, 0b11111111};
            customCharacters[5] = new short[]{0b00000000, 0b00000000, 0b11111111, 0b11111111, 0b11111111, 0b11111111, 0b11111111, 0b11111111};
            customCharacters[6] = new short[]{0b00000000, 0b11111111, 0b11111111, 0b11111111, 0b11111111, 0b11111111, 0b11111111, 0b11111111};
            customCharacters[7] = new short[]{0b11111111, 0b11111111, 0b11111111, 0b11111111, 0b11111111, 0b11111111, 0b11111111, 0b11111111};
        return customCharacters;
    }
}
