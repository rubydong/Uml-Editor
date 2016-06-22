/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pm.control;

import java.awt.image.RenderedImage;
import java.io.File;
import java.util.ArrayList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Cursor;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import org.controlsfx.control.CheckComboBox;
import pm.JClassMaker;
import pm.MethodDiagram;
import pm.UmlDiagram;
import pm.VariableDiagram;
import pm.data.DataManager;
import pm.file.FileManager;
import pm.gui.Workspace;

/**
 *
 * @author Ruby
 */
public class ActionManager {

    JClassMaker app;
    Workspace workspace;
    DataManager dataManager;
    FileManager fileManager;
    ArrayList<UmlDiagram> umlList;

    public ActionManager(JClassMaker app) {
        this.app = app;
        workspace = (Workspace) app.getWorkspaceComponent();
        dataManager = (DataManager) app.getDataComponent();
        fileManager = (FileManager) app.getFileComponent();
        umlList = dataManager.getUML();
    }

    public ActionManager() {

    }

    public void textFieldEvents(TextArea classTextField, TextField packageTextField,
            CheckComboBox parentsBox) {
        classTextField.setOnKeyReleased(e -> {
            if (dataManager.getSelectedNumber() != -1) {
                if (dataManager.getUML().get(dataManager.getSelectedNumber()).IsInterface()) {
                    dataManager.getUML().get(dataManager.getSelectedNumber()).getClassLabel().setText(dataManager.properPrint("«interface»\n" + classTextField.getText()));
                    dataManager.getUML().get(dataManager.getSelectedNumber()).setClassName(dataManager.properPrint("«interface»\n" + classTextField.getText()));
                } else if (dataManager.getUML().get(dataManager.getSelectedNumber()).isImportClass()) {

                } else {
                    dataManager.getUML().get(dataManager.getSelectedNumber()).getClassLabel().setText(dataManager.properPrint(classTextField.getText()));
                    dataManager.getUML().get(dataManager.getSelectedNumber()).setClassName(dataManager.properPrint(classTextField.getText()));
                }
                parentsBox.getItems().remove(dataManager.getSelectedNumber());
                parentsBox.getItems().add(dataManager.getSelectedNumber(), classTextField.getText());
            }
        });

        packageTextField.setOnKeyReleased(e -> {
            try {
                if (dataManager.getSelectedNumber() != -1) {
                    dataManager.getUML().get(dataManager.getSelectedNumber()).setPackageName(dataManager.properPrint(packageTextField.getText()));
                }
            } catch (Exception ex) {
                System.out.print("oopsie");
            }
        });
    }

    public void zoomEvents(Button zoomInButton, Button zoomOutButton,
            Button selectButton, Button resizeButton, Pane leftPane) {
        zoomInButton.setOnMouseClicked(e -> {
            if (leftPane.getScaleX() < 1.2) {
                leftPane.setScaleX(leftPane.getScaleX() * 1.2);
                leftPane.setScaleY(leftPane.getScaleY() * 1.2);
            }
        });

        zoomOutButton.setOnMouseClicked(e -> {
            if (leftPane.getScaleX() >= 1) {
                leftPane.setScaleX(leftPane.getScaleX() / 1.2);
                leftPane.setScaleY(leftPane.getScaleY() / 1.2);
            }
        });

        selectButton.setOnAction(e -> {
            dataManager.setSelectedNumber(-1);
            dataManager.unhighlightDiagrams();
            resizeButton.setDisable(false);

            for (int i = 0; i < dataManager.getUML().size(); i++) {
                dataManager.getUML().get(i).setCursor(Cursor.DEFAULT);
            }
        });

        resizeButton.setOnMouseClicked(e -> {
            resizeButton.setDisable(true);
        });
    }

    public int removeLines(ArrayList parentsChildren, ArrayList<Line> lines, UmlDiagram currentDiagram,
            UmlDiagram classDiagram, Pane leftPane) {
        int index = parentsChildren.indexOf(classDiagram);
        Line line = lines.get(index);
        lines.remove(line);
        parentsChildren.remove(index);
        leftPane.getChildren().remove(line);
        return index;
    }

