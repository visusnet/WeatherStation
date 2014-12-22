package net.shiruba.tinkerforge.weatherstation.display;

import com.tinkerforge.BrickletLCD20x4;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;
import net.shiruba.tinkerforge.weatherstation.data.*;
import net.shiruba.tinkerforge.weatherstation.display.format.MeasurementFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DisplayService {

    private Logger logger = LoggerFactory.getLogger(DisplayService.class);

    @Autowired
    private DisplayHolder displayHolder;

    @Autowired
    private TemperatureMeasurementRepository temperatureMeasurementRepository;

    @Autowired
    private AirPressureMeasurementRepository airPressureMeasurementRepository;

    @Autowired
    private HumidityMeasurementRepository humidityMeasurementRepository;

    @Autowired
    private IlluminanceMeasurementRepository illuminanceMeasurementRepository;

    @Autowired
    private MeasurementFormatter measurementFormatter;

    private Mode mode = Mode.OVERVIEW;

    private List<PaintRequest> pendingPaintRequests = new ArrayList<>();

    public void showOverview() {
        mode = Mode.OVERVIEW;
        try {
            BrickletLCD20x4 display = displayHolder.getDisplay();

            TemperatureMeasurement temperatureMeasurement = temperatureMeasurementRepository.findFirstByOrderByIdDesc();
            AirPressureMeasurement airPressureMeasurement = airPressureMeasurementRepository.findFirstByOrderByIdDesc();
            HumidityMeasurement humidityMeasurement = humidityMeasurementRepository.findFirstByOrderByIdDesc();
            IlluminanceMeasurement illuminanceMeasurement = illuminanceMeasurementRepository.findFirstByOrderByIdDesc();

            display.clearDisplay();

            List<String> formattedOutputStrings = measurementFormatter.format(temperatureMeasurement, airPressureMeasurement, humidityMeasurement, illuminanceMeasurement);
            for (String formattedOutputString : formattedOutputStrings) {
                display.writeLine(
                        (short) formattedOutputStrings.indexOf(formattedOutputString),
                        (short) 0,
                        formattedOutputString
                );
            }
        } catch (DisplayNotAvailableException e) {
            logger.error("Ignoring user request because the display is not yet available.");
        } catch (TimeoutException | NotConnectedException e) {
            logger.error("Encountered exception while writing output to the display.", e);
        }
    }

    public void showDiagram() {
        /**
        mode = Mode.DIAGRAM;

        BrickletLCD20x4 display = displayHolder.getDisplay();
        display.clearDisplay();

        List<TemperatureMeasurement> temperatureMeasurements = temperatureMeasurementRepository.findFirst20ByOrderByIdDesc();
        for (int lineNumber = 3; lineNumber >= 0; lineNumber--) {
            for (TemperatureMeasurement temperatureMeasurement : temperatureMeasurements) {
                int temperature = temperatureMeasurement.getTemperature();
                int firstDigit = getFirstDigit(temperature);
                if (lineNumber >= firstDigit) {

                }
            }
        }
         */
    }

    public void toggleBacklight() {
        try {
            BrickletLCD20x4 display = displayHolder.getDisplay();
            if (display.isBacklightOn()) {
                logger.trace("Disabling the displays backlight upon user request.");
                display.backlightOff();
            } else {
                logger.trace("Enabling the displays backlight upon user request.");
                display.backlightOn();
            }
        } catch (TimeoutException | NotConnectedException e) {
            logger.error("Encountered an exception while enabling the displays backlight.", e);
        } catch (DisplayNotAvailableException e) {
            logger.error("Ignoring user request because the display is not yet available.");
        }
    }

    @Scheduled(fixedDelayString = "${displayRefreshRate}")
    private void refreshDisplay() {
        switch (mode) {
            case OVERVIEW:
                showOverview();
                break;
            case DIAGRAM:
                showDiagram();
                break;
            default:
                break;
        }
    }

    public static int getFirstDigit(int i) {
        while (Math.abs(i) >= 10 ) {
            i = i / 10;
        }
        return Math.abs(i);
    }

    private enum Mode {
        OVERVIEW,
        DIAGRAM
    }

    private interface PaintRequest {
    }
}
