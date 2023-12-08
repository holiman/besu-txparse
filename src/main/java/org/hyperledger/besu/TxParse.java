package org.hyperledger.besu;

import org.hyperledger.besu.crypto.SignatureAlgorithmFactory;
import org.hyperledger.besu.ethereum.core.encoding.EncodingContext;
import org.hyperledger.besu.ethereum.core.encoding.TransactionDecoder;

import java.io.*;
import java.math.BigInteger;
import java.util.Scanner;

import org.apache.tuweni.bytes.Bytes;
import org.junit.Test;

public class TxParse {

  static final BigInteger halfCurveOrder = SignatureAlgorithmFactory.getInstance().getHalfCurveOrder();
  static final BigInteger chainId = new BigInteger("1", 10);

  public static void main(String args[]) {
    doit(System.in);
  }

  public static void doit(InputStream src){
    var sc = new Scanner(src);
    while(sc.hasNext()){
      try {
        var tx = Bytes.fromHexString(sc.nextLine().trim());
        var transaction = TransactionDecoder.decodeOpaqueBytes(tx, EncodingContext.BLOCK_BODY);

        // https://github.com/hyperledger/besu/blob/5fe49c60b30fe2954c7967e8475c3b3e9afecf35/ethereum/core/src/main/java/org/hyperledger/besu/ethereum/mainnet/MainnetTransactionValidator.java#L252
        if (transaction.getChainId().isPresent() && !transaction.getChainId().get().equals(chainId) ){
          throw new Exception("wrong chain id");
        }

        // https://github.com/hyperledger/besu/blob/5fe49c60b30fe2954c7967e8475c3b3e9afecf35/ethereum/core/src/main/java/org/hyperledger/besu/ethereum/mainnet/MainnetTransactionValidator.java#L270
        if (transaction.getS().compareTo(halfCurveOrder) > 0) {
          throw new Exception("signature s out of range");
        }
        System.out.println(transaction.getSender());
      } catch (Throwable t) {
        System.out.println("err: " + t.getMessage());
      }
    }
  }

  @Test
  public void test() {
    var testdata = "test\n" +
            "monkey\n" +
            "0x1\n" +
            "0x112391201239129312392139123 123 12 312 312 3\n" +
            "0xdead\n" +
            "0xf8d2b86702f864010180820fa08284d09411111111111111111111111111111111111111118080c001a0b7dfab36232379bb3d1497a4f91c1966b1f932eae3ade107bf5d723b9cb474e0a06261c359a10f2132f126d250485b90cf20f30340801244a08ef6142ab33d1904b86702f864010280820fa08284d09411111111111111111111111111111111111111118080c080a0d4ec563b6568cd42d998fc4134b36933c6568d01533b5adf08769270243c6c7fa072bf7c21eac6bbeae5143371eef26d5e279637f3bd73482b55979d76d935b1e9\n" +
            "02f864010180820fa08284d09411111111111111111111111111111111111111118080c001a0b7dfab36232379bb3d1497a4f91c1966b1f932eae3ade107bf5d723b9cb474e0a06261c359a10f2132f126d250485b90cf20f30340801244a08ef6142ab33d1904\n" +
            "0x02f864010180820fa08284d09411111111111111111111111111111111111111118080c001a0b7dfab36232379bb3d1497a4f91c1966b1f932eae3ade107bf5d723b9cb474e0a06261c359a10f2132f126d250485b90cf20f30340801244a08ef6142ab33d1904\n" +
            "deadest\n";
    var in = new ByteArrayInputStream(testdata.getBytes());
    doit(in);
  }
}