    public void removeEvent(Button removeButton, Button addClassButton, Button addInterfaceButton,
            Pane leftPane) {
        removeButton.setOnAction(e -> {
            try {
                int currentNumber = dataManager.getSelectedNumber();
                if (currentNumber != -1) {
                    UmlDiagram classDiagram = dataManager.getUML().get(currentNumber);
                    workspace = (Workspace) app.getWorkspaceComponent();
                    addClassButton.setDisable(false);
                    addInterfaceButton.setDisable(false);

                    Action action = new Action();
                    action.classRemoved(classDiagram, dataManager);
                    workspace.getUndo().push(action);
                    workspace.setPopped(true);
                    for (int i = 0; i < dataManager.getUML().size(); i++) {
                        UmlDiagram currentDiagram = dataManager.getUML().get(i);
                        if (currentDiagram.getParentInterfaces().contains(classDiagram)) {
                            int index = removeLines(currentDiagram.getParentInterfaces(), currentDiagram.getLines(),
                                    currentDiagram, classDiagram, leftPane);
                            Polygon diamond = currentDiagram.getDiamonds().get(index);
                            currentDiagram.getDiamonds().remove(diamond);
                            leftPane.getChildren().remove(diamond);
                        } else if (currentDiagram.getChildrenInterfaces().contains(classDiagram)) {
                            int index = currentDiagram.getChildrenInterfaces().indexOf(classDiagram);
                            currentDiagram.getChildrenInterfaces().remove(classDiagram);
                            currentDiagram.getDiamonds().remove(index);
                            leftPane.getChildren().removeAll(classDiagram.getLines());
                            leftPane.getChildren().removeAll(classDiagram.getDiamonds());
                        }
                        if (currentDiagram.getAggregateParents().contains(classDiagram)) {
                            int index = removeLines(currentDiagram.getAggregateParents(), currentDiagram.getAggregateLines(),
                                    currentDiagram, classDiagram, leftPane);
                            Polygon triangle = currentDiagram.getTriangles().get(index);
                            currentDiagram.getTriangles().remove(triangle);
                            leftPane.getChildren().remove(triangle);
                        } else if (currentDiagram.getAggregateChildren().contains(classDiagram)) {
                            int index = currentDiagram.getAggregateChildren().indexOf(classDiagram);
                            currentDiagram.getAggregateChildren().remove(classDiagram);
                            currentDiagram.getTriangles().remove(index);
                            leftPane.getChildren().removeAll(classDiagram.getAggregateLines());
                            leftPane.getChildren().removeAll(classDiagram.getTriangles());
                        }
                        if (currentDiagram.getAssociateParents().contains(classDiagram)) {
                            int index = removeLines(currentDiagram.getAssociateParents(), currentDiagram.getAssociateLines(),
                                    currentDiagram, classDiagram, leftPane);
                            Polygon arrow = currentDiagram.getArrows().get(index);
                            currentDiagram.getArrows().remove(arrow);
                            leftPane.getChildren().remove(arrow);
                        } else if (currentDiagram.getAssociateChildren().contains(classDiagram)) {

                            int index = currentDiagram.getAssociateChildren().indexOf(classDiagram);
                            currentDiagram.getAssociateChildren().remove(classDiagram);
                            currentDiagram.getArrows().remove(index);
                            leftPane.getChildren().removeAll(classDiagram.getAssociateLines());
                            leftPane.getChildren().removeAll(classDiagram.getArrows());
                        }
                    }
                    dataManager.getUML().remove(currentNumber);
                    dataManager.getLeftPane().getChildren().remove(classDiagram);
                    dataManager.unhighlightDiagrams();
                    if (!workspace.getParentsBox().getCheckModel().isEmpty()) {
                        workspace.getParentsBox().getItems().remove(currentNumber);
                    }
                    dataManager.getVariablesContainer().getChildren().clear();
                    dataManager.getMethodsContainer().getChildren().clear();
                    dataManager.makeMethodHeader();
                    dataManager.makeVariableHeader();
                }
            } catch (ArrayIndexOutOfBoundsException a) {
                System.out.print("Array out of bounds exception");
            }
        });

    }

