package org.educoins.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//TODO Herausfinden wieso es mit JSON nicht funktioniert...
public class Transaction {

	protected int version;
	protected int inputsCount;
	protected List<Input> inputs;
	protected int outputsCount;
	protected List<Output> outputs;

	public Transaction() {
		this.inputs = new ArrayList<>();
		this.inputsCount = this.inputs.size();
		this.outputs = new ArrayList<>();
		this.outputsCount = this.outputs.size();
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getInputsCount() {
		return this.inputsCount;
	}

	public List<Input> getInputs() {
		// [joeren]: return just a copy of the inputs list, because of
		// potential effects with inputsCount
		if (this.inputs != null) {
			return new ArrayList<Input>(this.inputs);
		}
		return null;
	}

	public void setInputs(List<Input> inputs) {
		this.inputs = inputs;
		if (this.inputs == null) {
			this.inputsCount = 0;
		} else {
			this.inputsCount = this.inputs.size();
		}
	}

	public void addInput(Input input) {
		if (this.inputs == null) {
			this.inputs = new ArrayList<>();
		}
		this.inputs.add(input);
		this.inputsCount = this.inputs.size();
	}

	public void addInputs(Collection<Input> inputs) {
		if (this.inputs == null) {
			this.inputs = new ArrayList<Input>();
		}
		this.inputs.addAll(inputs);
		this.inputsCount = this.inputs.size();
	}

	public int getOutputsCount() {
		return this.outputsCount;
	}

	public List<Output> getOutputs() {
		// [joeren]: look at getInputs()
		if (this.outputs != null) {
			return new ArrayList<Output>(this.outputs);
		}
		return null;
	}

	public void setOutputs(List<Output> outputs) {
		this.outputs = outputs;
		if (this.outputs == null) {
			this.outputsCount = 0;
		} else {
			this.outputsCount = this.outputs.size();
		}
	}

	public void addOutput(Output output) {
		if (this.outputs == null) {
			this.outputs = new ArrayList<>();
		}
		this.outputs.add(output);
		this.outputsCount = this.outputs.size();
	}

	public void addOutputs(Collection<Output> outputs) {
		if (this.outputs == null) {
			this.outputs = new ArrayList<>();
		}
		this.outputs.addAll(outputs);
		this.outputsCount = this.outputs.size();
	}

}
