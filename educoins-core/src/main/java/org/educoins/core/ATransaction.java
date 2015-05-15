package org.educoins.core;

import java.util.ArrayList;
import java.util.List;

public abstract class ATransaction {

	protected int version;
	protected List<Input> inputs;
	protected List<Output> outputs;

	public ATransaction() {
		this.inputs = new ArrayList<>();
		this.outputs = new ArrayList<>();
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getInputsCount() {
		if (this.inputs == null) {
			return 0;
		}
		return inputs.size();
	}

	public List<Input> getInputs() {
		return this.inputs;
	}

	public void setInputs(List<Input> inputs) {
		this.inputs = inputs;
	}

	public int getOutputsCount() {
		if (this.outputs == null) {
			return 0;
		}
		return this.outputs.size();
	}

	public List<Output> getOutputs() {
		return this.outputs;
	}

	public void setOutputs(List<Output> outputs) {
		this.outputs = outputs;
	}

}
