package nl.scuro.tools.javafx.hotreload;

import org.controlsfx.control.HiddenSidesPane;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

/**
 * Implementation of log consumer.<br>
 * This implementation sends all logs that were previously sent to standard output to a {@link TextArea} in a {@link HiddenSidesPane} on the frontend.
 * 
 * @author Deiv
 */
public class LogConsumer {

	private LogConsumer() {}

	private static TextArea logTextArea;

	public static void setLogTextAreaNode(TextArea textArea) {
		logTextArea = textArea;
	}

	public static void offerLog(String message) {
		Platform.runLater(() -> logTextArea.appendText(message + '\n'));
	}
}