    public void buttonEvents(Pane leftPane, Button addClassButton, Button addInterfaceButton,
            Button resizeButton, Button photoButton, CheckBox gridCheck) {
        addClassButton.setOnAction(e -> {
            addClassButton.setDisable(true);
            addInterfaceButton.setDisable(false);
            resizeButton.setDisable(false);
        });

        addInterfaceButton.setOnAction(e -> {
            addClassButton.setDisable(false);
            addInterfaceButton.setDisable(true);
            resizeButton.setDisable(false);
        });

        photoButton.setOnMouseClicked((MouseEvent e) -> {
            dataManager.setSelectedNumber(-1);
            dataManager.unhighlightDiagrams();
            WritableImage snapshot = leftPane.snapshot(new SnapshotParameters(), null);
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File("./work/"));
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Image Files", "png", "jpg"));

            try {
                RenderedImage renderedImage = SwingFXUtils.fromFXImage(snapshot, null);
                File selectedFile = fileChooser.showSaveDialog(app.getGUI().getWindow());
                ImageIO.write(renderedImage, "png", selectedFile);
            } catch (Exception a) {
                System.out.print("Snapshot cancelled");
            }
        });

        gridCheck.setOnMouseClicked(e -> {
            if (gridCheck.isSelected()) {
                leftPane.getStyleClass().add("left_pane_grid");
            } else {
                leftPane.getStyleClass().remove("left_pane_grid");
                leftPane.getStyleClass().add("left_pane");
            }
        });
    }

    public void variableMethods(Button addVariablesButton, Button removeVariablesButton,
            Button addMethodsButton, Button removeMethodsButton, Workspace workspace) {
        addVariablesButton.setOnMouseClicked(e -> {
            if (dataManager.getSelectedNumber() != -1) {
                VariableDiagram v = new VariableDiagram();
                v.makeVariableBox(dataManager, workspace, dataManager.getVariablesContainer());
            }
        });

        removeVariablesButton.setOnMouseClicked(e -> {
            int currentNumber = umlList.get(dataManager.getSelectedNumber()).getSelectedVariableNumber();
            if (currentNumber != -1) {
                HBox hbox = umlList.get(dataManager.getSelectedNumber()).getVariablesBoxes().get(currentNumber);
                umlList.get(dataManager.getSelectedNumber()).getVariablesBoxes().remove(hbox);
                dataManager.getVariablesContainer().getChildren().remove(hbox);

                VariableDiagram v = umlList.get(dataManager.getSelectedNumber()).getVariables().remove(currentNumber);
                String temp = umlList.get(dataManager.getSelectedNumber()).getVariablesLabel().getText();
                temp = temp.replace(v.getText() + "\n", "");
                umlList.get(dataManager.getSelectedNumber()).getVariablesLabel().setText(temp);
                umlList.get(dataManager.getSelectedNumber()).setSelectedVariableNumber(-1);
            }
        });

        addMethodsButton.setOnMouseClicked(e -> {
            if (dataManager.getSelectedNumber() != -1) {
                MethodDiagram m = new MethodDiagram();
                m.makeMethodBox(dataManager, workspace, dataManager.getMethodsContainer());
            }
        });

        removeMethodsButton.setOnMouseClicked(e -> {
            int currentNumber = umlList.get(dataManager.getSelectedNumber()).getSelectedMethodNumber();
            if (currentNumber != -1) {
                HBox hbox = umlList.get(dataManager.getSelectedNumber()).getMethodsBoxes().get(currentNumber);
                umlList.get(dataManager.getSelectedNumber()).getMethodsBoxes().remove(hbox);
                dataManager.getMethodsContainer().getChildren().remove(hbox);

                MethodDiagram m = umlList.get(dataManager.getSelectedNumber()).getMethods().remove(currentNumber);
                String temp = umlList.get(dataManager.getSelectedNumber()).getMethodsLabel().getText();
                temp = temp.replace(m.getText() + "\n", "");
                umlList.get(dataManager.getSelectedNumber()).getMethodsLabel().setText(temp);
                umlList.get(dataManager.getSelectedNumber()).setSelectedMethodNumber(-1);
            }
        });

    }

    public void unhighlightAllVariables() {
        for (int i = 0; i < umlList.get(dataManager.getSelectedNumber()).getVariablesBoxes().size(); i++) {
            umlList.get(dataManager.getSelectedNumber()).getVariablesBoxes().get(i).setStyle("-fx-background-color:#FFF5F2");
        }
    }

    public void unhighlightAllMethods() {
        for (int i = 0; i < umlList.get(dataManager.getSelectedNumber()).getVariablesBoxes().size(); i++) {
            umlList.get(dataManager.getSelectedNumber()).getVariablesBoxes().get(i).setStyle("-fx-background-color:#FFF5F2");
        }
    }

}
