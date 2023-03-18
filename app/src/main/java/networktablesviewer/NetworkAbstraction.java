package networktablesviewer;

import edu.wpi.first.networktables.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * This class provides a simpler way to access NT
 */
public class NetworkAbstraction {
	NetworkTableInstance netinst; //NT instance
	int internalState; //Value for error tracing
	MultiSubscriber subs; //Subscriber for all Topics
	NetworkTableListenerPoller poll; //Poller for all Topic updates
	Semaphore lock = new Semaphore(0, true); //Acts as a Mutex to stop multiple threads from using the NT instance at the same time
	
	/**
	 * Holds information about a Topic
	 */
	public class TopicValue{
		/** value for the topic */
		public NetworkTableValue value;

		/** 
		 * name of the topic
		 *  this should match with the name of the topic of the NetworkTableValue
		 */
		public String name;

		/** true if the topic is being published */
		public boolean exists = true;

		/**
		 * Empty constructor for empty topic
		 * Should only be used when value and name will be set afterwards externally
		 */
		public TopicValue() {}

		/**
		 * Creates a topic value given a Topic and name
		 * @param val NetworkTableValue for the topic
		 * @param nm The topic name. This should be the same as val's topic's name
		 */
		public TopicValue(NetworkTableValue val, String nm){
			value = val;
			name = nm;
		}
		
		/**
		 * Gets the string value of the TopicValue
		 * @return The String value of val
		 */
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

	/**
	 * Constructs a new NetworkAbstraction with identifier NTVC-lcl
	 * This should only by one client connected to a NT server
	 */
	public NetworkAbstraction(){
		netinst = NetworkTableInstance.create();
		netinst.startClient4("NTVC-lcl");

		subs = new MultiSubscriber(netinst, new String[] {"/"}); //Subscribe to all topics
		poll = new NetworkTableListenerPoller(netinst); //Poll all topics
		poll.addListener(subs, EnumSet.of(NetworkTableEvent.Kind.kValueAll));

		internalState = 0;
		lock.release();
	}

	/**
	 * Connects to the NT server using information from the Driver Station
	 * Driver Station must be running for this method to work
	 */
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

	/**
	 * Checks if NT port is open
	 * @return true if NT port is open
	 */
	public boolean isOpen() {
		return internalState == 1;
	}

	/**
	 * Connects to the NT server given a hostname
	 * @param host hostname of the server
	 */
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

	/**
	 * Checks if we are connected to the NT server
	 * It is possible for the port to be open but to not be connected
	 * @return true if conected with an NT server
	 */
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

	/**
	 * Closes the connection and acquires the semaphore but never releases it
	 * This instance will be useless after this call
	 * This should be used to stop all other threads from trying to use this NT instance
	 * This will also free up the network identifier NTVC-lcl
	 */
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

	/**
	 * Gets the previous error (sticky)
	 * @return error code: 0 - never connected, 1 - able to connect, 2 - disconnected
	 */
	public int getError(){
		return internalState;
	}
	
	/**
	 * Gets the latest topics from the server
	 * May include duplicates and older values
	 * @return latest TopicValues
	 */
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

	/**
	 * Checks to see which Topics are still being published
	 * @param latest Latest topic values
	 * @return Topic values with updates exists members
	 */
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

	/**
	 * Squashes latest and old TopicValues together
	 * Will include any old topics with no new values
	 * @param current Outdated topic values
	 * @param latest Latest topic values
	 * @return Latest topic values without duplicates
	 */
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
