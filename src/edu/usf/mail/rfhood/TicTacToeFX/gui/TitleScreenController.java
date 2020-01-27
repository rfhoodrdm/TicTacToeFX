package edu.usf.mail.rfhood.TicTacToeFX.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import static edu.usf.mail.rfhood.TicTacToeFX.gui.SceneDirectory.SCENE_NAME.*;

public class TitleScreenController {

    /* **************************************************************
                                Fields and Constants
       ************************************************************** */
    @FXML private Button playButton;
    @FXML private Button instructionsButton;
    @FXML private Canvas gamelogoCanvas;
    @FXML private BorderPane sceneBorderPane;       //reference to root element of the scene.


    /* **************************************************************
                        API
        ************************************************************** */
    @FXML
    void handleInstructionsButtonClick(ActionEvent event) {
       Stage primaryStage = (Stage) sceneBorderPane.getScene().getWindow();
       Scene instructionScene = SceneDirectory.getInstance().getScene(INSTRUCTIONS_SCENE);
       primaryStage.setScene(instructionScene);
    }

    @FXML
    void handlePlayButtonClick(ActionEvent event) {
        Stage primaryStage = (Stage) sceneBorderPane.getScene().getWindow();
        Scene gameBoardScene = SceneDirectory.getInstance().getScene(GAME_BOARD_SCENE);
        primaryStage.setScene(gameBoardScene);
    }

}
