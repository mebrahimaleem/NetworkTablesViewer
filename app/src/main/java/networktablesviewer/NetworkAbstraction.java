package networktablesviewer;

import edu.wpi.first.networktables.*;
import java.util.*;

public class NetworkAbstraction {
	NetworkTableInstance netinst;
	int internalState; 
	MultiSubscriber subs;
	NetworkTableListenerPoller poll;

	
	public class TopicValue{
		public NetworkTableValue value;
		public String name;
		public boolean exists = true;

		public TopicValue() {}

		public TopicValue(NetworkTableValue val, String nm){
			value = val;
			name = nm;
		}
	}

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
		internalState = 1;
	}

	public void connect(String host){
		netinst.setServer(host, NetworkTableInstance.kDefaultPort4);
		internalState = 1;
	}

	public boolean isConnected(){
		return netinst.isConnected();
	}

	public void close() {
		poll.close();
		subs.close();
		netinst.close();
		internalState = 0;
	}

	public int getError(){
		return internalState;
	}
	
	public ArrayList<TopicValue> getLatest(){
		if (!isConnected()){
			internalState = 2;
			return new ArrayList<TopicValue>();
		}

		internalState = 0;

		ArrayList<TopicValue> values = new ArrayList<TopicValue>();
		NetworkTableEvent[] events = poll.readQueue();

		for (NetworkTableEvent event : events){
			values.add(new TopicValue(event.valueData.value, event.valueData.getTopic().getName()));
		}

		return values;
	}

	public ArrayList<TopicValue> updateExists(ArrayList<TopicValue> latest){
		if (!isConnected()){
			internalState = 2;
			return latest;
		}

		internalState = 0;
		TopicValue t;
		for (int i = 0; i < latest.size(); i++){
			t = latest.get(i);
			if (t.exists != netinst.getTopic(t.name).exists()){
				t.exists = !t.exists;
				latest.remove(i);
				latest.add(i, t);
			}
		}
		return latest;
	}

	public static ArrayList<TopicValue> squashLatest(ArrayList<TopicValue> current, ArrayList<TopicValue> latest){
		current.addAll(latest);
		for (int i = 0; i < current.size(); i++)
			for (int j = 0; j < latest.size(); j++)
				if (current.get(i).value.getTime() < latest.get(j).value.getTime()){
					current.remove(i);
					current.add(i, latest.get(j));
					latest.remove(j);
				}
		return current;
	}
}
