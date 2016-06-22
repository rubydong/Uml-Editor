/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pm;

import java.util.ArrayList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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
public class MethodDiagram {

    String text;
    String code;
    HBox methodsRow;
    int counter = 0;
    String[] inputs = new String[8];
    boolean abstractOrNot = false;

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public int getCounter() {
        return counter;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean getAbstract() {
        return abstractOrNot;
    }

    public String getCode() {
        return code;
    }

    public String getText() {
        return text;
    }

    public HBox getMethodBox() {
        return methodsRow;
    }

    public String[] getInputs() {
        return inputs;
    }

    public String convertToCode(String name, String returnType, boolean staticOrNot,
            boolean abstractOrNot, String modifier, String arg1, String arg2, String arg3, boolean interfaceOrNot) {
        String method = "\t" + modifier;

        if (staticOrNot) {
            method += " static ";
        } else if (abstractOrNot) {
            method += " abstract ";
            this.abstractOrNot = true;
        } else if (!staticOrNot && !abstractOrNot) {
            method += " ";
        }

        method += returnType + " " + name;

        if (arg1.isEmpty() && arg2.isEmpty() && arg3.isEmpty()) {
            method += "(";
        } else {
            if (!arg1.isEmpty()) {
                method += "(" + arg1 + " arg1";
            }
            if (!arg2.isEmpty()) {
                method += ", " + arg2 + " arg2";
            }
            if (!arg3.isEmpty()) {
                method += ", " + arg3 + "  arg3";
            }
        }
        if (abstractOrNot == false && interfaceOrNot == false) {
            method += ") {\n\t";

            if (returnType.equals("void")) {
                method += "";
            } else if (returnType.equals("int") || returnType.equals("double")) {
                method += "return 0;";
            } else if (returnType.equals("boolean")) {
                method += "return false;";
            } else {
                method += "return null;";
            }
            method += "\n\t}";
        } else {
            method += ");";

        }
        return method;
    }

    public String translateToUml(String name, String returnType, boolean staticOrNot,
            boolean abstractOrNot, String modifier, String arg1, String arg2, String arg3) {
        String method = "";

        if (abstractOrNot) {
            method += "{abstract}\n";
        }

        if (modifier.contains("public") || modifier.equalsIgnoreCase("public")) {
            method += "+";
        } else if (modifier.contains("private") || modifier.equalsIgnoreCase("private")) {
            method += "-";
        } else if (modifier.contains("protected") || modifier.equalsIgnoreCase("protected")) {
            method += "#";
        } else {
            method += "";
        }

        if (staticOrNot) {
            method += "$";
        }
        method += name;

        if (arg1.isEmpty() && arg2.isEmpty() && arg3.isEmpty()) {
            method += "(";
        } else {
            if (!arg1.isEmpty()) {
                method += "(arg1 : " + arg1;
            }
            if (!arg2.isEmpty()) {
                method += ", arg2 : " + arg2;
            }
            if (!arg3.isEmpty()) {
                method += ", arg3 : " + arg3;
            }
        }

        method += ") : " + returnType;
        return method;
    }

    public void makeImportDiagram(String returnType, DataManager dataManager,
            Workspace workspace) {
        if (!returnType.equals("String")
                && !returnType.equals("int")
                && !returnType.equals("double")
                && !returnType.equals("boolean")
                && !returnType.equals("char")
                && !returnType.equals("short")
                && !returnType.equals("long")
                && !returnType.equals("void")) {
            boolean answer = true;
            for (int j = 0; j < dataManager.getUML().size(); j++) {
                if (dataManager.getUML().get(j).getClassName().equals(returnType)) {
                    answer = false;
                    UmlDiagram uml1 = dataManager.getUML().get(dataManager.getSelectedNumber());
                    UmlDiagram uml2 = dataManager.getUML().get(j);
                    
                    if (!uml2.getAssociateParents().contains(uml1)) {
                        Line l = workspace.createLine(uml1, uml2, "associate");
                    }
                }
            }
            if (answer) {
                dataManager.setCurrentImport(returnType);
                UmlDiagram uml1 = dataManager.getUML().get(dataManager.getSelectedNumber());
                UmlDiagram uml2 = workspace.makeClassDiagram(200.0, 200.0);
                Line l = workspace.createLine(uml1, uml2, "associate");
            }
        }
    }

    public void loadMethodBox(DataManager dataManager, Workspace workspace, VBox methodsContainer, String loadInputs) {
        methodsRow = new HBox();
        methodsRow.getStyleClass().add("hbox_variables");
        methodsRow.setSpacing(10);
        methodsRow.setAlignment(Pos.CENTER);

        ArrayList<UmlDiagram> umlList = dataManager.getUML();
        umlList.get(dataManager.getSelectedNumber()).getMethodsBoxes().add(methodsRow);
        umlList.get(dataManager.getSelectedNumber()).getMethods().add(this);

        methodsRow.setOnMouseClicked(e -> {
            int index = umlList.get(dataManager.getSelectedNumber()).getMethodsBoxes().indexOf(methodsRow);
            umlList.get(dataManager.getSelectedNumber()).setSelectedMethodNumber(index);
            methodsRow.setStyle("-fx-background-color: #FFFFFF");
            dataManager.unhighlightMethods();
        });

        String[] inputsArray = loadInputs.split(" ");
        for (int i = 0; i < inputsArray.length; i++) {
            if (inputsArray[i].equals("empty")) {
                inputsArray[i] = "";
            }
        }
        TextField name = new TextField(inputsArray[0]);
        name.setPrefWidth(60);

        TextField returnType = new TextField(inputsArray[1]);
        returnType.setPrefWidth(60);

        VBox checkThis = new VBox();
        CheckBox staticOrNot = new CheckBox("static");
        CheckBox abstractOrNot = new CheckBox("abstract");
        if (inputsArray[2].equals("true")) {
            staticOrNot.setSelected(true);
        }
        if (inputsArray[3].equals("true")) {
            abstractOrNot.setSelected(true);
        }
        checkThis.getChildren().add(staticOrNot);
        checkThis.getChildren().add(abstractOrNot);
        checkThis.setPrefWidth(70);

        TextField modifier = new TextField(inputsArray[4]);
        modifier.setPrefWidth(60);
        TextField arg1 = new TextField(inputsArray[5]);
        arg1.setPrefWidth(60);
        TextField arg2 = new TextField(inputsArray[6]);
        arg2.setPrefWidth(60);
        TextField arg3 = new TextField(inputsArray[7]);
        arg3.setPrefWidth(60);
        Button submit = new Button("ok");
        submit.setPrefWidth(60);

        methodsRow.getChildren().add(name);
        methodsRow.getChildren().add(returnType);
        methodsRow.getChildren().add(checkThis);
        methodsRow.getChildren().add(modifier);
        methodsRow.getChildren().add(arg1);
        methodsRow.getChildren().add(arg2);
        methodsRow.getChildren().add(arg3);
        methodsRow.getChildren().add(submit);

        String method2 = translateToUml(name.getText(), returnType.getText(), staticOrNot.isSelected(),
                abstractOrNot.isSelected(), modifier.getText(), arg1.getText(), arg2.getText(), arg3.getText());
        String method3 = convertToCode(
                name.getText(), returnType.getText(), staticOrNot.isSelected(),
                abstractOrNot.isSelected(), modifier.getText(), arg1.getText(), arg2.getText(), arg3.getText(),
                dataManager.getUML().get(dataManager.getSelectedNumber()).isInterface);
        this.setText(method2);
        this.setCode(method3);
        inputs[0] = name.getText().replace(" ", "");
        inputs[1] = returnType.getText().replace(" ", "");
        inputs[2] = staticOrNot.isSelected() + "";
        inputs[3] = abstractOrNot.isSelected() + "";
        inputs[4] = modifier.getText().replace(" ", "");
        inputs[5] = arg1.getText().replace(" ", "");
        inputs[6] = arg2.getText().replace(" ", "");
        inputs[7] = arg3.getText().replace(" ", "");

        for (int i = 0; i < inputs.length; i++) {
            if (inputs[i].isEmpty()) {
                inputs[i] = "empty";
            }
        }

        submit.setOnAction(e -> {
            int selectedNumber = dataManager.getSelectedNumber();
            String method = translateToUml(name.getText(), returnType.getText(), staticOrNot.isSelected(),
                    abstractOrNot.isSelected(), modifier.getText(), arg1.getText(), arg2.getText(), arg3.getText());
            String method1 = convertToCode(
                    name.getText(), returnType.getText(), staticOrNot.isSelected(),
                    abstractOrNot.isSelected(), modifier.getText(), arg1.getText(), arg2.getText(), arg3.getText(),
                    dataManager.getUML().get(selectedNumber).isInterface);
            this.setText(method);
            this.setCode(method1);

            makeImportDiagram(returnType.getText(), dataManager, workspace);
            makeImportDiagram(arg1.getText(), dataManager, workspace);
            makeImportDiagram(arg2.getText(), dataManager, workspace);
            makeImportDiagram(arg3.getText(), dataManager, workspace);
            inputs[0] = name.getText().replace(" ", "");
            inputs[1] = returnType.getText().replace(" ", "");
            inputs[2] = staticOrNot.isSelected() + "";
            inputs[3] = abstractOrNot.isSelected() + "";
            inputs[4] = modifier.getText().replace(" ", "");
            inputs[5] = arg1.getText().replace(" ", "");
            inputs[6] = arg2.getText().replace(" ", "");
            inputs[7] = arg3.getText().replace(" ", "");

            for (int i = 0; i < inputs.length; i++) {
                if (inputs[i].isEmpty()) {
                    inputs[i] = "empty";
                }
            }
            String methodEdited = "";

            for (int i = 0; i < umlList.get(selectedNumber).getMethods().size(); i++) {
                methodEdited += umlList.get(selectedNumber).getMethods().get(i).getText() + "\n";
            }
            umlList.get(selectedNumber).getMethodsLabel().setText(methodEdited);
        });
        methodsContainer.getChildren().add(methodsRow);
    }

    public void makeMethodBox(DataManager dataManager, Workspace workspace, VBox methodsContainer) {
        methodsRow = new HBox();
        methodsRow.getStyleClass().add("hbox_variables");
        methodsRow.setSpacing(10);
        methodsRow.setAlignment(Pos.CENTER);

        ArrayList<UmlDiagram> umlList = dataManager.getUML();
        umlList.get(dataManager.getSelectedNumber()).getMethodsBoxes().add(methodsRow);
        umlList.get(dataManager.getSelectedNumber()).getMethods().add(this);

        methodsRow.setOnMouseClicked(e -> {
            int index = umlList.get(dataManager.getSelectedNumber()).getMethodsBoxes().indexOf(methodsRow);
            umlList.get(dataManager.getSelectedNumber()).setSelectedMethodNumber(index);
            methodsRow.setStyle("-fx-background-color: #FFFFFF");
            dataManager.unhighlightMethods();
        });
        int random = (int) (Math.random() * 50);

        TextField name = new TextField("myMeth" + random);
        name.setPrefWidth(60);

        TextField returnType = new TextField("void");
        returnType.setPrefWidth(60);

        VBox checkThis = new VBox();
        CheckBox staticOrNot = new CheckBox("static");
        CheckBox abstractOrNot = new CheckBox("abstract");
        checkThis.getChildren().add(staticOrNot);
        checkThis.getChildren().add(abstractOrNot);
        checkThis.setPrefWidth(70);

        TextField modifier = new TextField("public");
        modifier.setPrefWidth(60);
        TextField arg1 = new TextField("int");
        arg1.setPrefWidth(60);
        TextField arg2 = new TextField("String");
        arg2.setPrefWidth(60);
        TextField arg3 = new TextField("double");
        arg3.setPrefWidth(60);
        Button submit = new Button("ok");
        submit.setPrefWidth(60);

        methodsRow.getChildren().add(name);
        methodsRow.getChildren().add(returnType);
        methodsRow.getChildren().add(checkThis);
        methodsRow.getChildren().add(modifier);
        methodsRow.getChildren().add(arg1);
        methodsRow.getChildren().add(arg2);
        methodsRow.getChildren().add(arg3);
        methodsRow.getChildren().add(submit);

        submit.setOnAction(e -> {
            int selectedNumber = dataManager.getSelectedNumber();
            String method = translateToUml(name.getText(), returnType.getText(), staticOrNot.isSelected(),
                    abstractOrNot.isSelected(), modifier.getText(), arg1.getText(), arg2.getText(), arg3.getText());
            String method1 = convertToCode(
                    name.getText(), returnType.getText(), staticOrNot.isSelected(),
                    abstractOrNot.isSelected(), modifier.getText(), arg1.getText(), arg2.getText(), arg3.getText(),
                    dataManager.getUML().get(selectedNumber).isInterface);
            this.setText(method);
            this.setCode(method1);
            dataManager.getUML().get(selectedNumber).setAbstract(abstractOrNot.isSelected());
            makeImportDiagram(returnType.getText(), dataManager, workspace);
            makeImportDiagram(arg1.getText(), dataManager, workspace);
            makeImportDiagram(arg2.getText(), dataManager, workspace);
            makeImportDiagram(arg3.getText(), dataManager, workspace);
            inputs[0] = name.getText().replace(" ", "");
            inputs[1] = returnType.getText().replace(" ", "");
            inputs[2] = staticOrNot.isSelected() + "";
            inputs[3] = abstractOrNot.isSelected() + "";
            inputs[4] = modifier.getText().replace(" ", "");
            inputs[5] = arg1.getText().replace(" ", "");
            inputs[6] = arg2.getText().replace(" ", "");
            inputs[7] = arg3.getText().replace(" ", "");

            for (int i = 0; i < inputs.length; i++) {
                if (inputs[i].isEmpty()) {
                    inputs[i] = "empty";
                }
            }
            if (counter == 0) {
                Label methodLabel = umlList.get(selectedNumber).getMethodsLabel();
                methodLabel.setText(methodLabel.getText() + method + "\n");

            } else {
                String methodEdited = "";
                for (int i = 0; i < umlList.get(selectedNumber).getMethods().size(); i++) {
                    methodEdited += umlList.get(selectedNumber).getMethods().get(i).getText() + "\n";
                }
                umlList.get(selectedNumber).getMethodsLabel().setText(methodEdited);
            }
            counter = 10;
        });
        methodsContainer.getChildren().add(methodsRow);
    }

}
