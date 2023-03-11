package networktablesviewer;

public class App {
    public static void main(String[] args) {
			System.out.println("NetworkTablesViewer 0.0.0 by Ebrahim Aleem");
			AppContainer appContainer = new AppContainer();
			appContainer.displayWindow();
			try	{
				appContainer.blockingLoop();
			}
			catch (Exception e){
				System.out.println("Error in main-thread -> blockingLoop: " + e.getMessage());
				e.printStackTrace();
				appContainer.dispose();
				System.exit(0);
			}
    }
}
