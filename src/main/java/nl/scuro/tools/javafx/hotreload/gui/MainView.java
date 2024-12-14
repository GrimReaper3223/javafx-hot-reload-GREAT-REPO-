package nl.scuro.tools.javafx.hotreload.gui;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;

import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import atlantafx.base.theme.CupertinoDark;
import atlantafx.base.theme.CupertinoLight;
import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import atlantafx.base.theme.Styles;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;
import nl.scuro.tools.javafx.hotreload.SelectableTheme;

public class MainView extends BorderPane {
    private TabPane tabPane;

    private Label leftStatusLabel = new Label("No classpath selected");
    private Label rightStatusLabel = new Label("No component loaded yet");
    private SimpleObjectProperty<File> selectedClassPath = new SimpleObjectProperty<>();
    private Consumer<ReloadingTab> tabCreatedConsumer;
    private Consumer<ReloadingTab> tabClosedConsumer;
    private ObservableList<String> optionsList = FXCollections.observableArrayList();
    private FilteredList<String> filteredOptionsList = new FilteredList<>(optionsList);

    public MainView() {
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        var dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Choose the classpath folder");
        final Button setClasspathButton = new Button("Select Classpath", new FontIcon(Feather.FOLDER));
        setClasspathButton.setPrefWidth(200);
        setClasspathButton.setOnAction(eh -> selectedClassPath.setValue(dirChooser.showDialog(null)));
        setClasspathButton.visibleProperty().bind(Bindings.isNull(selectedClassPath));
        setClasspathButton.managedProperty().bind(Bindings.isNull(selectedClassPath));

        var componentNameField = new ComboBox<>(filteredOptionsList);
        componentNameField.visibleProperty().bind(Bindings.isNotNull(selectedClassPath));
        componentNameField.setPromptText("Type the fully qualified class name");
        HBox.setHgrow(componentNameField, Priority.ALWAYS);

        componentNameField.setOnMouseClicked(eh-> componentNameField.show());
        componentNameField.setEditable(true);
        componentNameField.getEditor().setOnKeyTyped(eh->{

            if (componentNameField.getEditor().getText().isBlank()) {
              filteredOptionsList.setPredicate(null);   
            }
            else filteredOptionsList.setPredicate(i->{
                boolean contains = i.toLowerCase().contains(componentNameField.getEditor().getText().toLowerCase());
                return contains;
            });
            
            if (!componentNameField.isShowing()) {
                componentNameField.show();
            }
        });

        ComboBox<SelectableTheme> themeSelector = new ComboBox<>();
        themeSelector.setItems(createThemeList());
        themeSelector.getSelectionModel().select(0);
        themeSelector.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            Application.setUserAgentStylesheet(newValue.getStyleSheet());
        });

        final Button newWatchButton = new Button("New Watch", new FontIcon(Feather.PLUS));
        newWatchButton.setDefaultButton(true);
        newWatchButton.getStyleClass().addAll(Styles.BUTTON_OUTLINED);
        newWatchButton.disableProperty().bind(Bindings.isEmpty(componentNameField.valueProperty().asString()));
        newWatchButton.visibleProperty().bind(Bindings.isNotNull(selectedClassPath));

        final var toolbar = new ToolBar(setClasspathButton, componentNameField, newWatchButton, themeSelector);
        toolbar.setPadding(new Insets(5));
        setTop(toolbar);

        selectedClassPath.addListener(
                cl -> leftStatusLabel.setText("Classpath selected"));

        Label startMessage = new Label("Nothing here yet. Start by selecting a classpath and then load a view.");
        setCenter(startMessage);

        newWatchButton.setOnAction(e -> {
            if (tabPane == null) {
                initTabContainer();
            }
            String name = componentNameField.getValue();
            componentNameField.setValue("");
            var tab = new ReloadingTab(name, selectedClassPath.get(), leftLabelProperty());
            tabPane.getTabs().add(tab);
            tab.setOnCloseRequest(eh -> this.tabClosedConsumer.accept(tab));
            this.tabCreatedConsumer.accept(tab);
        });

        leftStatusLabel.setPrefHeight(20);
        rightStatusLabel.setPrefHeight(20);
        AnchorPane.setLeftAnchor(leftStatusLabel, 10d);
        AnchorPane.setRightAnchor(rightStatusLabel, 10d);
        AnchorPane statusBar = new AnchorPane(leftStatusLabel, rightStatusLabel);
        setBottom(statusBar);
    }

    public void setOptionsList(List<String> list) {
        optionsList.setAll(list);
    }

    private ObservableList<SelectableTheme> createThemeList() {
        SelectableTheme fxDefault = new SelectableTheme("JavaFx default");
        SelectableTheme primerLight = new SelectableTheme(new PrimerLight());
        SelectableTheme primerDark = new SelectableTheme(new PrimerDark());
        SelectableTheme cupertinoLight = new SelectableTheme(new CupertinoLight());
        SelectableTheme cupertinoDark = new SelectableTheme(new CupertinoDark());
        return FXCollections.observableArrayList(primerLight, primerDark, cupertinoDark, cupertinoLight, fxDefault);
    }

    private void initTabContainer() {
        tabPane = new TabPane();
        tabPane.getStyleClass().add(Styles.DENSE);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.SELECTED_TAB);
        tabPane.setMinWidth(450);
        setCenter(tabPane);
    }

    public void onTabCreated(Consumer<ReloadingTab> consumer) {
        this.tabCreatedConsumer = consumer;
    }

    public void onTabClosed(Consumer<ReloadingTab> consumer) {
        this.tabClosedConsumer = consumer;
    }

    public SimpleObjectProperty<File> getSelectedClassPath() {
        return selectedClassPath;
    }

    public StringProperty rightLabelProperty() {
        return rightStatusLabel.textProperty();
    }

    public StringProperty leftLabelProperty() {
        return leftStatusLabel.textProperty();
    }

}
