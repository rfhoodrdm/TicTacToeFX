package edu.usf.mail.rfhood.main;

import edu.usf.mail.rfhood.SceneDirectory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import static edu.usf.mail.rfhood.SceneDirectory.SCENE_NAME.*;

public class Main extends Application {

    /* **************************************************************
                                Fields and Constants
       ************************************************************** */

    SceneDirectory sceneDirectory;      //static scene directory reference.
    public static final int APP_HEIGHT = 600;
    public static final int APP_WIDTH = 600;


    /* **************************************************************
                                Constructor/Initialization
       ************************************************************** */

    /**
     * Init takes care of creating all of the scenes and registering them with the directory.
     * @throws Exception
     */
    @Override
    public void init() throws Exception {
        //load and set up our scenes.
        sceneDirectory = SceneDirectory.getInstance();

        //title scene
        Parent title = FXMLLoader.load(getClass().getClassLoader().getResource("edu/usf/mail/rfhood/title_screen.fxml"));
        Scene titleScene = new Scene(title, APP_WIDTH, APP_HEIGHT);
        sceneDirectory.registerScene(TITLE_SCENE, titleScene);

        //game board scene
        Parent gameBoard = FXMLLoader.load(getClass().getClassLoader().getResource("edu/usf/mail/rfhood/game_screen.fxml"));
        Scene gameBoardScene = new Scene(gameBoard, APP_WIDTH, APP_HEIGHT);
        sceneDirectory.registerScene(GAME_BOARD_SCENE, gameBoardScene);

        //instructions scene
        Parent instructions = FXMLLoader.load(getClass().getClassLoader().getResource("edu/usf/mail/rfhood/instruction_screen.fxml"));
        Scene instructionScene = new Scene(instructions, APP_WIDTH, APP_HEIGHT);
        sceneDirectory.registerScene(INSTRUCTIONS_SCENE, instructionScene);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Simple TicTacToe");
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
