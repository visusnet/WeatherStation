package net.shiruba.tinkerforge.weatherstation.display;

import com.tinkerforge.BrickletLCD20x4;
import org.springframework.stereotype.Component;

@Component
public class DisplayHolder {
    private BrickletLCD20x4 display;

    public BrickletLCD20x4 getDisplay() throws DisplayNotYetAvailableException {
        if (display == null) {
            throw new DisplayNotYetAvailableException();
        }
        return display;
    }

    public void setDisplay(BrickletLCD20x4 display) {
        this.display = display;
    }
}
