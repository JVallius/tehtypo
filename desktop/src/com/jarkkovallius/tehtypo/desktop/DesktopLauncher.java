package com.jarkkovallius.tehtypo.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.jarkkovallius.tehtypo.TehTypo;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

        config.setFromDisplayMode(LwjglApplicationConfiguration.getDesktopDisplayMode());

        config.vSyncEnabled = true;
        config.resizable = false ;
        config.samples = 4 ;

        config.fullscreen = false ;

        if (!config.fullscreen) {
            config.width /= 2f ;
            config.height /= 2f ;
        }

		new LwjglApplication(new TehTypo(), config);
	}
}
