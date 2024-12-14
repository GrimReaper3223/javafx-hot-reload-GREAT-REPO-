package nl.scuro.tools.javafx.hotreload;

import atlantafx.base.theme.Theme;

public class SelectableTheme {
    private String name, stylesheet;

    public SelectableTheme(String name) {
        this.name=name;
    }

    public SelectableTheme(Theme theme) {
        this.name = theme.getName();
        this.stylesheet = theme.getUserAgentStylesheet();
    }

    @Override
    public String toString() {
        return name;
    }

    public String getStyleSheet() {
        return stylesheet;
    }

}
