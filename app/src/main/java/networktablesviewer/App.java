package networktablesviewer;
import javax.swing.*;
import java.lang.*;
import java.awt.event.*;

/**
 * The class containing the main method for the application
 */
public class App {

		/**
		 * The main function for the application
		 * @param args Command line arguments, the application does not use these
		 */
    public static void main(String[] args) {
			SwingUtilities.invokeLater(new Runnable() { //Schedule to run in seperate thread
				public void run(){
					System.out.println("NetworkTablesViewer 0.0.0 by Ebrahim Aleem");
					Settings settings = new Settings(); //Get current settings
					settings.read();

					AppContainer appContainer = new AppContainer(settings); //Create JFrame
					appContainer.displayWindow();

					new Timer(500, new ActionListener() { //Schedule loopCycle to run in seperate thread every 1/2 second
						@Override
						public void actionPerformed(ActionEvent a){
							try	{
								appContainer.loopCycle();
							}

							catch (Exception e){
								System.out.println("Error in background: " + e.getMessage());
								e.printStackTrace();
							}
						}
					}).start();
				}
			});

    }
}
