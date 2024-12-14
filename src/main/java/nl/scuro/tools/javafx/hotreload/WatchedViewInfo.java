package nl.scuro.tools.javafx.hotreload;

public class WatchedViewInfo {
    private String name;
    private String viewImplementation;
    private String path;

    public WatchedViewInfo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getViewImplementation() {
        return viewImplementation;
    }

    public void setViewImplementation(String viewImplementation) {
        this.viewImplementation = viewImplementation;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    
}
