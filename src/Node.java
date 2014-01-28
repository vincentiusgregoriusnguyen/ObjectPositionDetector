import java.util.ArrayList;


public class Node {
	Coordinates coordinates = null;
	int value;	
	boolean visited = false;
	ArrayList<Node> neighbours;	
	Node parent = null;

	public Node(Coordinates c, int v){
		this.coordinates = c;
		this.value = v;
	}
	
	@Override
	public String toString() {return "Node " + coordinates + " Value:" +  value + " Neighbours:" + neighbours.size();}
	public ArrayList<Node> getNeighbours(){return neighbours;}
	public int getValue() {return value;}
	public void setValue(int value) {this.value = value;}
	public void setCoordinates(Coordinates coordinates) {this.coordinates = coordinates;}
	public Node(Coordinates c){this.coordinates = c;}
	public Coordinates getCoordinates(){return this.coordinates;}
	public boolean isVisited() {return visited;}
	public void setVisited(boolean visited) {this.visited = visited;}
	public void setNeighbours(ArrayList<Node> neighbours) {this.neighbours = neighbours;}
	public Node getParent() {return parent;}
	public void setParent(Node parent) {this.parent = parent;}
	
}
