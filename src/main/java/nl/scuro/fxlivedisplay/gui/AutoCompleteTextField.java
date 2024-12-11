package nl.scuro.fxlivedisplay.gui;

import javafx.scene.control.TextField;

public class AutoCompleteTextField extends TextField {
    AutoCompletePopover autoCompletePopover;

    public AutoCompleteTextField() {
        autoCompletePopover = new AutoCompletePopover();
    }
    
}
