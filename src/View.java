import java.io.File;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class View extends Application {
	private static Stage mainStage;
	private static Stage messagePopUp;
	private static Stage passwordPopUp;
	private static TableView<Room> table;
	private static Button uploadButton;
	private static Button loadButton;
	private static Button saveButton;
	private static String errorMessage;
	
	public static void displayView(String error) {
		errorMessage = error;
		View.launch();
	}

	@Override
	public void start(Stage stage) {
		setUserAgentStylesheet(STYLESHEET_CASPIAN); // change the JavaFX theme
		mainStage = stage;
		if(errorMessage != null)
			displayMessagePopUp();
		else
			displayPasswordPopUp();
	}
	
	@SuppressWarnings("unchecked")
	public static void displayMainWindow() {
		
		// create input filter
		/*
		TextField filterInput = new TextField();
		filterInput.setPromptText("Search an image");
		filterInput.setFocusTraversable(false);
		filterInput.setMaxWidth(400);
		*/

		// create table
		table = new TableView<Room>();
		table.setEditable(false);

		// create columns
		TableColumn<Room, ImageView> image = new TableColumn<Room, ImageView>("Image");
		image.setCellValueFactory(new PropertyValueFactory<Room, ImageView>("image"));
		TableColumn<Room, String> styleNumber = new TableColumn<Room, String>("Style number");
		styleNumber.setCellValueFactory(new PropertyValueFactory<Room, String>("styleNumber"));
		TableColumn<Room, String> name = new TableColumn<Room, String>("Name");
		name.setCellValueFactory(new PropertyValueFactory<Room, String>("name"));
		TableColumn<Room, String> size = new TableColumn<Room, String>("Size");
		size.setCellValueFactory(new PropertyValueFactory<Room, String>("size"));
		TableColumn<Room, String> frontImageUrl = new TableColumn<Room, String>("Front image URL");
		frontImageUrl.setCellValueFactory(new PropertyValueFactory<Room, String>("frontImageUrl"));
		TableColumn<Room, String> backImageUrl = new TableColumn<Room, String>("Back image URL");
		backImageUrl.setCellValueFactory(new PropertyValueFactory<Room, String>("backImageUrl"));
		TableColumn<Room, String> firstExtraImageUrl = new TableColumn<Room, String>("First extra image URL");
		firstExtraImageUrl.setCellValueFactory(new PropertyValueFactory<Room, String>("firstExtraImageUrl"));
		TableColumn<Room, String> secondExtraImageUrl = new TableColumn<Room, String>("Second extra image URL");
		secondExtraImageUrl.setCellValueFactory(new PropertyValueFactory<Room, String>("secondExtraImageUrl"));
		TableColumn<Room, String> thirdExtraImageUrl = new TableColumn<Room, String>("Third extra image URL");
		thirdExtraImageUrl.setCellValueFactory(new PropertyValueFactory<Room, String>("thirdExtraImageUrl"));

		// add columns to table
		table.setItems(Controller.getObservableTable());
		table.getColumns().addAll(image, styleNumber, name, size, frontImageUrl, backImageUrl, firstExtraImageUrl, secondExtraImageUrl, thirdExtraImageUrl);
		//table.setMinWidth(990);

		// adapt width of columns
		//image.prefWidthProperty().bind(table.widthProperty().multiply(0.2));
		image.setMaxWidth(Room.IMAGE_WIDTH);
		image.setPrefWidth(Room.IMAGE_WIDTH);
		styleNumber.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
		name.prefWidthProperty().bind(table.widthProperty().multiply(0.2));
		size.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
		frontImageUrl.prefWidthProperty().bind(table.widthProperty().multiply(0.2));
		backImageUrl.prefWidthProperty().bind(table.widthProperty().multiply(0.2));
		firstExtraImageUrl.prefWidthProperty().bind(table.widthProperty().multiply(0.2));
		secondExtraImageUrl.prefWidthProperty().bind(table.widthProperty().multiply(0.2));
		thirdExtraImageUrl.prefWidthProperty().bind(table.widthProperty().multiply(0.2));

		// buttons
		loadButton = new Button("Load sheet");
		loadButton.setMinWidth(100);
		loadButton.setOnAction(displayDialogBox);
		
		uploadButton = new Button("Upload images");
		uploadButton.setMinWidth(100);
		uploadButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Controller.runProcess("upload");
			}
		});
		
		saveButton = new Button("Save");
		saveButton.setMinWidth(100);
		saveButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(Controller.getWorkbookFile() != null)
					Controller.runProcess("save");
			}
		});
		
		// create vertical layout
		VBox vBox = new VBox();
		VBox.setVgrow(table, Priority.ALWAYS);
		vBox.setPadding(new Insets(10));
		vBox.setSpacing(10);
		//vBox.getChildren().add(filterInput);
		vBox.getChildren().add(table);

		// create border layout
		BorderPane borderPane = new BorderPane();
		borderPane.setPadding(new Insets(0, 50, 0, 50));
		borderPane.setLeft(loadButton);
		borderPane.setCenter(uploadButton);
		borderPane.setRight(saveButton);
		vBox.getChildren().add(borderPane);

		// display the GUI
		mainStage.setMaximized(true);
		mainStage.setScene(new Scene(vBox));
		mainStage.setTitle("Automated URL Program");
		mainStage.show();
		// delegate the focus to the container
		vBox.requestFocus();
	}
	
	public static void displayPasswordPopUp() {
		passwordPopUp = new Stage();
		
		// event when the user closes the pop-up
		passwordPopUp.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				passwordPopUp.close();
			}
		});
		
		// create the password input
		PasswordField passwordInput = new PasswordField();
		passwordInput.setAlignment(Pos.CENTER);
		passwordInput.setPrefWidth(200);
		passwordInput.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.ENTER && Controller.isValidPassword(passwordInput.getText())) {
                	passwordPopUp.close();
					displayMainWindow();
                }
            }
        });
		
		// create the submit button
		Button submitButton = new Button("Submit");
		submitButton.setPrefWidth(100);
		submitButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(Controller.isValidPassword(passwordInput.getText())) {
					passwordPopUp.close();
					displayMainWindow();
				}
			}
		});
		
		// create the vertical layout
		VBox vBox = new VBox();
		vBox.setPadding(new Insets(10));
		vBox.setSpacing(10);
		vBox.getChildren().add(passwordInput);
		vBox.getChildren().add(new BorderPane(submitButton));
		
		passwordPopUp.setScene(new Scene(vBox));
		passwordPopUp.setTitle("Password");
		passwordPopUp.initModality(Modality.NONE);
		passwordPopUp.initOwner(mainStage);
		passwordPopUp.show();
	}
	
	public static void displayMessagePopUp() {
		messagePopUp = new Stage();
		
		// event when the user closes the pop-up
		messagePopUp.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				Controller.cancelCurrentProcess();
				messagePopUp.close();
			}
		});
		
		// create the message label
		Label messageLabel = new Label();
		messageLabel.setAlignment(Pos.CENTER);
		if(errorMessage != null)
			messageLabel.setText(errorMessage);
		else
			messageLabel.textProperty().bind(Controller.getMessage());
		messageLabel.setMinWidth(400);
		
		// create the cancel button
		Button cancelButton = new Button(errorMessage != null ? "Exit" : "Cancel");
		cancelButton.setMinWidth(100);
		cancelButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Controller.cancelCurrentProcess();
				messagePopUp.close();
			}
		});
		
		// create the vertical layout
		VBox vBox = new VBox();
		vBox.setAlignment(Pos.CENTER);
		vBox.setPadding(new Insets(10));
		vBox.setSpacing(20);
		vBox.getChildren().add(messageLabel);
		vBox.getChildren().add(cancelButton);
		
		messagePopUp.setScene(new Scene(vBox));
		messagePopUp.setTitle("Process info");
		messagePopUp.initModality(Modality.NONE);
		messagePopUp.initOwner(mainStage);
		messagePopUp.show();
	}
	
	public static void dismissMessagePopUp() {
		messagePopUp.close();
	}
	
	// dialog box for select input files
	private static final EventHandler<ActionEvent> displayDialogBox = new EventHandler<ActionEvent>() {
		private Stage dialogStage;
		
		@Override
		public void handle(ActionEvent event) {
			this.dialogStage = new Stage();
			
			// create the submit button
			Button submitButton = new Button("Submit");
			submitButton.setMinWidth(100);
			
			
			Object source = event.getSource();
			Pane pane = null;
			if(source == loadButton) {
				pane = createSpreadSheetDialogBox();
				submitButton.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						if(Controller.getWorkbookFile() != null) {
							dialogStage.close();
							Controller.runProcess("load");
						}
					}
				});
			}
			/*
			else if(source == uploadButton) {
				pane = createArchiveDialogBox();
				submitButton.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						if(Controller.getZipArchive() != null) {
							dialogStage.close();
	                        Controller.runProcess("upload");
						}
					}
				});
			}
			*/
			else
				return;
			
			// create the vertical layout
			VBox vBox = new VBox();
			vBox.setPadding(new Insets(10));
			vBox.setSpacing(10);
			vBox.getChildren().add(pane);
			vBox.getChildren().add(new BorderPane(submitButton));
			
			// display the dialog box
			this.dialogStage.setScene(new Scene(vBox));
			this.dialogStage.setTitle("File dialog box");
			this.dialogStage.initModality(Modality.NONE);
			this.dialogStage.initOwner(mainStage);
			this.dialogStage.show();
		}
		
		/*
		private Pane createArchiveDialogBox() {
			// create the zip archive label
			File zipArchive = Controller.getZipArchive();
			Label zipArchiveLabel = new Label(zipArchive == null ? "Select the input ZIP archive to extract" : zipArchive.getAbsolutePath());
			zipArchiveLabel.setMinWidth(400);
			
			// create the zip archive button
			Button zipArchiveButton = new Button("Select ZIP archive");
			zipArchiveButton.setMinWidth(100);
			zipArchiveButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					FileChooser zipFileChooser = new FileChooser();
					zipFileChooser.setTitle("Select the input zip archive to extract");
					zipFileChooser.getExtensionFilters().add(new ExtensionFilter("ZIP archive", "*.zip"));
					File zipArchive = zipFileChooser.showOpenDialog(dialogStage);
					if(zipArchive != null) {
						zipArchiveLabel.setText(zipArchive.getAbsolutePath());
						Controller.setZipArchive(zipArchive);
					}
				}
			});
			
			// create the horizontal layout
			HBox hBox = new HBox();
			hBox.setPadding(new Insets(10));
			hBox.setSpacing(10);
			hBox.getChildren().add(zipArchiveLabel);
			hBox.getChildren().add(zipArchiveButton);
			return hBox;
		}
		*/
		
		private Pane createSpreadSheetDialogBox() {
			// create the spreadsheet file label
			File spreadSheetFile = Controller.getWorkbookFile();
			Label spreadSheetLabel = new Label(spreadSheetFile == null ? "Select the spreadsheet file to complete" : spreadSheetFile.getAbsolutePath());
			spreadSheetLabel.setMinWidth(400);

			// create the spread sheet button
			Button spreadSheetButton = new Button("Select spreadsheet file");
			spreadSheetButton.setMinWidth(100);
			spreadSheetButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					FileChooser spreadSheetChooser = new FileChooser();
					spreadSheetChooser.setTitle("Select the spreadsheet file to complete");
					spreadSheetChooser.getExtensionFilters().add(new ExtensionFilter("Spreadsheet file", "*.xlsx"));
					File spreadSheetFile = spreadSheetChooser.showOpenDialog(dialogStage);
					if(spreadSheetFile != null) {
						spreadSheetLabel.setText(spreadSheetFile.getAbsolutePath());
						Controller.setWorkbookFile(spreadSheetFile);
					}
				}
			});
			
			// create the horizontal layout
			HBox hBox = new HBox();
			hBox.setPadding(new Insets(10));
			hBox.setSpacing(10);
			hBox.getChildren().add(spreadSheetLabel);
			hBox.getChildren().add(spreadSheetButton);
			return hBox;
		}
	};
}