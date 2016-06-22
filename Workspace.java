package pm.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import pm.control.ActionManager;
import pm.JClassMaker;
import pm.UmlDiagram;
import pm.data.DataManager;
import saf.ui.AppGUI;
import saf.AppTemplate;
import saf.components.AppWorkspaceComponent;
import org.controlsfx.control.CheckComboBox;
import pm.control.Action;

public class Workspace extends AppWorkspaceComponent {

    Stage primaryStage;
    AppTemplate app;
    AppGUI gui;
    ActionManager actionManager;
    DataManager dataManager;
    ArrayList<UmlDiagram> umlList;
    Stack undoStack = new Stack();
    Stack redoStack = new Stack();
    HBox entireWorkspace = new HBox();
    ScrollPane leftPaneOuter = new ScrollPane();
    Pane leftPane = new Pane();
    GridPane rightPane = new GridPane();

    Label classNameLabel = new Label("Class \nName: ");
    Label packageLabel = new Label("Package: ");
    Label parentLabel = new Label("Parent or\nInterface: ");
    Label variablesLabel = new Label("Variables");
    Label methodsLabel = new Label("Methods");

    TextArea classTextField;
    TextField packageTextField;
    CheckComboBox parentsBox = new CheckComboBox();

    TextField parentTextField = new TextField();
    Button parentButton = new Button("Add parent");

    ScrollPane variablesScroll;
    VBox variablesContainer;
    Button addVariablesButton = new Button("+");
    Button removeVariablesButton = new Button("-");

    ScrollPane methodsScroll;
    VBox methodsContainer;
    Button addMethodsButton = new Button("+");
    Button removeMethodsButton = new Button("-");
    int numChecked = -1;
    double x;
    double y;
    double shapeX;
    double shapeY;
    Button addClassButton;
    Button addInterfaceButton;
    Button resizeButton;
    boolean popped = false;
    boolean redoPopped = false;
    CheckBox snapCheck;
    boolean loadCheck = true;

