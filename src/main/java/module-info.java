module nl.scuro.applets.appletcontainer {
    requires javafx.controls;
    requires javafx.fxml;
    
    requires transitive javafx.graphics;
    requires transitive javafx.base;
    requires AppletLib;
    requires java.net.http;
    
    requires InstanceProvider;
    requires java.logging;
    requires java.sql;
    
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.feather;
    requires org.kordamp.ikonli.material2;
    
    requires atlantafx.base;
    requires org.eclipse.yasson;
    requires jakarta.json.bind;
    
    exports nl.scuro.fxlivedisplay;
    uses nl.scuro.applets.lib.AppletView;
}
