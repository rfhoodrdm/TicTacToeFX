package edu.usf.mail.rfhood.TicTacToeFX.main;

import edu.usf.mail.rfhood.TicTacToeFX.gui.SceneDirectory;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static edu.usf.mail.rfhood.TicTacToeFX.gui.SceneDirectory.SCENE_NAME.*;

public class Main extends Application {

    /* **************************************************************
                                Fields and Constants
       ************************************************************** */

    SceneDirectory sceneDirectory;                //scene directory reference.

    public static final int APP_HEIGHT = 600;
    public static final int APP_WIDTH = 600;
    public static HostServices hostServices;      //static host services reference, for opening web pages.


    /* **************************************************************
                                Constructor/Initialization
       ************************************************************** */

    /**
     * Init takes care of creating all of the scenes and registering them with the directory.
     * @throws Exception
     */
    @Override
    public void init() throws Exception {
        //assign the reference to host services, so that our controllers can use it.
        hostServices = getHostServices();

        //load and set up our scenes.
        sceneDirectory = SceneDirectory.getInstance();

        //title scene
        Parent title = FXMLLoader.load(getClass().getClassLoader().getResource("gui/title_screen.fxml"));
        Scene titleScene = new Scene(title, APP_WIDTH, APP_HEIGHT);
        sceneDirectory.registerScene(TITLE_SCENE, titleScene);

        //game board scene
        Parent gameBoard = FXMLLoader.load(getClass().getClassLoader().getResource("gui/game_screen.fxml"));
        Scene gameBoardScene = new Scene(gameBoard, APP_WIDTH, APP_HEIGHT);
        sceneDirectory.registerScene(GAME_BOARD_SCENE, gameBoardScene);

        //instructions scene
        Parent instructions = FXMLLoader.load(getClass().getClassLoader().getResource("gui/instruction_screen.fxml"));
        Scene instructionScene = new Scene(instructions, APP_WIDTH, APP_HEIGHT);
        sceneDirectory.registerScene(INSTRUCTIONS_SCENE, instructionScene);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("TicTacToeFX");
        primaryStage.setScene(sceneDirectory.getScene(TITLE_SCENE));
        primaryStage.show();
    }

    /* **************************************************************
                                Main
        ************************************************************** */

    public static void main(String[] args) {
        launch(args);
    }

}
