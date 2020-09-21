package me.bingorufus.customizeddeathmessages.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import org.bukkit.Bukkit;

import me.bingorufus.customizeddeathmessages.CustomizedDeathMessages;

public class ExampleSaver {
CustomizedDeathMessages m;
	public ExampleSaver(CustomizedDeathMessages m) {
		this.m = m;
		downloadPreset();
		
	}


	public void downloadPreset() {
		if (new File(m.getDataFolder() + File.separator + "Example.zip").exists())
			return;
		Bukkit.getScheduler().runTaskAsynchronously(m, () -> {
			try {
				try (BufferedInputStream in = new BufferedInputStream(
						new URL("https://drive.google.com/uc?export=download&id=1Ogoz3fMF-CuZRZq7lbR_3IS1uAdznwiO")
								.openStream())) {
					FileOutputStream download = new FileOutputStream(
							m.getDataFolder() + File.separator + "Example.zip");
					byte dataBuffer[] = new byte[1024];
					int bytesRead;
					while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
						download.write(dataBuffer, 0, bytesRead);

					}
					download.close();
					
				}

			} catch (IOException e) {

				e.printStackTrace();
			}
		});
	}

	

        
	

		
		




}
