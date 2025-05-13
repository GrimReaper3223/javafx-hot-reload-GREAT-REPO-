package nl.scuro.tools.javafx.hotreload;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nl.scuro.tools.javafx.hotreload.gui.MainView;

public class MainApp extends Application {

    private DirectoryWatcherService directoryWatcherService;

    @Override
    public void start(Stage primaryStage) throws Exception {
        MainView mainView = new MainView();
        LogConsumer.setLogTextAreaNode(mainView.getLogTextArea());
        mainView.getSelectedClassPath().addListener((_, _, newValue) -> {
            directoryWatcherService = new DirectoryWatcherService(newValue);
            ObservableList<String> observableArrayList = FXCollections.observableArrayList(directoryWatcherService.getAvailableFiles());
            mainView.setOptionsList(new FilteredList<>(observableArrayList));
            mainView.rightLabelProperty().bind(directoryWatcherService.updateTimestampProperty());
        });
        mainView.onTabCreated(tab -> directoryWatcherService.registerTab(tab));
        mainView.onTabClosed(tab -> directoryWatcherService.deRegisterTab(tab));
        mainView.setPrefSize(600, 600);
        primaryStage.setTitle("JavaFx Hot Code Reload");
        primaryStage.setScene(new Scene(mainView.getRootNode()));
        primaryStage.setOnCloseRequest(_ -> {
            if (directoryWatcherService != null) {
                directoryWatcherService.shutdown();
            }
            System.exit(0);
        });
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
