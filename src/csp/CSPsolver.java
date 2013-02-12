package csp;

import graph.AdjacencyListGraph;
import graph.ListNode;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This class implements CSPsolver
 * Include backtracking, backjumping, CDBJ and MRV, LCV heuristic
 * @author chenkaikuang
 *
 */
public class CSPsolver {
	
	/* private instance variables */
	private ArrayList<Assignment> assignment;
	private ArrayList role[]; //store available roles for each player
	private ArrayList conflictSet[];
	private ArrayList<Integer> tokenPlayer; //players who have tokens
	private ArrayList<Integer> tokenPlayerCopy;
	private AdjacencyListGraph graph;
	private int assignedRole[];
	private int assignOrder[]; //store the order of players being assigned
	private boolean hasToken[];
	private boolean isLCV[]; //Indicate if the player's roles has been sorted with LCV
	private int backjumpID = -1;
	private int jumpFromID = -1;
	boolean isBackjump = false;
	private int tokenRole;
	private int tokenAssignedNum; //#of players with token who have been assigned a role
	private int verticesNum;
	private int edgesNum;
	private int tokensNum;
	private int order = 1;
	private int nodesNum;
	private float runTime;
	private String fileName = "1.graph";
	
	public CSPsolver() {
		assignment = new ArrayList<Assignment>();
		tokenPlayer = new ArrayList<Integer>();
		tokenPlayerCopy = new ArrayList<Integer>();
		tokenAssignedNum = 0;
		nodesNum = 0;
		runTime = 0;
		tokenRole = -1; //tokenRole has not been assigned yet
	}
	
