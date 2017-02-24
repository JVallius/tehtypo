package com.jarkkovallius.tehtypo.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.jarkkovallius.tehtypo.TehTypo;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 800 ;
		config.height = 600 ;
		new LwjglApplication(new TehTypo(), config);
	}
}
