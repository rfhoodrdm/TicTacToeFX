module Fun.Projects {

    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.media;
    requires java.logging;

    opens gui;
    opens audio;
    opens images;

    opens edu.usf.mail.rfhood.TicTacToeFX.state;
    opens edu.usf.mail.rfhood.TicTacToeFX.gui;
    opens edu.usf.mail.rfhood.TicTacToeFX.logic;
    opens edu.usf.mail.rfhood.TicTacToeFX.main;

}