    public Workspace(AppTemplate initApp) throws IOException {
        app = initApp;
        gui = app.getGUI();
        dataManager = (DataManager) app.getDataComponent();
        actionManager = new ActionManager((JClassMaker) app);
        umlList = dataManager.getUML();
        classTextField = dataManager.getClassTextField();
        packageTextField = dataManager.getPackageTextField();
        addClassButton = gui.getAddClassButton();
        addInterfaceButton = gui.getAddInterfaceButton();

        methodsContainer = dataManager.getMethodsContainer();
        variablesContainer = dataManager.getVariablesContainer();
        methodsScroll = dataManager.getMethodsScroll();
        variablesScroll = dataManager.getVariablesScroll();

        Button selectButton = gui.getSelectButton();
        Button removeButton = gui.getRemoveButton();
        Button photoButton = gui.getPhotoButton();
        resizeButton = gui.getResizeButton();
        Button zoomInButton = gui.getZoomInButton();
        Button zoomOutButton = gui.getZoomOutButton();
        Button undoButton = gui.getUndoButton();
        Button redoButton = gui.getRedoButton();
        CheckBox gridCheck = gui.getGridCheck();
        snapCheck = gui.getSnapCheck();

        addToRightPane();
        initStyle();

        variablesScroll.setContent(variablesContainer);
        dataManager.makeVariableHeader();

        methodsScroll.setContent(methodsContainer);
        dataManager.makeMethodHeader();

        actionManager.textFieldEvents(classTextField, packageTextField, parentsBox);
        actionManager.buttonEvents(leftPane, addClassButton, addInterfaceButton, resizeButton,
                photoButton, gridCheck);
        actionManager.removeEvent(removeButton, addClassButton, addInterfaceButton, leftPane);
        actionManager.zoomEvents(zoomInButton, zoomOutButton,
                selectButton, resizeButton, leftPane);
        actionManager.variableMethods(addVariablesButton, removeVariablesButton,
                addMethodsButton, removeMethodsButton, this);

        leftPane.setOnMouseClicked(e -> {
            if (addClassButton.isDisabled() || addInterfaceButton.isDisabled()) {
                dataManager.setLoadingBack(false);
                UmlDiagram classDiagram = makeClassDiagram(e.getX(), e.getY());
                Action action = new Action();
                action.moved(classDiagram);
                undoStack.push(action);
                popped = true;
//                action.classAdded(classDiagram);
//                undoStack.push(action);
//                popped = true;
                dataManager.unhighlightDiagrams();
                addClassButton.setDisable(false);
                addInterfaceButton.setDisable(false);
            }
        });

        parentButton.setOnMouseClicked(e -> {
            if (!parentTextField.getText().isEmpty() && dataManager.getSelectedNumber() != -1) {
                UmlDiagram selectedDiagram = umlList.get(dataManager.getSelectedNumber());
                boolean proceed = true;

                for (int i = 0; i < dataManager.getUML().size(); i++) {
                    if (parentTextField.getText().equals(dataManager.getUML().get(i).getClassName().replace("«interface»\n", "")
                    )) {
                        //if class exists but no line
                        if (!selectedDiagram.getParentInterfacesText().contains(parentTextField.getText())
                                || !selectedDiagram.getParentInterfacesText().contains("«interface»\n" + parentTextField.getText())) {
                            Line l = createLine(selectedDiagram, dataManager.getUML().get(i), "parent");
                            proceed = false;
                        }
                    }
                }
                if (proceed) {
                    UmlDiagram classDiagram = makeClassDiagram(300.0, 300.0);
                    classDiagram.setClassName(parentTextField.getText());
                    classDiagram.getClassLabel().setText(parentTextField.getText());
                    Line l = createLine(selectedDiagram, classDiagram, "parent");

                    parentsBox.getItems().add(classDiagram.getClassName());
                    //make new one completely    
                }
            }

            parentTextField.clear();

        }
        );
        undoButton.setOnMouseClicked(e
                -> {
                    if (!undoStack.empty()) {
                        if (popped == false && undoStack.size() > 1) {
                            redoStack.push(undoStack.pop());
                        }
                        Action temp = (Action) undoStack.pop();
                        redoStack.push(temp);
                        popped = true;
                        redoPopped = false;
                        temp.loadBack(dataManager, this);
                    }
                }
        );

        redoButton.setOnMouseClicked(e
                -> {
                    if (!redoStack.empty()) {
                        if (redoPopped == false && redoStack.size() > 1) {
                            undoStack.push(redoStack.pop());
                        }
                        Action temp = (Action) redoStack.pop();
                        undoStack.push(temp);
                        redoPopped = true;
                        popped = false;
                        temp.loadBack(dataManager, this);
                    }
                }
        );

        parentsBox.getCheckModel().getCheckedIndices().addListener(new ListChangeListener<String>() {

            public void onChanged(ListChangeListener.Change<? extends String> c) {
                UmlDiagram selectedDiagram = dataManager.getUML().get(dataManager.getSelectedNumber());

                if (dataManager.getSelectedNumber() != -1 && loadCheck) {
                    String checkedItems = "" + parentsBox.getCheckModel().getCheckedItems();
                    checkedItems = checkedItems.replace("[", "");
                    checkedItems = checkedItems.replace("]", "");
                    String[] finalChecked;
                    if (checkedItems.contains(",")) {
                        finalChecked = checkedItems.split(", ");
                    } else if (checkedItems.equals("")) {
                        finalChecked = new String[0];
                    } else {
                        finalChecked = new String[1];
                        finalChecked[0] = checkedItems + "";
                    }
                    UmlDiagram currentDiagram = null;
                    for (int i = 0; i < finalChecked.length; i++) {
                        for (int j = 0; j < dataManager.getUML().size(); j++) {
                            currentDiagram = dataManager.getUML().get(j);
                            if (((currentDiagram.getClassName().equals(finalChecked[i])
                                    || currentDiagram.getClassName().equals("«interface»\n" + finalChecked[i]))
                                    && !selectedDiagram.getParentInterfacesText().contains(currentDiagram.getClassName()))) {
                                Line l = createLine(selectedDiagram, currentDiagram, "parent");
                            }
                        }
                    }

                    for (int i = 0; i < parentsBox.getCheckModel().getItemCount(); i++) {
                        //if it's not selected
                        if (!parentsBox.getCheckModel().getCheckedItems().contains(parentsBox.getCheckModel().getItem(i))) {
                            for (int j = 0; j < selectedDiagram.getParentInterfacesText().size(); j++) {
                                String name = selectedDiagram.getParentInterfacesText().get(j);
                                name = name.replace("«interface»\n", "");
                                if (parentsBox.getCheckModel().getItem(i).equals(name)) {
                                    selectedDiagram.getParentInterfacesText().remove(j);
                                    selectedDiagram.getParentInterfaces().remove(j);
                                    Line line = selectedDiagram.getLines().get(j);
                                    Polygon diamond = selectedDiagram.getDiamonds().get(j);
                                    selectedDiagram.getLines().remove(line);
                                    selectedDiagram.getDiamonds().remove(diamond);

                                    UmlDiagram classDiagram = findUML(name);
                                    if (classDiagram == null) {
                                        name = "«interface»\n" + name;
                                        classDiagram = findUML(name);
                                    }
                                    classDiagram.getChildrenInterfaces().remove(selectedDiagram);
                                    classDiagram.getDiamonds().remove(diamond);
                                    leftPane.getChildren().remove(line);
                                    leftPane.getChildren().remove(diamond);
                                    System.out.println("it removed");
                                }
                            }
                        }
                    }
                }
            }
        }
        );

        workspace = new Pane();
        entireWorkspace.setPrefSize(
                gui.getPrimaryScene().getWidth(),
                gui.getPrimaryScene().getHeight() - 50);
        entireWorkspace.getChildren()
                .addAll(leftPaneOuter, rightPane);
        workspace.getChildren()
                .add(entireWorkspace);
        workspaceActivated = false;
    }

