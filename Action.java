/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pm.control;

import java.util.ArrayList;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import pm.MethodDiagram;
import pm.UmlDiagram;
import pm.VariableDiagram;
import pm.data.DataManager;
import pm.gui.Workspace;

/**
 *
 * @author Ruby
 */
public class Action {

    // if moved 
    // if new class/interface 
    // if resized
    double x;
    double y;
    double width;
    double height;
    UmlDiagram classDiagram;
    int selectedNumber;
    int numBeforeRemove;
    boolean moved = false;
    boolean classAdded = false;
    boolean classRemoved = false;
    String className;
    String packageName;
    ArrayList<VariableDiagram> variables = new ArrayList<>();
    ArrayList<MethodDiagram> methods = new ArrayList<>();

    String methodInputs = "";
    String variableInputs = "";

    public void moved(UmlDiagram classDiagram) {
        this.classDiagram = classDiagram;
        this.x = classDiagram.getLayoutX();
        this.y = classDiagram.getLayoutY();
        this.width = classDiagram.getPrefWidth();
        this.height = classDiagram.getPrefHeight();
        moved = true;
    }

    public void classAdded(UmlDiagram classDiagram) {
        this.classDiagram = classDiagram;
        x = classDiagram.getLayoutX();
        y = classDiagram.getLayoutY();
        width = classDiagram.getPrefWidth();
        height = classDiagram.getPrefHeight();
        classAdded = true;
    }

    public void classRemoved(UmlDiagram classDiagram, DataManager dataManager) {
        this.classDiagram = classDiagram;
        x = classDiagram.getLayoutX();
        y = classDiagram.getLayoutY();
        width = classDiagram.getPrefWidth();
        height = classDiagram.getPrefHeight();
        className = classDiagram.getClassName();
        packageName = classDiagram.getPackageName();
        variables = classDiagram.getVariables();
        methods = classDiagram.getMethods();
        int numBeforeRemove = dataManager.getUML().indexOf(classDiagram);

        String v = "";
        String m = "";

        for (int i = 0; i < variables.size(); i++) {
            v += variables.get(i).getText() + " ;";
            for (int j = 0; j < variables.get(i).getInputs().length; j++) {
                variableInputs += variables.get(i).getInputs()[j] + " ";
            }
            variableInputs += ";";
        }

        for (int i = 0; i < methods.size(); i++) {
            m += methods.get(i).getText() + " ;";
            for (int j = 0; j < methods.get(i).getInputs().length; j++) {
                methodInputs += methods.get(i).getInputs()[j] + " ";
            }
            methodInputs += ";";
        }
        classRemoved = true;

    }

    public UmlDiagram getUmlDiagram() {
        return classDiagram;
    }

    public void add(DataManager dataManager, Workspace workspace) {
        workspace.makeClassDiagram(classDiagram.getLayoutX(), classDiagram.getLayoutY());
        UmlDiagram uml = dataManager.getUML().get(dataManager.getUML().size() - 1);
        dataManager.getUML().remove(dataManager.getUML().size() - 1);
        dataManager.getUML().add(numBeforeRemove, uml);
        Label classLabel = new Label(className);
        uml.getClassLabel().setText(className);
        uml.setClassName(className);
        uml.setPackageName(packageName);
        uml.setPrefSize(width, height);

        String v = "";
        for (int i = 0; i < variables.size(); i++) {
            v += variables.get(i).getText() + "\n";
        }

        String m = "";
        for (int i = 0; i < methods.size(); i++) {
            m += methods.get(i).getText() + "\n";
        }

        uml.getVariablesLabel().setText(v);
        uml.getMethodsLabel().setText(m);
        uml.getChildren().clear();
        uml.add(classLabel, 1, 1);
        uml.add(uml.getVariablesLabel(), 1, 2);
        uml.add(uml.getMethodsLabel(), 1, 3);

        String[] va = variableInputs.split(" ;");
        for (int j = 0; j < va.length; j++) {
            VariableDiagram vee = new VariableDiagram();
            vee.loadVariableBox(dataManager, dataManager.getWorkspace(), dataManager.getVariablesContainer(), va[j]);
        }
        dataManager.getVariablesContainer().getChildren().clear();
        dataManager.makeVariableHeader();

        String[] ma = methodInputs.split(" ;");
        for (int j = 0; j < ma.length; j++) {
            MethodDiagram mee = new MethodDiagram();
            mee.loadMethodBox(dataManager, dataManager.getWorkspace(), dataManager.getMethodsContainer(), ma[j]);
        }
        dataManager.getMethodsContainer().getChildren().clear();
        dataManager.makeMethodHeader();
    }

    public void remove(DataManager dataManager, Workspace workspace) {
        System.out.println("ddig oin ");
        dataManager.getUML().remove(dataManager.getUML().indexOf(classDiagram));
        dataManager.getLeftPane().getChildren().remove(classDiagram);
        dataManager.unhighlightDiagrams();
//        workspace.getParentsBox().getItems().remove(classDiagram);
    }

    public void loadBack(DataManager dataManager, Workspace workspace) {
        if (classAdded && dataManager.getUML().size() > 0) {
            remove(dataManager, workspace);
        } else if (moved) {
            System.out.println("went into moved");
            classDiagram.setLayoutX(x);
            classDiagram.setLayoutY(y);
            classDiagram.setHgrow(classDiagram.getClassLabel(), Priority.ALWAYS);
            classDiagram.setHgrow(classDiagram.getVariablesLabel(), Priority.ALWAYS);
            classDiagram.setHgrow(classDiagram.getMethodsLabel(), Priority.ALWAYS);
            classDiagram.setVgrow(classDiagram.getClassLabel(), Priority.ALWAYS);
            classDiagram.setVgrow(classDiagram.getVariablesLabel(), Priority.ALWAYS);
            classDiagram.setVgrow(classDiagram.getMethodsLabel(), Priority.ALWAYS);
            classDiagram.setPrefSize(width, height);
        } else if (classRemoved) {
            add(dataManager, workspace);
        }
    }

    public int getSelectedNumber() {
        return selectedNumber;
    }
}
