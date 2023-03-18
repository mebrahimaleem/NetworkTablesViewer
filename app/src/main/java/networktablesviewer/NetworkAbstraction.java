package networktablesviewer;

import edu.wpi.first.networktables.*;
import java.util.*;
import java.util.concurrent.*;

public class NetworkAbstraction {
	NetworkTableInstance netinst;
	int internalState; 
	MultiSubscriber subs;
	NetworkTableListenerPoller poll;
	Semaphore lock = new Semaphore(0, true);
	
	public class TopicValue{
		public NetworkTableValue value;
		public String name;
		public boolean exists = true;

		public TopicValue() {}

		public TopicValue(NetworkTableValue val, String nm){
			value = val;
			name = nm;
		}

		public String getString() {
			switch (this.value.getType()){
				case kBoolean:
					return String.valueOf(this.value.getBoolean());

				case kDouble:
					return String.valueOf(this.value.getDouble());

				case kFloat:
					return String.valueOf(this.value.getFloat());

				case kInteger:
					return String.valueOf(this.value.getInteger());

				case kString:
					return String.valueOf(this.value.getString());

				case kBooleanArray:
					return Arrays.toString(this.value.getBooleanArray());

				case kDoubleArray:
					return Arrays.toString(this.value.getDoubleArray());

				case kFloatArray:
					return Arrays.toString(this.value.getFloatArray());

				case kIntegerArray:
					return Arrays.toString(this.value.getIntegerArray());

				case kStringArray:
					return Arrays.toString(this.value.getStringArray());

				default:
					return "<?>";
			}
		}
	}

	public NetworkAbstraction(){
		netinst = NetworkTableInstance.create();
		netinst.startClient4("NTVC-lcl");

		subs = new MultiSubscriber(netinst, new String[] {"/"}); 
		poll = new NetworkTableListenerPoller(netinst);
		poll.addListener(subs, EnumSet.of(NetworkTableEvent.Kind.kValueAll));

		internalState = 0;
		lock.release();
	}

	public void connect(){
		try {
			lock.acquire();
			netinst.startDSClient();
			internalState = 1;
		}

		catch (Exception e) {}

		finally {
			lock.release();
		}
	}

	public boolean isOpen() {
		return internalState == 1;
	}

	public void connect(String host){
		try {
			lock.acquire();
			netinst.setServer(host, NetworkTableInstance.kDefaultPort4);
			internalState = 1;
		}

		catch (Exception e) {}

		finally {
			lock.release();
		}
	}

	public boolean isConnected(){
		try {
			lock.acquire();
			return netinst.isConnected();
		}

		catch (Exception e) {}

		finally {
			lock.release();
			return false;
		}
	}

	public void closeAndLock() {
		try {
			lock.acquire();
			poll.close();
			subs.close();
			netinst.close();
			internalState = 0;
		}

		catch (Exception e) {}
	}

	public int getError(){
		return internalState;
	}
	
	public ArrayList<TopicValue> getLatest(){
		ArrayList<TopicValue> values = new ArrayList<TopicValue>();

		try {
			lock.acquire();
			if (!netinst.isConnected()) {
				internalState = 2;
				return values;
			}

			internalState = 0;
			NetworkTableEvent[] events = poll.readQueue();

			for (NetworkTableEvent event : events){
				values.add(new TopicValue(event.valueData.value, event.valueData.getTopic().getName()));
			}
		}

		catch (Exception e) {}

		finally {
			lock.release();
		}

		return values;
	}

	public ArrayList<TopicValue> updateExists(ArrayList<TopicValue> latest){
		try {
			lock.acquire();
			if (!netinst.isConnected()) {
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
		}

		catch (Exception e) {}

		finally {
			lock.release();
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
