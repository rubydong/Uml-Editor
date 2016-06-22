package saf.ui;

import java.util.ArrayList;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import properties_manager.PropertiesManager;
import saf.controller.AppFileController;
import saf.AppTemplate;
import static saf.settings.AppPropertyType.*;
import static saf.settings.AppStartupConstants.FILE_PROTOCOL;
import static saf.settings.AppStartupConstants.PATH_IMAGES;
import saf.components.AppStyleArbiter;

public class AppGUI implements AppStyleArbiter {

    protected AppFileController fileController;
    protected Stage primaryStage;
    protected Scene primaryScene;
    protected BorderPane appPane;
    protected FlowPane topPane;
    protected HBox fileToolbarPane;
    protected HBox editToolbarPane;
    protected HBox viewToolbarPane;
    protected VBox checkBoxPane;
    protected Button newButton;
    protected Button saveButton;
    protected Button exitButton;
    protected Button loadButton;
    protected Button saveAsButton;
    protected Button photoButton;
    protected Button exportButton;
    protected Button selectButton;
    protected Button resizeButton;
    protected Button addClassButton;
    protected Button addInterfaceButton;
    protected Button removeButton;
    protected Button undoButton;
    protected Button redoButton;
    protected Button zoomInButton;
    protected Button zoomOutButton;
    protected ArrayList<Button> buttonsList = new ArrayList<Button>();
    protected CheckBox gridCheck;
    protected CheckBox snapCheck;

    protected AppYesNoCancelDialogSingleton yesNoCancelDialog;
    protected String appTitle;

    public AppGUI(Stage initPrimaryStage,
            String initAppTitle,
            AppTemplate app) {
        primaryStage = initPrimaryStage;
        appTitle = initAppTitle;
        initFileToolbar(app);
        initWindow();
        System.out.print("testing");
    }

    public ArrayList<Button> getButtonsList() {
        return buttonsList;
    }

    public BorderPane getAppPane() {
        return appPane;
    }

    public Scene getPrimaryScene() {
        return primaryScene;
    }

    public Stage getWindow() {
        return primaryStage;
    }

    public void updateToolbarControls(boolean saved) {
        for (int i = 0; i < buttonsList.size(); i++) {
            buttonsList.get(i).setDisable(false);
        }
        //saveButton.setDisable(saved);
        //saveAsButton.setDisable(saved);
    }

    private void initFileToolbar(AppTemplate app) {
        fileToolbarPane = new HBox();
        editToolbarPane = new HBox();
        viewToolbarPane = new HBox();
        checkBoxPane = new VBox();

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        fileToolbarPane.setPrefSize(bounds.getWidth() / 3.04, 50);
        fileToolbarPane.setAlignment(Pos.CENTER);

        editToolbarPane.setPrefSize(bounds.getWidth() / 3.04, 50);
        editToolbarPane.setAlignment(Pos.CENTER);

        viewToolbarPane.setPrefSize(bounds.getWidth() / 3.04, 50);
        viewToolbarPane.setAlignment(Pos.CENTER);

        topPane = new FlowPane();
        topPane.setAlignment(Pos.CENTER);
        topPane.setStyle("-fx-background-color: #FFEDED");

        newButton = initChildButton(fileToolbarPane, NEW_ICON.toString(), NEW_TOOLTIP.toString(), false);
        loadButton = initChildButton(fileToolbarPane, LOAD_ICON.toString(), LOAD_TOOLTIP.toString(), false);
        saveButton = initChildButton(fileToolbarPane, SAVE_ICON.toString(), SAVE_TOOLTIP.toString(), true);
        saveAsButton = initChildButton(fileToolbarPane, SAVE_AS_ICON.toString(), SAVE_AS_TOOLTIP.toString(), true);
        exitButton = initChildButton(fileToolbarPane, EXIT_ICON.toString(), EXIT_TOOLTIP.toString(), false);
        photoButton = initChildButton(fileToolbarPane, PHOTO_ICON.toString(), PHOTO_TOOLTIP.toString(), true);
        exportButton = initChildButton(fileToolbarPane, CODE_ICON.toString(), CODE_TOOLTIP.toString(), true);
        selectButton = initChildButton(editToolbarPane, SELECT_ICON.toString(), SELECT_TOOLTIP.toString(), true);
        resizeButton = initChildButton(editToolbarPane, RESIZE_ICON.toString(), RESIZE_TOOLTIP.toString(), true);
        addClassButton = initChildButton(editToolbarPane, ADD_CLASS_ICON.toString(), ADD_CLASS_TOOLTIP.toString(), true);
        addInterfaceButton = initChildButton(editToolbarPane, ADD_INTERFACE_ICON.toString(), ADD_INTERFACE_TOOLTIP.toString(), true);
        removeButton = initChildButton(editToolbarPane, REMOVE_ICON.toString(), REMOVE_TOOLTIP.toString(), true);
        undoButton = initChildButton(editToolbarPane, UNDO_ICON.toString(), UNDO_TOOLTIP.toString(), true);
        redoButton = initChildButton(editToolbarPane, REDO_ICON.toString(), REDO_TOOLTIP.toString(), true);
        zoomInButton = initChildButton(viewToolbarPane, ZOOM_IN_ICON.toString(), ZOOM_OUT_TOOLTIP.toString(), true);
        zoomOutButton = initChildButton(viewToolbarPane, ZOOM_OUT_ICON.toString(), ZOOM_OUT_TOOLTIP.toString(), true);

        gridCheck = new CheckBox("Grid");
        snapCheck = new CheckBox("Snap");

        checkBoxPane.getChildren().add(gridCheck);
        checkBoxPane.getChildren().add(snapCheck);
        checkBoxPane.setAlignment(Pos.BASELINE_LEFT);
        viewToolbarPane.getChildren().add(checkBoxPane);
        buttonsList.add(newButton);
        buttonsList.add(loadButton);
        buttonsList.add(saveButton);
        buttonsList.add(saveAsButton);
        buttonsList.add(exitButton);
        buttonsList.add(photoButton);
        buttonsList.add(exportButton);
        buttonsList.add(selectButton);
        buttonsList.add(resizeButton);
        buttonsList.add(addClassButton);
        buttonsList.add(addInterfaceButton);
        buttonsList.add(removeButton);
        buttonsList.add(undoButton);
        buttonsList.add(redoButton);
        buttonsList.add(zoomInButton);
        buttonsList.add(zoomOutButton);

        fileController = new AppFileController(app);
        newButton.setOnAction(e -> {
            fileController.handleNewRequest();
        });
        saveButton.setOnAction(e -> {
            fileController.handleSaveRequest();
        });
        
        saveAsButton.setOnAction(e -> {
            fileController.handleSaveAsRequest();
        });
        
        exitButton.setOnAction(e -> {
            fileController.handleExitRequest();
        });

        loadButton.setOnAction(e -> {
            fileController.handleLoadRequest();
        });

        exportButton.setOnAction(e -> {
            fileController.handleExportRequest();
        });

    }

