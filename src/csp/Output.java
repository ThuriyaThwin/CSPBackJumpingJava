package csp;

/**
 * This class collects output data
 * @author chenkaikuang
 *
 */

public class Output {
	
	public int nodesNum;
	public float runTime;
	public boolean feasibility;
	
	public Output(int nodesNum, float runTime, boolean feasibility) {
		this.nodesNum = nodesNum;
		this.runTime = runTime;
		this.feasibility = feasibility;
	}
}
