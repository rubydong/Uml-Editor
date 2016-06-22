package pm.data;

import java.util.ArrayList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import pm.UmlDiagram;
import pm.gui.Workspace;
import saf.components.AppDataComponent;
import saf.AppTemplate;

/**
 * This class serves as the data management component for this application.
 *
 * @author Richard McKenna
 * @author ?
 * @version 1.0
 */
public class DataManager implements AppDataComponent {

    AppTemplate app;
    ArrayList<UmlDiagram> UmlDiagrams = new ArrayList<>();
    int selectedNumber = -1;
   
    TextArea classTextField = new TextArea();
    TextField packageTextField = new TextField();
    VBox variablesContainer = new VBox();
    VBox methodsContainer = new VBox();
    ScrollPane variablesScroll = new ScrollPane();
     ScrollPane methodsScroll = new ScrollPane();
    String currentImport;
    boolean loadingBackDiagram;
    
    public DataManager(AppTemplate initApp) throws Exception {
        app = initApp;
    }

    public DataManager() {
    }

    public Workspace getWorkspace() {
        Workspace workspace = (Workspace) app.getWorkspaceComponent();
        return workspace;
    }

    public boolean isLoadingBack() {
        return loadingBackDiagram;
    }
    public void setLoadingBack(boolean answer) {
        this.loadingBackDiagram = answer;
    }
    public ScrollPane getVariablesScroll() {
        return variablesScroll;
    }

    public ScrollPane getMethodsScroll() {
          return methodsScroll;
    }

    public void unhighlightDiagrams() {
        for (int i = 0; i < UmlDiagrams.size(); i++) {
            if (selectedNumber != i) {
                UmlDiagrams.get(i).setEffect(null);
            }
        }
    }

    public void setCurrentImport(String currentImport) {
        this.currentImport = currentImport;
    }
    
    public String getCurrentImport() {
        return currentImport;
    }
    
    public void makeVariableHeader() {
        HBox h = new HBox();
        h.getStyleClass().add("hbox_variable_header");

        Label name = new Label("Name");
        name.setPrefWidth(50);
        Label type = new Label("Type");
        type.setPrefWidth(50);
        Label staticNonstatic = new Label("Static");
        staticNonstatic.setPrefWidth(50);
        Label access = new Label("Access");
        access.setPrefWidth(55);

        Label submit = new Label("Submit");
        submit.setPrefWidth(56);
        h.setSpacing(10);
        h.setAlignment(Pos.CENTER);
        h.getChildren().add(name);
        h.getChildren().add(type);
        h.getChildren().add(staticNonstatic);
        h.getChildren().add(access);
        h.getChildren().add(submit);
        variablesContainer.getChildren().add(h);
    }

    public void makeMethodHeader() {
        HBox h = new HBox();
        h.getStyleClass().add("hbox_variable_header");
        h.setSpacing(7);
        h.setAlignment(Pos.CENTER);

        Label name = new Label("Name");
        name.setPrefWidth(60);
        Label returnType = new Label("Return");
        returnType.setPrefWidth(60);
        Label check = new Label("Check");
        check.setPrefWidth(70);
        Label modifier = new Label("Modifier");
        modifier.setPrefWidth(70);
        Label arg1 = new Label("Arg1");
        arg1.setPrefWidth(60);
        Label arg2 = new Label("Arg2");
        arg2.setPrefWidth(60);
        Label arg3 = new Label("Arg3");
        arg3.setPrefWidth(60);
        Label submit = new Label("Submit");
        submit.setPrefWidth(60);
        h.getChildren().add(name);
        h.getChildren().add(returnType);
        h.getChildren().add(check);
        h.getChildren().add(modifier);
        h.getChildren().add(arg1);
        h.getChildren().add(arg2);
        h.getChildren().add(arg3);
        h.getChildren().add(submit);
        methodsContainer.getChildren().add(h);
    }
    
     public void unhighlightVariables() {
         int number = UmlDiagrams.get(selectedNumber).getSelectedVariableNumber();
         
        for (int i = 0; i < UmlDiagrams.get(selectedNumber).getVariablesBoxes().size(); i++) {
            if (number != i) {
                UmlDiagrams.get(selectedNumber).getVariablesBoxes().get(i).setStyle("-fx-background-color:#FFF5F2");
            }
        }
    }

    public void unhighlightMethods() {
        int number = UmlDiagrams.get(selectedNumber).getSelectedMethodNumber();
         for (int i = 0; i < UmlDiagrams.get(selectedNumber).getMethodsBoxes().size(); i++) {
            if (number != i) {
                UmlDiagrams.get(selectedNumber).getMethodsBoxes().get(i).setStyle("-fx-background-color:#FFF5F2");
            }
        }
    }

    public VBox getVariablesContainer() {
        return variablesContainer;
    }

    public VBox getMethodsContainer() {
        return methodsContainer;
    }

    public ArrayList<UmlDiagram> getUML() {
        return UmlDiagrams;
    }
    

    public String properPrint(String text) {
        text = text.replaceAll(" ", "");
        return text;
    }

    public AppTemplate getApp() {
        return app;
    }

    public int getSelectedNumber() {
        return selectedNumber;
    }

    public void setSelectedNumber(int selectedNumber) {
        this.selectedNumber = selectedNumber;
    }

    public Pane getLeftPane() {
        Workspace workspace = (Workspace) app.getWorkspaceComponent();
        return workspace.getLeftPane();
    }

    public TextArea getClassTextField() {
        return classTextField;
    }

    public TextField getPackageTextField() {
        return packageTextField;
    }

    @Override
    public void reset() {
        Workspace workspace = (Workspace) app.getWorkspaceComponent();
        workspace.getLeftPane().getChildren().clear();
        workspace.getLeftPane().setPrefSize(1500, 800);
        workspace.getLeftPane().setScaleX(1.0);
        workspace.getLeftPane().setScaleY(1.0);
        unhighlightDiagrams();

        UmlDiagrams.clear();
        packageTextField.clear();
        classTextField.clear();
        variablesContainer.getChildren().clear();
        methodsContainer.getChildren().clear();
           
        workspace.getUndo().clear();
        workspace.getRedo().clear();
        workspace.getParentsBox().getItems().clear();
        
        makeVariableHeader();
        makeMethodHeader();
    }
}
