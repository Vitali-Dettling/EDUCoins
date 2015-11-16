package org.educoins.core;

import java.util.ArrayList;
import java.util.List;

public class Gateway{
	
	private static final int randomNumberLength256 = 256;
	
	private List<Gate> gates;
	
	public Gateway(){
		
		this.gates = new ArrayList<Gate>();
	}


	public int getGatesCount(){
		return this.gates.size();
	}
	
	public List<Gate> getGates() {
		return gates;
	}

	public void setAllGates(List<Gate> gateList) {
		this.gates.addAll(gateList);
	}
	
	public void addGate(Gate gate){
		this.gates.add(gate);
	}
	
}
