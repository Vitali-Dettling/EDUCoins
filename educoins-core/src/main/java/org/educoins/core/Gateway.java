package org.educoins.core;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Objects;

public class Gateway{
	
	private static final int randomNumberLength256 = 256;
	
	private List<Gate> gates;
	
	public Gateway(){
		
		this.gates = new ArrayList<Gate>();
	}


	public int getGatesCount(){
		return this.gates.size();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("randomNumberLength256", randomNumberLength256)
				.add("gates", this.gates.toString())
				.toString();
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
	
	public byte[] getConcatedGateway(){
		
		SecureRandom secureRandom = new SecureRandom();
		byte[] nextByte = new byte[randomNumberLength256];
		secureRandom.nextBytes(nextByte);
		return nextByte;
	}
	
}
