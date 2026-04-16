module com.felix_71241153.app.chatbot_sibarista {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens com.felix_71241153.app.chatbot_sibarista to javafx.fxml;
    exports com.felix_71241153.app.chatbot_sibarista;
    exports controller;
    opens controller to javafx.fxml;
}