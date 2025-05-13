package nl.scuro.tools.javafx.hotreload.gui;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;

import org.controlsfx.control.HiddenSidesPane;
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
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.DirectoryChooser;
import nl.scuro.tools.javafx.hotreload.SelectableTheme;

public class MainView extends BorderPane {
    private TabPane tabPane;
    private HiddenSidesPane logSidePane;

    private Label leftStatusLabel = new Label("No classpath selected");
    private Label rightStatusLabel = new Label("No component loaded yet");
    private Label logIndicatorLabel = new Label("Log Register");
    private SimpleObjectProperty<File> selectedClassPath = new SimpleObjectProperty<>();
    private Consumer<ReloadingTab> tabCreatedConsumer;
    private Consumer<ReloadingTab> tabClosedConsumer;
    private ObservableList<String> optionsList = FXCollections.observableArrayList();
    private FilteredList<String> filteredOptionsList = new FilteredList<>(optionsList);

    private TextArea logTextArea = new TextArea();
    private Button cleanButton = new Button("Clear", new FontIcon(Feather.TRASH_2));
    private Button copyButton = new Button("Copy", new FontIcon(Feather.COPY));
    private HBox buttonHBox = new HBox(copyButton, cleanButton);
    private VBox logBox = new VBox(logIndicatorLabel, logTextArea, buttonHBox);

    public MainView() {
    	// configure the hidden log panel
    	configureLogPane();

    	// classpath button config
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        var dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Choose the classpath folder");
        final Button setClasspathButton = new Button("Select Classpath", new FontIcon(Feather.FOLDER));
        setClasspathButton.setPrefWidth(200);
        setClasspathButton.setOnAction(_ -> selectedClassPath.setValue(dirChooser.showDialog(null)));
        setClasspathButton.visibleProperty().bind(Bindings.isNull(selectedClassPath));
        setClasspathButton.managedProperty().bind(Bindings.isNull(selectedClassPath));

    	// componentNameField config
        var componentNameField = new ComboBox<>(filteredOptionsList);
        componentNameField.visibleProperty().bind(Bindings.isNotNull(selectedClassPath));
        componentNameField.setPromptText("Type the fully qualified class name");
        HBox.setHgrow(componentNameField, Priority.ALWAYS);

        componentNameField.setOnMouseClicked(_ -> componentNameField.show());
        componentNameField.setEditable(true);
        componentNameField.getEditor().setOnKeyTyped(_ -> {

            filteredOptionsList.setPredicate(componentNameField
            		.getEditor()
            		.getText()
            		.isBlank() ? null : i -> i.toLowerCase()
            						  		  .contains(componentNameField.getEditor()
            						  				  						 .getText()
            						  				  						 .toLowerCase()));

            if (!componentNameField.isShowing()) {
                componentNameField.show();
            }
        });

    	// theme selector config
        ComboBox<SelectableTheme> themeSelector = new ComboBox<>();
        themeSelector.setItems(createThemeList());
        themeSelector.getSelectionModel().select(0);
        themeSelector.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> Application.setUserAgentStylesheet(newValue.getStyleSheet()));

    	// watch button config
        final Button newWatchButton = new Button("New Watch", new FontIcon(Feather.PLUS));
        newWatchButton.setDefaultButton(true);
        newWatchButton.getStyleClass().addAll(Styles.BUTTON_OUTLINED);
        newWatchButton.disableProperty().bind(Bindings.isEmpty(componentNameField.valueProperty().asString()));
        newWatchButton.visibleProperty().bind(Bindings.isNotNull(selectedClassPath));

        // toolbar config
        final var toolbar = new ToolBar(setClasspathButton, componentNameField, newWatchButton, themeSelector);
        toolbar.setPadding(new Insets(5));
        setTop(toolbar);

        // labels and actions config
        selectedClassPath.addListener(
                _ -> leftStatusLabel.setText("Classpath selected"));

        Label startMessage = new Label("Nothing here yet. Start by selecting a classpath and then load a view.");
        setCenter(startMessage);

        newWatchButton.setOnAction(_ -> {
            if (tabPane == null) {
                initTabContainer();
            }
            String name = componentNameField.getValue();
            componentNameField.setValue("");
        	ReloadingTab tab = new ReloadingTab(name, selectedClassPath.get(), leftLabelProperty());
        	tabPane.getTabs().add(tab);
        	tab.setOnCloseRequest(_ -> this.tabClosedConsumer.accept(tab));
        	this.tabCreatedConsumer.accept(tab);
        });

        // final bottom layout config
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

    private void configureLogPane() {
    	// implementation of log side pane and logBox config
    	logSidePane = new HiddenSidesPane(this, null, logBox, null, null);

    	logBox.setSpacing(10);
    	logBox.setPadding(new Insets(10));
    	logBox.setBackground(Background.fill(Color.LIGHTGRAY));
    	logBox.setAlignment(Pos.CENTER_LEFT);
    	buttonHBox.setSpacing(10);
    	buttonHBox.setPadding(new Insets(5, 0, 5, 5));
    	buttonHBox.setAlignment(Pos.CENTER_RIGHT);

    	logTextArea.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, new CornerRadii(3), BorderWidths.DEFAULT)));
    	logTextArea.setEditable(false);
    	logIndicatorLabel.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.MEDIUM, 20));
    	cleanButton.getStyleClass().add(Styles.BUTTON_OUTLINED);
    	copyButton.getStyleClass().add(Styles.BUTTON_OUTLINED);

    	VBox.setVgrow(logTextArea, Priority.SOMETIMES);

    	// define button logics
    	cleanButton.setOnAction(_ -> logTextArea.clear());
    	copyButton.setOnAction(_ -> {
    		logTextArea.selectAll();
    		logTextArea.copy();
    		logTextArea.deselect();
    	});
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

    public HiddenSidesPane getRootNode() {
    	return logSidePane;
    }

    public TextArea getLogTextArea() {
    	return logTextArea;
    }

    public StringProperty rightLabelProperty() {
        return rightStatusLabel.textProperty();
    }

    public StringProperty leftLabelProperty() {
        return leftStatusLabel.textProperty();
    }
}
