package nl.scuro.fxlivedisplay.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.Skin;

public class AutoCompletePopoverSkin implements Skin<AutoCompletePopover> {
    private final AutoCompletePopover popover;

    public AutoCompletePopoverSkin(AutoCompletePopover popover, ObservableList<String> options) {
        this.popover = popover;
        popover.getListView().setItems(options);
        
    }

    @Override
    public AutoCompletePopover getSkinnable() {
        return popover;
    }

    @Override
    public Node getNode() {
        return popover.getListView();
    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'dispose'");
    }

}
