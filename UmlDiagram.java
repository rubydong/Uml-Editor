/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pm;

import java.util.ArrayList;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Ruby
 */
public class UmlDiagram extends GridPane {

    String className;
    String packageName;

    Label classLabel = new Label();

    Label variablesLabel = new Label();
    Label methodsLabel = new Label();

    boolean isAbstract = false;
    boolean isInterface = false;
    boolean isImportClass = false;

    ArrayList<VariableDiagram> variables = new ArrayList<>();
    ArrayList<HBox> variablesBoxes = new ArrayList<>();
    int variableBoxesNumber = -1;

    ArrayList<MethodDiagram> methods = new ArrayList<>();
    ArrayList<HBox> methodsBoxes = new ArrayList<>();
    int methodBoxesNumber = -1;

    ArrayList<UmlDiagram> parentsInterfaces = new ArrayList<>();
    ArrayList<String> parentsInterfacesText = new ArrayList<>();
    ArrayList<UmlDiagram> childrenInterfaces = new ArrayList<>();
    ArrayList<Line> lines = new ArrayList<>();
    private ArrayList<Polygon> diamonds = new ArrayList<>();

    ArrayList<UmlDiagram> aggregateParents = new ArrayList<>();
    ArrayList<UmlDiagram> aggregateChildren = new ArrayList<>();
    ArrayList<Line> aggregateLines = new ArrayList<>();
    private ArrayList<Polygon> triangles = new ArrayList<>();

    ArrayList<UmlDiagram> associateParents = new ArrayList<>();
    ArrayList<UmlDiagram> associateChildren = new ArrayList<>();
    ArrayList<Line> associateLines = new ArrayList<>();
    private ArrayList<Polygon> arrows = new ArrayList<>();

    public UmlDiagram(String className, String packageName) {
        this.className = className;
        this.packageName = packageName;
    }

    public UmlDiagram() {
    }

    public void setClassLabel(Label classLabel) {
        this.classLabel = classLabel;
    }

    public void setVariablesLabel(Label variableLabel) {
        this.variablesLabel = variableLabel;
    }

    public void setMethodsLabel(Label methodLabel) {
        this.methodsLabel = methodLabel;
    }

    public Label getClassLabel() {
        return classLabel;
    }

    public Label getVariablesLabel() {
        return variablesLabel;
    }

    public Label getMethodsLabel() {
        return methodsLabel;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setInterface(boolean answer) {
        this.isInterface = answer;
    }

    public void setAbstract(boolean answer) {
        this.isAbstract = answer;
    }

    public boolean IsAbstract() {
        return isAbstract;
    }

    public boolean IsInterface() {
        return isInterface;
    }

    public String getClassName() {
        return className;
    }

    public String getPackageName() {
        return packageName;
    }

    public ArrayList<MethodDiagram> getMethods() {
        return methods;
    }

    public ArrayList<HBox> getMethodsBoxes() {
        return methodsBoxes;
    }

    public int getSelectedMethodNumber() {
        return methodBoxesNumber;
    }

    public boolean isImportClass() {
        return isImportClass;
    }

    public void setIsImportClass(boolean answer) {
        isImportClass = answer;
    }

    public void setSelectedMethodNumber(int methodBoxesNumber) {
        this.methodBoxesNumber = methodBoxesNumber;
    }

    public ArrayList<VariableDiagram> getVariables() {
        return variables;
    }

    public ArrayList<HBox> getVariablesBoxes() {
        return variablesBoxes;
    }

    public int getSelectedVariableNumber() {
        return variableBoxesNumber;
    }

    public void setSelectedVariableNumber(int variableBoxesNumber) {
        this.variableBoxesNumber = variableBoxesNumber;
    }

    public void setParentInterfaces(ArrayList<UmlDiagram> parentInterfaces) {
        this.parentsInterfaces = parentInterfaces;
    }

    public ArrayList<String> getParentInterfacesText() {
        return parentsInterfacesText;
    }

    public ArrayList<UmlDiagram> getParentInterfaces() {
        return parentsInterfaces;
    }

    public ArrayList<UmlDiagram> getChildrenInterfaces() {
        return childrenInterfaces;
    }

    public void setLines(ArrayList<Line> lines) {
        this.lines = lines;
    }

    public ArrayList<Line> getLines() {
        return lines;
    }

    public ArrayList<Line> getAggregateLines() {
        return aggregateLines;
    }

    public ArrayList<Line> getAssociateLines() {
        return associateLines;
    }

    public ArrayList<UmlDiagram> getAggregateParents() {
        return aggregateParents;
    }

    public ArrayList<UmlDiagram> getAggregateChildren() {
        return aggregateChildren;
    }

    public ArrayList<UmlDiagram> getAssociateParents() {
        return associateParents;
    }

    public ArrayList<UmlDiagram> getAssociateChildren() {
        return associateChildren;
    }

    public ArrayList<Polygon> getDiamonds() {
        return diamonds;
    }

    public ArrayList<Polygon> getTriangles() {
        return triangles;
    }

    public ArrayList<Polygon> getArrows() {
        return arrows;
    }

}