	/**
	 * Read the file to construct graph
	 */
	public void readFile() {
		
		Scanner scanner = null;
		
		try {
			scanner = new Scanner(new FileReader(fileName));
			verticesNum = scanner.nextInt();
			edgesNum = scanner.nextInt();
			tokensNum = scanner.nextInt();
			graph = new AdjacencyListGraph(verticesNum);
			for(int i = 0; i < edgesNum; i ++) {
				graph.addEdge(scanner.nextInt(), scanner.nextInt());
			}
			
			assignedRole = new int[verticesNum];
			assignOrder = new int[verticesNum];
			hasToken = new boolean[verticesNum];
			isLCV = new boolean[verticesNum];
			
			conflictSet = new ArrayList[verticesNum];
			for(int i = 0; i < verticesNum; i ++) {
				conflictSet[i] = new ArrayList<Integer>();
			}
			
			for(int i = 0; i < verticesNum; i ++) {
				hasToken[i] = false;
			}
			
			while(scanner.hasNextInt()) {
				int temp = scanner.nextInt();
				tokenPlayer.add(temp);
				tokenPlayerCopy.add(temp);
				hasToken[temp] = true;
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		role = new ArrayList[verticesNum];
		for(int i = 0; i < verticesNum; i ++) {
			ArrayList<Integer> list = new ArrayList<Integer>();
			list.add(Role.Blacksmith);
			list.add(Role.Archer);
			list.add(Role.Sorceress);
			list.add(Role.Warrior);
			role[i] = list;
		}
	}
	
	/**
	 * Run cspsolver
	 * @param id the former recursive call's id
	 * @return true if this call find a consistent role or assignment is completed
	 */
	public Output backtracking(int id){
		if(assignment.size() == (verticesNum)) return (new Output(nodesNum, runTime, true));
		/* 
		 * Choose method to get currentID
		 * Uncomment on method.
		 */
		int currentID = getUnassignedVariable(id);
		//int currentID = getUnassignedVariable_MRV(id);
		
		while(true) {
			/* 
			 * Choose method to get role for currentID 
			 * Uncomment on method.
			 */
			//int role = getRole(currentID);
			int role = getRole_LCV(currentID);
			
			if(role != -1) nodesNum ++;
			if(role == -1) { //if there is no value available
				if(assignment.size() == 0) return (new Output(nodesNum, runTime, false));
				backTrack(id, currentID);	
				return (new Output(nodesNum, runTime, false)); //no value available
			}
			if(isConsistent(currentID, role)) {
				assignment.add(new Assignment(currentID, role));
				assignedRole[currentID] = role;
				
				forwardChecking(currentID, role);
				
				Output result = backtracking(currentID);
				if(result.feasibility == true) return result;
			}
		}
	}
	
	/**
	 * Run backjumping
	 * @param id the former recursive call's id
	 * @return true if this call find a consistent role or assignment is completed
	 */
	public Output backjumping(int id){
		if(assignment.size() == (verticesNum)) return(new Output(nodesNum, runTime, true));
		
		/* 
		 * Choose method to get currentID
		 * Uncomment on method.
		 */
		//int currentID = getUnassignedVariable(id);
		int currentID = getUnassignedVariable_MRV(id);
		
		if(currentID == jumpFromID) jumpFromID = -1;
		while(true) {
			int role;
			if(isBackjump && currentID != backjumpID) {
				backTrack(id, currentID);		
				return (new Output(nodesNum, runTime, false)); //no value available
			} else {
				
				/* 
				 * Choose method to get role for currentID 
				 * Uncomment on method.
				 */
				//role = getRole(currentID);
				role = getRole_LCV(currentID);
				isBackjump = false;
			}
			
			if(role != -1) nodesNum ++;
			
			if(role == -1) { //if there is no value available
				if(assignment.size() == 0) return (new Output(nodesNum, runTime, false));
				if(jumpFromID == -1) {
					jumpFromID = currentID;
					if(conflictSet[currentID].size() == 0) return  (new Output(nodesNum, runTime, false));
					backjumpID = (int) conflictSet[currentID].get(conflictSet[currentID].size() - 1);
					isBackjump = true;
				}
				backTrack(id, currentID);
				return (new Output(nodesNum, runTime, false)); //no value available
			}
			if(isConsistent(currentID, role)) {
				assignment.add(new Assignment(currentID, role));
				assignedRole[currentID] = role;
				
				//add currentID to friends' conflictSet
				ListNode friend = graph.vertexArray[currentID].head.next;
				while(friend != null) {
					if(assignedRole[friend.value] == 0) {
						conflictSet[friend.value].add(currentID);
					}
					friend = friend.next;
				}
				
				//forwardChecking(currentID, role);
				
				Output result = backjumping(currentID);
				if(result.feasibility == true) return result;
			}
		}
	}
	
	/**
	 * Run backjumping_CDBJ
	 * @param id the former recursive call's id
	 * @return true if this call find a consistent role or assignment is completed
	 */
	public Output backjumping_CDBJ(int id){
		if(assignment.size() == (verticesNum)) 
			return(new Output(nodesNum, runTime, true));
		
		/* 
		 * Choose method to get currentID
		 * Uncomment on method.
		 */
		//int currentID = getUnassignedVariable(id);
		int currentID = getUnassignedVariable_MRV(id);
	
		while(true) {
			int role;
			if(isBackjump && currentID != backjumpID) {
				if(assignment.size() == 0) return (new Output(nodesNum, runTime, false));
				backTrack(id, currentID);
				return (new Output(nodesNum, runTime, false)); //no value available
			} else {
				
				/* 
				 * Choose method to get role for currentID 
				 * Uncomment on method.
				 */
				//role = getRole(currentID);
				role = getRole_LCV(currentID);
				isBackjump = false;
			}
			if(role != -1) nodesNum ++;
			if(role == -1) { //if there is no value available
				if(assignment.size() == 0 || conflictSet[currentID].size() == 0) 
					return (new Output(nodesNum, runTime, false));
				
				isBackjump = true;	
				backjumpID = (int) conflictSet[currentID].get(conflictSet[currentID].size() - 1);
				unitConflictSet(currentID, backjumpID);
				backTrack(id, currentID);
				return (new Output(nodesNum, runTime, false)); //no value available
			}
			if(isConsistent(currentID, role)) {
				assignment.add(new Assignment(currentID, role));
				assignedRole[currentID] = role;
				assignOrder[currentID] = order;
				order ++;
				
				//add currentID to friends' conflictSet
				ListNode friend = graph.vertexArray[currentID].head.next;
				while(friend != null) {
					if(assignedRole[friend.value] == 0 && 
							conflictSet[friend.value].contains(currentID)!=true) {
						conflictSet[friend.value].add(currentID);
					}
					friend = friend.next;
				}
				
				//forwardChecking(currentID, role);
				
				Output result = backjumping_CDBJ(currentID);
				if(result.feasibility == true) return result;
			}
		}
	}
	
	/**
	 * When jump to currentID, set new value to conf(currentID) 
	 */
	public void unitConflictSet(int id, int currentID) {
		for(int i = 0; i < conflictSet[id].size(); i++) {
			if(conflictSet[currentID].contains(conflictSet[id].get(i)) || 
					(int)conflictSet[id].get(i) == currentID) continue;
			conflictSet[currentID].add(conflictSet[id].get(i));
		}
		
		//Put the most recently assigned id to the last
		for(int i = 0; i < conflictSet[currentID].size() - 1; i ++) {
			if(assignOrder[(int)conflictSet[currentID].get(i)] > 
			assignOrder[(int)conflictSet[currentID].get(i + 1)]) {
				int temp = (int)conflictSet[currentID].get(i);
				conflictSet[currentID].set(i, (int)conflictSet[currentID].get(i + 1));
				conflictSet[currentID].set(i + 1, temp);
			}	
		}
	}
	
	public void forwardChecking(int id, int roleAssigned) {
		if(hasToken[id]) {
			for(int i = 0; i < tokensNum; i ++) {
				int tempID = tokenPlayerCopy.get(i);
				if(assignedRole[tempID] == 0) {
					ArrayList<Integer> onlyTokenRole = new ArrayList<Integer>();
					onlyTokenRole.add(roleAssigned);
					role[tempID] = onlyTokenRole;
				}
			}
		}
		
		ListNode friend = graph.vertexArray[id].head.next;
		while(friend != null) {
			if(assignedRole[friend.value] == 0 && !hasToken[friend.value]) {
				for(int i = 0; i < role[friend.value].size(); i ++) {
					if((int)role[friend.value].get(i) == roleAssigned) 
						role[friend.value].remove(i);
				}
			}
			friend = friend.next;
		}
	}
	
	/**
	 * Naive way to get next unassigned variable
	 */
	public int getUnassignedVariable(int id) {
		return (id + 1);
	}
	
	/**
	 * Using MRV to get next unassigned variable
	 */
	public int getUnassignedVariable_MRV(int id) {
		if(tokenPlayer.size() > 0) { //If there still exists unassigned tokenPlayer
			int variable = tokenPlayer.get(0);
			tokenPlayer.remove(0);
			return variable;
		} else {
			int MRV = 5; //initial MRV = 5, ensures it will be replace by the first time
			int MRV_ID = 0;
			for(int i = 0; i < verticesNum; i ++) {
				if(assignedRole[i] != 0) continue;
				ArrayList<Integer> unavailableRoles = new ArrayList<Integer>();
				ListNode friend = graph.vertexArray[i].head.next;
				while(friend != null) {
					int friendRole = assignedRole[friend.value];
					if(friendRole != 0 && 
							unavailableRoles.contains(friendRole) == false) {
						unavailableRoles.add(friendRole);
					}
					friend = friend.next;
				}
				int friend_MRV = 4 - unavailableRoles.size();
				if(friend_MRV < MRV) {
					MRV = friend_MRV;
					MRV_ID = i;
				}
			}
			return MRV_ID;
		}
	}
	
	/**
	 * Retrieve all the available roles in id's role ArrayList
	 */
	public void retrieveRole(int id) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(Role.Blacksmith);
		list.add(Role.Archer);
		list.add(Role.Sorceress);
		list.add(Role.Warrior);
		role[id] = list;
		isLCV[id] = false;
	}
	
	/**
	 * Naive way to get next available role
	 */
	public int getRole(int id) {
		if(role[id].size() == 0) return -1; //no available role
		int choice = (int) role[id].get(0);
		role[id].remove(0); //Remove this role from available list
		return choice;
	}
	
	/**
	 * Get id's role using lCV
	 */
	public int getRole_LCV(int id) {
		if(isLCV[id] == false) {
			role[id] = priorityRole(id);
			isLCV[id] = true;
		}
		if(role[id].size() == 0) return -1;
		int choice = (int)role[id].get(0);
		role[id].remove(0);
		return choice;		
	}
	
	/**
	 * Get the priority role of certain id(LCV)
	 */
	public ArrayList<Integer> priorityRole(int id) {
		int roleCount[] = new int[4];
		ListNode friend = graph.vertexArray[id].head.next;
		boolean flag[] = new boolean[4];
		while(friend != null) {
			if(assignedRole[friend.value] != 0) {
				friend = friend.next;
				continue;
			}
			ListNode friend_friend = graph.vertexArray[friend.value].head.next;
			while(friend_friend != null) {
				int role = assignedRole[friend_friend.value];
				if(role == 0) {
					friend_friend = friend_friend.next;
					continue;
				}
				if(flag[role - 1] == false) {
					roleCount[role - 1] ++;
					flag[role - 1] = true;
				}
				friend_friend = friend_friend.next;
			}
			for(int i = 0; i < 4; i ++) {
				flag[i] = false;
			}
			friend = friend.next;
		}	
		
		//Render the priority role ArrayList from roleCount
		PriorityRole priorityRoleArray[] = new PriorityRole[4];
		for(int i = 0; i < 4; i ++) {
			priorityRoleArray[i] = new PriorityRole(i + 1, roleCount[i]);
		}
		for(int i = 3; i > 0; i --) {
			for(int j = 0; j < i; j ++) {
				if(priorityRoleArray[j].count < priorityRoleArray[j + 1].count) {
					PriorityRole temp = priorityRoleArray[j];
					priorityRoleArray[j] = priorityRoleArray[j + 1];
					priorityRoleArray[j + 1] = temp;
				}
			}
		}
		ArrayList<Integer> result = new ArrayList<Integer>();
		for(int i = 0; i < 4; i ++) {
			result.add(priorityRoleArray[i].role);
		}
		return result;
	}
	
	/**
	 * This method check if the chosen role is consistent for player with id
	 */	
	public boolean isConsistent(int id, int role) {
		boolean tokenAssigned = false; // If 
		//check if this player has same role as his friends
		ListNode friend = graph.vertexArray[id].head.next;
		while(friend != null) {
			int friendID = friend.value;
			if(assignedRole[friendID] == role) {
				if(hasToken[id] == true && hasToken[friendID] == true) {
					tokenAssignedNum ++;
					return true;
				}
				return false;
			}
			friend = friend.next;
		}
		
		//check if this player is consistent as a token player
		if(hasToken[id]) {
			if(tokenRole == -1) {
				if(tokenAssignedNum != 0) System.out.println("Something wrong!!!");
				tokenRole = role;
				tokenAssigned = true;
			} else {
				if(role != tokenRole) {
					return false;
				}
				tokenAssigned = true;
			}
		}
		
		if(tokenAssigned) tokenAssignedNum ++;
		return true;
	}
	
	public void backTrack(int id, int currentID) {
		assignment.remove(assignment.size() - 1);
		retrieveRole(currentID);
		assignedRole[id] = 0;
		if(hasToken[id]) tokenAssignedNum --;
		if(tokenAssignedNum == 0) tokenRole = -1;
	}
	
	public void checkCorrectness(Output result) {
		if(result.feasibility) {
			for(int i = 0; i < verticesNum; i ++) {
				ListNode friend = graph.vertexArray[i].head.next; 
				while(friend != null) {
					if(assignedRole[i] == assignedRole[friend.value] && 
							(hasToken[i] != true || hasToken[friend.value] != true)) {
						System.out.println("Wrong!!!!!!!!!!!" + i + "  " + 
							friend.value +" " + assignedRole[i]);
					}
					friend = friend.next;
				}
			}
		}
	}
	
	public static void main(String[] args){
		CSPsolver cspsolver = new CSPsolver();
		cspsolver.readFile();
		long start = System.nanoTime();  
		//Output result = cspsolver.backtracking(-1);
		Output result = cspsolver.backjumping(-1);
		//Output result = cspsolver.backjumping_CDBJ(-1);
//		for(int i = 0; i < cspsolver.verticesNum; i ++) {
//			System.out.println(cspsolver.assignedRole[i]);
//		}
		long elapsedTime = System.nanoTime() - start;
		System.out.println(result.nodesNum + " " + result.feasibility + " " + (double)elapsedTime/1000000000);
		cspsolver.checkCorrectness(result);
	}
	
	
	private class PriorityRole {
		public int role;
		public int count;
		
		public PriorityRole(int role, int count) {
			this.role = role;
			this.count = count;
		}
	}
	
}
