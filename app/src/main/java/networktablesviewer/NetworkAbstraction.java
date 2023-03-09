package networktablesviewer;

import edu.wpi.first.networktables.*;
import java.util.ArrayList;

public class NetworkAbstraction{
	NetworkTableInstance netinst;
	private int internalState;

	public NetworkAbstraction(){
		netinst = NetworkTableInstance.create();
		netinst.startClient4("NTVC-lcl");
	}

	public void connect(){
		netinst.startDSClient();
	}

	public void connect(String host){
		netinst.setServer(host, NetworkTableInstance.kDefaultPort4);
	}

	public boolean isConnected(){
		return netinst.isConnected();
	}

	public void close(){
		netinst.close();
	}

	public void p(){
		netinst.getTable("root").getEntry("node").setDouble(1);
	}

}
