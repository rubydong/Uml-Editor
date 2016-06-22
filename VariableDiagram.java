/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pm;

import java.util.ArrayList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import pm.data.DataManager;
import pm.gui.Workspace;

/**
 *
 * @author Ruby
 */
public class VariableDiagram {

    int counter = 0;
    HBox variablesRow;
    String text;
    String code;
    String[] inputs = new String[4];

    public VariableDiagram() {
    }

    public String[] getInputs() {
        return inputs;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCode() {
        return code;
    }

    public String getText() {
        return text;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public int getCounter() {
        return counter;
    }

    public HBox getVariableBox() {
        return variablesRow;
    }
// boolean , byte , char , short , int , long , float and doubl

    public void makeImportDiagram(String type, DataManager dataManager,
            Workspace workspace) {
        if (!type.equals("String")
                && !type.equals("int")
                && !type.equals("double")
                && !type.equals("boolean")
                && !type.equals("char")
                && !type.equals("byte")
                && !type.equals("short")
                && !type.equals("long")
                && !type.equals("float")) {
            boolean answer = true;
            for (int j = 0; j < dataManager.getUML().size(); j++) {
                if (dataManager.getUML().get(j).getClassName().equals(type)) {
                    answer = false;
                    //for class exists but need to make the line anyway!
                    UmlDiagram uml1 = dataManager.getUML().get(dataManager.getSelectedNumber());
                    UmlDiagram uml2 = dataManager.getUML().get(j);

                    if (!uml2.getAggregateParents().contains(uml1)) {
                        Line l = workspace.createLine(uml1, uml2, "aggregate");
                    }
                }
            }
            if (answer) {
                //class does not exist, so make completely new class
                dataManager.setCurrentImport(type);
                UmlDiagram uml1 = dataManager.getUML().get(dataManager.getSelectedNumber());
                UmlDiagram uml2 = workspace.makeClassDiagram(200.0, 200.0);
                Line l = workspace.createLine(uml1, uml2, "aggregate");
            }
        }
    }

    public void loadVariableBox(DataManager dataManager, Workspace workspace, VBox variablesBox, String loadInputs) {
        this.variablesRow = new HBox();
        variablesRow.getStyleClass().add("hbox_variables");
        ArrayList<UmlDiagram> umlList = dataManager.getUML();
        umlList.get(dataManager.getSelectedNumber()).getVariablesBoxes().add(variablesRow);
        umlList.get(dataManager.getSelectedNumber()).getVariables().add(this);
        variablesRow.setSpacing(12);
        variablesRow.setOnMouseClicked(e -> {
            int index = umlList.get(dataManager.getSelectedNumber()).getVariablesBoxes().indexOf(variablesRow);
            umlList.get(dataManager.getSelectedNumber()).setSelectedVariableNumber(index);
            variablesRow.setStyle("-fx-background-color: #FFFFFF");
            dataManager.unhighlightVariables();
        });
        variablesRow.setAlignment(Pos.CENTER);

        String[] inputsArray = loadInputs.split(" ");

        for (int i = 0; i < inputsArray.length; i++) {
            if (inputsArray[i].equals("empty")) {
                inputsArray[i] = "";
            }
        }
        TextField name = new TextField(inputsArray[0]);
        name.setPrefWidth(50);

        TextField type = new TextField(inputsArray[1]);
        type.setPrefWidth(50);

        TextField staticNonstatic = new TextField(inputsArray[2]);
        staticNonstatic.setPrefWidth(50);

        TextField modifier = new TextField(inputsArray[3]);
        modifier.setPrefWidth(50);

        Button submit = new Button("ok");
        submit.setPrefWidth(50);

        variablesRow.getChildren().add(name);
        variablesRow.getChildren().add(type);
        variablesRow.getChildren().add(staticNonstatic);
        variablesRow.getChildren().add(modifier);
        variablesRow.getChildren().add(submit);

        String variable2 = translateToUml(name.getText(), type.getText(), staticNonstatic.getText(), modifier.getText());
        String variable3 = convertToCode(name.getText(), type.getText(), staticNonstatic.getText(), modifier.getText(),
                umlList.get(dataManager.getSelectedNumber()).isInterface);
        this.setText(variable2);
        this.setCode(variable3);
        inputs[0] = name.getText().replace(" ", "");
        inputs[1] = type.getText().replace(" ", "");
        inputs[2] = staticNonstatic.getText().replace(" ", "");
        inputs[3] = modifier.getText().replace(" ", "");

        for (int i = 0; i < inputs.length; i++) {
            if (inputs[i].isEmpty()) {
                inputs[i] = "empty";
            }
        }
        submit.setOnAction(e -> {
            int selectedNumber = dataManager.getSelectedNumber();
            String variable = translateToUml(name.getText(), type.getText(), staticNonstatic.getText(), modifier.getText());
            String variable1 = convertToCode(name.getText(), type.getText(), staticNonstatic.getText(), modifier.getText(),
                    umlList.get(dataManager.getSelectedNumber()).isInterface);
            this.setText(variable);
            this.setCode(variable1);

            makeImportDiagram(type.getText(), dataManager, workspace);
            inputs[0] = name.getText().replace(" ", "");
            inputs[1] = type.getText().replace(" ", "");
            inputs[2] = staticNonstatic.getText().replace(" ", "");
            inputs[3] = modifier.getText().replace(" ", "");

            for (int i = 0; i < inputs.length; i++) {
                if (inputs[i].isEmpty()) {
                    inputs[i] = "empty";
                }
            }
            Label variableLabel = umlList.get(selectedNumber).getVariablesLabel();
            variableLabel.setText(variableLabel.getText() + variable + "\n");
            String variableEdited = "";
            for (int i = 0; i < umlList.get(selectedNumber).getVariables().size(); i++) {
                variableEdited += umlList.get(selectedNumber).getVariables().get(i).getText() + "\n";
            }
            System.out.println("variableEdited is " + variableEdited);
            umlList.get(selectedNumber).getVariablesLabel().setText(variableEdited);
            umlList.get(selectedNumber).getVariablesLabel().getText();
        });
        variablesBox.getChildren().add(variablesRow);
    }

    public void makeVariableBox(DataManager dataManager, Workspace workspace, VBox variablesBox) {
        this.variablesRow = new HBox();
        variablesRow.getStyleClass().add("hbox_variables");
        ArrayList<UmlDiagram> umlList = dataManager.getUML();
        umlList.get(dataManager.getSelectedNumber()).getVariablesBoxes().add(variablesRow);
        umlList.get(dataManager.getSelectedNumber()).getVariables().add(this);
        variablesRow.setSpacing(12);
        variablesRow.setOnMouseClicked(e -> {
            int index = umlList.get(dataManager.getSelectedNumber()).getVariablesBoxes().indexOf(variablesRow);
            umlList.get(dataManager.getSelectedNumber()).setSelectedVariableNumber(index);
            variablesRow.setStyle("-fx-background-color: #FFFFFF");
            dataManager.unhighlightVariables();
        });
        variablesRow.setAlignment(Pos.CENTER);
        int random = (int) (Math.random() * 50);
        TextField name = new TextField("myVar" + random);
        name.setPrefWidth(50);

        TextField type = new TextField("int");
        type.setPrefWidth(50);

        TextField staticNonstatic = new TextField("static");
        staticNonstatic.setPrefWidth(50);

        TextField modifier = new TextField("public");
        modifier.setPrefWidth(50);

        Button submit = new Button("ok");
        submit.setPrefWidth(50);

        variablesRow.getChildren().add(name);
        variablesRow.getChildren().add(type);
        variablesRow.getChildren().add(staticNonstatic);
        variablesRow.getChildren().add(modifier);
        variablesRow.getChildren().add(submit);

        submit.setOnAction(e -> {
            int selectedNumber = dataManager.getSelectedNumber();
            String variable = translateToUml(name.getText(), type.getText(), staticNonstatic.getText(), modifier.getText());
            String variable1 = convertToCode(name.getText(), type.getText(), staticNonstatic.getText(), modifier.getText(), 
                umlList.get(dataManager.getSelectedNumber()).isInterface);
            this.setText(variable);
            this.setCode(variable1);

            makeImportDiagram(type.getText(), dataManager, workspace);
            inputs[0] = name.getText().replace(" ", "");
            inputs[1] = type.getText().replace(" ", "");
            inputs[2] = staticNonstatic.getText().replace(" ", "");
            inputs[3] = modifier.getText().replace(" ", "");

            for (int i = 0; i < inputs.length; i++) {
                if (inputs[i].isEmpty()) {
                    inputs[i] = "empty";
                }
            }
            if (counter == 0) {
                Label variableLabel = umlList.get(selectedNumber).getVariablesLabel();
                variableLabel.setText(variableLabel.getText() + variable + "\n");
            } else {
                String variableEdited = "";
                for (int i = 0; i < umlList.get(selectedNumber).getVariables().size(); i++) {
                    variableEdited += umlList.get(selectedNumber).getVariables().get(i).getText() + "\n";
                }
                umlList.get(selectedNumber).getVariablesLabel().setText(variableEdited);
            }
            counter = 10;
        });
        variablesBox.getChildren().add(variablesRow);
    }

    public String convertToCode(String name, String type, String staticOrNot, String modifier, boolean interfaceOrNot) {
        String variable = "\t";
        if (!staticOrNot.equals("static")) {
            staticOrNot = "";
            variable += modifier + " " + type + " " + name;
        } else {
            variable += modifier + " " + staticOrNot + " " + type + " " + name;
        }
        
        if (interfaceOrNot ) {
            if (type.equals("double") || type.equals("int")) {
                variable += " = 0;";
            } 
            else if (type.equals("boolean")) {
                variable += " = false;";
            }
            else {
                variable += " = null;";
            }
        }
        else {
            
        variable += ";";

        }
        return variable;
    }

    public String translateToUml(String name, String type, String staticOrNot, String modifier) {
        String variable = "";
        if (modifier.contains("public") || modifier.equalsIgnoreCase("public")) {
            variable += "+";
        } else if (modifier.contains("private") || modifier.equalsIgnoreCase("private")) {
            variable += "-";
        } else if (modifier.contains("protected") || modifier.equalsIgnoreCase("protected")) {
            variable += "#";
        } else {
            variable += "";
        }
        if (staticOrNot.equals("static") || staticOrNot.equals("yes")) {
            variable += "$";
        }
        variable += name + " : " + type;
        return variable;
    }
}
