import java.io.*;
import java.lang.Math.*;
import java.net.URL;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.Scene;
import javafx.scene.text.*;
import javafx.scene.web.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import netscape.javascript.JSObject;
import org.json.simple.*;


public class myApp extends Application {
     
    private Scene scene;
	/* antikeimeno tis klasis NetMan, stin opoia ektelountai
	 oi kiries leitourgies tis askisis.
	 */
	NetMan myNetMan;
	/* panel to opoio vrisketai sti mesi tou kirios panel,
	 deixnontas ka8e fora ta apotelesmata, eite se xarti 
	 eite se chart 
	 */
	StackPane displayResults;
	/* Panel me koumpia epilogis provolis xarti i chart. 
	 * sto kato meros tou kirios panel 
	 */
	HBox mapOrChart;
	/* Panel gia tin provoli charts */
	VBox chartPanel;
	/* antikeimeno tipou MyBrowser gia tin provoli tou xarti kai tis javascript */
	MyBrowser myBrowser;
	/* panel me tin istoselida pou periexei to xarti */
	StackPane myPage;
	/* ta pedia tis formas apo ta opoia lamvanoume tin eisodo tou xristi */
	TextField sD;;
	TextField eD;
	TextField ur;
	TextField dM;
	TextField tMa;
	TextField tMi;
	TextField epsT;
	TextField minPtsT;
	Button submit;
	/* koumpia epilogis map or chart */
	Button mapButton;
	Button chartButton;
	/* me to msg emfanizoume minimata sto xristi, otan eisagei lan8asmeni eisodo */
	Text msg;
	LineChart<String, Number> userBatChart;
	/* metavliti pou mas deixnei poio koumpi einai epilegmeno */
	int activeButton = 0;
	/* metavliti pou mas deixnei poio koumpi einai epilegmeno gia map i chart.
	 0: xartis, 1: chart */
    int activeMoc = 0;
	
	public static void main(String[] args) {
        launch(args);
    }
	
    @Override
    public void start(Stage primaryStage) {
		Group root = new Group();
		scene = new Scene(root, 1200, 800);
		scene.getStylesheets().add("myCSS.css");
		GridPane menu = getMenu();
		displayResults = new StackPane();
		mapOrChart = getMapOrChart();
		chartPanel = new VBox();
		myBrowser = new MyBrowser();
		myNetMan = new NetMan();
		
		displayResults.getChildren().add(myPage);
		/* kirios panel, pou periexei ola ta ipoloipa.
		 Aristera vrisketai to menu kai i forma eisagogis.
		 Sto kentro ta optikopoiimena apotelesmata (map, chart).
		 Sto kato meros epilogi gia provoli xarti i chart.
		 */
		BorderPane myPanel = new BorderPane();
		myPanel.setCenter(displayResults);
		myPanel.setLeft(menu);
		myPanel.setBottom(mapOrChart);
		myPanel.prefHeightProperty().bind(scene.heightProperty());
        myPanel.prefWidthProperty().bind(scene.widthProperty());

		root.getChildren().addAll(myPanel);
		
		primaryStage.setTitle("My App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
	
	/** Methodos gia ti dimiourgia tou menu epilogon, ka8os kai tis formas
	  * eisagogis eisodou apo to xristi. Perilamvanei kai ena pedio keimenou,
	  * gia tin emfanisi minimaton se periptosi lan8asmenis eisodou.
	  */
	public GridPane getMenu() {
		GridPane myMenu = new GridPane();
		VBox myOptions = new VBox(8);
		GridPane myForm = new GridPane();
		
		Label title = new Label("Choose action:");
		/* koumpia me tis epiloges */
		Button visualize = new Button("Visualize");
		Button analyze = new Button("Analyze");
		Button analyzeSPs = new Button("Find SPs");
		Button analyzePOIs = new Button("Find POIs");
		Button export = new Button("Export");
		
		/* efarmogi diamorfoseon css */
		title.setId("menutitle");
		title.setMaxWidth(Double.MAX_VALUE);
		visualize.setMaxWidth(Double.MAX_VALUE);
		analyze.setMaxWidth(Double.MAX_VALUE);
		export.setMaxWidth(Double.MAX_VALUE);
		visualize.getStyleClass().add("myButton");
		analyze.getStyleClass().add("myButton");
		export.getStyleClass().add("myButton");
		analyzeSPs.getStyleClass().add("subButton");
		analyzePOIs.getStyleClass().add("subButton");
		
		myOptions.setPadding(new Insets(5, 10, 5, 10));
		myOptions.getChildren().addAll(title, visualize, analyze, analyzeSPs, analyzePOIs, export);
		
		/* dimiourgia ton pedion eisagogis eisodou apo to xristi */
		Label startDate = new Label("Starting date:");
		sD = new TextField("2015-03-27");
		Label endDate = new Label("Ending date:");
		eD = new TextField("2015-05-06");
		Label user = new Label("User:");
		ur = new TextField("0");		
		Label dMax = new Label("Dmax (metres):");
		dM = new TextField("0");
		Label tMax = new Label("Tmax (minutes):");
		tMa = new TextField("0");
		Label tMin = new Label("Tmin (minutes):");
		tMi = new TextField("0");	
		Label epsL = new Label("eps (metres):");
		epsT = new TextField("0");
		Label minPtsL = new Label("min points:");
		minPtsT = new TextField("0");
		/* dimiourgia koumpiou submit, gia na diavastei i eisodos kai na ektelestei i katallili leitourgia */
		submit = new Button("Submit");
		
		/* efarmogi diamorfoseon css */
		startDate.getStyleClass().add("myLabel");
		sD.getStyleClass().add("myText");
		endDate.getStyleClass().add("myLabel");
		eD.getStyleClass().add("myText");
		user.getStyleClass().add("myLabel");
		ur.getStyleClass().add("myText");
		dMax.getStyleClass().add("myLabel");
		dM.getStyleClass().add("myText");
		tMax.getStyleClass().add("myLabel");
		tMa.getStyleClass().add("myText");
		tMin.getStyleClass().add("myLabel");
		tMi.getStyleClass().add("myText");
		epsL.getStyleClass().add("myLabel");
		epsT.getStyleClass().add("myText");
		minPtsL.getStyleClass().add("myLabel");
		minPtsT.getStyleClass().add("myText");
		submit.setId("submit");
		
		/* apenergopoiisi olon ton pedion eisodou kai tou koumpiou submit */
		sD.setDisable(true);
		eD.setDisable(true);
		ur.setDisable(true);
		dM.setDisable(true);
		tMa.setDisable(true);
		tMi.setDisable(true);
		epsT.setDisable(true);
		minPtsT.setDisable(true);
		submit.setDisable(true);
		
		/* pros8iki olon ton pedion sti forma */
		myForm.add(startDate, 0, 0);
		myForm.add(sD, 0, 1);
		myForm.add(endDate, 0, 2);
		myForm.add(eD, 0, 3);
		myForm.add(user, 0, 4);
		myForm.add(ur, 0, 5);
		myForm.add(dMax, 0, 6);
		myForm.add(dM, 0, 7);
		myForm.add(tMax, 0, 8);
		myForm.add(tMa, 0, 9);
		myForm.add(tMin, 0, 10);
		myForm.add(tMi, 0, 11);		
		myForm.add(epsL, 0, 12);
		myForm.add(epsT, 0, 13);
		myForm.add(minPtsL, 0, 14);
		myForm.add(minPtsT, 0, 15);
		myForm.add(submit, 0, 16);
		
		/* diamorfosi tis formas */
		myForm.setHgap(2);
		myForm.setVgap(2);
		myForm.setGridLinesVisible(false);
		myForm.setPadding(new Insets(5, 10, 5, 10));
		
		/* dimiourgia kai diamorfosi pediou gia minima enimerosis xristi */
		msg = new Text();
		msg.setId("myMsg");
		msg.setX(msg.getX() + 10);
		msg.setTextAlignment(TextAlignment.JUSTIFY);
		msg.setFont(Font.font(null, FontWeight.BOLD, 12));
		
		/* prosthiki ton epilogon, tis formas kai tou pediou minimatos sto menu */
		myMenu.add(myOptions, 0, 0);
		myMenu.add(myForm, 0, 1);
		myMenu.add(msg, 0, 2);
		
		/* xeiristis gia to patima tou koumpiou "visualize".
		 Dinoume to default xroma sta ipoloipa koumpia, eno sto energopoiimeno
		 ena pio foteino. 
		 Thetoume to activeButton stin timi 1.
		 Energopoioume ta aparaitita pedia eisagogis eisodou ki apenergopoioume
		 ta ipoloipa.
		 */
		visualize.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent arg0) {
				analyze.setStyle("-fx-background-color:  #16A085;");
				analyzeSPs.setStyle("-fx-background-color: #D17519;");
				analyzePOIs.setStyle("-fx-background-color: #D17519;");
				export.setStyle("-fx-background-color:  #16A085;");
				visualize.setStyle("-fx-background-color:  #00CC99;");
				chartButton.setStyle("-fx-background-color:  #16A085;");
				mapButton.setStyle("-fx-background-color:  #00CC99;");
				activeButton = 1;
				activeMoc = 0;
				sD.setDisable(false);
				eD.setDisable(false);
				ur.setDisable(false);
				dM.setDisable(true);
				tMa.setDisable(true);
				tMi.setDisable(true);
				epsT.setDisable(true);
				minPtsT.setDisable(true);
				submit.setDisable(false);
				mapButton.setDisable(false);
				chartButton.setDisable(false);
            }
		});
		
