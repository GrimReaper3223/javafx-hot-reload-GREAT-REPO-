package nl.scuro.tools.javafx.hotreload.gui;

import java.io.File;
import java.net.URI;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.layout.StackPane;
import nl.scuro.tools.javafx.hotreload.FxViewModuleLoader;
import nl.scuro.tools.javafx.hotreload.LogConsumer;
import nl.scuro.tools.javafx.hotreload.NodeUpdatedEvent;

public class ReloadingTab extends Tab {
    private FxViewModuleLoader loader;
    private StackPane stackPane;
    private String simpleName;

    public ReloadingTab(String className, File pathToWatch, StringProperty statusLabel)  {
        super(className);
        String[] split = className.split("\\.");
        simpleName = split[split.length-1];
        stackPane = new StackPane();
        stackPane.setPadding(new Insets(10));

        String path= "file://" + pathToWatch.getAbsolutePath();
        try {
            loader = new FxViewModuleLoader(URI.create(path));
            updateTabContent();
        } catch (Exception e) {
        	LogConsumer.offerLog('\n' + e.toString());
            statusLabel.set("Error loading component");
        }
        stackPane.addEventHandler(NodeUpdatedEvent.RELOAD_TAB, _ ->
	    	Platform.runLater(() -> {
	            try {
	                updateTabContent();
	                statusLabel.set("");
	            } catch (Exception e) {
	            	LogConsumer.offerLog('\n' + e.toString());
	                statusLabel.set("Error loading component");
	            }
	        }
	    ));
        setContent(stackPane);
    }

    private void updateTabContent() {
        Node node = loader.loadFxView(this.getText());
        stackPane.getChildren().clear();
        stackPane.getChildren().add(node);
    }

    public String getSimpleName() {
        return simpleName;
    }


}
