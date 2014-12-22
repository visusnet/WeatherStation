package net.shiruba.tinkerforge.weatherstation.device;

import com.tinkerforge.BrickletLCD20x4;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;
import net.shiruba.tinkerforge.weatherstation.Application;
import net.shiruba.tinkerforge.weatherstation.display.DisplayHolder;
import net.shiruba.tinkerforge.weatherstation.display.DisplayNotAvailableException;
import net.shiruba.tinkerforge.weatherstation.listener.ButtonReleasedListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class DisplayManager implements DeviceInitializer, DeviceDestroyer {
	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	@Autowired
	private ButtonReleasedListener buttonReleasedListener;

	@Autowired
	private DisplayHolder displayHolder;

	@Override
	public void initialize(IPConnection ipConnection, String uid, int deviceIdentifier) throws TimeoutException, NotConnectedException {
		BrickletLCD20x4 display = new BrickletLCD20x4(uid, ipConnection);
		display.addButtonReleasedListener(buttonReleasedListener);

		setCustomCharacters(display);

		displayHolder.setDisplay(display);
	}

	public void setCustomCharacters(BrickletLCD20x4 display) throws TimeoutException, NotConnectedException {
		short[][] customCharacters = getCustomCharacters();
		for (short i = 0; i < customCharacters.length; i++) {
			display.setCustomCharacter(i, customCharacters[i]);
		}
	}

	@Override
	public void destroy() {
		try {
			BrickletLCD20x4 display = displayHolder.getDisplay();
			display.clearDisplay();
			display.backlightOff();
			displayHolder.clearDisplay();
		} catch (TimeoutException | NotConnectedException e) {
			logger.error("Encountered exception: ", e);
		} catch (DisplayNotAvailableException e) {
			logger.error("Cannot clear display and disable the backlight because the display is not available.");
		}
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
