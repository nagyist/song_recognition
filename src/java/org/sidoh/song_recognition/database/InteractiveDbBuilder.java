package org.sidoh.song_recognition.database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import org.sidoh.io.StdinPrompts;
import org.sidoh.song_recognition.audio_io.WavFileException;
import org.sidoh.song_recognition.benchmark.Settings;
import org.sidoh.song_recognition.signature.StarHashSignature;

public class InteractiveDbBuilder {
	public static void main(String[] args) throws IOException, WavFileException, SQLException {
		HashSignatureDatabase db;
		
		Settings settings = Settings.defaults();
		File dbFile = StdinPrompts.promptForFile("Please enter file to save DB to. If it exists, it will be loaded.", false, false);
		db = new HashSignatureDatabase(RdbmsHelper.getConnection(dbFile.getAbsolutePath()), settings);

		System.out.println("DB Loaded!");
		System.out.println();
		
		WavDatabaseBuilder<StarHashSignature, HashSignatureDatabase> dbBuilder
			= new WavDatabaseBuilder<StarHashSignature, HashSignatureDatabase>(
					db, 
					settings.getStarHashExtractor(),
					settings.getBufferBuilder(),
					settings.getSpectrogramBuilder());
		
		while (true) {
			File songFile = StdinPrompts.promptForFile("Enter a .wav file (blank to stop): ", true, true);
			
			if (songFile.getName().isEmpty()) {
				break;
			}
			
			String songName = StdinPrompts.promptForLine("Enter a name for this song:");
			dbBuilder.addSong(songName, songFile.getAbsolutePath());
		}
		
		db.save(new FileOutputStream(dbFile));
	}
}
