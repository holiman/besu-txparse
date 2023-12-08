package org.hyperledger.besu;

import org.apache.tuweni.bytes.Bytes;
import org.hyperledger.besu.crypto.SignatureAlgorithmFactory;
import org.hyperledger.besu.ethereum.core.encoding.EncodingContext;
import org.hyperledger.besu.ethereum.core.encoding.TransactionDecoder;

import com.gitlab.javafuzz.core.AbstractFuzzTarget;
import java.math.BigInteger;

public class FuzzTx extends AbstractFuzzTarget {

    static final BigInteger halfCurveOrder = SignatureAlgorithmFactory.getInstance().getHalfCurveOrder();
    static final BigInteger chainId = new BigInteger("1", 10);

    public void fuzz(byte[] data) {
        try {
            Bytes tx = Bytes.wrap(data);
            var transaction = TransactionDecoder.decodeOpaqueBytes(tx, EncodingContext.BLOCK_BODY);

            // https://github.com/hyperledger/besu/blob/5fe49c60b30fe2954c7967e8475c3b3e9afecf35/ethereum/core/src/main/java/org/hyperledger/besu/ethereum/mainnet/MainnetTransactionValidator.java#L252
            if (transaction.getChainId().isPresent() && !transaction.getChainId().get().equals(chainId) ){
                throw new Exception("wrong chain id");
            }

            // https://github.com/hyperledger/besu/blob/5fe49c60b30fe2954c7967e8475c3b3e9afecf35/ethereum/core/src/main/java/org/hyperledger/besu/ethereum/mainnet/MainnetTransactionValidator.java#L270
            if (transaction.getS().compareTo(halfCurveOrder) > 0) {
                throw new Exception("signature s out of range");
            }
            transaction.getSender();
        } catch (Throwable t) {
            // ignore
        }
    }
}
