package networktablesviewer;

import edu.wpi.first.networktables.*;
import java.util.*;

public class NetworkAbstraction {
	NetworkTableInstance netinst;
	int internalState; //TODO: use internalState to track errors
	MultiSubscriber subs;
	NetworkTableListenerPoller poll;

	public NetworkAbstraction(){
		netinst = NetworkTableInstance.create();
		netinst.startClient4("NTVC-lcl");

		subs = new MultiSubscriber(netinst, new String[] {"/"});
		poll = new NetworkTableListenerPoller(netinst);
		poll.addListener(subs, EnumSet.of(NetworkTableEvent.Kind.kValueAll));

		internalState = 0;
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

	public void close() {
		poll.close();
		subs.close();
		netinst.close();
	}

	public int getError(){
		return internalState;
	}

	public ArrayList<NetworkTableValue> getLatest(){
		ArrayList<NetworkTableValue> values = new ArrayList<NetworkTableValue>();
		NetworkTableEvent[] events = poll.readQueue();

		for (NetworkTableEvent event : events){
			values.add(event.valueData.value);
		}

		return values;
	}

	public static ArrayList<NetworkTableValue> squashLatest(ArrayList<NetworkTableValue> current, ArrayList<NetworkTableValue> latest){
		for (int i = 0; i < current.size(); i++)
			for (int j = 0; j < latest.size(); j++)
				if (current.get(i).getTime() < latest.get(j).getTime()){
					current.remove(i);
					current.add(i, latest.get(j));
					latest.remove(j);
				}
		return current;
	}
}
