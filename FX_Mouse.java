/**
 * Copyright (c) 2008, 2012 Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 */

// FX Imports
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.*;
//import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.effect.Lighting;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

// Other imports
import java.io.*;
import java.awt.image.*;
import javax.imageio.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

/**
 * A sample with a control that creates a transparent stage that is centered on
 * your desktop. You can drag the stage with your mouse or use the scene
 * controls to minimize or close it. With a transparent stage, you must add
 * your own event handlers to perform these actions.
 *
 * @see javafx.stage.Stage
 * @see javafx.scene.Scene
 * @see javafx.stage.StageStyle
 * @see javafx.application.Platform
 * @related scenegraph/stage/Stage
 */
// AdvancedStageSample
public class FX_Mouse extends Application implements ActionListener
{
	//variables for storing initial position of the stage at the beginning of drag
	private double initX;
	private double initY;
	public Text text = null;
	public int ts = 0;
	public javax.swing.Timer timer = null;
	public int delay = 15;
	public int cPosX = 0;
	public int cPosY = 0;
	public int posX = 0;
	public int posY = 0;
	public int command = 0; // 1 - give focus, 2 - hide
	public Stdin_Watcher watcher = null;
	public Stage _stage;
	public BufferedImage cursor = null;

	public void actionPerformed(java.awt.event.ActionEvent e)
	{
		// Position update
		if((cPosX != posX) || (cPosY != posY))
		{
			cPosX = posX;
			cPosY = posY;
			_stage.setX(cPosX);
			_stage.setY(cPosY);
		}

		// Focus/Hide commands
		if(command == 1)
		{
			

			command = 0;
		}
		else if(command == 2)
		{
			

			command = 0;
		}
	}

	private void init(Stage primaryStage) 
	{
		final Stage stage = primaryStage;
		_stage = stage;
		stage.initStyle(StageStyle.TRANSPARENT);

		javafx.scene.image.Image img2 = new javafx.scene.image.Image("./images/cursor.png");

		int w = (int)img2.getWidth();
		int h = (int)img2.getHeight();


		Group rootGroup = new Group();
		//create scene with set width, height and color
		Scene scene = new Scene(rootGroup, 31, 54, Color.TRANSPARENT);
		//set scene to stage
		stage.setScene(scene);
		//center stage on screen
		stage.centerOnScreen();
		//show the stage
		stage.show();

		


		// CREATION OF THE DRAGGER (CIRCLE)
	  
		//create dragger with desired size
		//Rectangle dragger = new Rectangle(0, 0, 20, 20);
		//ImageView img = new ImageView(cursor);
		//fill the dragger with some nice radial background
		
		try
		{
			cursor = ImageIO.read(new File("./images/cursor.png"));
		}
		catch(Exception ex){ex.printStackTrace();}
		/*
		dragger.setFill(new RadialGradient(-0.3, 135, 0.5, 0.5, 1, true, CycleMethod.NO_CYCLE, new Stop[] {
		    new Stop(0, Color.TRANSPARENT),
		    new Stop(1, Color.TRANSPARENT)
		 }));
		 */
		
		if(cursor != null)
			cursor = new BufferedImage(20,20,BufferedImage.TYPE_INT_RGB);
		ImageView img = new ImageView(img2);

		//when mouse button is pressed, save the initial position of screen
		rootGroup.setOnMousePressed(new EventHandler<MouseEvent>() 
		{
			public void handle(MouseEvent me) 
			{
				initX = me.getScreenX() - stage.getX();
				initY = me.getScreenY() - stage.getY();
			}
		});

		//when screen is dragged, translate it accordingly
		rootGroup.setOnMouseDragged(new EventHandler<MouseEvent>() 
		{
			public void handle(MouseEvent me) 
			{
				stage.setX(me.getScreenX() - initX);
				stage.setY(me.getScreenY() - initY);
			}
		});
	       
		// CREATE MIN AND CLOSE BUTTONS
		//create button for closing application
		Button close = new Button("Close me");
		close.setOnAction(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent event) 
			{
			//in case we would like to close whole demo
			//javafx.application.Platform.exit();

			//however we want to close only this instance of stage
				stage.close();
			}
		});

		//create button for minimalising application
		Button min = new Button("Minimize me");
		min.setOnAction(new EventHandler<ActionEvent>() 
		{
			public void handle(ActionEvent event) 
			{
				stage.setIconified(true);
			}
		});


		// CREATE SIMPLE TEXT NODE
		text = new Text("JavaFX"); //20, 110,
		text.setFill(Color.WHITESMOKE);
		text.setEffect(new Lighting());
		text.setBoundsType(TextBoundsType.VISUAL);
		text.setFont(Font.font(Font.getDefault().getFamily(), 50));

		// USE A LAYOUT VBOX FOR EASIER POSITIONING OF THE VISUAL NODES ON SCENE
		VBox vBox = new VBox();
		vBox.setSpacing(10);
		vBox.setPadding(new Insets(60, 0, 0, 20));
		vBox.setAlignment(Pos.TOP_CENTER);
		vBox.getChildren().addAll(text, min, close);

		//add all nodes to main root group
		//rootGroup.getChildren().addAll(dragger, vBox);
		rootGroup.getChildren().addAll(img);
		timer = new javax.swing.Timer(delay,this);
		timer.start();

		watcher = new Stdin_Watcher(this);
		watcher.start();

		stage.setOnCloseRequest(new javafx.event.EventHandler<javafx.stage.WindowEvent>()
		{
			@Override
			public void handle(javafx.stage.WindowEvent t) 
			{
				Platform.exit();
				System.exit(0);
			}
		});
	}

	@Override public void start(Stage primaryStage) throws Exception
	{
		init(primaryStage);
		primaryStage.show();
	}
	public static void main(String[] args) { launch(args); }
}
class Stdin_Watcher extends Thread
{
	FX_Mouse parent = null;
	public Stdin_Watcher(FX_Mouse parent)
	{
		this.parent = parent;
	}

	public void run()
	{
		try
		{
			BufferedReader sn = new BufferedReader(new InputStreamReader(System.in));
			String[] s = null;
			String ss = "";
			while(true)
			{
				try
				{
					ss = sn.readLine();
					s = ss.split(" ");
					if(s[0].equals("move"))
					{
						parent.posX = Integer.parseInt(s[1]);
						parent.posY = Integer.parseInt(s[2]);
					}
					else if(s[0].equals("focus"))
					{
						parent.command = 1;
					}
					else if(s[0].equals("hide"))
					{
						parent.command = 2;
					}
				}
				catch(Exception ex)
				{
				
				}
				try{Thread.sleep(20);}catch(Exception ex){} // 20 milliseconds}
			}
		}
		catch(Exception ex2)
		{
			
		}
	}
}
