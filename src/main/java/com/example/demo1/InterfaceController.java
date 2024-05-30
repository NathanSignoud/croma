package com.example.demo1;

// For animation and timing
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
// JavaFX image conversion
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
// Various components of the JavaFX user interface
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import javafx.stage.FileChooser;                // File management
import javafx.util.Duration;                    // Time management

import javax.imageio.ImageIO;           // Reading and writing images
import java.io.File;                    // File management
import java.io.IOException;
// I/O exception handling
// Use of lists
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


import com.tsan.chromaynk.datatypes.Cursor;
import com.tsan.chromaynk.*;



public class InterfaceController {

    @FXML
    public Button saveButton;

    ObservableList<String> SpeedList  = FXCollections.observableArrayList("Instant", "Slow", "Medium", "Fast");

    @FXML
    private Pane drawingPane;

    @FXML
    private List<String> drawingInstructions;

    @FXML
    private ChoiceBox speedBox;

    private int currentInstructionIndex = 0;

    private Timeline timeline;

    private GraphicsContext gc;

    private boolean hasDrawn = false;

    private Scene scene;

    @FXML
    public void initialize() {
        drawingInstructions = new ArrayList<>();
        speedBox.setItems(SpeedList);
        speedBox.setValue("Instant");
        Canvas canvas = new Canvas(drawingPane.getPrefWidth(), drawingPane.getPrefHeight());    // Create a canvas for the design and define the necessary parameters
        drawingPane.getChildren().add(canvas);
        gc = canvas.getGraphicsContext2D();
        gc.setLineWidth(1000000);                     // Set default line width
        gc.setStroke(Color.RED);              // Set default stroke color

        drawingPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
                    if (newScene != null) {
                        scene = newScene;
                    }
                }
        );
    }

    /*@FXML
    private void handleSpeedSelection() {
        String selectedSpeed = speedComboBox.getValue();
        switch (selectedSpeed) {
            case "Lent":
                handleSlowSpeedButtonClicked();
                break;
            case "Moyen":
                handleMediumSpeedButtonClicked();
                break;
            case "Rapide":
                handleFastSpeedButtonClicked();
                break;
            default:
                break;
        }
    }*/

    // Handler for loading instructions from a file
    @FXML
    private void handleLoadFileButtonClicked() throws InterruptedException {
        FileChooser fileChooser = new FileChooser();            // Creates a file selection dialog box
        fileChooser.setTitle("Open Instruction File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.abbas"));
        File file = fileChooser.showOpenDialog(drawingPane.getScene().getWindow());         // Show file chooser dialog
        if (file != null) {
            loadInstructionsFromFile(file);                         // Loads instructions from the file
        }

        //loadFileButton.setDisable(true);
    }

    // Method to load drawing instructions from a file
    private void loadInstructionsFromFile(File file) throws InterruptedException {
        /*drawingInstructions.clear();                // Delete existing instructions
        currentInstructionIndex = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {            // Each line is added to the list of instructions after being cleaned up with trim()
            String line;
            while ((line = br.readLine()) != null) {
                drawingInstructions.add(line.trim());                // Add each line like instructions
            }
        } catch (IOException e) {
            e.printStackTrace();                                // Print Error
        }
        
        if (!drawingInstructions.isEmpty()) {
            stepButton.setDisable(false);                       // Activate the step-by-step button if instructions are loaded
            hasDrawn = true;
            startDrawing(defaultDelay);
            loadFileButton.setText("Loaded file");              // Update Button
        }*/ 
        //file.toPath()
        int defaultDelay = 1000;

        //drawingPane.getChildren().clear();               // Clear drawing pane
        Canvas canvas = new Canvas(drawingPane.getPrefWidth(), drawingPane.getPrefHeight());        // Create new canvas
        drawingPane.getChildren().add(canvas);           // Add canvas to drawing pane
        gc = canvas.getGraphicsContext2D();              // Get graphics context for drawing
        gc.setLineWidth(1);
        gc.setStroke(Color.BLACK);
        hasDrawn = true;

        /*if (timeline != null) {
            timeline.stop();
        }

        timeline = new Timeline(new KeyFrame(Duration.millis(defaultDelay), event -> drawNextInstruction()));
        timeline.setCycleCount(drawingInstructions.size());
        timeline.play();*/

        System.out.println("file: " + file.toPath());

        Client client = new Client(this);

        client.tokenize(file.toPath().toString());
        
        System.out.println("\nresult of tokenization process:");
        client.display();

        System.out.println("\nresult of parsing process:");
        client.parse();

        System.out.println("\nexecuting " + file.toPath().toFile() + ":");
        client.execute();

    }

    // Handler for step button click
    @FXML
    private void handleStepButtonClicked() {                // Draws the next instruction
        if (timeline != null) {                             // Stop timeline if it's running
            timeline.stop();
        }
        drawNextInstruction();
    }

    @FXML
    private void handleDeleteButtonClicked() {                      // Deletes the contents of the drawing
        drawingPane.getChildren().clear();
        if (timeline != null) {
            timeline.stop();
        }
        currentInstructionIndex = 0;                        // Reset current instruction index
        drawingInstructions.clear();
        //loadFileButton.setDisable(false);
        hasDrawn = false;
        // Set flag to indicate no drawing has been done
    }

    @FXML
    private void handleSaveButtonClicked() {             // Save the design
        if (hasDrawn) {
            WritableImage snapshot = drawingPane.snapshot(null, null);              // Take snapshot of drawing pane

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Drawing");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("PNG Files", "*.png"),
                    new FileChooser.ExtensionFilter("JPEG Files", "*.jpg", "*.jpeg")
            );
            File file = fileChooser.showSaveDialog(drawingPane.getScene().getWindow());                         // Show file chooser dialog
            if (file != null) {
                try {
                    ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file);   //Save image to file
                } catch (IOException ex) {
                    System.out.println("Error saving image: " + ex.getMessage());                   // Print error if saving fails
                }
            }
        } else {
            System.out.println("Nothing to save. Drawing area is empty.");                          // Print message if there's nothing to save
        }
    }


    /*@FXML
    private void handleSlowSpeedButtonClicked() {
        startDrawing(2000); // Start drawing with a delay of 2000 milliseconds
    }

    @FXML
    private void handleMediumSpeedButtonClicked() {
        startDrawing(1000); // Start drawing with a delay of 1000 milliseconds
    }

    @FXML
    private void handleFastSpeedButtonClicked() {
        startDrawing(500); // Start drawing with a delay of 500 milliseconds
    }*/


    // Method to draw next instruction
    private void drawNextInstruction() {                    // Draw the next instruction, otherwise stop the animation
        /*if (currentInstructionIndex < drawingInstructions.size()) {     // Check if there are more instructions to draw
            String instruction = drawingInstructions.get(currentInstructionIndex);
            interpretInstruction(instruction);              // Interpret and execute the instruction
            currentInstructionIndex++;
        } else {
            if (timeline != null) {                         // Check if there is a last instruction
                timeline.stop();
            }
        }*/

    }

    public void test()
    {
        System.out.println("hallo");
    }

    //FORWARD function, used in the interpreter
    public int FWD(Cursor c, double distance, int percent) throws InterruptedException {
        System.out.println("FWD CALLED");
        if(percent != 0 && percent != 1){
            System.out.println("You must put a number between 0 and 1");
            return 0;
        }
        if(percent == 1){
             distance = Math.max(drawingPane.getPrefWidth(), drawingPane.getPrefHeight()) * distance / 100;
        }
        double newX = c.getX() + distance * Math.cos(Math.toRadians(c.getRotation()));   // Calculates the new cursor coordinates
        double newY = c.getY() + distance * Math.sin(Math.toRadians(c.getRotation()));
        if( newX > drawingPane.getWidth() ||newX < 0){
            System.out.println("You are out of borders, you cannot draw more than " + drawingPane.getWidth() + " pixels");
            return 0;
        }
        if( newY > drawingPane.getHeight() || newY < 0){
            System.out.println("You are out of borders, you cannot draw more than " + drawingPane.getHeight() + " pixels");
            return 0;
        }
        System.out.println("Forwarding ... "+"newX = " + newX + ", newY = " + newY);

        /*meline timeLine = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> gc.strokeLine(c.getX(), c.getY(), newX, newY))
        );
        timeLine.play();*/
        //Thread.sleep(500);
        gc.strokeLine(c.getX(), c.getY(), newX, newY);
         // Draw line from current position to new position
        //System.out.println(c.getX() + " " + c.getY());
        c.setX(newX);                               // New coordinates
        c.setY(newY);
        return 1;
    }


    //BWD function, used in the interpreter
    public int BWD(Cursor c, double size, int percent){
        if(percent != 0 && percent != 1){
            System.out.println("You must put a number between 0 and 1");
            return 0;
        }
        if(percent == 1){
            size = Math.max(drawingPane.getPrefWidth(), drawingPane.getPrefHeight()) * size / 100;
        }
        double newX = c.getX() - size * Math.cos(Math.toRadians(c.getRotation()));   // Calculates the new cursor coordinates
        double newY = c.getY() - size * Math.sin(Math.toRadians(c.getRotation()));
        if( newX > drawingPane.getWidth() || newX < 0){
            System.out.println("You are out of borders, you cannot draw more than " + drawingPane.getWidth() + " pixels");
            return 0;
        }
        if( newY > drawingPane.getHeight()  || newY < 0){
            System.out.println("You are out of borders, you cannot draw more than " + drawingPane.getHeight() + " pixels");
            return 0;
        }
        gc.strokeLine(c.getX(), c.getY(), newX, newY);        // Draw line from current position to new position
        c.setX(newX);                               // New coordinates
        c.setY(newY);
        return 1;
    }

    //TURN function, used in the interpreter
    public int TURN(Cursor c, double degree){
        c.setRotation((c.getRotation() + degree) % 360);
        return 1;
    }

    public int COLORHEX(String hex){
        Color color = Color.web(hex);  // Take the second element as a hexadecimal code or colour name
        gc.setStroke(color);
        return 1;
    }

    public int COLORRGB(double red, double green, double blue){

        Color color = Color.color(red, green, blue);   // Take the second element as a hexadecimal code or colour name
        gc.setStroke(color);
        return 1;
    }

    public int MOV(Cursor c, double x, double y, int percent){
        if(percent != 0 && percent != 1){
            System.out.println("You must put a number between 0 and 1");
            return 0;
        }
        if(percent == 1){
             x = drawingPane.getPrefWidth() * x / 100;
             y = drawingPane.getPrefHeight() * y / 100;
        }

        x = c.getX() + x;
        y = c.getY() + y;

        if( x > drawingPane.getWidth() || x < 0){
            System.out.println("You are out of borders, you cannot move outside of " + drawingPane.getWidth());
            return 0;
        }
        if( y > drawingPane.getHeight()  || y < 0){
            System.out.println("You are out of borders, you cannot move outside of" + drawingPane.getHeight());
            return 0;
        }
        c.setX(x);                              // New coordinates
        c.setY(y);
        gc.moveTo(x, y);
        return 1;
    }

    public int POS(Cursor c, double x, double y, int percent) throws InterruptedException {
        if(percent != 0 && percent != 1){
            System.out.println("You must put a number between 0 and 1");
            return 0;
        }
        if(percent == 1){
            x = drawingPane.getWidth() * x / 100;
            y = drawingPane.getHeight() * y / 100;
            System.out.println(drawingPane.getWidth() + " " + drawingPane.getHeight()+" + "+x+" "+y);
        }
        if( x > drawingPane.getWidth() || x < 0){
            System.out.println("You are out of borders, you cannot move outside of " + drawingPane.getWidth());
            return 0;
        }
        if( y > drawingPane.getHeight()  || y < 0){
            System.out.println("You are out of borders, you cannot move outside of" + drawingPane.getHeight());
            return 0;
        }
        double New_x = x;
        double New_y = y;
        gc.strokeLine(c.getX(), c.getY(), x, y);

        c.setX(x);                               // New coordinates
        c.setY(y);
        gc.moveTo(x, y);
        System.out.println("POS changed");
        return 1;
    }

    public int PRESS(Cursor c, double value){
        if(value < 0 || value > 1){
            System.out.println("You must put a number between 0 and 1");
            return 0;
        }
        gc.setGlobalAlpha(value);
        return 1;
    }

    public int LOOKAT(Cursor c, double x, double y, int percent) {
        if(percent != 0 && percent != 1){
            System.out.println("You must put a number between 0 and 1");
            return 0;
        }
        if(percent == 1){
            x = drawingPane.getPrefWidth() * x / 100;
            y = drawingPane.getPrefHeight() * y / 100;
        }
        if(c.getX() == x && c.getY() == y){
            c.setRotation(0);
            return 1;
        }
        double hyp = Math.sqrt((x - c.getX())*(x - c.getX()) + (y - c.getY())*(y - c.getY()));
        double opp = Math.sqrt((x - c.getX())*(x - c.getX()));
         c.setRotation(Math.toDegrees(Math.acos(opp/hyp)));
        return 1;
    }

    public int LOOKATCursor(Cursor c1, Cursor c2){
        return LOOKAT(c1, c2.getX(), c2.getY(), 0);
    }

    public int THICK(double value){
        if(value < 0){
            return  0;
        }
        gc.setLineWidth(value);// Width definition
        return 1;
    }

}