    public UmlDiagram findUML(String className) {
        UmlDiagram classDiagram = null;
        for (int j = 0; j < dataManager.getUML().size(); j++) {
            if (dataManager.getUML().get(j).getClassName().equals(className)) {
                classDiagram = dataManager.getUML().get(j);
            }
        }
        return classDiagram;
    }

    public void shapeAction(Node shape, Line line, UmlDiagram uml2) {

        shape.setLayoutX(uml2.getLayoutX());
        shape.setLayoutY(uml2.getLayoutY());
        line.endXProperty().bind(shape.layoutXProperty());
        line.endYProperty().bind(shape.layoutYProperty());
        leftPane.getChildren().add(shape);

        shape.setOnMousePressed(e -> {
            shapeX = e.getX();
            shapeY = e.getY();
        });
        shape.setOnMouseDragged(e -> {
            double xValue = (e.getX() - shapeX) + shape.getLayoutX();
            double yValue = (e.getY() - shapeY) + shape.getLayoutY();
            if (xValue + 5 >= uml2.getLayoutX() && xValue - 2 <= (uml2.getLayoutX() + uml2.getWidth())
                    && yValue + 5 >= uml2.getLayoutY() && yValue <= (uml2.getLayoutY() + uml2.getHeight())) {
                shape.setLayoutX((e.getX() - shapeX) + shape.getLayoutX());
                shape.setLayoutY((e.getY() - shapeY) + shape.getLayoutY());
            }
        });
    }

