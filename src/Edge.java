/**
 * the edge class represents an edge on a graph to be used for connecting 
 * communities and calculating the distance between them
 * @author cory lawrence
 *
 */
public class Edge {
	private int distance;
	private String community1;
	private String community2;

	public Edge(String distance, String community1, String community2) {
		this.distance = Integer.parseInt(distance);
		this.community1 = community1;
		this.community2 = community2;
	}
	
	public int getDistance() {
		return distance;
	}
	
	public String getCommunity1() {
		return community1;
	}
	
	public String getCommunity2() {
		return community2;
	}
	public String toString() {
		String output = "";
		output += community1;
		output += community2;
	    output += distance;
	    return output;
	}
}
