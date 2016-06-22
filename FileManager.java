package pm.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;
import javax.swing.JFileChooser;
import pm.control.ActionManager;
import pm.JClassMaker;
import pm.MethodDiagram;
import pm.UmlDiagram;
import pm.VariableDiagram;
import pm.data.DataManager;
import pm.gui.Workspace;

import saf.components.AppDataComponent;
import saf.components.AppFileComponent;

/**
 * This class serves as the file management component for this application,
 * providing all I/O services.
 *
 * @author Richard McKenna
 * @author ?
 * @version 1.0
 */
public class FileManager implements AppFileComponent {

    double x;
    double y;

    private JsonObject makeValueJsonObject(UmlDiagram classDiagram, String className, String packageName,
            ArrayList<VariableDiagram> variables, ArrayList<MethodDiagram> methods) {
        double xPosition = classDiagram.getLayoutX();
        double yPosition = classDiagram.getLayoutY();
        double width = classDiagram.getWidth();
        double height = classDiagram.getHeight();
        String v = "";
        String m = "";
        String methodInputs = "";
        String variableInputs = "";
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
        //gotta fix loading lines for interface
        String parentsText = "";
        for (int i = 0; i < classDiagram.getParentInterfaces().size(); i++) {
            parentsText += classDiagram.getParentInterfaces().get(i).getClassName() + ",";
        }
        String aggregatesText = "";
        for (int i = 0; i < classDiagram.getAggregateParents().size(); i++) {
            aggregatesText += classDiagram.getAggregateParents().get(i).getClassName() + ",";
        }
        String associatesText = "";
        for (int i = 0; i < classDiagram.getAssociateParents().size(); i++) {
            associatesText += classDiagram.getAssociateParents().get(i).getClassName() + ",";
        }
        if (className == null) {
            className = "";
        }
        if (packageName == null) {
            packageName = "";
        }

        JsonObject jso = Json.createObjectBuilder()
                .add("Class Name", className)
                .add("Import Class", classDiagram.isImportClass())
                .add("Interface", classDiagram.IsInterface())
                .add("Abstract", classDiagram.IsAbstract())
                .add("Package Name", packageName)
                .add("Variables", v)
                .add("Variable Inputs", variableInputs)
                .add("Methods", m)
                .add("Method Inputs", methodInputs)
                .add("Parents/Interfaces", parentsText)
                .add("Aggregates", aggregatesText)
                .add("Associates", associatesText)
                .add("X Location", xPosition)
                .add("Y Location", yPosition)
                .add("Width", width)
                .add("Height", height)
                .build();
        return jso;
    }

    public void fillWithClasses(JsonArrayBuilder arrayBuilder, DataManager data) {
        for (int i = 0; i < data.getUML().size(); i++) {
            JsonObject classObject = makeValueJsonObject(data.getUML().get(i),
                    data.getUML().get(i).getClassName(), data.getUML().get(i).getPackageName(),
                    data.getUML().get(i).getVariables(), data.getUML().get(i).getMethods());
            arrayBuilder.add(classObject);
        }
    }

    @Override
    public void saveData(AppDataComponent data, String filePath) throws IOException {
        StringWriter sw = new StringWriter();

        DataManager dataManager = (DataManager) data;
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        fillWithClasses(arrayBuilder, dataManager);
        JsonArray classesArray = arrayBuilder.build();

        JsonObject dataManagerJSO = Json.createObjectBuilder()
                .add("UML Diagrams", classesArray)
                .build();

        Map<String, Object> properties = new HashMap<>(1);
        properties.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
        JsonWriter jsonWriter = writerFactory.createWriter(sw);
        jsonWriter.writeObject(dataManagerJSO);
        jsonWriter.close();

        OutputStream os = new FileOutputStream(filePath + ".json");
        JsonWriter jsonFileWriter = Json.createWriter(os);
        jsonFileWriter.writeObject(dataManagerJSO);
        String prettyPrinted = sw.toString();
        PrintWriter pw = new PrintWriter(filePath + ".json");
        pw.write(prettyPrinted);
        pw.close();
    }

    public void loadData(AppDataComponent data, String filePath) throws IOException {
        DataManager dataManager = (DataManager) data;
        dataManager.reset();
        loadClasses(dataManager, filePath);
        JsonObject json = loadJSONFile(filePath);
    }

