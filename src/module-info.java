module edu.usf.mail.rfhood.TicTacToeFX {

    //dependencies
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.media;
    requires java.logging;

    //resources
    opens gui;
    opens audio;
    opens images;

    //required for JavaFX reflection.
    opens edu.usf.mail.rfhood.TicTacToeFX.gui;
    opens edu.usf.mail.rfhood.TicTacToeFX.main;

}