module JavaFxHotReload {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires java.logging;

    requires transitive org.controlsfx.controls;

    requires transitive atlantafx.base;
    requires org.kordamp.ikonli.material2;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.feather;

    exports nl.scuro.tools.javafx.hotreload;
    exports nl.scuro.tools.javafx.hotreload.gui;
}
