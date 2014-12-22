package net.shiruba.tinkerforge.weatherstation.listener;

import com.tinkerforge.BrickletLCD20x4;
import net.shiruba.tinkerforge.weatherstation.display.DisplayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ButtonReleasedListener implements BrickletLCD20x4.ButtonReleasedListener {
    private static final int BUTTON_0 = 0;
    private static final int BUTTON_1 = 1;
    private static final int BUTTON_2 = 2;
    private static final int BUTTON_3 = 3;

    @Autowired
    private DisplayService displayService;

    @Override
    public void buttonReleased(short button) {
        switch (button) {
            case BUTTON_0:
                displayService.showOverview();
                break;
            case BUTTON_1:
                displayService.toggleBacklight();
                break;
            case BUTTON_2:
                break;
            case BUTTON_3:
                break;
            default:
                break;
        }
    }
}
