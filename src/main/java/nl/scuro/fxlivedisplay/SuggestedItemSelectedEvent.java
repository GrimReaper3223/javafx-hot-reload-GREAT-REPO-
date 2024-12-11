package nl.scuro.fxlivedisplay;

import javafx.event.Event;
import javafx.event.EventType;

public class SuggestedItemSelectedEvent extends Event {

    public static final EventType<SuggestedItemSelectedEvent> ITEM_SELECTED = new EventType<>(ANY, "Suggested Item Selected");
    private String selectedValue;

    public SuggestedItemSelectedEvent(EventType<? extends Event> eventType, String selectedValue) {
        super(eventType);
        this.selectedValue = selectedValue;

    }

    public String getSelectedValue() {
        return selectedValue;
    }

}
