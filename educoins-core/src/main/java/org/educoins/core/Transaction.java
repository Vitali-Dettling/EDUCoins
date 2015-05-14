package org.educoins.core;

import java.util.ArrayList;
import java.util.List;

public class Transaction {

	private int version;
	private List<Input> vin;
	private List<Output> vout;

	public Transaction() {
		this.vin = new ArrayList<>();
		this.vout = new ArrayList<>();
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getVinCnt() {
		if (this.vin == null) {
			return 0;
		}
		return vin.size();
	}

	public List<Input> getVin() {
		return this.vin;
	}

	public void setVin(List<Input> vin) {
		this.vin = vin;
	}

	public int getVoutCnt() {
		if (this.vout == null) {
			return 0;
		}
		return this.vout.size();
	}

	public List<Output> getVout() {
		return this.vout;
	}

	public void setVout(List<Output> vout) {
		this.vout = vout;
	}

}
