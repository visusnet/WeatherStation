package net.shiruba.tinkerforge.weatherstation.display.format;

import net.shiruba.tinkerforge.weatherstation.data.AirPressureMeasurement;
import net.shiruba.tinkerforge.weatherstation.data.HumidityMeasurement;
import net.shiruba.tinkerforge.weatherstation.data.IlluminanceMeasurement;
import net.shiruba.tinkerforge.weatherstation.data.TemperatureMeasurement;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class MeasurementFormatter {

    private static final int DISPLAY_SIZE = 20;
    private static final String TEMPERATURE_LABEL = "Temperatur";
    private static final String AIR_PRESSURE_LABEL = "Luftdruck";
    private static final String HUMIDITY_LABEL = "Luftfeuchte";
    private static final String ILLUMINANCE_LABEL = "Luminanz";

    public List<String> format(TemperatureMeasurement temperatureMeasurement,
                               AirPressureMeasurement airPressureMeasurement,
                               HumidityMeasurement humidityMeasurement,
                               IlluminanceMeasurement illuminanceMeasurement) {
        return Arrays.asList(
                formatTemperatureMeasurement(temperatureMeasurement),
                formatAirPressureMeasurement(airPressureMeasurement),
                formatHumidityMeasurement(humidityMeasurement),
                formatIlluminanceMeasurement(illuminanceMeasurement)
        );
    }

    private String formatTemperatureMeasurement(TemperatureMeasurement temperatureMeasurement) {
        float temperature = temperatureMeasurement.getTemperature() / 100f;
        return padToLineSize(
                TEMPERATURE_LABEL,
                String.format("%2.1f %cC", temperature, 0xDF)
        );
    }

    private String formatAirPressureMeasurement(AirPressureMeasurement airPressureMeasurement) {
        float airPressure = airPressureMeasurement.getAirPressure() / 1000f;
        return padToLineSize(
                AIR_PRESSURE_LABEL,
                String.format("%4.1f mb", airPressure)
        );
    }

    private String formatHumidityMeasurement(HumidityMeasurement humidityMeasurement) {
        float humidity = humidityMeasurement.getHumidity() / 10f;
        return padToLineSize(
                HUMIDITY_LABEL,
                String.format("%3.1f %%", humidity)
        );
    }

    private String formatIlluminanceMeasurement(IlluminanceMeasurement illuminanceMeasurement) {
        float illuminance = illuminanceMeasurement.getIlluminance() / 10f;
        return padToLineSize(
                ILLUMINANCE_LABEL,
                String.format("%6.2f lx", illuminance)
        );
    }

    private String padToLineSize(String string1, String string2) {
        int numberOfCharactersToPad = DISPLAY_SIZE - string1.length() - string2.length();
        return new StringBuilder()
                .append(string1)
                .append(StringUtils.repeat(' ', numberOfCharactersToPad))
                .append(string2)
                .toString();
    }
}
