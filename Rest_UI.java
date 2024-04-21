import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class Main extends Application {

    private Stage primaryStage;
    private VBox reservationBox;
    private GridPane tableInfoGrid;
    private VBox tableDetailsBox;
    private Text tableInfoText;
    private Text tableOrderText;
    private Text totalBillText;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Create main layout
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        // Reservation information box
        reservationBox = createReservationBox();

        // Table information box
        tableInfoGrid = createTableInfoGrid();

        // Table details box
        tableDetailsBox = createBox("Table Details");

        // Add table information, order, and total bill to the table details box
        tableInfoText = new Text();
        tableOrderText = new Text();
        totalBillText = new Text();
        tableDetailsBox.getChildren().addAll(createTitle("Table Information"), tableInfoText, createTitle("Table Order"), tableOrderText, createTitle("Total Bill"), totalBillText);

        // Bind width and height of sub-boxes to 90% of the window's width and height
        double subBoxWidth = primaryStage.widthProperty().multiply(0.9).doubleValue();
        double subBoxHeight = primaryStage.heightProperty().multiply(0.9).doubleValue();
        reservationBox.prefWidthProperty().bind(Bindings.createDoubleBinding(() -> subBoxWidth, primaryStage.widthProperty()));
        reservationBox.prefHeightProperty().bind(Bindings.createDoubleBinding(() -> subBoxHeight, primaryStage.heightProperty()));
        tableInfoGrid.prefWidthProperty().bind(Bindings.createDoubleBinding(() -> subBoxWidth, primaryStage.widthProperty()));
        tableInfoGrid.prefHeightProperty().bind(Bindings.createDoubleBinding(() -> subBoxHeight, primaryStage.heightProperty()));
        tableDetailsBox.prefWidthProperty().bind(Bindings.createDoubleBinding(() -> subBoxWidth, primaryStage.widthProperty()));
        tableDetailsBox.prefHeightProperty().bind(Bindings.createDoubleBinding(() -> subBoxHeight, primaryStage.heightProperty()));

        // Add boxes to main layout
        root.getChildren().addAll(reservationBox, tableInfoGrid, tableDetailsBox);

        // Create a scroll pane and set root VBox as its content
        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        // Create scene and set stage
        Scene scene = new Scene(scrollPane, 600, 400);
        primaryStage.setTitle("Restaurant Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Method to create a generic box with a title
    private VBox createBox(String titleText) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-border-color: black; -fx-border-width: 1px;");
        Text title = createTitle(titleText);
        box.getChildren().add(title);
        return box;
    }

    // Method to create a title text
    private Text createTitle(String titleText) {
        Text title = new Text(titleText);
        title.setStyle("-fx-font-weight: bold");
        return title;
    }

    // Method to create reservation information box with random reservations
    private VBox createReservationBox() {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-border-color: black; -fx-border-width: 1px;");
        Text title = new Text("Reservation Information");
        box.getChildren().add(title);

        // Generate 10 random reservations
        List<Hyperlink> hyperlinks = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            String name = generateRandomName();
            String time = generateRandomTime();
            Hyperlink reservationLink = new Hyperlink("Reservation " + i + ": " + name + " - " + time);
            reservationLink.setOnAction(event -> showCancelReservationDialog(name, reservationLink)); // Action handling for cancelling reservations
            hyperlinks.add(reservationLink);
        }

        // Sort reservations based on time
        Collections.sort(hyperlinks, Comparator.comparing(h -> getTimeFromHyperlink((Hyperlink) h)));

        // Add sorted reservations to the box
        box.getChildren().addAll(hyperlinks);
        return box;
    }

    // Method to create table information grid
    private GridPane createTableInfoGrid() {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));
        gridPane.setStyle("-fx-border-color: black; -fx-border-width: 1px;");
        Text title = new Text("Table Information");
        gridPane.add(title, 0, 0, 3, 1); // Span title across 3 columns
        int tableNumber = 1;
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 3; col++) {
                StackPane tablePane = createTablePane(tableNumber, generateTableCapacity());
                gridPane.add(tablePane, col, row + 1); // Start from row 1
                tableNumber++;
            }
        }
        return gridPane;
    }

    // Method to create a table pane with name and capacity
    private StackPane createTablePane(int tableNumber, int capacity) {
        Rectangle tableRect = new Rectangle(100, 50);
        tableRect.setStroke(Color.BLACK);
        tableRect.setStrokeWidth(2);
        tableRect.setFill(Color.GREEN); // Green color for free tables
        tableRect.setArcWidth(10);
        tableRect.setArcHeight(10);

        Text tableText = new Text("Table " + tableNumber + "\nCapacity: " + capacity);
        tableText.setFill(Color.WHITE);
        StackPane tablePane = new StackPane(tableRect, tableText);
        tablePane.setAlignment(Pos.CENTER);

        tablePane.setOnMouseClicked(event -> {
            if (tableRect.getFill().equals(Color.RED)) {
                // Show table information in the third sub-box
                String tableInfo = "Table " + tableNumber + "\nCapacity: " + capacity + "\nStatus: Occupied";
                String tableOrder = "Table " + tableNumber + " Order:\n- <Food and drinks>\n- <Food and drinks>\n- <Food and drinks>";
                String totalBill = "Total Bill: $XXX"; // Placeholder for the total bill
                updateTableDetails(tableInfo, tableOrder, totalBill);

                // Add "Cheque" button to make the table available again
                Button chequeButton = new Button("Cheque");
                chequeButton.setOnAction(e -> showMakeTableAvailableDialog(tableNumber, capacity, tableRect, tableText));
                VBox buttonBox = new VBox(5, chequeButton);
                buttonBox.setAlignment(Pos.CENTER);
                tableDetailsBox.getChildren().add(buttonBox);
            } else {
                if (tableRect.getFill().equals(Color.GREEN)) {
                    tableRect.setFill(Color.RED);
                    tableText.setText("Table " + tableNumber + "\nCapacity: " + capacity + "\nOccupied");
                } else {
                    tableRect.setFill(Color.GREEN);
                    tableText.setText("Table " + tableNumber + "\nCapacity: " + capacity);
                }
            }
        });

        return tablePane;
    }

    // Method to update table details in the third sub-box
    private void updateTableDetails(String tableInfo, String tableOrder, String totalBill) {
        tableInfoText.setText(tableInfo);
        tableOrderText.setText(tableOrder);
        totalBillText.setText(totalBill);
    }

    // Method to extract time from Hyperlink text
    private String getTimeFromHyperlink(Hyperlink hyperlink) {
        String[] parts = hyperlink.getText().split(": ");
        return parts[1].split(" - ")[1];
    }

    // Method to generate a random name
    private String generateRandomName() {
        String[] names = {"Alice", "Bob", "Charlie", "David", "Emma", "Frank", "Grace", "Henry", "Ivy", "Jack"};
        Random random = new Random();
        return names[random.nextInt(names.length)];
    }

    // Method to generate a random time within the restaurant's opening hours
    private String generateRandomTime() {
        Random random = new Random();
        int hour = random.nextInt(8) + 17; // Random hour between 17:00 and 00:00
        int minute = random.nextInt(61); // Random minute
        return String.format("%02d:%02d", hour, minute);
    }

    // Method to generate table capacity (2, 4, or 8 people)
    private int generateTableCapacity() {
        Random random = new Random();
        int[] capacities = {2, 4, 8}; // Table capacities: 2, 4, 8
        return capacities[random.nextInt(capacities.length)];
    }

    // Method to show make table available dialog
    private void showMakeTableAvailableDialog(int tableNumber, int capacity, Rectangle tableRect, Text tableText) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        VBox dialogVBox = new VBox(20);
        dialogVBox.setAlignment(Pos.CENTER);
        dialogVBox.setPadding(new Insets(20));
        Text dialogText = new Text("Do you want to make Table " + tableNumber + " available?");
        Button okButton = new Button("OK");
        okButton.setOnAction(event -> {
            tableRect.setFill(Color.GREEN);
            tableText.setText("Table " + tableNumber + "\nCapacity: " + capacity);
            dialogStage.close();
        });
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(event -> dialogStage.close());
        dialogVBox.getChildren().addAll(dialogText, okButton, cancelButton);
        Scene dialogScene = new Scene(dialogVBox, 300, 200);
        dialogStage.setTitle("Make Table Available");
        dialogStage.setScene(dialogScene);
        dialogStage.show();
    }

    // Method to show cancel reservation dialog
    private void showCancelReservationDialog(String name, Hyperlink reservationLink) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        VBox dialogVBox = new VBox(20);
        dialogVBox.setAlignment(Pos.CENTER);
        dialogVBox.setPadding(new Insets(20));
        Text dialogText = new Text("Do you want to cancel reservation for " + name + "?");
        Button okButton = new Button("Yes");
        okButton.setOnAction(event -> {
            reservationBox.getChildren().remove(reservationLink);
            dialogStage.close();
        });
        Button cancelButton = new Button("No");
        cancelButton.setOnAction(event -> dialogStage.close());
        dialogVBox.getChildren().addAll(dialogText, okButton, cancelButton);
        Scene dialogScene = new Scene(dialogVBox, 300, 200);
        dialogStage.setTitle("Cancel Reservation");
        dialogStage.setScene(dialogScene);
        dialogStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