    public Line createLine(UmlDiagram uml1, UmlDiagram uml2, String type) {
        Line line = new Line();
        line.startXProperty().bind(uml1.layoutXProperty());
        line.startYProperty().bind(uml1.layoutYProperty());
        if (type.equals("parent")) {
            Polygon diamond = new Polygon();
            diamond.getPoints().addAll(new Double[]{
                5.0, 0.0,
                0.0, 7.0,
                5.0, 14.0,
                10.0, 7.0
            });
            diamond.setFill(Color.WHITE);
            diamond.setStroke(Color.BLACK);
            if (!uml1.getParentInterfaces().contains(uml2)) {
                uml1.getParentInterfaces().add(uml2);
                uml1.getParentInterfacesText().add(uml2.getClassName());
                uml1.getLines().add(line);
                uml1.getDiamonds().add(diamond);
            }
            if (!uml2.getChildrenInterfaces().contains(uml1)) {
                uml2.getChildrenInterfaces().add(uml1);
                uml2.getDiamonds().add(diamond);
            }
            shapeAction(diamond, line, uml2);

        } else if (type.equals("aggregate")) {
            //variables
            Polygon triangle = new Polygon();
            triangle.getPoints().addAll(new Double[]{
                5.0, 10.0,
                0.0, 0.0,
                10.0, 0.0

            });
            triangle.setFill(Color.WHITE);
            triangle.setStroke(Color.BLACK);
            shapeAction(triangle, line, uml2);
            if (!uml1.getAggregateParents().contains(uml2)) {
                uml1.getAggregateParents().add(uml2);
                uml1.getAggregateLines().add(line);
                uml1.getTriangles().add(triangle);
            }
            if (!uml2.getAggregateChildren().contains(uml1)) {
                uml2.getAggregateChildren().add(uml1);
                uml2.getTriangles().add(triangle);
            }

        } else if (type.equals("associate")) {
            Polygon arrow = new Polygon();
            arrow.getPoints().addAll(new Double[]{
                0.0, 0.0,
                5.0, 10.0,
                10.0, 0.0,
                5.0, 3.0});
            arrow.setFill(Color.BLACK);
            //arrow.setStroke(Color.BLACK);
            shapeAction(arrow, line, uml2);

            if (!uml1.getAssociateParents().contains(uml2)) {
                uml1.getAssociateParents().add(uml2);
                uml1.getAssociateLines().add(line);
                uml1.getArrows().add(arrow);
            }
            if (!uml2.getAssociateChildren().contains(uml1)) {
                uml2.getAssociateChildren().add(uml1);
                uml2.getArrows().add(arrow);
            }
        }
        leftPane.getChildren().add(line);
        return line;
    }

    public UmlDiagram makeClassDiagram(Double x, Double y) {
        UmlDiagram classDiagram = new UmlDiagram();
        classDiagram.getStyleClass().add("class_diagram_pane");
        classDiagram.setLayoutX(x);
        classDiagram.setLayoutY(y);

        if (addClassButton.isDisabled()) {
            classDiagram.setInterface(false);
        } else if (addInterfaceButton.isDisabled()) {
            classDiagram.setInterface(true);
            classDiagram.getClassLabel().setText("«interface»\n");
        }
        if (x == 200.0 && y == 200.0) {
            classDiagram.getClassLabel().setText(dataManager.getCurrentImport());
            classDiagram.setClassName(dataManager.getCurrentImport());
            classDiagram.setIsImportClass(true);
            classDiagram.add(classDiagram.getClassLabel(), 1, 1);
        } else {
            classDiagram.add(classDiagram.getClassLabel(), 1, 1);
            classDiagram.add(classDiagram.getVariablesLabel(), 1, 2);
            classDiagram.add(classDiagram.getMethodsLabel(), 1, 3);
        }

        if (x != 300.0 && y != 300.0) {
            parentsBox.getItems().add(classDiagram.getClassName());
        }
        loadCheck = false;
        parentsBox.getCheckModel().clearChecks();
        loadCheck = true;
        classTextField.clear();
        packageTextField.clear();
        dataManager.getUML().add(classDiagram);
        leftPane.getChildren().add(classDiagram);
        mouseAction(classDiagram);
        return classDiagram;
    }

    public void clearBoxes(UmlDiagram classDiagram) {
        variablesContainer.getChildren().clear();
        dataManager.makeVariableHeader();
        int selectedNumber = dataManager.getSelectedNumber();
        if (!umlList.get(selectedNumber).getVariablesBoxes().isEmpty()) {
            for (int i = 0; i < umlList.get(selectedNumber).getVariablesBoxes().size(); i++) {
                variablesContainer.getChildren().add(umlList.get(selectedNumber).getVariablesBoxes().get(i));
            }
            actionManager.unhighlightAllVariables();
        }

        methodsContainer.getChildren().clear();
        dataManager.makeMethodHeader();
        if (!umlList.get(selectedNumber).getMethodsBoxes().isEmpty()) {
            for (int i = 0; i < umlList.get(selectedNumber).getMethodsBoxes().size(); i++) {
                methodsContainer.getChildren().add(umlList.get(selectedNumber).getMethodsBoxes().get(i));
            }
            actionManager.unhighlightAllMethods();
        }

        loadCheck = false;
        parentsBox.getCheckModel().clearChecks();
        for (int i = 0; i < classDiagram.getParentInterfacesText().size(); i++) {
            String parent = classDiagram.getParentInterfacesText().get(i);
            parent = parent.replace("«interface»\n", "");
            if (parentsBox.getCheckModel().getItemIndex(parent) != -1) {
                parentsBox.getCheckModel().check(parent);
            }
        }
        loadCheck = true;
    }

