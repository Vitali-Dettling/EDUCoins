
package org.educoins.core;

import org.educoins.core.transaction.*;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class ClientUnitTest {

    private static Client client;
    // Mocks
    private static List<Output> previousOutputs;
    private static List<Transaction> approvedTransactions;
    private static ITransactionFactory transactionFactory;
    private static List<Block> blockBuffer;

    @BeforeClass
    public static void init() {
        previousOutputs = new ArrayList<>();
        approvedTransactions = new ArrayList<>();
        transactionFactory = mock(ITransactionFactory.class);
        blockBuffer = new ArrayList<>();
        client = new Client(previousOutputs, approvedTransactions, transactionFactory, blockBuffer);
    }

    @After
    public void afterTest() {
        previousOutputs.clear();
        approvedTransactions.clear();
        reset(transactionFactory);
        blockBuffer.clear();
    }

    @Ignore
    @Test
    public void t_generateRegularTransaction() {
        String receiver = "affe";
        int amount = 11; // Amount that will be sent to the receiver
        int outputAmount = 21; // Account balance before spending

        // Prepare
        Output output = new Output(outputAmount, receiver);
        previousOutputs.add(output);

        Transaction transaction = new RegularTransaction(previousOutputs, Arrays.asList(
                new Input(amount, output.hash(), receiver)));
        when(transactionFactory.generateRegularTransaction(previousOutputs, amount, receiver))
                .thenReturn(transaction);

        Transaction tx = client.generateRegularTransaction(amount, receiver);

        assertEquals(transaction, tx);
    }

    @Ignore
    @Test
    public void t_generateApprovedTransaction() {
        fail("Not implemented yet.");
    }

    @Ignore
    @Test
    public void t_generateRevokeTransaction() {
        fail("Not implemented yet.");
    }

    @Ignore
    @Test
    public void t_distructOwnOutputs() {
        fail("Not implemented yet.");
    }

    @Ignore
    @Test
    public void t_getEDICoinsAmount() {
        fail("Not implemented yet.");
    }

    @Ignore
    @Test
    public void t_getApprovedCoins() {
        fail("Not implemented yet.");
    }

    @Ignore
    @Test
    public void t_getIntInput() {
        fail("Not implemented yet.");
    }

    @Ignore
    @Test
    public void t_getHexInput() {
        fail("Not implemented yet.");
    }

    @Ignore
    @Test
    public void t_getListOfTransactions() {
        fail("Not implemented yet.");
    }
}