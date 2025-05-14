package nl.scuro.tools.javafx.hotreload;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Tab;
import nl.scuro.tools.javafx.hotreload.gui.ReloadingTab;

public class DirectoryWatcherService {
    private WatchService watchService;

    private Map<String, Tab> tabMap;
    private List<WatchKey> watchKeys;
    private List<String> availableFiles = new ArrayList<>();
    private DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
    private StringProperty updateTimestamp = new SimpleStringProperty("No update yet");
    private boolean runThread = true;

    public DirectoryWatcherService(Path path) {
	    watchKeys = new ArrayList<>();
	    tabMap = new HashMap<>();
	    try {
	    	final String basePath = path.toString();
	        watchService = FileSystems.getDefault().newWatchService();
	        Files.walkFileTree(path, new SimpleFileVisitor<>() {

	            @Override
	            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
	                    throws IOException {
	                String subPath = dir.toString().substring(basePath.length());
	                String packagePath = subPath.replace("/", ".");

	                LogConsumer.offerLog("Path: " + packagePath);
	                File[] listFiles = dir.toFile().listFiles();
	                for (File file : listFiles) {
	                    String fileName = file.getName();
	                    int index = fileName.lastIndexOf('.');
	                    String name = fileName.substring(0, index > 0 ? index : fileName.length());
	                    String fqName = packagePath + "." + name;
	                    if (fqName.startsWith(".")){
	                        fqName = fqName.substring(1);
	                    }
	                    if (file.isFile()) {
	                    	LogConsumer.offerLog(fqName);
	                        availableFiles.add(fqName);
	                    }
	                }
	                WatchKey key = dir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
	                watchKeys.add(key);
	                return FileVisitResult.CONTINUE;
	            }
	        });

	    } catch (IOException e) {
	    	LogConsumer.offerLog('\n' + e.toString());
	    } catch (NullPointerException e) {}
	    runWatcher();
	}

    public List<String> getAvailableFiles() {
        return availableFiles;
    }

    private void runWatcher() {
    	new Thread(() -> {
            // Infinite loop to continuously watch for events
            while (runThread) {
                try {
                    WatchKey key = watchService.take();
                    for (WatchEvent<?> event : key.pollEvents()) {
                        if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                            updateTabContent(event.context().toString());
                        }
                    }

                    // To receive further events, reset the key
                    key.reset();
                } catch (InterruptedException ex) {
                	LogConsumer.offerLog('\n' + ex.toString());
                    Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
                    Thread.currentThread().interrupt();
                } catch (NullPointerException e) {}
            }
        }).start();
    }

    protected void updateTabContent(String file) {
        String simpleName = file.substring(0, file.indexOf('.'));
        Tab tab = tabMap.get(simpleName);
        if (tab != null) {
            tab.getContent().fireEvent(new NodeUpdatedEvent(NodeUpdatedEvent.RELOAD_TAB));
            Platform.runLater(()-> updateTimestamp.set("Updated at " + timeFormat.format(LocalTime.now())));
        } else {
        	LogConsumer.offerLog("Could not find tab for name " + simpleName);
        	LogConsumer.offerLog("In map: " + tabMap);
        }
    }

    public void deRegisterTab(Tab tab) {
    	LogConsumer.offerLog("deregister " + tab.getText());
        tabMap.entrySet()
        	  .stream()
        	  .filter(entry -> entry.getValue().equals(tab))
              .map(Map.Entry::getKey)
              .findAny()
              .ifPresent(tabMap::remove);
    }

    public void registerTab(ReloadingTab tab) {
    	LogConsumer.offerLog("Register " + tab.getSimpleName());
        tabMap.put(tab.getSimpleName(), tab);
        LogConsumer.offerLog("Tab map: " + tabMap);

    }

    public void shutdown() {
        watchKeys.forEach(k -> k.cancel());
        watchKeys.clear();
        runThread = false;
    }

    public StringProperty updateTimestampProperty() {
        return updateTimestamp;
    }
}
