package networktablesviewer;
import javax.swing.*;
import java.lang.*;
import java.awt.event.*;

public class App {
    public static void main(String[] args) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run(){
					System.out.println("NetworkTablesViewer 0.0.0 by Ebrahim Aleem");
					Settings settings = new Settings();
					settings.read();

					AppContainer appContainer = new AppContainer(settings);
					appContainer.displayWindow();

					new Timer(500, new ActionListener() {
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
