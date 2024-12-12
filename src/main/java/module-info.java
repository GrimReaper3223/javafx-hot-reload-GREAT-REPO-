module nl.scuro.applets.appletcontainer {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires transitive javafx.graphics;
    requires transitive javafx.base;
    
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.feather;
    requires org.kordamp.ikonli.material2;
    
    requires atlantafx.base;
    
    exports nl.scuro.fxlivedisplay;
}
