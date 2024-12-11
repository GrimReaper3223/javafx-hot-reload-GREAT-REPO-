package nl.scuro.fxlivedisplay;

import java.io.File;
import java.net.MalformedURLException;

import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nl.scuro.fxlivedisplay.gui.MainView;

public class FxLiveDisplay extends Application {

    private DirectoryWatcherService directoryWatcherService;

    @Override
    public void start(Stage primaryStage) throws Exception {

        MainView mainView = new MainView();
        mainView.getSelectedClassPath().addListener((ChangeListener<File>) (observable, oldValue, newValue) -> {
            directoryWatcherService = new DirectoryWatcherService(newValue);
            ObservableList<String> observableArrayList = FXCollections.observableArrayList(directoryWatcherService.getAvailableFiles());
            mainView.setFilteredList(new FilteredList<>(observableArrayList));
        });
        mainView.onTabCreated(tab -> directoryWatcherService.registerTab(tab));
        mainView.onTabClosed(tab -> directoryWatcherService.deRegisterTab(tab));
        mainView.setPrefSize(600, 600);
        Scene scene = new Scene(mainView);
        primaryStage.setTitle("JavaFx live code display");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(eh -> {
            if (directoryWatcherService != null) {
                directoryWatcherService.shutdown();
            }
        });
        primaryStage.show();
    }

    public static void main(String[] args) throws MalformedURLException {
        launch();
    }
}
