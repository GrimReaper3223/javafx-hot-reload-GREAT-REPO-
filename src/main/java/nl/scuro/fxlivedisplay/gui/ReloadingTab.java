package nl.scuro.fxlivedisplay.gui;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.layout.StackPane;
import nl.scuro.fxlivedisplay.FxViewModuleLoader;
import nl.scuro.fxlivedisplay.NodeUpdatedEvent;

public class ReloadingTab extends Tab {
    private FxViewModuleLoader loader;
    private StackPane stackPane;
    private String simpleName;
    public ReloadingTab(String className, File pathToWatch)  {
        super(className);
        String[] split = className.split("\\.");
        simpleName = split[split.length-1];
        stackPane = new StackPane();
        stackPane.setPadding(new Insets(10));
        
        String path= "file://" + pathToWatch.getAbsolutePath();
        try {
            loader = new FxViewModuleLoader(URI.create(path));
            updateTabContent();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        stackPane.addEventHandler(NodeUpdatedEvent.RELOAD_TAB, event -> {
            Platform.runLater(()->updateTabContent());
        });
        setContent(stackPane);

    }
    
    

    public void updateTabContent() {
        Node node = loader.loadFxView(this.getText());
        stackPane.getChildren().clear();
        stackPane.getChildren().add(node);
    }


    public String getSimpleName() {
        return simpleName;
    }


}
