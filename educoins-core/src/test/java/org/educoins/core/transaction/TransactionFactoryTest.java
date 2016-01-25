package org.educoins.core.transaction;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by dacki on 19.01.16.
 */
public class TransactionFactoryTest {

    ITransactionFactory factory = new TransactionFactory();
    private List<Output> previousOutputs = new ArrayList<>();

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
        previousOutputs.clear();
    }

    @Test
    public void testGenerateCoinbasedTransaction() throws Exception {
        int amount = 1;
        String receiver = "1010101";
        Transaction tx = factory.generateCoinbasedTransaction(amount, receiver);

        assertEquals(1, tx.getOutputsCount());
        assertEquals(amount, tx.getAmount(receiver));
    }

    @Ignore
    @Test
    public void testGenerateRevokeTransaction() throws Exception {
        throw new RuntimeException("not implemented yet.");

    }

    @Ignore
    @Test
    public void testGenerateApprovedTransaction() throws Exception {
        throw new RuntimeException("not implemented yet.");

    }

    @Test
    public void testGenerateRegularTransaction() throws Exception {
        String sender = "000000000000";
        String receiver = "80808080";
        int accountBalanceSender = 100;
        int sendAmount = 11;

        // Create old Output for reference
        previousOutputs.add(new Output(accountBalanceSender, sender));
        Transaction tx = factory.generateRegularTransaction(previousOutputs, sendAmount, receiver);

        assertNotNull(tx);
        assertEquals(1, tx.getInputsCount());
        // 2 Outputs, spend amount and change
        assertEquals(2, tx.getOutputsCount());
        assertEquals(sendAmount, tx.getAmount(receiver));
        assertEquals(sendAmount, tx.getAmount(sender));
        assertTotalBalance(tx, accountBalanceSender);
    }

    private void assertTotalBalance(Transaction tx, int totalBalance) {
        int inputs = 0;
        int outputs = 0;
        for (Input input : tx.getInputs()) {
            inputs += input.getAmount();
        }
        for (Output output : tx.getOutputs()) {
            outputs += output.getAmount();
        }
        int previousOutputBalance = totalBalance;
        assertEquals(previousOutputBalance, inputs);
        assertEquals(previousOutputBalance, outputs);
    }

    @Ignore
    @Test
    public void testCreateInputs() throws Exception {
        throw new RuntimeException("not implemented yet.");

    }

    @Ignore
    @Test
    public void testCreateOutputs() throws Exception {
        throw new RuntimeException("not implemented yet.");

    }
}