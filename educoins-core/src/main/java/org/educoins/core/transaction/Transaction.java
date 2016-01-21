package org.educoins.core.transaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.educoins.core.Wallet;
import org.educoins.core.utils.ByteArray;
import org.educoins.core.utils.Hashable;
import org.educoins.core.utils.Sha256Hash;

public class Transaction implements Hashable {

	protected int version;

	protected List<Input> inputs;

	protected List<Output> outputs;

	protected List<Approval> approvals;

	private Sha256Hash approvedTransaction;

	public Transaction() {
		this.inputs = new ArrayList<>();
		this.outputs = new ArrayList<>();
		this.approvals = new ArrayList<>();

		this.approvedTransaction = null;
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getInputsCount() {
		return this.inputs.size();
	}

	public List<Input> getInputs() {
		if (this.inputs != null) {
			return new ArrayList<Input>(this.inputs);
		}
		return null;
	}

	public int getAmount(String ownPublicKey){
		int amount = 0;
		for (Output o : outputs){
			if (!Wallet.getPublicKeys().contains(o.getLockingScript()))
			{
				amount += o.getAmount();
			}
		}
		return amount;
	}

	public void setInputs(List<Input> inputs) {
		this.inputs = inputs;
	}

	public void signInputs() {
		for (Input in : inputs) {
			// TODO Change unterlying methods so that it's not necessary to call toString on hash
			String signature = Wallet.getSignature(in.getUnlockingScript(), this.hash().toString());
			in.setSignature(signature);
		}
	}

	public void signApprovals(String holderSignature) {
		for (Approval app : approvals) {
			app.setHolderSignature(holderSignature);
		}
	}

	public void addInput(Input input) {
		if (this.inputs == null) {
			this.inputs = new ArrayList<>();
		}
		this.inputs.add(input);
	}

	public void addInputs(Collection<Input> inputs) {
		if (this.inputs == null) {
			this.inputs = new ArrayList<Input>();
		}
		this.inputs.addAll(inputs);
	}

	public int getOutputsCount() {
		return this.outputs.size();
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
	}

	public void addOutput(Output output) {
		if (this.outputs == null) {
			this.outputs = new ArrayList<>();
		}
		this.outputs.add(output);
	}

	public void addOutputs(Collection<Output> outputs) {
		if (this.outputs == null) {
			this.outputs = new ArrayList<>();
		}
		this.outputs.addAll(outputs);
	}

	public int getApprovalsCount() {
		return this.approvals.size();
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
	}

	public void addApproval(Approval approval) {
		if (this.approvals == null) {
			this.approvals = new ArrayList<>();
		}
		this.approvals.add(approval);
	}

	public void addApprovals(Collection<Approval> approvals) {
		if (this.approvals == null) {
			this.approvals = new ArrayList<>();
		}
		this.approvals.addAll(approvals);
	}

	public Sha256Hash getApprovedTransaction() {
		return approvedTransaction;
	}

	public void setApprovedTransaction(Sha256Hash approvedTransaction) {
		this.approvedTransaction = approvedTransaction;
	}

	public ETransaction whichTransaction() {
		// Coinbase:
		// inputs = 0; outputs > 0; approvals = 0;
		// Regular:
		// inputs > 0; outputs > 0; approvals = 0;
		// Approval:
		// inputs > 0; outputs >= 0; approvals > 0

		// Check for transaction type.
		if (this.approvedTransaction == null) {
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
					&& (this.getOutputs() != null && this.getOutputs().size() > 0) && this.getApprovals() != null
					&& this.getApprovals().size() > 0) {
				return ETransaction.APPROVED;
			}
		} else {
			if (inputs != null && inputs.size() != 0) {
				return ETransaction.REVOKE;
			}
		}
		return null;
	}

	@Override
	public Sha256Hash hash() {
		return Transaction.hash(this);
	}

	private static Sha256Hash hash(Transaction transaction) {

		byte[] input;
		byte[] output;
		byte[] approved;
		byte[] toBeHashed = null;
		// Check for transaction type.
		switch (transaction.whichTransaction()) {
		case APPROVED:
			input = getByteArrayInput(transaction);
			approved = getByteArrayApproved(transaction);
			toBeHashed = ByteArray.concatByteArrays(input, approved);
			break;
		case COINBASE:
			toBeHashed = getByteArrayOutput(transaction);
			break;
		case REGULAR:
			input = getByteArrayInput(transaction);
			output = getByteArrayOutput(transaction);
			toBeHashed = ByteArray.concatByteArrays(input, output);
			break;
		case REVOKE:
			toBeHashed = transaction.approvedTransaction.getBytes();
			break;
		}
		// hash concatenated header fields and return
		return Sha256Hash.createDouble(toBeHashed);

	}

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

	private static byte[] getByteArrayApproved(Transaction transaction) {
		int length = 0;
		for (Approval approval : transaction.getApprovals()) {
			length += approval.getConcatedApproval().length;
		}
		byte[] byteArray = new byte[length];
		int index = 0;
		for (Approval approval : transaction.getApprovals()) {

			System.arraycopy(approval.getConcatedApproval(), 0, byteArray, index,
					approval.getConcatedApproval().length);
			index += approval.getConcatedApproval().length;
		}
		return byteArray;
	}

	public enum ETransaction {
		APPROVED, COINBASE, REGULAR, REVOKE,
	}

	@Override
	public String toString() {
		return "Transaction [version=" + version + ", inputsCount=" + inputs.size() + ", inputs=" + inputs
				+ ", outputsCount=" + outputs.size() + ", outputs=" + outputs + ", approvalsCount=" + approvals.size()
				+ ", approvals=" + approvals + ", approvedTransaction=" + approvedTransaction + "]";
	}

	/**
	 * Needs to be overwritten. Cannot be abstract, otherwise the database will
	 * not get block from the BC which has transaction in it.
	 */
	public Transaction create() {
		return null;
	}
}