    private void initWindow() {
        primaryStage.setTitle(appTitle);

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        primaryStage.setX(bounds.getMinX());
        primaryStage.setY(bounds.getMinY());
        primaryStage.setWidth(bounds.getWidth());
        primaryStage.setHeight(bounds.getHeight());

        appPane = new BorderPane();
        appPane.setTop(topPane);
        topPane.getChildren().add(fileToolbarPane);
        topPane.getChildren().add(editToolbarPane);
        topPane.getChildren().add(viewToolbarPane);

        primaryScene = new Scene(appPane);

        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String appIcon = FILE_PROTOCOL + PATH_IMAGES + props.getProperty(APP_LOGO);
        primaryStage.getIcons().add(new Image(appIcon));

        primaryStage.setScene(primaryScene);
        primaryStage.show();
    }

    public Button initChildButton(Pane toolbar, String icon, String tooltip, boolean disabled) {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String imagePath = FILE_PROTOCOL + PATH_IMAGES + props.getProperty(icon);
        Image buttonImage = new Image(imagePath);
        Button button = new Button();
        button.setDisable(disabled);
        button.setGraphic(new ImageView(buttonImage));
        Tooltip buttonTooltip = new Tooltip(props.getProperty(tooltip));
        button.setTooltip(buttonTooltip);

        toolbar.getChildren().add(button);

        return button;
    }

    @Override
    public void initStyle() {
        fileToolbarPane.getStyleClass().add(CLASS_BORDERED_PANE);
        editToolbarPane.getStyleClass().add(CLASS_BORDERED_PANE);
        viewToolbarPane.getStyleClass().add(CLASS_BORDERED_PANE);
        checkBoxPane.getStyleClass().add("checkbox_pane");
        newButton.getStyleClass().add(CLASS_FILE_BUTTON);
        loadButton.getStyleClass().add(CLASS_FILE_BUTTON);
        saveButton.getStyleClass().add(CLASS_FILE_BUTTON);
        exitButton.getStyleClass().add(CLASS_FILE_BUTTON);
    }

    public Button getAddClassButton() {
        return addClassButton;
    }

    public Button getRemoveButton(){
        return removeButton;
    }
    public Button getSelectButton() {
        return selectButton;
    }

    public Button getPhotoButton() {
        return photoButton;
    }
    
    public Button getZoomInButton() {
        return zoomInButton;
    }
    
    public Button getZoomOutButton() {
        return zoomOutButton;
    }
    
    public Button getAddInterfaceButton() {
        return addInterfaceButton;
    }
}
