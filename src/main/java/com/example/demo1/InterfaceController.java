package com.example.demo1;

// For animation and timing
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
// JavaFX image conversion
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
// Various components of the JavaFX user interface
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import javafx.stage.FileChooser;                // File management
import javafx.util.Duration;                    // Time management

import javax.imageio.ImageIO;           // Reading and writing images
import java.io.File;                    // File management
import java.io.IOException;             // I/O exception handling
import java.io.BufferedReader;          // BufferedReader for file reading
import java.io.FileReader;              // FileReader for file reading
// Use of lists
import java.util.ArrayList;
import java.util.List;


public class InterfaceController {

    @FXML
    private Pane drawingPane;
    @FXML
    private Button stepButton;
    @FXML
    private List<String> drawingInstructions;
    @FXML
    private Button loadFileButton;
    @FXML
    private Button slowSpeedButton;
    @FXML
    private Button mediumSpeedButton;
    @FXML
    private Button fastSpeedButton;
    @FXML
    private ComboBox<String> speedComboBox;

    private int currentInstructionIndex = 0;

    private Timeline timeline;

    private GraphicsContext gc;

    private boolean hasDrawn = false;

    private Scene scene;

    public class Cursor {

        private double x = 0, y = 0;
        private int red = 0, green = 0, blue = 0;
        private double rotation = 0;

        public Cursor(){}
    }

    Cursor cursor1 = new Cursor();


