module JavaFxHotReload {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires transitive javafx.graphics;
    requires transitive javafx.base;
    
    requires atlantafx.base;
    requires org.kordamp.ikonli.material2;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.feather;
    
    exports nl.scuro.tools.javafx.hotreload to javafx.graphics;
}