    public void loadClasses(DataManager data, String filePath) throws IOException {
        JsonObject json = loadJSONFile(filePath);
        JsonArray propertiesArray = json.getJsonArray("UML Diagrams");

        ActionManager actionManager = new ActionManager((JClassMaker) data.getApp());
        actionManager.textFieldEvents(data.getClassTextField(), data.getPackageTextField(),
                data.getWorkspace().getParentsBox());

        for (int i = 0; i < propertiesArray.size(); i++) {
            JsonObject prop = propertiesArray.getJsonObject(i);

            String classNameText = prop.getString("Class Name");
            String packageNameText = prop.getString("Package Name");
            boolean importClass = prop.getBoolean("Import Class");
            boolean interfaceClass = prop.getBoolean("Interface");
            boolean abstractClass = prop.getBoolean("Abstract");
            String variables = prop.getString("Variables");
            String methods = prop.getString("Methods");
            String variableInputs = prop.getString("Variable Inputs");
            String methodInputs = prop.getString("Method Inputs");
            double x = prop.getInt("X Location");
            double y = prop.getInt("Y Location");
            double width = prop.getInt("Width");
            double height = prop.getInt("Height");

            UmlDiagram classDiagram = new UmlDiagram(classNameText, packageNameText);
            data.getUML().add(classDiagram);
            data.setSelectedNumber(data.getUML().indexOf(classDiagram));
            classDiagram.getStyleClass().add("class_diagram_pane");

            classDiagram.setIsImportClass(importClass);
            classDiagram.setInterface(interfaceClass);
            classDiagram.setAbstract(abstractClass);
            classDiagram.setLayoutX(x);
            classDiagram.setLayoutY(y);
            classDiagram.setMinSize(width, height);
            classDiagram.setGridLinesVisible(true);
            if (!variables.equals("") || !variables.isEmpty()) {
                variables = variables.replace(" ;", "\n");
                String[] v = variableInputs.split(" ;");
                for (int j = 0; j < v.length; j++) {
                    VariableDiagram vee = new VariableDiagram();
                    vee.loadVariableBox(data, data.getWorkspace(), data.getVariablesContainer(), v[j]);
                }
                data.getVariablesContainer().getChildren().clear();
                data.makeVariableHeader();
            }

            if (!methods.equals("") || !methods.isEmpty()) {
                methods = methods.replace(" ;", "\n");
                String[] m = methodInputs.split(" ;");
                for (int j = 0; j < m.length; j++) {
                    MethodDiagram mee = new MethodDiagram();
                    mee.loadMethodBox(data, data.getWorkspace(), data.getMethodsContainer(), m[j]);
                }
                data.getMethodsContainer().getChildren().clear();
                data.makeMethodHeader();
            }

            Label className = new Label(classNameText);
            Label variableNames = new Label(variables);
            Label methodNames = new Label(methods);
            classDiagram.add(className, 1, 1);
            classDiagram.setClassLabel(className);

            if (!classDiagram.isImportClass()) {
                classDiagram.add(variableNames, 1, 2);
                classDiagram.setVariablesLabel(variableNames);
                classDiagram.add(methodNames, 1, 3);
                classDiagram.setMethodsLabel(methodNames);
            }
            data.getWorkspace().getParentsBox().getItems().add(classDiagram.getClassName().replace("«interface»\n", ""));

            data.getClassTextField().clear();
            data.getPackageTextField().clear();
            data.getLeftPane().getChildren().add(classDiagram);
            data.getWorkspace().mouseAction(classDiagram);
            //data.getWorkspace().getParentsBox().getItems().add(classNameText);
            classDiagram.setHgrow(className, Priority.ALWAYS);
            classDiagram.setHgrow(variableNames, Priority.ALWAYS);
            classDiagram.setHgrow(methodNames, Priority.ALWAYS);
            classDiagram.setVgrow(className, Priority.ALWAYS);
            classDiagram.setVgrow(variableNames, Priority.ALWAYS);
            classDiagram.setVgrow(methodNames, Priority.ALWAYS);
        }

        for (int i = 0; i < propertiesArray.size(); i++) {

            JsonObject prop = propertiesArray.getJsonObject(i);
            String className = prop.getString("Class Name");
            UmlDiagram classDiagram = null;
            classDiagram = data.getWorkspace().findUML(className);
            String parentsInterfaces = prop.getString("Parents/Interfaces");
            String aggregates = prop.getString("Aggregates");
            String associates = prop.getString("Associates");

            String p[] = parentsInterfaces.split(",");
            for (int j = 0; j < p.length; j++) {
                data.getWorkspace().createLine(classDiagram, data.getWorkspace().findUML(p[j]), "parent");
            }
            String agg[] = aggregates.split(",");
            for (int j = 0; j < agg.length; j++) {
                data.getWorkspace().createLine(classDiagram, data.getWorkspace().findUML(agg[j]), "aggregate");
            }
            String a[] = associates.split(",");
            for (int j = 0; j < a.length; j++) {
                data.getWorkspace().createLine(classDiagram, data.getWorkspace().findUML(a[j]), "associate");
            }
        }
        data.setSelectedNumber(-1);
    }

    // HELPER METHOD FOR LOADING DATA FROM A JSON FORMAT
    public JsonObject loadJSONFile(String jsonFilePath) throws IOException {
        InputStream is = new FileInputStream(jsonFilePath);
        JsonReader jsonReader = Json.createReader(is);
        JsonObject json = jsonReader.readObject();
        jsonReader.close();
        is.close();
        return json;
    }

    @Override
    public void exportData(AppDataComponent data, String filePath) throws IOException {
        JFileChooser f = new JFileChooser();

        f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        f.showSaveDialog(null);

        writeCode(data, f);

    }

