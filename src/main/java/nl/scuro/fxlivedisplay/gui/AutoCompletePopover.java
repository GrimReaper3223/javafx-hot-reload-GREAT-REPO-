package nl.scuro.fxlivedisplay.gui;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.PopupControl;
import javafx.scene.control.Skin;
import javafx.scene.control.TextInputControl;
import nl.scuro.fxlivedisplay.SuggestedItemSelectedEvent;

public class AutoCompletePopover extends PopupControl {

    private final ListView<String> listView = new ListView<>();
    ObservableList<String> options;
    

    public void show(Node parent, ObservableList<String> options) {
        System.out.println("here");
        this.options=options;
        final Scene scene = parent.getScene();

        final Point2D windowCoord = new Point2D(scene.getWindow()
                .getX(), scene.getWindow().getY());
        System.out.println("Window coorinates: " + windowCoord);
        final Point2D sceneCoord = new Point2D(scene.getX(), scene.getY());
        System.out.println("Scene coorinates: " + sceneCoord);
        final Point2D nodeCoord = parent.localToScene(0.0, 0.0);
        Bounds boundsInParent = parent.getBoundsInLocal();
        System.out.println("Node coorinates: " + nodeCoord);
        final double x = Math.round(windowCoord.getX()
                + sceneCoord.getX() + nodeCoord.getX());

        final double y = Math.round(windowCoord.getY()
                + sceneCoord.getY() + boundsInParent.getHeight());

        listView.setPrefWidth(boundsInParent.getWidth());
        listView.setPrefHeight(200);
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            parent.fireEvent(new SuggestedItemSelectedEvent(SuggestedItemSelectedEvent.ITEM_SELECTED, newValue));
            super.hide();
        });
        
        super.show(scene.getWindow(), x, y);
    }

    

    @Override
    protected Skin<?> createDefaultSkin() {

        return new AutoCompletePopoverSkin(this, options);
    }



    public ListView<String> getListView() {
        return listView;
    }

}
