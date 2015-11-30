package org.educoins.core;

import org.educoins.core.cryptography.SHA256Hasher;
import org.educoins.core.utils.ByteArray;
import org.educoins.core.utils.Hashable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class Transaction implements Hashable {

	private static final int HAS_NO_ENTRIES = 0;

	protected int version;

	protected int inputsCount;
	protected List<Input> inputs;

	protected int outputsCount;
	protected List<Output> outputs;

	protected int approvalsCount;
	protected List<Approval> approvals;

	public Transaction() {
		this.inputs = new ArrayList<>();
		this.inputsCount = this.inputs.size();
		this.outputs = new ArrayList<>();
		this.outputsCount = this.outputs.size();
		this.approvals = new ArrayList<>();
		this.approvalsCount = this.approvals.size();
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

	public int getApprovalsCount() {
		return approvalsCount;
	}

	public List<Approval> getApprovals() {
		// [joeren]: look at getInputs()
		if (this.approvals != null) {
			return new ArrayList<Approval>(this.approvals);
		}
		return null;
	}

	public void setApprovals(List<Approval> approvals) {
		this.approvals = approvals;
		if (this.approvals == null) {
			this.approvalsCount = 0;
		} else {
			this.approvalsCount = this.approvals.size();
		}

	}

	public void addApproval(Approval output) {
		if (this.approvals == null) {
			this.approvals = new ArrayList<>();
		}
		this.approvals.add(output);
		this.approvalsCount = this.approvals.size();
	}

	public void addApprovals(Collection<Approval> approvals) {
		if (this.approvals == null) {
			this.approvals = new ArrayList<>();
		}
		this.approvals.addAll(approvals);
		this.approvalsCount = this.approvals.size();
	}

	public ETransaction whichTransaction() {

		// Coinbase:
		// inputs = 0;
		// outputs > 0;
		// approvals = 0;
		// Regular:
		// inputs > 0;
		// outputs > 0;
		// approvals = 0;
		// Approval:
		// inputs > 0;
		// outputs = 0 || > 0
		// approvals > 0

		// Check for transaction type.
		if ((this.getInputs() == null || this.getInputs().size() == 0)
				&& (this.getOutputs() != null && this.getOutputs().size() > 0)
				&& (this.getApprovals() == null || this.getApprovals().size() == 0)) {
			return ETransaction.COINBASE;
		}
		if ((this.getInputs() != null && this.getInputs().size() > 0)
				&& (this.getOutputs() != null && this.getOutputs().size() > 0)
				&& (this.getApprovals() == null || this.getApprovals().size() == 0)) {
			return ETransaction.REGULAR;
		}
		if ((this.getInputs() != null && this.getInputs().size() > 0)
				&& ((this.getOutputs() == null || this.getOutputs().size() == 0) || (this.getOutputs() != null && this
						.getOutputs().size() > 0)) && (this.getApprovals() != null && this.getApprovals().size() > 0)) {
			return ETransaction.APPROVED;
		}
		return null;
	}

	@Override
	public byte[] hash() {
		return Transaction.hash(this);
	}

	public static byte[] hash(Transaction transaction) {

		byte[] toBeHashed = null;
		// Check for transaction type.
		if (transaction.whichTransaction() == ETransaction.COINBASE) {
			toBeHashed = getByteArrayOutput(transaction);
		} else if (transaction.whichTransaction() == ETransaction.REGULAR) {
			byte[] input = getByteArrayInput(transaction);
			byte[] output = getByteArrayOutput(transaction);
			toBeHashed = ByteArray.concatByteArrays(input, output);
		} else if (transaction.whichTransaction() == ETransaction.APPROVED) {
			byte[] input = getByteArrayInput(transaction);
			byte[] approved = getByteArrayApproved(transaction);
			toBeHashed = ByteArray.concatByteArrays(input, approved);
		}

		// hash concatenated header fields and return
		byte[] hash = SHA256Hasher.hash(SHA256Hasher.hash(toBeHashed));
		return hash;

	}

	// TODO[Vitali] Much better implementation, with generic class and
	// so!!!!!!!!!!!!!!!!!!!
	private static byte[] getByteArrayInput(Transaction transaction) {
		int length = 0;
		for (Input input : transaction.getInputs()) {
			length += input.getConcatedInput().length;
		}
		byte[] byteArray = new byte[length];
		int index = 0;
		for (Input input : transaction.getInputs()) {

			System.arraycopy(input.getConcatedInput(), 0, byteArray, index, input.getConcatedInput().length);
			index += input.getConcatedInput().length;
		}
		return byteArray;
	}

	// TODO[Vitali] Much better implementation, with generic class and
	// so!!!!!!!!!!!!!!!!!!!
	private static byte[] getByteArrayOutput(Transaction transaction) {
		int length = 0;
		for (Output output : transaction.getOutputs()) {
			length += output.getConcatedOutput().length;
		}
		byte[] byteArray = new byte[length];
		int index = 0;
		for (Output output : transaction.getOutputs()) {

			System.arraycopy(output.getConcatedOutput(), 0, byteArray, index, output.getConcatedOutput().length);
			index += output.getConcatedOutput().length;
		}
		return byteArray;
	}

	// TODO[Vitali] Much better implementation, with generic class and
	// so!!!!!!!!!!!!!!!!!!!
	private static byte[] getByteArrayApproved(Transaction transaction) {
		int length = 0;
		for (Approval approval : transaction.getApprovals()) {
			length += approval.getConcatedApproval().length;
		}
		byte[] byteArray = new byte[length];
		int index = 0;
		for (Approval approval : transaction.getApprovals()) {

			System.arraycopy(approval.getConcatedApproval(), 0, byteArray, index, approval.getConcatedApproval().length);
			index += approval.getConcatedApproval().length;
		}
		return byteArray;
	}

	public enum ETransaction {

		APPROVED, COINBASE, REGULAR,

	}

}
