package edu.usf.mail.rfhood.TicTacToeFX.gui;

import javafx.scene.Scene;

import java.util.HashMap;
import java.util.Map;

/**
 * SceneDirectory is the poor man's spring container. It holds reference to all the scenes of the application.
 */
public class SceneDirectory {

    /* **************************************************************
                                Fields and Constants
       ************************************************************** */

    public static SceneDirectory sceneDirectory = new SceneDirectory();

    private Map<SCENE_NAME, Scene> sceneMap = new HashMap<>();


    /* **************************************************************
                                Constructor/Initialization
       ************************************************************** */

    private SceneDirectory() {} //private constructor.


    /* **************************************************************
                            API
       ************************************************************** */

    public static SceneDirectory getInstance() {
        return sceneDirectory;
    }

    public void registerScene( SCENE_NAME sceneName, Scene scene) {
        //check if null first. No null scenes.
        if ( null != sceneName  &&  null != scene ) {
            sceneMap.put(sceneName, scene);
        }
    }

    public Scene getScene ( SCENE_NAME sceneToGet ) {
        return sceneMap.get(sceneToGet);
    }


    /* **************************************************************
                            Inner classes/Enums
       ************************************************************** */

    public static enum SCENE_NAME {
        TITLE_SCENE,
        GAME_BOARD_SCENE,
        INSTRUCTIONS_SCENE;

    }
}
