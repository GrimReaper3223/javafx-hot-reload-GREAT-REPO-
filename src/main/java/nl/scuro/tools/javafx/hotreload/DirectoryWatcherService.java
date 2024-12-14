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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import nl.scuro.tools.javafx.hotreload.gui.ReloadingTab;

public class DirectoryWatcherService {
    private WatchService watchService;

    private Map<String, Tab> tabMap;
    private List<WatchKey> watchKeys;
    private List<String> availableFiles = new ArrayList();
    private StringProperty updateTimestamp = new SimpleStringProperty("No update yet");
    private boolean runThread = true;

    public DirectoryWatcherService(File file) {
        watchKeys = new ArrayList<WatchKey>();
        tabMap = new HashMap<>();
        final String basePath = file.toPath().toString();
        try {
            watchService = FileSystems.getDefault().newWatchService();
            Files.walkFileTree(file.toPath(), new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                        throws IOException {
                    String subPath = dir.toString().substring(basePath.length());
                    String packagePath = subPath.replaceAll("/", ".");
                    
                    System.out.println("Path: " + packagePath);
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
                            System.out.println(fqName);
                            availableFiles.add(fqName);
                        }
                    }
                    WatchKey key = dir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
                    watchKeys.add(key);
                    return FileVisitResult.CONTINUE;
                }

            });

        } catch (IOException e) {
            e.printStackTrace();
        }
        runWatcher();
    }

    public List<String> getAvailableFiles() {
        return availableFiles;
    }

    private void runWatcher() {

        Runnable r = new Runnable() {
            @Override
            public void run() {
                // Infinite loop to continuously watch for events
                while (runThread) {
                    try {
                        WatchKey key = watchService.take();
                        System.out.println("Watching key " + key);
                        for (WatchEvent<?> event : key.pollEvents()) {
                            if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                                System.out.println("File modified: " + event.context());
                                updateTabContent(event.context().toString());
                            }
                        }

                        // To receive further events, reset the key
                        key.reset();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
        new Thread(r).start();
    }

    protected void updateTabContent(String file) {
        String simpleName = file.substring(0, file.indexOf('.'));
        Tab tab = tabMap.get(simpleName);
        if (tab != null) {
            tab.getContent().fireEvent(new NodeUpdatedEvent(NodeUpdatedEvent.RELOAD_TAB));
            Platform.runLater(()-> updateTimestamp.set(LocalTime.now().toString()));
        } else {
            System.err.println("Could not find tab for name " + simpleName);
            System.err.println("In map: " + tabMap);
        }
    }

    public void deRegisterTab(Tab tab) {
        System.err.println("deregister " + tab.getText());
        Optional<String> key = tabMap.entrySet().stream().filter(entry -> entry.getValue().equals(tab))
                .map(Map.Entry::getKey).findAny();
        key.ifPresent(k -> {
            tabMap.remove(k);
        });
    }

    public void registerTab(ReloadingTab tab) {
        System.out.println("Register " + tab.getSimpleName());
        tabMap.put(tab.getSimpleName(), tab);
        System.out.println("Tab map: " + tabMap);

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