    public void mouseAction(UmlDiagram classDiagram) {
        classDiagram.setOnMousePressed(e -> {
            x = e.getX();
            y = e.getY();
            classDiagram.setEffect(new DropShadow());
            dataManager.setSelectedNumber(dataManager.getUML().indexOf(classDiagram));
            clearBoxes(classDiagram);

            classTextField.setText(dataManager.getUML().get(dataManager.getSelectedNumber()).getClassName());
            packageTextField.setText(dataManager.getUML().get(dataManager.getSelectedNumber()).getPackageName());
            dataManager.unhighlightDiagrams();
        });

        classDiagram.setOnMouseDragged(e -> {
            classDiagram.setEffect(new DropShadow());
            dataManager.setSelectedNumber(umlList.indexOf(classDiagram));
            clearBoxes(classDiagram);

            if (resizeButton.isDisabled()) {
                classDiagram.setCursor(Cursor.SE_RESIZE);
                classDiagram.setHgrow(classDiagram.getClassLabel(), Priority.ALWAYS);
                classDiagram.setHgrow(classDiagram.getVariablesLabel(), Priority.ALWAYS);
                classDiagram.setHgrow(classDiagram.getMethodsLabel(), Priority.ALWAYS);
                classDiagram.setVgrow(classDiagram.getClassLabel(), Priority.ALWAYS);
                classDiagram.setVgrow(classDiagram.getVariablesLabel(), Priority.ALWAYS);
                classDiagram.setVgrow(classDiagram.getMethodsLabel(), Priority.ALWAYS);
                classDiagram.setPrefHeight(e.getY() - classDiagram.getMaxHeight());
                classDiagram.setPrefWidth(e.getX() - classDiagram.getMaxWidth());

            } else if (snapCheck.isSelected()) {
                classDiagram.setCursor(Cursor.DEFAULT);
                int xValue = 10 * (int) Math.round(((e.getX() - x) + classDiagram.getLayoutX()) / 10);
                int yValue = 10 * (int) Math.round(((e.getY() - y) + classDiagram.getLayoutY()) / 10);
                classDiagram.setLayoutX(xValue + 0.5);
                classDiagram.setLayoutY(yValue + 0.5);
            } else {
                classDiagram.setCursor(Cursor.DEFAULT);
                classDiagram.setLayoutX((e.getX() - x) + classDiagram.getLayoutX());
                classDiagram.setLayoutY((e.getY() - y) + classDiagram.getLayoutY());
            }

            if (!classDiagram.getChildrenInterfaces().isEmpty()) {
                for (int i = 0; i < classDiagram.getDiamonds().size(); i++) {
                    classDiagram.getDiamonds().get(i).setLayoutX(classDiagram.getLayoutX());
                    classDiagram.getDiamonds().get(i).setLayoutY(classDiagram.getLayoutY());
                }
            }

            if (!classDiagram.getAggregateChildren().isEmpty()) {
                for (int i = 0; i < classDiagram.getTriangles().size(); i++) {
                    classDiagram.getTriangles().get(i).setLayoutX(classDiagram.getLayoutX());
                    classDiagram.getTriangles().get(i).setLayoutY(classDiagram.getLayoutY());
                }
            }
            if (!classDiagram.getAssociateChildren().isEmpty()) {
                for (int i = 0; i < classDiagram.getArrows().size(); i++) {
                    classDiagram.getArrows().get(i).setLayoutX(classDiagram.getLayoutX());
                    classDiagram.getArrows().get(i).setLayoutY(classDiagram.getLayoutY());
                }
            }
            classTextField.setText(umlList.get(dataManager.getSelectedNumber()).getClassName());
            packageTextField.setText(umlList.get(dataManager.getSelectedNumber()).getPackageName());
            dataManager.unhighlightDiagrams();
        });

        classDiagram.setOnMouseReleased(e -> {
            if (dataManager.getSelectedNumber() != -1) {
                Action action = new Action();
                action.moved(classDiagram);
                undoStack.push(action);
                popped = false;
            }
        });
    }