		/* xeiristis gia to patima tou koumpiou "analyze".
		 Dinoume to default xroma sta ipoloipa koumpia, eno sto energopoiimeno
		 ena pio foteino. 
		 Thetoume to activeButton stin timi 2.
		 Apenergopoioume ola ta pedia eisodou mexri o xristis na epile3ei
		 mia apo tis dio leitourgies pou prosferontai edo.
		 */
		analyze.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent arg0) {
				visualize.setStyle("-fx-background-color:  #16A085;");
				export.setStyle("-fx-background-color:  #16A085;");
				analyze.setStyle("-fx-background-color:  #00CC99;");
				analyzeSPs.setStyle("-fx-background-color: #D17519;");
				analyzePOIs.setStyle("-fx-background-color: #D17519;");
				activeButton = 2;
				sD.setDisable(true);
				eD.setDisable(true);
				ur.setDisable(true);
				dM.setDisable(true);
				tMa.setDisable(true);
				tMi.setDisable(true);
				epsT.setDisable(true);
				minPtsT.setDisable(true);
				submit.setDisable(true);
				mapButton.setDisable(true);
				chartButton.setDisable(true);
            }
		});
		
		/* xeiristis gia to patima tou koumpiou "find SPs".
		 Dinoume to default xroma sta ipoloipa koumpia, eno sto energopoiimeno
		 ena pio foteino. 
		 Thetoume to activeButton stin timi 21.
		 Energopoioume ta aparaitita pedia eisagogis eisodou ki apenergopoioume
		 ta ipoloipa.
		 */
		analyzeSPs.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent arg0) {
				visualize.setStyle("-fx-background-color:  #16A085;");
				export.setStyle("-fx-background-color:  #16A085;");
				analyze.setStyle("-fx-background-color:  #00CC99;");
				analyzePOIs.setStyle("-fx-background-color: #D17519;");
				analyzeSPs.setStyle("-fx-background-color: #FFA319;");
				activeButton = 21;
				sD.setDisable(false);
				eD.setDisable(false);
				ur.setDisable(false);
				dM.setDisable(false);
				tMa.setDisable(false);
				tMi.setDisable(false);
				epsT.setDisable(true);
				minPtsT.setDisable(true);
				submit.setDisable(false);
				mapButton.setDisable(true);
				chartButton.setDisable(true);
            }
		});
		
		/* xeiristis gia to patima tou koumpiou "find POIs".
		 Dinoume to default xroma sta ipoloipa koumpia, eno sto energopoiimeno
		 ena pio foteino. 
		 Thetoume to activeButton stin timi 22.
		 Energopoioume ta aparaitita pedia eisagogis eisodou.
		 */
		analyzePOIs.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent arg0) {
				visualize.setStyle("-fx-background-color:  #16A085;");
				export.setStyle("-fx-background-color:  #16A085;");
				analyze.setStyle("-fx-background-color:  #00CC99;");
				analyzePOIs.setStyle("-fx-background-color: #FFA319;");
				analyzeSPs.setStyle("-fx-background-color: #D17519;");
				activeButton = 22;
				sD.setDisable(false);
				eD.setDisable(false);
				ur.setDisable(true);
				dM.setDisable(false);
				tMa.setDisable(false);
				tMi.setDisable(false);
				epsT.setDisable(false);
				minPtsT.setDisable(false);
				submit.setDisable(false);
				mapButton.setDisable(true);
				chartButton.setDisable(true);
            }
		});
		
		/* xeiristis gia to patima tou koumpiou "export".
		 Dinoume to default xroma sta ipoloipa koumpia, eno sto energopoiimeno
		 ena pio foteino. 
		 Thetoume to activeButton stin timi 3.
		 Energopoioume ta aparaitita pedia eisagogis eisodou ki apenergopoioume
		 ta ipoloipa.
		 Kaloume tis aparaitites methodous ektelesis ton zitoumenon (den xreiazetai
		 na patithei to submit s auti tin periptosi) kai emfanizoume ta charts.
		 */
		export.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent arg0) {
				visualize.setStyle("-fx-background-color:  #16A085;");
				analyze.setStyle("-fx-background-color:  #16A085;");
				analyzeSPs.setStyle("-fx-background-color: #D17519;");
				analyzePOIs.setStyle("-fx-background-color: #D17519;");
				export.setStyle("-fx-background-color:  #00CC99;");
				activeButton = 3;
				sD.setDisable(true);
				eD.setDisable(true);
				ur.setDisable(true);
				dM.setDisable(true);
				tMa.setDisable(true);
				tMi.setDisable(true);
				epsT.setDisable(true);
				minPtsT.setDisable(true);
				submit.setDisable(true);
				mapButton.setDisable(true);
				chartButton.setDisable(true);
				
				chartPanel.getChildren().clear();
				myNetMan.exportResults();
				usersPerBrandChart(myNetMan.usersPerBrand);
				lowBatPerHourChart(myNetMan.lowBatPerHour);
				displayResults.getChildren().clear();
				displayResults.getChildren().add(chartPanel);
            }
		});
        
		/* xeiristis gia tin eisagogi tou mouse stin perioxi tou koumpiou submit.
		 Otan to koumpi einai energopoiimeno, o cursor ginetai xeri. 
		 */
		submit.setOnMouseEntered(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent me) {
					if (!submit.isDisabled()) {
						scene.setCursor(Cursor.HAND); //Change cursor to hand
					}
				}
		});
		
		/* xeiristis gia tin eksodo tou mouse apo tin perioxi tou koumpiou submit.
		 O cursor pairnei tin klassiki morfi. 
		 */
		submit.setOnMouseExited(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent me) {
					scene.setCursor(Cursor.DEFAULT);
				}
		});
		
		return myMenu;
	}
	
	/** Methodos gia ti dimiourgia menou dio epilogon:
	  * emfanisi xarti i chart.
	  */
	public HBox getMapOrChart() {
		HBox moc = new HBox(2);
		
		/* dimiourgia ton koumpion kai prosthiki sto panel */
		mapButton = new Button("Map");
		chartButton = new Button("Chart");
		mapButton.getStyleClass().add("showButton");
		chartButton.getStyleClass().add("showButton");	
		mapButton.setDisable(true);
		chartButton.setDisable(true);	
		moc.setAlignment(Pos.TOP_RIGHT);
		moc.getChildren().addAll(mapButton, chartButton);
		
		/* xeiristis gia to patima tou koumpiou "map".
		 Dinoume pio foteino xroma sto epilegmeno koumpi. 
		 An proigoumenos vlepame chart, tora fortonoume to xarti.
		 */
		mapButton.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent arg0) {
				chartButton.setStyle("-fx-background-color:  #16A085;");
				mapButton.setStyle("-fx-background-color:  #00CC99;");
				if (activeMoc == 1) {
					displayResults.getChildren().clear();
					displayResults.getChildren().add(myPage);
				}
				activeMoc = 0;
			}
		});
		
		/* xeiristis gia to patima tou koumpiou "chart".
		 Dinoume pio foteino xroma sto epilegmeno koumpi. 
		 An proigoumenos eixame xarti, tora fortonoume to chart.
		 */
		chartButton.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent arg0) {
				mapButton.setStyle("-fx-background-color:  #16A085;");
				chartButton.setStyle("-fx-background-color:  #00CC99;");
				if (activeMoc == 0) {
					displayResults.getChildren().clear();
					displayResults.getChildren().add(chartPanel);
				}
				activeMoc = 1;
			}
		});
		
		/* xeiristis gia tin eisagogi tou mouse stin perioxi tou koumpiou mapButton.
		 Otan to koumpi einai energopoiimeno, o cursor ginetai xeri. 
		 */
		mapButton.setOnMouseEntered(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent me) {
					if (!mapButton.isDisabled()) {
						scene.setCursor(Cursor.HAND); //Change cursor to hand
					}
				}
		});
		
		/* xeiristis gia tin eksodo tou mouse apo tin perioxi tou koumpiou mapButton.
		 O cursor pairnei tin klassiki morfi. 
		 */
		mapButton.setOnMouseExited(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent me) {
					scene.setCursor(Cursor.DEFAULT);
				}
		});
		
		/* xeiristis gia tin eisagogi tou mouse stin perioxi tou koumpiou chartButton.
		 Otan to koumpi einai energopoiimeno, o cursor ginetai xeri. 
		 */
		chartButton.setOnMouseEntered(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent me) {
					if (!chartButton.isDisabled()) {
						scene.setCursor(Cursor.HAND); //Change cursor to hand
					}
				}
		});
		
		/* xeiristis gia tin eksodo tou mouse apo tin perioxi tou koumpiou chartButton.
		 O cursor pairnei tin klassiki morfi. 
		 */
		chartButton.setOnMouseExited(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent me) {
					scene.setCursor(Cursor.DEFAULT);
				}
		});
		
		return moc;
	}

	/** Klasi gia ti dimiourgia istoselidas, i opoia einai aparaititi gia tin ektelesi
	  * javascript kai optikopoiisi ton apotelesmaton se xarti. Xrisimopoioume ti vivlio8iki
	  * json gia epikoinonia anamesa stis dio glosses.
	  */
    public class MyBrowser extends Region { 
		WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();   
         
        public MyBrowser(){
			/* i istoselida mas vrisketai sto arxeio myHTML.html */
            final URL urlApp = getClass().getResource("myHTML.html");
            webEngine.load(urlApp.toExternalForm());
            webEngine.setJavaScriptEnabled(true);
			
			webEngine.getLoadWorker().stateProperty().addListener(
            new ChangeListener<State>(){
				@Override
				public void changed(ObservableValue<? extends State> ov, State oldState, State newState) {
					if(newState == State.SUCCEEDED){
						JSObject window = (JSObject)webEngine.executeScript("window");
						/* sindeoume to antikeimeno myNetMan, gia na mporoume na kalesoume tis me8odous tou
						 apo tin javascript, meso tou onomatos "app".
						 */
						window.setMember("app", myNetMan);
					}
				}
			});
             
            JSObject window = (JSObject)webEngine.executeScript("window");
            window.setMember("app", myNetMan);
			
			/* xeristis gia to patima tou koumpiou submit. */
			submit.setOnAction(new EventHandler<ActionEvent>(){
				@Override
				public void handle(ActionEvent arg0) {
					/* ka8arizoume to pedio tou minimatos msg */
					msg.setText("");
					/* apomakrinoume tixon proigoumeno chart */
					chartPanel.getChildren().clear();
					/* kaloume tis katalliles methodous javascript gia tin apomakrinsi
					 tixon markers, monopation i poligonon pou prosthesame noritera.
					 */
					webEngine.executeScript("deleteAllMarkers()");
					webEngine.executeScript("removePath()");
					webEngine.executeScript("deleteAllPolygons()");
					/* fortonoume to xarti */
					displayResults.getChildren().clear();
					displayResults.getChildren().add(myPage);
					activeMoc = 0;
					/* ka8arizoume oles tis listes me tixon proigoumena apotelesmata */
					if (myNetMan.userAPs != null) { myNetMan.userAPs.clear(); }
					if (myNetMan.userPath != null) { myNetMan.userPath.clear(); }
					if (myNetMan.userBatLevel != null) { myNetMan.userBatLevel.clear(); }
					if (myNetMan.userCells != null) { myNetMan.userCells.clear(); }
					if (myNetMan.stayPoints != null) { myNetMan.stayPoints.clear(); }
					if (myNetMan.POIs != null) { myNetMan.POIs.clear(); }
					if (myNetMan.usersPerBrand != null) { myNetMan.usersPerBrand.clear(); }
					
					/* diavazoume apo tin eisodo tis imerominies kai to xristi.*/
					String maxD, maxT, minT;
					String start = sD.getText();
					String end = eD.getText();
					String user = ur.getText();
					/* elegxoume tis imerominies ki an einai lathos emfanizoume katallilo minima */
					if (!checkInputDates(start, end)) { 
						msg.setText("  Check your dates!!\n  S: 2015-03-27\n  E: 2015-05-06"); 
					}
					/* elegxoume to xristi ki an einai lathos emfanizoume katallilo minima */
					else if (!checkInputUser(user)) {
						msg.setText("  Check your user!!\n  Range: 0-76");
					}
					/* ean imerominies kai xristis einai sosta proxorame */
					else {
						user = "user" + user;
						/* elegxoume poia leitourgia epilex8ike. Analoga me tin epilogi, elegxoume kai ta
						 ipoloipa pedia eisodou kai ekteloume tis katalliles methodous java apo tin klasi NetMan
						 i/kai tin javascript.
						 */
						switch (activeButton) {
							case 1:
								myNetMan.visualizeData(user, start, end);
								webEngine.executeScript("getVisualizeData()");
								userBatLevelChart(myNetMan.userBatLevel);
								break;
							case 21:
								maxD = dM.getText();
								maxT = tMa.getText();
								minT = tMi.getText();
								if (!checkInputDouble(maxD)) {
									msg.setText("  Dmax must be a \n  positive number!!");
								}
								else if (!checkInputInteger(maxT)) {
									msg.setText("  Tmax must be a \n  positive integer!!");
								}
								else if (!checkInputInteger(minT)) {
									msg.setText("  Tmin must be a \n  positive integer!!");								
								}
								else {
									myNetMan.analyzeData1(user, start, end, Double.parseDouble(maxD), Integer.parseInt(maxT), Integer.parseInt(minT));
									webEngine.executeScript("addSPs()");
								}
								break;
							case 22:
								maxD = dM.getText();
								maxT = tMa.getText();
								minT = tMi.getText();
								String eps = epsT.getText();
								String minPts = minPtsT.getText();
								if (!checkInputDouble(maxD)) {
									msg.setText("  Dmax must be a \n positive double number!!");
								}
								else if (!checkInputInteger(maxT)) {
									msg.setText("Tmax must be a \n positive integer!!");
								}
								else if (!checkInputInteger(minT)) {
									msg.setText("  Tmin must be a \n positive integer!!");								
								}
								else if (!checkInputDouble(eps)) {
									msg.setText("  eps must be a \n positive double number!!");								
								}
								else if (!checkInputInteger(minPts)) {
									msg.setText("  minPts must be a \n positive integer!!");								
								}
								else {
									myNetMan.analyzeData2(start, end, Double.parseDouble(maxD), Integer.parseInt(maxT), Integer.parseInt(minT), Double.parseDouble(eps), Integer.parseInt(minPts));
									webEngine.executeScript("addPOIs()");
								}
								break;
							case 3:
								/* de xreiazetai na patisoume submit sto export */
								break;
						}
					}
				}
			});
			
			myPage = new StackPane();
			myPage.getChildren().addAll(webView);
		}
    }
	
	/** Methodos gia ton elegxo ton imerominion pou dinontai apo to xristi */
	public boolean checkInputDates(String dateStart, String dateEnd) {
		/* To format pou prepei na exoun oi imerominies */
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		/* Apodexti arxiki kai teliki imerominia */
		String s = "2015-03-27";
		String e = "2015-05-06";
		/* flag */
		boolean okDates = false;

		try {
			df.parse(dateStart);
			df.parse(dateEnd);
			
			if (dateStart.compareTo(dateEnd) > 0) { 
				/* i arxiki imerominia pou dothike einai meta tin teliki */ 
				okDates = false;
			}
			else if ((dateStart.compareTo(s) < 0) || (dateStart.compareTo(e) > 0) || (dateEnd.compareTo(s) < 0) || (dateEnd.compareTo(e) > 0)){
				/* oi imerominies pou dothikan den einai sta plaisia ton apodekton imerominion */
				okDates = false;
			}
			else { okDates = true; }
			
		} catch (ParseException exc) {
			okDates = false;
		}
		return okDates;
	}
	
	/** Methodos gia ton elegxo tou xristi (arxeiou) pou dinetai apo tin eisodo */
	public boolean checkInputUser(String user) {
		/* Elaxisti kai megisti apodekti timi */
		int minUser = 0;
		int maxUser = 76;
		/* flag */
		boolean okUser = false;
			
		try {
			/* Anamenoume akeraio arithmo */
			int u = Integer.parseInt(user);
			
			if ((u < minUser) || (u > maxUser)) {
				/* o xristis einai ektos apodekton orion */
				okUser = false;
			}
			else { okUser = true; }
		} catch (NumberFormatException ex) {
			okUser = false;
		}
		return okUser;
	}
	
	/** Methodos gia ton elegxo ton pedion eisodou, opou anamenoume akeraio arithmo. */
	public boolean checkInputInteger(String number) {
		boolean okNum = false;
		
		try {
			int n = Integer.parseInt(number);
			
			if (n < 0) { 
				/* o arithmos einai arnitikos */
				okNum = false;
			}
			else { okNum = true; }	
		} catch (NumberFormatException ex) {
			okNum = false;
		}
		return okNum;
	}
	
	/** Methodos gia ton elegxo ton pedion eisodou, opou anamenoume dekadiko arithmo. */	
	public boolean checkInputDouble(String number) {
		boolean okNum = false;
			
		try {
			double n = Double.parseDouble(number);
			
			if (n < 0) { //number must be positive
				okNum = false;
			}
			else { okNum = true; }
		} catch (NumberFormatException ex) {
			okNum = false;
		}
		return okNum;
	}
	
	/** Methodos gia ti dimiourgia chart mpatarias/xronou enos xristi */
	public void userBatLevelChart(List<List<String>> data) {
		DateFormat mydf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Time");
		yAxis.setLabel("Battery Level");
		userBatChart = new LineChart<String, Number>(xAxis,yAxis);
		XYChart.Series<String,Number> series = new XYChart.Series<String,Number>();
		for (int i=0; i<data.size(); i++) {
			Date x = new Date();
			int y = Integer.parseInt(data.get(i).get(1));
			try {
				x = mydf.parse(data.get(i).get(2));
				series.getData().add(new XYChart.Data<String, Number>("" + x, y));				
			} catch (ParseException e) {
				e.printStackTrace();
				System.out.println("\nDate is not in the right format:\n" + x + "\n");
			}
		}
		userBatChart.getData().add(series);
		userBatChart.setLegendVisible(false);
		chartPanel.getChildren().add(userBatChart);
	}
	
	/** Methodos gia ti dimiourgia chart xriston ana etaireia */
	public void usersPerBrandChart(List<List<String>> data) {
        NumberAxis yAxis = new NumberAxis();
		CategoryAxis xAxis = new CategoryAxis();
        yAxis.setLabel("Users");
		xAxis.setLabel("Brand");
		BarChart<String, Number> ubchart = new BarChart<String, Number>(xAxis,yAxis);
		XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
		for (int i=0; i<data.size(); i++) {			
			int y = Integer.parseInt(data.get(i).get(1));
			String x = data.get(i).get(0);
			series.getData().add(new XYChart.Data<String, Number>(x, y));
		}	
		ubchart.setPrefWidth(1100);
		ubchart.getData().add(series);
		ubchart.setLegendVisible(false);
		chartPanel.getChildren().add(ubchart);
	}
	
	/** Methodos gia ti dimiourgia chart xriston me xamilo pososto mpatarias (<15%)
	  * ana ora, gia oles tis imeres.
	  */
	public void lowBatPerHourChart(List<List<String>> data) {
		ScrollPane scPanel = new ScrollPane();
        NumberAxis yAxis = new NumberAxis();
		CategoryAxis xAxis = new CategoryAxis();
        yAxis.setLabel("Users");
		xAxis.setLabel("Date");
		BarChart<String, Number> lbphchart = new BarChart<String, Number>(xAxis,yAxis);
		/* mia seira dedomenon gia kathe ora */
		List<XYChart.Series<String, Number>> series = new ArrayList<XYChart.Series<String, Number>>();	
		for (int i=0; i < 24; i++) {
			series.add(new XYChart.Series<String, Number>());
			series.get(i).setName(Integer.toString(i));
		}
		
		String curDay = data.get(0).get(0);
		int curHour = 0;
		for (int i=0; i < data.size(); i++) {
			String day = data.get(i).get(0);
			int hour = Integer.parseInt(data.get(i).get(1));
			
			if (!day.equals(curDay)) {
				/* gia tis ores pou den exoume pliroforia, prosthetoume eggrafi me arithmo xriston = 0. 
				 Gia na ginetai sosta i taksinomisi ston pinaka. 
				 */
				for (int j=curHour; j<24; j++) {
					series.get(j).getData().add(new XYChart.Data<String, Number>(curDay, 0));
				} 
				curHour = 0;
			}
			
			/* gia tis ores pou den exoume pliroforia, prosthetoume eggrafi me arithmo xriston = 0 */
			if (hour != curHour) {
				for (int j=curHour; j<hour; j++) {
					series.get(j).getData().add(new XYChart.Data<String, Number>(day, 0));
				}
			} 
			
			/* prosthetoume tin eggrafi gia to hour */
			int y = Integer.parseInt(data.get(i).get(2));
			series.get(hour).getData().add(new XYChart.Data<String, Number>(day, y));
			
			if (hour == 23) {
				curHour = 0;
			}
			else {
				curHour = hour + 1;
			}
			curDay = day;
		}
		
		for (int j=curHour; j<24; j++) {
			series.get(j).getData().add(new XYChart.Data<String, Number>(curDay, 0));
		}
		
		/* prosthetoume ola ta series sto chart */
		for (int i=0; i < 24; i++) {
			lbphchart.getData().add(series.get(i));
		}
				
		lbphchart.setBarGap(3);
		lbphchart.setCategoryGap(20);
		lbphchart.setMinWidth(10000);
		scPanel.setPrefWidth(1100);
		scPanel.setContent(lbphchart);
		chartPanel.getChildren().add(scPanel);
	}
	
	/** Klasi gia tin ektelesi ton vasikon leitourgion tis askisis. */
	public class NetMan {
		/* arxeio eisodou wifi */
		String wifiFile = "wifi.csv";
		/* arxeio eisodou gps */
		String gpsFile = "gps.csv";
		/* arxeio eisodou battery */
		String batteryFile = "battery.csv";
		/* arxeio eisodou base stations */
		String bsFile = "base_station.csv";
		
		/* Lista my listes gia ola ta dedomena wifi */
		List<List<String>> wifiData = new ArrayList<List<String>>();
		/* Lista my listes gia tis theseis ton APs. Format: bssid, lat, lon */
		List<List<String>> APlocations = new ArrayList<List<String>>();
		/* Lista my listes gia ola ta dedomena gps */
		List<List<String>> gpsData = new ArrayList<List<String>>();
		/* Lista my listes gia ola ta dedomena battery */
		List<List<String>> batteryData = new ArrayList<List<String>>();
		/* Lista my listes gia ola ta dedomena BS */
		List<List<String>> bsData = new ArrayList<List<String>>();
		
		/* Listes gia ta apotelesmata tou erotimatos visualize */
		List<List<String>> userAPs;
		List<List<String>> userPath;
		List<List<String>> userBatLevel;
		List<List<String>> userCells;
		
		/* Listes gia ta apotelesmata tou erotimatos analyze */
		List<List<String>> stayPoints;
		List<List<String>> POIs;
		
		/* Listes gia ta apotelesmata tou erotimatos export */
		List<List<String>> usersPerBrand;
		List<List<String>> lowBatPerHour;
		
		/* oi stiles sto arxeio wifi */
		int WuserCol = 1;
		int WbssidCol = 3;
		int WlevelCol = 4;
		int WfreqCol = 5;
		int WlatCol = 6;
		int WlongCol = 7;
		int WdateCol = 8;
		/* oi stiles sto arxeio gps */
		int GuserCol = 1;
		int GlatCol = 2;
		int GlongCol = 3;
		int GdateCol = 4;
		/* oi stiles sto arxeio battery */
		int BuserCol = 1;
		int BlevelCol = 2;
		int BdateCol = 6;
		/* oi stiles sto arxeio base station */
		int SuserCol = 1;
		int SoperCol = 2;
		int ScellCol = 5;
		int SlatCol = 7;
		int SlongCol = 8;
		int SdateCol = 9;
	
		public NetMan() {
			/* Diavazoume ola ta arxeia */
			wifiData = readFile(wifiFile);
			gpsData = readFile(gpsFile);
			batteryData = readFile(batteryFile);
			bsData = readFile(bsFile);
			
			/* Apomakrinoume tixon diples eggrafes apo ola ta arxeia,
			 me ti voitheia hashmap. 
			 */
			HashSet<List<String>> hs = new HashSet<List<String>>();
			hs.addAll(wifiData);
			wifiData.clear();
			wifiData.addAll(hs);
			hs.clear();
			
			hs.addAll(gpsData);
			gpsData.clear();
			gpsData.addAll(hs);
			hs.clear();
			
			hs.addAll(batteryData);
			batteryData.clear();
			batteryData.addAll(hs);
			hs.clear();
			
			hs.addAll(bsData);
			bsData.clear();
			bsData.addAll(hs);
			hs.clear();
			
			/* Kaloume ti methodo gia ipologismo ton theseon ton APs */
			getAPLocations();
		}
		
		/** 
		* Methodos gia tin anagnosi arxeiou csv grammi grammi kai apothikeusi tou apotelesmatos se mia lista liston.
		*/
		public List<List<String>> readFile(String fileName) {
			/* grammi pou diavazoume */
			String line = null;
			/* Lista me listes me ola ta dedomena */
			List<List<String>> data = new ArrayList<List<String>>();

			try {
				BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
			
				int cur = 0;
				/* i proti grammi periexei kefalida */
				line = reader.readLine();
				/* diavazoume to ipoloipo arxeio */ 				
				while ((line = reader.readLine()) != null) {
					if (line.trim().length() == 0) {
						line = reader.readLine();
						continue; /* skip empty lines */
					}
		
					data.add(new ArrayList<String>());
					/* diaxoristiko pedion arxeiou: tab */
					String[] dataArray = line.split("\t"); 
				
					/* prosthetoume ola ta pedia sti lista */
					for (String item:dataArray) {	
						data.get(cur).add(item.trim());
					}
			
					cur++;
				}
				reader.close();
			} catch (IOException ioe){
				System.out.println("Couldn't read file " + fileName + 	" (cause: " + ioe.getMessage() + ")");
			}	
		
			return data;
		}
		
		/** 
		* Methodos gia tin ektimisi ton topothesion ton APs.
		*/
		public List<List<String>> getAPLocations() {
			/* Taksinomoume to arxeio wifi me vasi to bssid */
			Collections.sort(wifiData, new myCompareClass(WbssidCol)); 
		
			int cur = 0;
			/* Diavazoume to proto bssid */
			String bssid1 = wifiData.get(0).get(WbssidCol);
		
			double totalWeight = 0;
			double temp1 = 0, temp2 = 0;	

			/* Ipologizoume to midpoint gia kathe BSSID */
			for(int i=0; i<wifiData.size(); i++) {
				/* diavazoume to bssid sti thesi i */
				String bssid2 = wifiData.get(i).get(WbssidCol);
			
				/* to bssid s auti ti thesi den einai to idio me to proigoumeno.
				 Epomenos grafoume ta apotelesmata pou ipologisame eos tora kai ksekiname
				 na ipologisoume gia to kainourio bssid.
				 */
				if (!bssid2.equals(bssid1))
				{
					/* prosthetoume ta telika apotelsmata gia to proigoumeno bssid sti lista */
					APlocations.add(new ArrayList<String>());
					APlocations.get(cur).add(wifiData.get(i-1).get(WbssidCol));
					/* meso latitude se moires */
					APlocations.get(cur).add(Double.toString(temp1/totalWeight*180/Math.PI));
					/* meso longtitude se moires */
					APlocations.get(cur).add(Double.toString(temp2/totalWeight*180/Math.PI));
				
					cur++;
					/* midenizoume tous proigoumenous ipologismous */
					totalWeight = 0;
					temp1 = 0;
					temp2 = 0;
				}		
				
				/* ipologizoume to varos me vasi to rssi opos deixnei i askisi */				
				double w = Math.pow(10, Double.parseDouble(wifiData.get(i).get(WlevelCol))/10);
				/* metatrepoume tin parousa latitude apo moires se aktinia, pollaplasiazoume me to varos
				 kai prosthetoume me ola ta proigoumena 
				 */
				temp1 += (Double.parseDouble(wifiData.get(i).get(WlatCol)) * Math.PI / 180) * w;
				/* metatrepoume tin parousa longtitude apo moires se aktinia, pollaplasiazoume me to varos
				 kai prosthetoume me ola ta proigoumena 
				 */
				temp2 += (Double.parseDouble(wifiData.get(i).get(WlongCol)) * Math.PI / 180) * w;
				/* sinoliko varos = proigoumeno + trexon */
				totalWeight += w;
			
				bssid1 = bssid2;
			}
			
			/* prosthetoume tin teleutaia eggrafi */
			APlocations.add(new ArrayList<String>());
			APlocations.get(cur).add(wifiData.get(wifiData.size()-1).get(WbssidCol));
			APlocations.get(cur).add(Double.toString(temp1/totalWeight*180/Math.PI));
			APlocations.get(cur).add(Double.toString(temp2/totalWeight*180/Math.PI));
			
			return APlocations;
		}
		
		/** Methodos pou ekteleite otan o xristis epileksei "visualize".
		  * Ektelountai oi katalliles methodoi gia ta tessera ipoerotimata.
		  */
		public void visualizeData(String user, String start, String end) {				
			userAPs = getUserAPs(user, start, end);
			userPath = getPath(user, start, end);
			userBatLevel = getUserBatLevel(user, start, end);
			userCells = getUserCells(user, start, end);
		}
		
		/** Methodos pou ekteleite otan o xristis epileksei "find SPs". */
		public void analyzeData1(String user, String start, String end, double Dmax, int Tmax, int Tmin) {
			stayPoints = findStayPoints(getPath(user, start, end), Dmax, Tmax, Tmin);
		}
		
		/** Methodos pou ekteleite otan o xristis epileksei "find POIs". */
		public void analyzeData2(String start, String end, double Dmax, int Tmax, int Tmin, double eps, int minPts) {
			/* Lista me ta POIs */
			POIs = new ArrayList<List<String>>();			
			/* Lista me ola ta stay points */
			List<List<String>> allStayPoints = new ArrayList<List<String>>();
			/* Lista me ta points tou trexontos xristi */
			List<List<String>> lp = new ArrayList<List<String>>();
			/* Lista me ta dedomena gps pou aforoun tis imeromines pou epilegisan */
			List<List<String>> rightGpsData = new ArrayList<List<String>>();
		
			int cur = 0, spcur = 0;

			/* Taksinomoume to arxeio gps me vasi tin imerominia */
			Collections.sort(gpsData, new myCompareClass(GdateCol));
			/* diavazoume oles tis eggrafes */
			for(int i=0; i<gpsData.size(); i++) {	
				if ((gpsData.get(i).get(GdateCol)).compareTo(start) >= 0 && (gpsData.get(i).get(GdateCol)).compareTo(end) <= 0) {
					/* i eggrafi einai entos ton imerominion, opote tin prosthetoume sti lista rightGpsData */
					rightGpsData.add(new ArrayList<String>());
					rightGpsData.get(cur).addAll(gpsData.get(i));
					cur++;
				}
				else if (cur > 0) { break; } /* ta ipoloipa dedomena einai se metepeita imerominia */
			}
		
			/* taksinomoume ti rightGpsData me vasi to xristi */
			Collections.sort(rightGpsData, new myCompareClass(GuserCol));
		
			/*
			System.out.println("\nrightGpsData");
			for(int b=0; b<rightGpsData.size(); b++) {  
				for(int v=0; v<rightGpsData.get(b).size(); v++) {  
					System.out.print(rightGpsData.get(b).get(v) + "\t");  
				}  
			System.out.println();
			}
			*/
			
			String user1 = rightGpsData.get(0).get(GuserCol);
			cur = 0;
			
			/* Ipologizoume ta SPs gia kathe xristi kai ta prosthetoume sti lista allStayPoints */
			for(int i=0; i<rightGpsData.size(); i++) {
				String user2 = rightGpsData.get(i).get(GuserCol);
			
				/* o xristis sti thesi i den einai idios me ton proigoumeno, ara exoume oles tis eggrafes (tou i-1)
				 kai mporoume na ipologisoume ta SPs tou.
				 */
				if (!user2.equals(user1))
				{
					/* kaloume ti sinartisi gia ipologismo ton SPs */
					List<List<String>> curSP = findStayPoints(lp, Dmax, Tmax, Tmin);
					if (curSP.size() > 0) {
						/* ean iparxoun SPs ta prosthetoume sti lista */
						for (int j=0; j<curSP.size(); j++) {
							allStayPoints.add(new ArrayList<String>());
							allStayPoints.get(spcur).add(curSP.get(j).get(0));
							allStayPoints.get(spcur).add(curSP.get(j).get(1));
							allStayPoints.get(spcur).add(curSP.get(j).get(2));
							allStayPoints.get(spcur).add(curSP.get(j).get(3));
							spcur++;
						}
					}
					/* ka8arizoume ti lista me ta proigoumena points */
					lp.clear();
					cur = 0;
				}		
				
				/* prosthetoume sti lista to kainourio simeio pou diavasame */
				lp.add(new ArrayList<String>());
				lp.get(cur).addAll(rightGpsData.get(i));
				cur++;
				user1 = user2;
			}
		
			/* prosthetoume tin teleutaia eggrafi */
			List<List<String>> curSP = findStayPoints(lp, Dmax, Tmax, Tmin);
			if (curSP.size() > 0) {
				for (int j=0; j<curSP.size(); j++) {
					allStayPoints.add(new ArrayList<String>());
					allStayPoints.get(spcur).add(curSP.get(j).get(0));
					allStayPoints.get(spcur).add(curSP.get(j).get(1));
					allStayPoints.get(spcur).add(curSP.get(j).get(2));
					allStayPoints.get(spcur).add(curSP.get(j).get(3));
					spcur++;
				}
			}
			
			/* Afairoume tixon dipla points */
			HashSet<List<String>> hs = new HashSet<List<String>>();
			hs.addAll(allStayPoints);
			allStayPoints.clear();
			allStayPoints.addAll(hs);
		
			/*
			System.out.println("\nallStayPoints");
			for(int b=0; b<allStayPoints.size(); b++) {  
				for(int v=0; v<allStayPoints.get(b).size(); v++) {  
					System.out.print(allStayPoints.get(b).get(v) + "\t");  
				}  
			System.out.println();
			}
			*/
		
			/* ean iparxoun stay points, ipologizoume ta points of interest */
			if (allStayPoints.size() > 0) {
				List<Point> aSP = new ArrayList<Point>();
				for (int i=0; i<allStayPoints.size(); i++) {
					aSP.add(new Point(Double.parseDouble(allStayPoints.get(i).get(0)), Double.parseDouble(allStayPoints.get(i).get(1))));
				}
				/* xrisimopoioume ton algorithmo dbscan */
				DBSCAN myDbscan = new DBSCAN(eps, minPts);
				List<List<Point>> allClusters = myDbscan.makeClusters(aSP);
				List<List<String>> aCl = new ArrayList<List<String>>();
		
				/* Gia kathe cluster ipologizoume: centroid point, min lat, max lat, min long, max long */
				for (int i=0; i<allClusters.size(); i++) {
					aCl.clear();
					/* lista me ola ta simeia tou cluster */
					for (int j=0; j < allClusters.get(i).size(); j++) {
						aCl.add(new ArrayList<String>());
						aCl.get(j).add(Double.toString(allClusters.get(i).get(j).getX()));
						aCl.get(j).add(Double.toString(allClusters.get(i).get(j).getY()));
					}
					POIs.add(new ArrayList<String>());
					/* ipologizoume to geografiko kentro ton simeion */
					POIs.get(i).addAll(estimateCentroid(aCl, 0, 1));
					/* taksinomoume me vasi to latitude */
					Collections.sort(aCl, new myCompareClass(0));
					/* prosthetoume to min latitude (proti eggrafi) */
					POIs.get(i).add(aCl.get(0).get(0));
					/* prosthetoume to max latitude (teleutaia eggrafi) */
					POIs.get(i).add(aCl.get(aCl.size()-1).get(0));
					/* taksinomoume me vasi to longtitude */
					Collections.sort(aCl, new myCompareClass(1));
					/* prosthetoume to min longtitude (proti eggrafi)*/
					POIs.get(i).add(aCl.get(0).get(1));
					/* prosthetoume to max longtitude (teleutaia eggrafi)*/
					POIs.get(i).add(aCl.get(aCl.size()-1).get(1));
				}
			}
			else {
				System.out.println("No stay points for these parameters.");
			}
		}
	
		/** Methodos pou ekteleitai otan o xristis epileksei "export". */
		public void exportResults() {
			usersPerBrand = getUsersPerBrand();
			lowBatPerHour = getLowBatPerHour();
		}
		
		
		/** 
		* Methodos gia tin euresi ton APs tou xristi gia to xroniko diastima pou epilex8ike. 
		* Gia kathe simeio exoume: Sintetagmenes midpoint (opos ipologistikan proigoumenos gia to sigkekrimeno bssid),
		* meso RSSI level apo tis eggrafes tou xristi kai ti sixnotita.
		*/
		public List<List<String>> getUserAPs(String user, String start, String end) {
			/* Lista me tis eggires eggrafes gia tis sigkekrimenes imerominies */
			List<List<String>> validAPs = new ArrayList<List<String>>();
			/* Lista me ta APs. Mia eggrafi gia kathe BSSID */
			List<List<String>> finalAPs = new ArrayList<List<String>>();
			int cur = 0;
			
			/* Taksinomisi me vasi tin imerominia */
			Collections.sort(wifiData, new myCompareClass(WdateCol));
			/* Taksinomisi me vasi to xristi */
			Collections.sort(wifiData, new myCompareClass(WuserCol));
		
			/* Diavazoume ola ta dedomena wifi kai kratame auta pou aforoun to xristi kai tis sigkekrimenes imerominies */
			for(int i=0; i<wifiData.size(); i++) {	
				if ((wifiData.get(i).get(WuserCol)).equals(user))
				{
					/* i eggrafi afora to xristi mas */
					if ((wifiData.get(i).get(WdateCol)).compareTo(start) >= 0 && (wifiData.get(i).get(WdateCol)).compareTo(end) <= 0) {
						/* ta dedomena einai entos xronikou diastimatos */
						validAPs.add(new ArrayList<String>());
						/* prosthetoume tin eggrafi sti validAPs */
						for (int j=0; j<9; j++) {
							validAPs.get(cur).add(wifiData.get(i).get(j));
						}
						cur++;
					}
					else if (cur > 0) {break;} /* ta ipoloipa dedomena einai se metepeita imerominia */
				}
				else if (cur > 0) {break;} /* ta ipoloipa dedomena den aforoun to xristi mas */
			}
		
			/* Taksinomoume me vasi to bssid */
			Collections.sort(validAPs, new myCompareClass(WbssidCol));
		
			/*
			 Kratame kathe bssid mono mia fora me meso RSSI level. 
			 Pedia: bssid, level, frequency, latitude and longtitute (midpoint). 
			*/
			if (validAPs.size() > 0) {
				cur = 0;
				double totalLevel = 0;
				int num = 0;
				String bssid1 = validAPs.get(0).get(WbssidCol);
		
				for(int i=0; i<validAPs.size(); i++) {
					String bssid2 = validAPs.get(i).get(WbssidCol);
			
					/* to kainourio bssid den einai idio me to proigoumeno, ara grafoume
					 ta mexri tora apotelesmata kai proxorame sto epomeno.
					 */
					if (!bssid2.equals(bssid1))
					{
						finalAPs.add(new ArrayList<String>());
						finalAPs.get(cur).add(validAPs.get(i-1).get(WbssidCol));
						/* meso rssi */
						finalAPs.get(cur).add(Double.toString((int)(Math.round(totalLevel/(double)num))));
						finalAPs.get(cur).add(validAPs.get(i-1).get(WfreqCol));
						cur++;
				
						num = 0;
						totalLevel = 0;
					}
				
					/* athroistiko rssi */
					totalLevel += Double.parseDouble(validAPs.get(i).get(WlevelCol));
					num++;
					bssid1 = bssid2;
				}
				
				/* prosthetoume tin teleutaia eggrafi */
				finalAPs.add(new ArrayList<String>());
				finalAPs.get(cur).add(validAPs.get(validAPs.size()-1).get(WbssidCol));
				finalAPs.get(cur).add(Double.toString((int)(Math.round(totalLevel/(double)num))));
				finalAPs.get(cur).add(validAPs.get(validAPs.size()-1).get(WfreqCol));
			
			
				/* Taksinomoume me vasi to bssid ta finalAPs kai ta APlocations */
				Collections.sort(finalAPs, new myCompareClass(0));
				Collections.sort(APlocations, new myCompareClass(0));
				
				cur = 0;
				/* vriskoume to kathe bssid sti lista me ta locations kai prosthetoume san sintetagmenes tou to midpoint */
				for (int i=0; i<APlocations.size(); i++) {
					if ((finalAPs.get(cur).get(0)).equals(APlocations.get(i).get(0))) {
						/* prosthiki midpoint tou AP */
						finalAPs.get(cur).add(APlocations.get(i).get(1));
						finalAPs.get(cur).add(APlocations.get(i).get(2));
						cur++;
					}
					if (cur == finalAPs.size()) { /* ta vrikame ola */
						break;
					}
				}
			}
			return finalAPs;
		}
		
		/**
		 * Methodos gia tin euresi tou monopatiou tou xristi gia to dosmeno xroniko diastima.
		 */
		public List<List<String>> getPath(String user, String start, String end) {
			/* Lista me ta egkira dedomena gps */
			List<List<String>> path = new ArrayList<List<String>>();
		
			/* Taksinomisi me vasi tin imerominia */
			Collections.sort(gpsData, new myCompareClass(GdateCol));
			/* Taksinomisi me vasi to xristi */
			Collections.sort(gpsData, new myCompareClass(GuserCol));
		
			int cur = 0;
			for(int i=0; i<gpsData.size(); i++) {	
				if ((gpsData.get(i).get(GuserCol)).equals(user))  
				{
					/* i eggrafi afora to xristi mas */
					if ((gpsData.get(i).get(GdateCol)).compareTo(start) >= 0 && (gpsData.get(i).get(GdateCol)).compareTo(end) <= 0) {
						/* einai entos xronikou diastimatos */
						path.add(new ArrayList<String>());
						for (int j=0; j<5; j++) {
							path.get(cur).add(gpsData.get(i).get(j));
						}
						cur++;
					}
					else if (cur > 0) {break;} /* ta ipoloipa dedomena einai se metepeita xrono */
				}
				else if (cur > 0) {break;} /* ta ipoloipa dedomena den aforoun to xristi mas */
			}
		
			/* Taksinomoume to monopati me vasi tin imerominia */
			Collections.sort(path, new myCompareClass(GdateCol));
		
			return path;
		}
		
		/**
		 * Methodos gia tin euresi ton battery levels gia to user se sigkekrimeno xroniko diastima.
		 * user, level, timestamp
		 */
		public List<List<String>> getUserBatLevel (String user, String start, String end) {
			/** Lista me ta egkira dedomena battery */
			List<List<String>> batLevels = new ArrayList<List<String>>();

			/* taksinomoume to batteryData me vasi tin imerominia */
			Collections.sort(batteryData, new myCompareClass(BdateCol));
			/* taksinomoume to batteryData me vasi to xristi */
			Collections.sort(batteryData, new myCompareClass(BuserCol));
		
			int cur = 0;
			for(int i=0; i<batteryData.size(); i++) {	
				if ((batteryData.get(i).get(BuserCol)).equals(user))
				{
					/* i eggrafi afora to xristi mas */
					if ((batteryData.get(i).get(BdateCol)).compareTo(start) >= 0 && (batteryData.get(i).get(BdateCol)).compareTo(end) <= 0) {
						batLevels.add(new ArrayList<String>());
						/* prosthetoume to xristi */
						batLevels.get(cur).add(batteryData.get(i).get(BuserCol));
						/* prosthetoume to epipedo tis mpatarias */
						batLevels.get(cur).add(batteryData.get(i).get(BlevelCol));
						/* prosthetoume tin imerominia */
						batLevels.get(cur).add(batteryData.get(i).get(BdateCol));
						cur++;
					}
					else if (cur > 0) {break;} /* ta ipoloipa dedomena einai se metepeita xrono */
				}
				else if (cur > 0) {break;} /* ta ipoloipa dedomena den aforoun to xristi mas */
			}
		
			/* Taksinomoume to apotelesma me vasi tin imerominia */
			Collections.sort(batLevels, new myCompareClass(2));
			
			return batLevels;
		}
	
		
		/**
		 * Methodos gia tin euresi ton cells, pou sindethike o xristis sto sigkekrimeno xroniko diastima.
		 */
		public List<List<String>> getUserCells(String user, String start, String end) {
			/* Lista me ta egkira dedomena apo to base station */
			List<List<String>> bs = new ArrayList<List<String>>();
	
			/* taksinomisi ton bsData me vasi tin imerominia */
			Collections.sort(bsData, new myCompareClass(SdateCol));
			/* taksinomisi ton bsData me vasi to xristi */
			Collections.sort(bsData, new myCompareClass(SuserCol));
			/* flag: 1 = vrikame to xristi */
			int found = 0;
			/* counter: metritis ton kelion pou prosthesame */
			int cur = 0;
			
			for(int i=0; i<bsData.size(); i++) {	
				if ((bsData.get(i).get(SuserCol)).equals(user))
				{
					/* i eggrafi afora to xristi mas */
					if ((bsData.get(i).get(SdateCol)).compareTo(start) >= 0 && (bsData.get(i).get(SdateCol)).compareTo(end) <= 0) {
						/* einai entos xronikou diastimatos */
						if (!bsData.get(i).get(SlatCol).equals("No Latitude")) {
							/* periexei geografiki pliroforia */
							bs.add(new ArrayList<String>());
							/* prosthetoume ola ta pedia */
							for (int j=0; j<10; j++) {
								bs.get(cur).add(bsData.get(i).get(j));
							}
							cur++;
						}
						/* vrikame to xristi */
						found = 1;
					}
					else if (found == 1) {break;} /* ta ipoloipa dedomena einai se metepeita xrono */
				}
				else if (found == 1) {break;} /* ta ipoloipa dedomena den aforoun to xristi mas */
			}
		
			/* taksinomoume to apotelesma me vasi tin imerominia */
			Collections.sort(bs, new myCompareClass(SdateCol));
			
			return bs;
		}
		
		/** 
		* Methodos gia tin euresi ton stay points. 
		* Algorithmos opos stin ekfonisi.
		* lp: lista me ta simeia pros eksetasi.
		* format: lat, lon, Tstart, Tend
		*/
		public List<List<String>> findStayPoints(List<List<String>> lp, double Dmax, int Tmax, int Tmin) {
			/* Lista me ta trexonta simeia */
			List<List<String>> tempPoints = new ArrayList<List<String>>();
			/* Lista me ta stay points */
			List<List<String>> lsp = new ArrayList<List<String>>();
			int cur = 0, k = 0, temp = 0;
		
			/* Taksinomoume ta simeia me vasi tin imerominia */
			Collections.sort(lp, new myCompareClass(GdateCol));
		
			/* ekteloume ton algorithmo */
			while (k < lp.size()-1) {
				int m = k + 1;
				while (m < lp.size()) {
					if (dateDiff(lp.get(m).get(GdateCol), lp.get(m-1).get(GdateCol)) > Tmax) {
						if (dateDiff(lp.get(k).get(GdateCol), lp.get(m-1).get(GdateCol)) > Tmin) {
							/* prosthetoume to stay point pou apoteleitai apo ta simeia k eos m-1 */
							tempPoints.clear();
							temp = 0;
							for (int i=k; i<m; i++) {
								tempPoints.add(new ArrayList<String>());
								tempPoints.get(temp).add(lp.get(i).get(GlatCol));
								tempPoints.get(temp).add(lp.get(i).get(GlongCol));
								tempPoints.get(temp).add(lp.get(i).get(GdateCol));
								temp++;
							}
							lsp.add(new ArrayList<String>());
							lsp.get(cur).addAll(estimateStayPoint(tempPoints));
							cur++;
						}
						k = m;
						break;
					}
					else if (geoDistance(Double.parseDouble(lp.get(k).get(GlatCol)), 
										 Double.parseDouble(lp.get(k).get(GlongCol)), 
										 Double.parseDouble(lp.get(m).get(GlatCol)), 
										 Double.parseDouble(lp.get(m).get(GlongCol))) > Dmax) {
						if (dateDiff(lp.get(k).get(GdateCol), lp.get(m-1).get(GdateCol)) > Tmin) {
							/* prosthetoume to stay point pou apoteleitai apo ta simeia k eos m-1 */
							tempPoints.clear();
							temp = 0;
							for (int i=k; i<m; i++) {
								tempPoints.add(new ArrayList<String>());
								tempPoints.get(temp).add(lp.get(i).get(GlatCol));
								tempPoints.get(temp).add(lp.get(i).get(GlongCol));
								tempPoints.get(temp).add(lp.get(i).get(GdateCol));
								temp++;
							}
							lsp.add(new ArrayList<String>());
							lsp.get(cur).addAll(estimateStayPoint(tempPoints));
							cur++;
							k = m;
							break;
						}
						k++;
						break;
					}
					else if (m == lp.size() - 1) {
						if (dateDiff(lp.get(k).get(GdateCol), lp.get(m).get(GdateCol)) > Tmin) {
							/* prosthetoume to stay point pou apoteleitai apo ta simeia k eos m */
							tempPoints.clear();
							temp = 0;
							for (int i=k; i<=m; i++) {
								tempPoints.add(new ArrayList<String>());
								tempPoints.get(temp).add(lp.get(i).get(GlatCol));
								tempPoints.get(temp).add(lp.get(i).get(GlongCol));
								tempPoints.get(temp).add(lp.get(i).get(GdateCol));
								temp++;
							}
							lsp.add(new ArrayList<String>());
							lsp.get(cur).addAll(estimateStayPoint(tempPoints));
							cur++;
						}
						k = m;
						break;
					}
					m++;
				}
			}
			return lsp;
		}
		
		/** 
		 * Methodos gia tin ektimisi tou stay point (lat, long, Tstart, Tend).
		 */
		public List<String> estimateStayPoint(List<List<String>> points) {
			/* stp: to stay point */
			List<String> stp = new ArrayList<String>();
			/* kaloume ti methodo gia ipologismo tou geografikou kentrou */
			stp.addAll(estimateCentroid(points, 0, 1));
			/* taksinomoume ta simeia me vasi tin imerominia */
			Collections.sort(points, new myCompareClass(2));
			/* prosthetoume tin arxiki imerominia (proti eggrafi) */
			stp.add(points.get(0).get(2));
			/* prosthetoume tin teliki imerominia (teleutaia eggrafi) */
			stp.add(points.get(points.size() - 1).get(2));
			return stp;		
		}
	
		/** 
		 * Methodos gia tin ektimisi tou kentrou geografikon simeion.
		 * Xrisimopoioume ton algorithmo opos stin ekfonisi, askisi 1, me w = 1.
		 */
		public List<String> estimateCentroid(List<List<String>> points, int latCol, int longCol) {
			List<String> latlon = new ArrayList<String>();
			/* meso lat, meso lon */
			double midLat = 0, midLong = 0;
			for(int i=0; i<points.size(); i++) {
				midLat += Double.parseDouble(points.get(i).get(latCol));
				midLong += Double.parseDouble(points.get(i).get(longCol));
			}
			midLat = midLat / points.size();
			midLong = midLong / points.size();
			latlon.add(Double.toString(midLat));
			latlon.add(Double.toString(midLong));
			return latlon;		
		}
		
		/** 
		 * Methodos gia ton ipologismo tis apostasis dio geografikon simeion.
		 * Xrisimopoioume ti formula haversine.
		 */
		public double geoDistance(double lat1, double lon1, double lat2, double lon2) {
			/* aktina tis gis se metra */
			double R = 6371000;
			/* sintetagmenes se aktinia */
			lat1 = lat1 * Math.PI/180;
			lat2 = lat2 * Math.PI/180;
			double dlat = lat2 - lat1;
			double dlon = lon2 - lon1;

			double a = Math.sin(dlat/2) * Math.sin(dlat/2) + Math.cos(lat1) * Math.cos(lat2) * Math.sin(dlon/2) * Math.sin(dlon/2);
			double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
			double d = R * c;
			return d;
		}
		
		/** Methodos gia ton ipologismo ton xriston ana etaireia */
		public List<List<String>> getUsersPerBrand() {
			List<List<String>> list = new ArrayList<List<String>>();
			List<List<String>> result = new ArrayList<List<String>>();
			
			/* taksinomisi tou bsData me vasi to xristi */
			Collections.sort(bsData, new myCompareClass(SuserCol));
			/* dimiourgoume lista me mona pedia stin ka8e eggrafi to xristi kai tin etaireia */
			for (int i=0; i < bsData.size(); i++) {
				list.add(new ArrayList<String>());
				list.get(i).add(bsData.get(i).get(SuserCol));
				/* omadopoioume tis eggrafes gia tis etaireies:
				 cosmote (cosmot, cosmote, cosmote - mobile..., gr comsote, gr cosmote)
				 vodafone (vodafone gr, vodafonegr, cu-x)
				*/
				String temp = bsData.get(i).get(SoperCol).toUpperCase();
				if (temp.contains("COSMO") || temp.contains("OTE")) {
					list.get(i).add("COSMOTE");
				}
				else if (temp.contains("VODAFONE") || temp.contains("CU")) {
					list.get(i).add("VODAFONE");
				}
				else {
					list.get(i).add(temp);
				}
			}
			
			/* apaloifoume tis diples eggrafes, ara tora exoume mono mia fora tin kathe etairia gia ton kathe xristi */
			HashSet<List<String>> hs = new HashSet<List<String>>();
			hs.addAll(list);
			list.clear();
			list.addAll(hs);
			
			/* taksinomisi tis neas listas me vasi tin etaireia */
			Collections.sort(list, new myCompareClass(1));
			
			/*
			System.out.println("user - brand");
			for(int i=0; i<list.size(); i++) {  
				for(int j=0; j<list.get(i).size(); j++) {  
					System.out.print(list.get(i).get(j) + "\t");  
				}  
			System.out.println();
			}
			*/
			
			int cur = 0, sum = 0;
			String operator1 = list.get(0).get(1);
			
			for (int i=0; i<list.size(); i++) {
				String operator2 = list.get(i).get(1);
				
				if (!operator2.equals(operator1)) {
					/* allazoume operator, epomenos grafoume to sinoliko arithmo xriston gia ton proigoumeno */
					result.add(new ArrayList<String>());
					result.get(cur).add(list.get(i-1).get(1));
					result.get(cur).add(Integer.toString(sum));
					cur++;
					sum = 0;
				}
				
				sum++;
				operator1 = operator2;
			}
			
			/* prosthetoume tin teleutaia eggrafi */
			result.add(new ArrayList<String>());
			result.get(cur).add(list.get(list.size()-1).get(1));
			result.get(cur).add(Integer.toString(sum));
			
			/*
			System.out.println("users per brand");
			for(int i=0; i<result.size(); i++) {  
				for(int j=0; j<result.get(i).size(); j++) {  
					System.out.print(result.get(i).get(j) + "\t");  
				}  
			System.out.println();
			}
			*/
			return result;
		}
		
		/** Methodos gia ton ipologismo ton xriston me xamilo pososto mpatarias (<15%)
		  * ana ora, gia oles tis imeres.
		  */
		public List<List<String>> getLowBatPerHour() {
			/* format: date, hour, no of users */ 
			List<List<String>> result = new ArrayList<List<String>>();
			List<List<String>> tempList = new ArrayList<List<String>>();
			
			/* taksinomisi tou batteryData me vasi tin imerominia */
			Collections.sort(batteryData, new myCompareClass(BdateCol));
			
			/* ftiaxnoume mia kainouria lista me pedia:
			 date (px. 2015-07-03), hour (px. 00, 17, 23 etc), user
			 mono gia tis eggrafes me battery level < 15%
			 */
			int cur = 0;
			for (int i=0; i<batteryData.size(); i++) {
				/* ean to battery level < 15%  prosthetoume tin eggrafi */
				if (Integer.parseInt(batteryData.get(i).get(BlevelCol)) < 15) {
					/* xorizoume tin imerominia apo tin ora */
					String[] tempd = batteryData.get(i).get(BdateCol).split(" ");
					/* xorizoume tin ora */
					String[] temph = tempd[1].split(":");
					tempList.add(new ArrayList<String>());
					tempList.get(cur).add(tempd[0]);
					tempList.get(cur).add(temph[0]);
					tempList.get(cur).add(batteryData.get(i).get(BuserCol));
					cur++;
				}
			}
			
			/* apaloifoume tixon diples eggrafes, ara tora exoume mono mia fora
			 gia kathe mera kai kathe ora ton idio xristi
			 */
			HashSet<List<String>> hs = new HashSet<List<String>>();
			hs.addAll(tempList);
			tempList.clear();
			tempList.addAll(hs);
					
			/* taksinomisi tou apotelesmatos me vasi tin ora */
			Collections.sort(tempList, new myCompareClass(1));
			/* taksinomisi tou apotelesmatos me vasi tin imerominia */
			Collections.sort(tempList, new myCompareClass(0));
			
			/* dilonoume tin proti mera pou emfanizetai sto arxeio */
			String day1 = tempList.get(0).get(0);
			/* dilonoume tin proti ora pou emfanizetai sto arxeio */
			int curTime = Integer.parseInt(tempList.get(0).get(1));
			int sum = 0;
			cur = 0;
			for (int i=0; i<tempList.size(); i++){
				String day2 = tempList.get(i).get(0);
				/* ean i kainouria mera den einai idia me prin, prosthetoume to proigoumeno apotelesma sti lista */
				if (!day2.equals(day1)) {
					result.add(new ArrayList<String>());
					result.get(cur).add(day1);
					/* prosthetoume mideniko stis ores me mono psifio gia na ginetai sosta i taksinomisi */
					if (curTime < 10) {
						result.get(cur).add("0" + Integer.toString(curTime));
					}
					else {
						result.get(cur).add(Integer.toString(curTime));
					}
					result.get(cur).add(Integer.toString(sum));
					cur++;
					sum = 0;
					curTime = Integer.parseInt(tempList.get(i).get(1));
				}
				
				if (Integer.parseInt(tempList.get(i).get(1)) == curTime) {
					sum++;
				}
				/* an i ora einai megaliteri apo to curTime, prosthetoume to proigoumeno apotelesma sti lista */
				else {
					result.add(new ArrayList<String>());
					result.get(cur).add(day1);
					if (curTime < 10) {
						result.get(cur).add("0" + Integer.toString(curTime));
					}
					else {
						result.get(cur).add(Integer.toString(curTime));
					}
					result.get(cur).add(Integer.toString(sum));
					cur++;
					curTime++;
					sum = 1;
				}
				day1 = day2;
			}
			
			/* prosthetoume tin teleutaia eggrafi */
			result.add(new ArrayList<String>());
			result.get(cur).add(day1);
			if (curTime < 10) {
				result.get(cur).add("0" + Integer.toString(curTime));
			}
			else {
				result.get(cur).add(Integer.toString(curTime));
			}
			result.get(cur).add(Integer.toString(sum));
				
			/* taksinomisi tou apotelesmatos me vasi tin ora */
			Collections.sort(result, new myCompareClass(1));
			/* taksinomisi tou apotelesmatos me vasi tin imerominia */
			Collections.sort(result, new myCompareClass(0));		

			/*
			System.out.println("low battery per hour");
			for(int i=0; i<result.size(); i++) {  
				for(int j=0; j<result.get(i).size(); j++) {  
					System.out.print(result.get(i).get(j) + "\t");  
				}  
			System.out.println();
			}
			*/
			
			return result;
		}
		
		/** Methodos gia ton ipologismo tis diaforas ton imerominion se lepta */
		public double dateDiff(String one, String two) {
			/* format ton imerominion */
			DateFormat ndf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date d1 = new Date();
			Date d2 = new Date();

			try {
				d1 = ndf.parse(one);
				d2 = ndf.parse(two);			
			} catch (ParseException e) {
				e.printStackTrace();
				System.out.println("\nDates are not in the right format:\n" + one + "\n" + two + "\n");
			}
		
			Calendar c1 = Calendar.getInstance();
			c1.clear();
			c1.setTime(d1);
		
			Calendar c2 = Calendar.getInstance();
			c2.clear();
			c2.setTime(d2);
		
			double time1 = c1.getTimeInMillis();
			double time2 = c2.getTimeInMillis();

			/* Ipologismos diaforas se millisecond */
			double diff;
			if (time1 > time2) {
				diff = time1 - time2;
			} 
			else {
				diff = time2 - time1;
			}

			/* Ipologismos diaforas se lepta */
			double diffSec = diff / 1000 / 60;
			return diffSec;
		}

	
		/** 
		* Methodos gia tin epistrofi ton locations ton APs se JSON morfi, 
		* gia na mporoun na diavastoun apo ti javascript.
		*/
		public String getJsonLocations() {
			String jsonloc = JSONValue.toJSONString(APlocations);

			return jsonloc;
		}
		
		/** 
		* Methodos gia tin epistrofi tou erotimatos 2.1 se JSON morfi, 
		* gia na mporei na diavastei apo ti javascript.
		*/
		public String getJsonUserAPs() {
			/*
			System.out.println("\nuserAPs");
			for(int b=0; b<userAPs.size(); b++) {  
				for(int v=0; v<userAPs.get(b).size(); v++) {  
					System.out.print(userAPs.get(b).get(v) + "\t");  
				}  
			System.out.println();
			}
			*/
			String jsonlist = JSONValue.toJSONString(userAPs);
			return jsonlist;
		}
		
		/** 
		* Methodos gia tin epistrofi tou erotimatos 2.2 se JSON morfi, 
		* gia na mporei na diavastei apo ti javascript.
		*/
		public String getJsonUserPath() {
			/*
			System.out.println("\nuserPath");
			for(int b=0; b<userPath.size(); b++) {  
				for(int v=0; v<userPath.get(b).size(); v++) {  
					System.out.print(userPath.get(b).get(v) + "\t");  
				}  
			System.out.println();
			}
			*/
			String jsonlist = JSONValue.toJSONString(userPath);
			return jsonlist;
		}
		
		/** 
		* Methodos gia tin epistrofi tou erotimatos 2.4 se JSON morfi, 
		* gia na mporei na diavastei apo ti javascript.
		*/
		public String getJsonUserCells() {
			/*
			System.out.println("\nuserCells");
			for(int b=0; b<userCells.size(); b++) {  
				for(int v=0; v<userCells.get(b).size(); v++) {  
					System.out.print(userCells.get(b).get(v) + "\t");  
				}  
			System.out.println();
			}
			*/
			String jsonlist = JSONValue.toJSONString(userCells);
			return jsonlist;
		}
		
		/** 
		* Methodos gia tin epistrofi tou erotimatos 3.1 se JSON morfi, 
		* gia na mporei na diavastei apo ti javascript.
		*/
		public String getJsonSPs() {
			/*
			System.out.println("\nstayPoints");
			for(int b=0; b<stayPoints.size(); b++) {  
				for(int v=0; v<stayPoints.get(b).size(); v++) {  
					System.out.print(stayPoints.get(b).get(v) + "\t");  
				}  
			System.out.println();
			}
			*/
			String jsonlist = JSONValue.toJSONString(stayPoints);
			return jsonlist;
		}
		
		/** 
		* Methodos gia tin epistrofi tou erotimatos 3.2 se JSON morfi, 
		* gia na mporei na diavastei apo ti javascript.
		*/
		public String getJsonPOIs() {	
			/*
			System.out.println("\nPoints of interest");
			for(int b=0; b<POIs.size(); b++) {  
				for(int v=0; v<POIs.get(b).size(); v++) {  
					System.out.print(POIs.get(b).get(v) + "\t");  
				}  
			System.out.println();
			}
			*/
			
			String jsonlist = JSONValue.toJSONString(POIs);
			return jsonlist;
		}
		
		
		/** Klasi pou xrisimopoieitai gia tin taksinomisi me vasi tin stili pou theloume */
		private class myCompareClass implements Comparator<List<String>> {
	
			private int bC;
	
			public myCompareClass(int column) {
				bC = column;
			}
	
			public int compare(List<String> a, List<String> b) {
				int result = 0;
				ArrayList ena = (ArrayList)a;
				ArrayList dio = (ArrayList)b;
				if (ena.size() == 0 || dio.size() == 0) {
					result = 0;
				} else {
					String one = (String)ena.get(bC);
					String two = (String)dio.get(bC);
					int r = one.compareTo(two);
					if (r == 0) {
						result = 0;
					}
					else if (r < 0) {
						result = -1;
					}
					else {
						result = 1;
					}
				}	
				return result;  
			}
		}
		
		/** Class gia ton algorithmo DBScan.
		  * O kodikas vasizetai sti wikipedia kai stin ilopoiisi tou apache:
		  * http://commons.apache.org/proper/commons-math/apidocs/src-html/org/apache/commons/math3/stat/clustering/DBSCANClusterer.html.
		  * Elegxoume ti geografiki apostasi meta3i ton simeion.
		  */
		private class DBSCAN {
			private double eps;
			private int minPts;
	
			public DBSCAN(double eps, int minPts) {
				this.eps = eps;
				this.minPts = minPts;
			}
		
			public List<List<Point>> makeClusters(List<Point> points) {
				List<List<Point>> clusters = new ArrayList<List<Point>>();
				Map<Point, Integer> visited = new HashMap<Point, Integer>();
			
				for (Point point : points) {
					if (visited.containsKey(point)) {
						continue;
					}
					List<Point> neighbors = getNeighbors(point, points);
					if (neighbors.size() >= minPts) {
						List<Point> cluster = new ArrayList<Point>();
						clusters.add(expandCluster(cluster, point, neighbors, points, visited));
					} 
					else {
						/* 	1: Noise
							2: Part of Cluster */
						visited.put(point, 1);
					}
				}
				return clusters;
			}
		
			private List<Point> expandCluster(List<Point> cluster, Point point, List<Point> neighbors, List<Point> points, Map<Point, Integer> visited) {
				cluster.add(point);
				visited.put(point, 2);

				List<Point> seeds = new ArrayList<Point>(neighbors);
				int index = 0;
				while (index < seeds.size()) {
					Point current = seeds.get(index);
					int pStatus = 0;
					if (visited.containsKey(current)) { 
						 pStatus = visited.get(current);
					}
					
					// only check non-visited points
					if (pStatus == 0) {
						List<Point> currentNeighbors = getNeighbors(current, points);
						if (currentNeighbors.size() >= minPts) {
							seeds.addAll(currentNeighbors);
						}
					}

					if (pStatus != 2) {
						visited.put(current, 2);
						cluster.add(current);
					}

					index++;
				}
				return cluster;
			}

			private List<Point> getNeighbors(Point point, List<Point> points) {
				List<Point> neighbors = new ArrayList<Point>();
				for (Point neighbor : points) {
					if (point != neighbor && neighbor.distanceFrom(point) <= eps) {
						neighbors.add(neighbor);
					}
				}
				return neighbors;
			}
		}
		
		/** Klasi gia ena simeio (x, y) */
		private class Point {
			private double x;
			private double y;
	
			public Point(double x, double y) {
				this.x = x;
				this.y = y;
			}
		
			/* xrisimopoioume ti geografiki apostasi */
			public double distanceFrom(Point other) {
				return geoDistance(this.x, this.y, other.x, other.y);
			}
		
			public double getX() {
				return this.x;
			}

			public double getY() {
				return this.y;
			}
	
			public String toString() {
				return String.format("(%f, %f)", x, y);
			}
		
			public boolean equals(Point p) {
				if (p == this) {
					return true;
				}
				else {
					return (x == p.x && y == p.y);
				}
			}
		}
	}
}