package graph;




/**
 * This class implements Graph with Adjacency List
 * @author chenkaikuang
 *
 */
public class AdjacencyListGraph{
	
	public List[] vertexArray;
	public int vertexNum;
	
	/**
	 * Constructor for Graph class. This class can contain most maxVertices vertices
	 * @param maxVertices  int value of the max vertices
	 */
	public AdjacencyListGraph(int vertexNum) {
		vertexNum = vertexNum + 1;
		vertexArray = new List[vertexNum];
		this.vertexNum = vertexNum;
		for(int i = 0; i < vertexNum; i ++) {
			vertexArray[i] = new List(i);
		}
	}
	
	public boolean addEdge(int v1, int v2) {
		
		//if the edge already exists, return false
		List l = vertexArray[v1];
		//if(l.isLinked(v2)) return false;
		
		vertexArray[v1].addNode(v2);
		vertexArray[v2].addNode(v1);
		return true;
	}
	
}