    public void addToRightPane() {
        rightPane.add(classNameLabel, 1, 1); //column, row
        rightPane.add(classTextField, 2, 1);

        rightPane.add(packageLabel, 1, 2);
        rightPane.add(packageTextField, 2, 2);

        HBox h = new HBox();
        h.getChildren().add(parentTextField);
        parentTextField.setPrefHeight(25);
        parentTextField.setStyle("-fx-border-radius: 5px;");
        parentButton.setPrefHeight(25);
        h.getChildren().add(parentButton);
        rightPane.add(parentLabel, 1, 3);
        rightPane.add(parentsBox, 2, 3);
        rightPane.add(h, 2, 4);

        HBox h1 = new HBox();
        h1.setAlignment(Pos.CENTER);
        h1.setSpacing(6);
        h1.getChildren().add(variablesLabel);
        addVariablesButton.setPrefSize(30, 30);
        h1.getChildren().add(addVariablesButton);
        removeVariablesButton.setPrefSize(30, 30);
        h1.getChildren().add(removeVariablesButton);
        rightPane.add(h1, 1, 5, 3, 1);
        rightPane.add(variablesScroll, 1, 6, 2, 1);

        HBox h2 = new HBox();
        h2.setAlignment(Pos.CENTER);
        h2.setSpacing(6);
        h2.getChildren().add(methodsLabel);
        h2.getChildren().add(addMethodsButton);
        addMethodsButton.setPrefSize(30, 30);
        h2.getChildren().add(removeMethodsButton);
        removeMethodsButton.setPrefSize(30, 30);
        rightPane.add(h2, 1, 7, 3, 1);
        rightPane.add(methodsScroll, 1, 8, 2, 1);
        rightPane.setVisible(true);
    }

    public Pane getLeftPane() {
        return leftPane;
    }

    public CheckComboBox getParentsBox() {
        return parentsBox;
    }

    public void setLoadCheck(boolean answer) {
        this.loadCheck = answer;
    }

    public Stack getRedo() {
        return redoStack;
    }

    public Stack getUndo() {
        return undoStack;
    }

    public boolean getPopped() {
        return popped;
    }

    public void setPopped(boolean popped) {
        this.popped = popped;
    }

    @Override
    public void initStyle() {
        leftPaneOuter.setPrefSize(gui.getWindow().getWidth() / 1.5, gui.getWindow().getHeight() / 3);
        leftPaneOuter.getStyleClass().add("left_pane_outer");

        leftPane.getStyleClass().add("left_pane");
        leftPaneOuter.setContent(leftPane);

        rightPane.setPrefSize(gui.getWindow().getWidth() / 3, gui.getWindow().getHeight() / 3);
        rightPane.setStyle("-fx-background-color: #FFF5F2");
        rightPane.setAlignment(Pos.CENTER);

        classTextField.setPrefSize(300, 40);
        packageTextField.setPrefSize(300, 25);

        classNameLabel.getStyleClass().add("prompt_text_field");
        packageLabel.getStyleClass().add("prompt_text_field");
        parentLabel.getStyleClass().add("prompt_text_field");
        variablesLabel.getStyleClass().add("prompt_text_field");
        methodsLabel.getStyleClass().add("prompt_text_field");

        parentsBox.setPrefSize(225, 25);

        variablesScroll.setPrefSize(330, 200);
        variablesScroll.setHbarPolicy(ScrollBarPolicy.NEVER);
        variablesContainer.setPrefSize(375, 200);
        variablesContainer.setStyle("-fx-background-color: #FFFFFF");
        variablesScroll.setStyle("-fx-focus-color: transparent");

        methodsScroll.setPrefSize(330, 200);
        methodsContainer.setPrefSize(560, 200);
        methodsContainer.setStyle("-fx-background-color: #FFFFFF");
        methodsScroll.setStyle("-fx-focus-color: transparent");
    }

    @Override
    public void reloadWorkspace() {

    }
}
