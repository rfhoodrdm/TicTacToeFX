package edu.usf.mail.rfhood;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import static edu.usf.mail.rfhood.SceneDirectory.SCENE_NAME.*;

public class InstructionScreenController {

    /* **************************************************************
                                Fields and Constants
       ************************************************************** */

    @FXML private Button instructionsPlayButton;
    @FXML private Button instructionsBackButton;
    @FXML private TextArea instructionsTextArea;
    @FXML private BorderPane sceneBorderPane;       //reference to root element of the scene.


    /* **************************************************************
                        API
        ************************************************************** */
    @FXML
    void handleBackToTitleButtonClick(ActionEvent event) {
        Stage primaryStage = (Stage) sceneBorderPane.getScene().getWindow();
        Scene titleScene = SceneDirectory.getInstance().getScene(TITLE_SCENE);
        primaryStage.setScene(titleScene);
    }

    @FXML
    void handlePlayButtonClick(ActionEvent event) {
        Stage primaryStage = (Stage) sceneBorderPane.getScene().getWindow();
        Scene gameBoardScene = SceneDirectory.getInstance().getScene(GAME_BOARD_SCENE);
        primaryStage.setScene(gameBoardScene);
    }

}