    public void writeCode(AppDataComponent data, JFileChooser f) throws IOException {
        DataManager dataManager = (DataManager) data;
        PrintWriter writer;
        File file;
        String path = "";
        for (int i = 0; i < dataManager.getUML().size(); i++) {
            try {

                UmlDiagram currentDiagram = dataManager.getUML().get(i);
                if (!currentDiagram.isImportClass()) {

                    String packageName = "";
                    packageName += currentDiagram.getPackageName();
                    packageName = packageName.replaceAll("\\.", "/");
                    packageName = packageName.replace(",", "");

                    path = f.getSelectedFile().getAbsolutePath() + "/" + packageName;

                    if (currentDiagram.IsInterface()) {
                        String className = currentDiagram.getClassName();
                        className = className.replace("«interface»\n", "");
                        file = new File(path, className + ".java");
                    } else {
                        file = new File(path, dataManager.getUML().get(i).getClassName() + ".java");
                    }

                    file.getParentFile().mkdirs();
                    file.createNewFile();

                    writer = new PrintWriter(file);

                    writer.println("package " + dataManager.getUML().get(i).getPackageName() + ";\n");

                    String importList = "";

                    for (int j = 0; j < currentDiagram.getParentInterfaces().size(); j++) {
                        UmlDiagram parent = currentDiagram.getParentInterfaces().get(j);
                        if (!parent.getPackageName().equals(currentDiagram.getPackageName())) {
                            importList += "import " + parent.getPackageName() + "."
                                    + parent.getClassName().replace("«interface»\n", "") + ";\n";
                        }
                    }
                    for (int j = 0; j < currentDiagram.getAggregateParents().size(); j++) {
                        UmlDiagram parent = currentDiagram.getAggregateParents().get(j);
                        if (!parent.getPackageName().equals(currentDiagram.getPackageName())) {
                            importList += "import " + parent.getPackageName() + "."
                                    + parent.getClassName() + ";\n";
                        }
                    }

                    for (int j = 0; j < currentDiagram.getAssociateParents().size(); j++) {
                        UmlDiagram parent = currentDiagram.getAssociateParents().get(j);
                        if (!parent.getPackageName().equals(currentDiagram.getPackageName())) {
                            importList += "import " + parent.getPackageName() + "."
                                    + parent.getClassName() + ";\n";

                        }
                    }

                    writer.println(importList);

                    boolean firstInterface = true;
                    boolean firstParent = true;
                    if (dataManager.getUML().get(i).IsInterface()) {
                        String temp = dataManager.getUML().get(i).getClassName();
                        temp = temp.replace("«interface»\n", "");
                        writer.write("public interface " + temp + " {\n");
                    } else if (dataManager.getUML().get(i).IsAbstract()) {
                        writer.write("public abstract class " + dataManager.getUML().get(i).getClassName() + " {\n");
                    } else if (!dataManager.getUML().get(i).getParentInterfaces().isEmpty()) {
                        writer.write("public class " + dataManager.getUML().get(i).getClassName());
                        for (int j = 0; j < dataManager.getUML().get(i).getParentInterfaces().size(); j++) {
                            if (!dataManager.getUML().get(i).getParentInterfaces().get(j).IsInterface() && firstParent) {
                                writer.write(" extends " + dataManager.getUML().get(i).getParentInterfacesText().get(j));
                            }
                        }
                        for (int j = 0; j < dataManager.getUML().get(i).getParentInterfaces().size(); j++) {
                            if (dataManager.getUML().get(i).getParentInterfaces().get(j).IsInterface() && firstInterface) {
                                writer.write(" implements " + dataManager.getUML().get(i).getParentInterfacesText().get(j).replace("«interface»\n", ""));
                                firstInterface = false;
                            } else if (dataManager.getUML().get(i).getParentInterfaces().get(j).IsInterface() && firstInterface == false) {
                                writer.write(" ," + dataManager.getUML().get(i).getParentInterfacesText().get(j).replace("«interface»\n", ""));
                            }
                        }
                        writer.write("{\n");
                    } else {
                        writer.write("public class " + dataManager.getUML().get(i).getClassName() + " {\n");
                    }

                    for (int j = 0; j < dataManager.getUML().get(i).getVariables().size(); j++) {
                        writer.write(dataManager.getUML().get(i).getVariables().get(j).getCode() + "\n");
                    }
                    for (int j = 0; j < dataManager.getUML().get(i).getMethods().size(); j++) {
                        writer.write(dataManager.getUML().get(i).getMethods().get(j).getCode() + "\n");
                    }

                    writer.write("\n}");
                    writer.close();
                }

            } catch (FileNotFoundException ex) {
                System.out.print("writing error hahaha");
            } catch (NullPointerException ex) {
                System.out.println("Name or package name is null");
            }
        }
    }

    /**
     * This method is provided to satisfy the compiler, but it is not used by
     * this application.
     */
    public void importData(AppDataComponent data, String filePath) throws IOException {
        // NOTE THAT THE Web Page Maker APPLICATION MAKES
        // NO USE OF THIS METHOD SINCE IT NEVER IMPORTS
        // EXPORTED WEB PAGES
    }
}