    @FXML
    public void initialize() {
        drawingInstructions = new ArrayList<>();
        stepButton.setDisable(true);            // Deactivate step at start-up
        Canvas canvas = new Canvas(drawingPane.getPrefWidth(), drawingPane.getPrefHeight());    // Create a canvas for the design and define the necessary parameters
        drawingPane.getChildren().add(canvas);
        gc = canvas.getGraphicsContext2D();
        gc.setLineWidth(1);                     // Set default line width
        gc.setStroke(Color.BLACK);              // Set default stroke color

        cursor1.x = canvas.getWidth() / 2;          // Set initial x position at the center of the canvas
        cursor1.y = canvas.getHeight() / 2;         // Set initial y position at the center of the canvas
        cursor1.rotation = 0;                          // Initialize angle to 0


        drawingPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
                    if (newScene != null) {
                        scene = newScene;
                    }
                }
        );
    }

    @FXML
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
    }

    // Handler for loading instructions from a file
    @FXML
    private void handleLoadFileButtonClicked() {
        FileChooser fileChooser = new FileChooser();            // Creates a file selection dialog box
        fileChooser.setTitle("Open Instruction File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fileChooser.showOpenDialog(drawingPane.getScene().getWindow());         // Show file chooser dialog
        if (file != null) {
            loadInstructionsFromFile(file);                         // Loads instructions from the file
        }
    }

    // Method to load drawing instructions from a file
    private void loadInstructionsFromFile(File file) {
        drawingInstructions.clear();                // Delete existing instructions
        currentInstructionIndex = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {            // Each line is added to the list of instructions after being cleaned up with trim()
            String line;
            while ((line = br.readLine()) != null) {
                drawingInstructions.add(line.trim());                // Add each line like instructions
            }
        } catch (IOException e) {
            e.printStackTrace();                                // Print Error
        }
        int defaultDelay = 1000;
        if (!drawingInstructions.isEmpty()) {
            stepButton.setDisable(false);                       // Activate the step-by-step button if instructions are loaded
            hasDrawn = true;
            startDrawing(defaultDelay);
            loadFileButton.setText("Loaded file");              // Update Button
        }
    }

    // Deletes the contents of the drawing pane and resets the canvas
    @FXML
    private void handleDrawButtonClicked() {
        drawingPane.getChildren().clear();               // Clear drawing pane
        Canvas canvas = new Canvas(drawingPane.getPrefWidth(), drawingPane.getPrefHeight());        // Create new canvas
        drawingPane.getChildren().add(canvas);           // Add canvas to drawing pane
        gc = canvas.getGraphicsContext2D();              // Get graphics context for drawing
        gc.setLineWidth(1);
        gc.setStroke(Color.BLACK);

        cursor1.x = canvas.getWidth() / 2;
        cursor1.y = canvas.getHeight() / 2;
        cursor1.rotation = 0;
        int defaultDelay = 1000;
        if (!drawingInstructions.isEmpty()) {           // Start drawing if instructions are available             // Start drawing
            stepButton.setDisable(false);
            hasDrawn = true;
            startDrawing(defaultDelay);
        }
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
        stepButton.setDisable(true);
        hasDrawn = false;                                   // Set flag to indicate no drawing has been done
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


    @FXML
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
    }


    // Method to start drawing instructions with default delay
    private void startDrawing(int delay) {
        if (timeline != null) {
            timeline.stop();
        }

        timeline = new Timeline(new KeyFrame(Duration.millis(delay), event -> drawNextInstruction()));
        timeline.setCycleCount(drawingInstructions.size());
        timeline.play();
    }



    // Method to draw next instruction
    private void drawNextInstruction() {                    // Draw the next instruction, otherwise stop the animation
        if (currentInstructionIndex < drawingInstructions.size()) {     // Check if there are more instructions to draw
            String instruction = drawingInstructions.get(currentInstructionIndex);
            interpretInstruction(instruction);              // Interpret and execute the instruction
            currentInstructionIndex++;
        } else {
            if (timeline != null) {                         // Check if there is a last instruction
                timeline.stop();
            }
        }
    }

    //FORWARD function, used in the interpreter
    public int FWD(Cursor c, double distance, int percent){
        if(percent != 0 && percent != 1){
            System.out.println("You must put a number between 0 and 1");
            return 0;
        }
        if(percent == 1){
             distance = Math.max(drawingPane.getPrefWidth(), drawingPane.getPrefHeight()) * distance;
        }
        double newX = c.x + distance * Math.cos(Math.toRadians(cursor1.rotation));   // Calculates the new cursor coordinates
        double newY = c.y + distance * Math.sin(Math.toRadians(cursor1.rotation));
        if( newX > drawingPane.getWidth() ||newX < 0){
            System.out.println("You are out of borders, you cannot draw more than " + drawingPane.getWidth() + " pixels");
            return 0;
        }
        if( newY > drawingPane.getHeight() || newY < 0){
            System.out.println("You are out of borders, you cannot draw more than " + drawingPane.getHeight() + " pixels");
            return 0;
        }
        System.out.println("Forwarding ...\n "+"newX = " + newX + "\nnewY = " + newY);
        gc.strokeLine(cursor1.x, cursor1.y, newX, newY);        // Draw line from current position to new position
        c.x = newX;                               // New coordinates
        c.y = newY;
        return 1;
    }

    //BWD function, used in the interpreter
    public int BWD(Cursor c, double size, int percent){
        if(percent != 0 && percent != 1){
            System.out.println("You must put a number between 0 and 1");
            return 0;
        }
        if(percent == 1){
            size = Math.max(drawingPane.getPrefWidth(), drawingPane.getPrefHeight()) * size;
        }
        double newX = cursor1.x - size * Math.cos(Math.toRadians(cursor1.rotation));   // Calculates the new cursor coordinates
        double newY = cursor1.y - size * Math.sin(Math.toRadians(cursor1.rotation));
        if( newX > drawingPane.getWidth() || newX < 0){
            System.out.println("You are out of borders, you cannot draw more than " + drawingPane.getWidth() + " pixels");
            return 0;
        }
        if( newY > drawingPane.getHeight()  || newY < 0){
            System.out.println("You are out of borders, you cannot draw more than " + drawingPane.getHeight() + " pixels");
            return 0;
        }
        gc.strokeLine(cursor1.x, cursor1.y, newX, newY);        // Draw line from current position to new position
        c.x = newX;                               // New coordinates
        c.y = newY;
        return 1;
    }

    //TURN function, used in the interpreter
    public int TURN(Cursor c, double degree){
        c.rotation = (c.rotation + degree) % 360;
        return 1;
    }

    public int COLORHEX(String hex){
        Color color = Color.web(hex);  // Take the second element as a hexadecimal code or colour name
        gc.setStroke(color);
        return 1;
    }

    public int COLORRGB(double red, double green, double blue){

        Color color = Color.color(red, green, blue);;  // Take the second element as a hexadecimal code or colour name
        gc.setStroke(color);
        return 1;
    }

    public int MOV(Cursor c, double x, double y, int percent){
        if(percent != 0 && percent != 1){
            System.out.println("You must put a number between 0 and 1");
            return 0;
        }
        if(percent == 1){
             x = drawingPane.getPrefWidth() * x;
             y = drawingPane.getPrefHeight() * y;
        }

        x = c.x + x;
        y = c.y + y;

        if( x > drawingPane.getWidth() || x < 0){
            System.out.println("You are out of borders, you cannot move outside of " + drawingPane.getWidth());
            return 0;
        }
        if( y > drawingPane.getHeight()  || y < 0){
            System.out.println("You are out of borders, you cannot move outside of" + drawingPane.getHeight());
            return 0;
        }
        c.x = x;                               // New coordinates
        c.y = y;
        gc.moveTo(x, y);
        return 1;
    }

    public int POS(Cursor c, double x, double y, int percent){
        if(percent != 0 && percent != 1){
            System.out.println("You must put a number between 0 and 1");
            return 0;
        }
        if(percent == 1){
            x = drawingPane.getPrefWidth() * x;
            y = drawingPane.getPrefHeight() * y;
        }
        if( x > drawingPane.getWidth() || x < 0){
            System.out.println("You are out of borders, you cannot move outside of " + drawingPane.getWidth());
            return 0;
        }
        if( y > drawingPane.getHeight()  || y < 0){
            System.out.println("You are out of borders, you cannot move outside of" + drawingPane.getHeight());
            return 0;
        }
        c.x = x;                               // New coordinates
        c.y = y;
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
            x = drawingPane.getPrefWidth() * x;
            y = drawingPane.getPrefHeight() * y;
        }
        if(c.x == x && c.y == y){
            c.rotation = 0;
            return 1;
        }
        double hyp = Math.sqrt((x - c.x)*(x - c.x) + (y - c.y)*(y - c.y));
        double opp = Math.sqrt((x - c.x)*(x - c.x));
         c.rotation = Math.toDegrees(Math.acos(opp/hyp));
        return 1;
    }

    public int LOOKATCursor(Cursor c1, Cursor c2){
        return LOOKAT(c1, c2.x, c2.y, 0);
    }

    public int THICK(double value){
        if(value < 0){
            return  0;
        }
        gc.setLineWidth(value);// Width definition
        return 1;
    }

    // Method to interpret and execute drawing instruction
    private void interpretInstruction(String instruction) {
        String[] parts = instruction.split(" ");          // Create an array shares to store and use a space as a delimiter
        if (parts.length < 1) return;                           // Check, table not empty

        String command = parts[0];                          // Get command from instruction

        try {
            switch (command) {                              // Select the block of code to be executed
                case "FWD":                                 // Moves the cursor relatively
                    if (parts.length < 2)
                        throw new IllegalArgumentException("FWD command requires 1 parameter");
                    System.out.println("Forward "+parts[1]);
                    FWD(cursor1, Double.parseDouble(parts[1]), 0);
                    break;

                case "TURN":                    // rotates the cursor relatively in degrees
                    if (parts.length < 2) throw new IllegalArgumentException("TURN command requires 1 parameter");
                    System.out.println("TURN "+parts[1]);
                    TURN(cursor1, Double.parseDouble(parts[1]));      // Adds the angle of rotation to the current one
                    break;

                case "COLOR":                           // Determines the colour
                    if (parts.length < 2) throw new IllegalArgumentException("COLOR command requires 1 parameter");
                    System.out.println("COLOR "+parts[1]);
                    COLORHEX(parts[1]);
                    break;

                case "BWD":                     // Moves the cursor back relatively
                    if (parts.length < 2) throw new IllegalArgumentException("BWD command requires 1 parameter");
                    System.out.println("BWD "+parts[1]);
                    BWD(cursor1,Double.parseDouble(parts[1]), 0);
                    break;

                case "MOV":                     // Moves the cursor relatively
                    if (parts.length < 3) throw new IllegalArgumentException("MOV command requires 2 parameters");
                    System.out.println("MOV "+parts[1]+" "+parts[2]);
                    MOV(cursor1, Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), 0);
                    break;

                case "THICK":                   // Is used to define the thickness of a line before moving the cursor
                    if (parts.length < 2) throw new IllegalArgumentException("THICK command requires 1 parameter");
                    System.out.println("THICK "+parts[1]);
                    THICK(Double.parseDouble(parts[1]));
                    break;

                case "HIDE":                    //hide the cursor on the screen
                    if (scene != null) {
                        scene.setCursor(javafx.scene.Cursor.NONE);
                    }
                    break;

                case "SHOW":                    //displays the cursor on the screen
                    if (scene != null) {
                        scene.setCursor(javafx.scene.Cursor.CROSSHAIR);
                    }
                    break;

                case "POS":             // positions the cursor on the screen
                    if (parts.length < 3) throw new IllegalArgumentException("POS command requires 2 parameters");
                    System.out.println("POS "+parts[1]+" "+parts[2]);
                    POS(cursor1, Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), 1);
                    break;

                case "PRESS":           // indicates the pressure with which the cursor draws the shape
                    if (parts.length < 2) throw new IllegalArgumentException("PRESS command requires 1 parameter");
                    System.out.println("PRESS "+parts[1]);
                    PRESS(cursor1, Double.parseDouble(parts[1]));
                    break;

                case "LOOKAT":          // rotates the current cursor
                    if (parts.length < 3) throw new IllegalArgumentException("LOOKAT command requires 1 parameter");
                    System.out.println("LOOKAT "+parts[1]+" "+parts[2]);
                    LOOKAT(cursor1,Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), 1);
                    break;

                default:
                    throw new IllegalArgumentException("Unknown command: " + command);          // Throw exception for unknown command
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());         // Print Error message
        }
    }
}
