package com.nordic.util;

import java.io.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

import com.nordic.controller.NordicFeedApplication;

/**
 * AudioUtil is used to play sounds.  Helpful to indicate when program action
 * is complete without having to check the screen.
 * 
 * @author Mike Sioda
 *
 */
public class AudioUtil {



	
	
	/**
	 * Play welcome sound.
	 */
	public static void playWelcome(){
		 playSound("welcome.wav");
	}
	
	/**
	 * Play start sound.
	 */
	public static void playStart(){
		 playSound("start.wav");
	}
	

	/**
	 * Play OK sound.
	 */
	public static void playOk(){
		playSound("ok.wav");
	}
	
	/**
	 * Play Failure sound.
	 */
	public static void playFailed(){
		playSound("failed.wav");
	}
	
	/**
	 * Please the sound of specific file name (from lib directory).
	 * @param fileName
	 */
	private static void playSound(String fileName) {	

	  try{
		  if(!enableSound()) return;
	    
		  String fileDir = PropUtil.projectRootDir +  "sound" + PropUtil.slash;

	      File soundFile = new File(fileDir+fileName);
	      AudioInputStream audio = AudioSystem.getAudioInputStream(soundFile);

		    DataLine.Info info = new DataLine.Info(Clip.class, audio.getFormat());
		    Clip clip = (Clip) AudioSystem.getLine(info);
		    clip.open(audio);
		    clip.addLineListener(new LineListener() {
		      public void update(LineEvent event) {
		        if (event.getType() == LineEvent.Type.STOP) {
		          event.getLine().close();
		        }
		      }
		    });

	    clip.start();
	  }catch(Exception ex){
		  LogUtil.error(ex);
	  }
	  
  }
	
	/**
	 * Return property value.
	 * @return
	 */
	private static boolean enableSound(){
		
//		if(NordicFeedApplication.getAction().equals(NordicFeedApplication.ACTION_DAILY_FEED_AND_DB_LOAD)){
//			return false;
//		}
		
		if(PropUtil.enableSound!=null && PropUtil.enableSound.equals("Y"))
			return true;
	
		return false;
	}
  
}