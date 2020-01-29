package edu.usf.mail.rfhood.TicTacToeFX.gui.audio;

import javafx.scene.media.AudioClip;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Singleton Bean that manages loading sound effect resources, and plays them on command
 * when the game logic dictates.
 */
public class SFXManager {

    /* **************************************************************
                                Fields and Constants
       ************************************************************** */

    private static SFXManager sfxManager = new SFXManager();

    private Map<SFX, AudioClip> sfxAudioClipMap;    //mapping of enumerated SFX names to their respective audio clips.
    Logger logger = Logger.getLogger( this.getClass().getSimpleName() );    //Logger for this component


    /* **************************************************************
                                Constructor/Initialization
       ************************************************************** */
    /**
     *  Private constructor to enforce singleton nature.
     *  */
    private SFXManager() {
        // Initialize the audio clip map
        sfxAudioClipMap = new HashMap<>();

        //load all listed SFX audio clips into the map for later playback.
        for ( SFX currentSFX : SFX.values() ) {
            String audioPath = currentSFX.getPath();
            URL audioResource = getClass().getClassLoader().getResource(audioPath);
            AudioClip currentAudioClip = new AudioClip( audioResource.toString() );

            //check to see if we could get the audio clip loaded.
            // On failure, mention in the log. On success, store the clip for later.
            if ( null == currentAudioClip ) {
                logger.info("Attempt to load " + currentSFX + " failed.");
            } else {
                sfxAudioClipMap.put(currentSFX, currentAudioClip);
            }
        }

    }


    /* **************************************************************
                                API
        ************************************************************** */

    /**
     * Method to allow callers to get the one instance of the SFXManager class,
     * per the Singleton pattern.
     * @return
     */
    public static SFXManager getInstance() { return sfxManager; }


    /**
     * Take a request to play an audio clip.
     * @param sfxToPlay
     */
    public void playSFX( SFX sfxToPlay ) {

        //get the audio clip and play it. Check for null because reasons.
        if ( null == sfxToPlay ) {
            logger.info("SFX Manager was asked to play a sound effect, but offered NULL.");
        }

        AudioClip sfxAudioClip = sfxAudioClipMap.get(sfxToPlay);
        if ( null != sfxAudioClip ) {
            sfxAudioClip.play();
        } else {
            logger.info("The SFX that was requested (" + sfxToPlay + ") was actually NULL.")   ;
        }
    }


    /* **************************************************************
                                Enums/Inner Classes
        ************************************************************** */

    /**
     * Enumeration for all sound effects in the game.
     * These also contain the filenames of each particular sound effect.
     */
    public static enum SFX {
        MOVE_1  ("MOVE_1.aif"),
        MOVE_2  ("MOVE_2.aif"),
        MOVE_3  ("MOVE_3.aif"),
        MOVE_4  ("MOVE_4.aif"),
        MOVE_5  ("MOVE_5.aif"),
        MOVE_6  ("MOVE_6.aif"),
        MOVE_7  ("MOVE_7.aif"),
        MOVE_8  ("MOVE_8.aif"),
        GAME_OVER_VICTORY   ("GAME_OVER_VICTORY.aif"),
        GAME_OVER_DEFEAT    ("GAME_OVER_DEFEAT.aif"),
        GAME_OVER_DRAW      ("GAME_OVER_DRAW.aif");

        private String sfxFileName;
        private final static String sfxResourcePath = "audio/";

        SFX ( String pSFXFileName ) {
            sfxFileName = pSFXFileName;
        }

        /**
         * path prefix + filename
         */
        public String getPath() {
            return sfxResourcePath + sfxFileName;
        }
    }

}
