package nl.scuro.fxlivedisplay;

import javafx.event.Event;
import javafx.event.EventType;

public class NodeUpdatedEvent extends Event {

    public static final EventType<NodeUpdatedEvent> RELOAD_TAB = new EventType<>(ANY, "Reload");

    public NodeUpdatedEvent(EventType<? extends Event> eventType) {
        super(eventType);
    }


}
