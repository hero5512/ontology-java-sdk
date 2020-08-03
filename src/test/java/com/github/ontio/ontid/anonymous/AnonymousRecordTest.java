package com.github.ontio.ontid.anonymous;

import com.github.ontio.OntSdk;
import com.github.ontio.common.Helper;
import com.github.ontio.sdk.wallet.Identity;
import com.github.ontio.smartcontract.neovm.AnonymousRecord;
import com.github.ontio.smartcontract.neovm.anonymous.Accumulator;
import junit.framework.TestCase;

import java.io.IOException;

public class AnonymousRecordTest extends TestCase {

    static String password = "passwordtest";

    public static final int ISSUER_PUBLIC_SIZE = 928;
    public static final int ACC_VALUE_SIZE = 96;
    public static final int ACC_WITNESS_SIZE = 96 * 3 + 48 + 8;
    public static final int MASTER_SECRET_SIZE = 32;
    public static final int NONCE_SIZE = 16;

    public void test() throws Exception {
        OntSdk ontSdk = getOntSdk();

        com.github.ontio.account.Account payer = ontSdk.getWalletMgr().getAccount(
                "AUNB7xQuBVg8hnRfVz9pyAuZQUqPBiDxDF", password);

        Identity issuerIdentity = ontSdk.getWalletMgr().getWallet().getIdentity("did:ont:AJ4C9aTYxTGUhEpaZdPjFSqCqzMCqJDRUd");

        AnonymousRecord anonymousRecord = ontSdk.neovm().anonymousRecord();
        anonymousRecord.setContractAddress("e97c53d050e1f2cb6ef5f447a50c22cea843292a");

        byte[] issuerPublicKey = Helper.hexToBytes("a2252d9c14eb6296a78b8b385340bdd2326f7970cd81cb0ec5348091beb75632880017d216bd2cfab1ba0a48a3c4d95a05dca6b73de13462bda8d80f46e6471cc39d01471a87f89686d669230d61ee75369a8c77844a564e4e69138fc3e01025bc33dc723ecf20675280b29a73227bc2940d42642b671a43be84836658ac2b8ba0409b569b0959890f759b13883efccebbb2d70a50c98038132a8c34de5ca5dd0a2e63cdd4645f1c75ba1470b7c102bc307bacaae708cbe3dc4fca1d52ccb282054362fdc32cb691f250c8e37c2ca0dd0912a4e4e459bbe861f8a7fc46b19a893794b056a434618138a0de6860da3db1f9438164df4114a6614c610b30a9bc7a1556a178828ca8b805e52a9a90773db3dcb7235bdce6d2435f9d6dc7fb6c7a03c36a0f3b1effbe006c2e7df48f522fc904de02ee5e15bae60b452c0c11010aeb165d1ebcf37fa686a16c91bb9ee5a6c568f17bde6928b25c3589d2421176d6af17cce76f05a90eec7ed41f875a4fa4445d324daa1c799362c8c824b161f51c7364593c178995b09812a5f15a9e07b68e0ba70ecfb17c5f0988bc170e87b169b8673985c0612085fd72ab907150f164a1783c13eb1700d0f5a9dbfea1bebf1bc00490c19fcfbc20a1aeec863244a3d5de5a500dd0e3bfc9a49e46546df77d8c02ced5afcf8b09f2327dea20057d4a22650fb7cc9a542d7ff4f3754a6635bc6e43649869d01add4c864078ee62f72f49a2836f13a95abd63a4afdd59c9736d876017ff117054eaeca6d43f326b94ba43ddc9802a9d8e80c9cbd6d3412f8c6835e8cfd189d5a136cac8cd5c8c272585059605dac0b81d3413163786b63489c4a67e8e0c8a925e7a6e80092094a00cd79a28d402192c13b23e9a828ac413a8e7c3090cfc073d3c24ee7960e89e0e3ced8d62cd84c3a9fa8cdb2aa837dd5fa9ad0dfc2bc8c2c24f044ed91de60fd710a8cea5070528e088a1d5be3291d94d699b764d44d1a30c0dda378a957345e674c9a4885a115519d094a6d1df63ba3fb638b4cb840e4608c71c4cb13c6127ed9996dc0ba4184f105fb9ef836200e151b46ae316252f671b3a2d56f7619dd0dc0410c1d1b987bb76db06cb7aaa74dd04dc6a1cf6f49e1da9f54a5da531c01f57f83c7ecb8e7e7568a14ae288b9c1b60ec4c2337193f8b4e41bb7bfc8877beec8696ef2c8d1ebc0276b68ecf980368dd1a5131ff904a2b6ff2e4f536db381f4556a42a6fe29b6e971793ae19ad59c956b6913c3ae7203e8c20dbe9d5a76670e631ffad447f48fe9b24be6d806303ad12d56e5f7cd");

        String txHash = anonymousRecord.bind(issuerIdentity.ontid, password, issuerIdentity.controls.get(0).getSalt(), 1, issuerPublicKey, payer, ontSdk.DEFAULT_GAS_LIMIT, 2500);
        System.out.println("bind tx hash: " + txHash);

        byte[] accumulator = Helper.hexToBytes("9831cf29e03522ac557bcea14e97dc6aeeba111876cf69115923cb676b584aab3889120bbccc55f1d06946af048c7fbd063c134f2b5870885311317ded469025aa6c85a4a18b14e4b112c1701bea1bade765075fc28847aa4c835b430b6d9b86");
        byte[] witness = Helper.hexToBytes("0000000000000001928b17f348664696c84465b5e779cbf778ddfe73afa8944b3f9b33cd3997e91e96aa69836905e16bf3dd13d1222097798545188418de4cb188e1f68cb74c41b887e419c2ec0ca83a1e8815a252a0797829a547ba9a334a7e54e4c5c795dc9d8319000d9b0b0e3ee1a4ed59f17190156fd401d035cbcdae5b2de9c3156c1a3655e3971756c35d9e155e5d10244aa84c70868b037966df00b22cc4bb1929ba075e9be25ca8949d4ec6601a1da0710dd71b48306be8081544259bc9e2e45e42f43a1465ac5a37d0f926d17e89c297cdb97585671859af01e45875d36f21eb1f5151a72b6ddc4747d8e64f788717e03cb2d493a7c1b6a1c09cb8a5c6987f0d2e0b346eb95c69e96dfb414b636fd7c004ef8ca24cfc7a0e1e28ecb4cb9fc2f865e767021567789d012e8d2526065e8bd7a9f0f41783542c9dc4790c9dd437216729e113cfd767db02a3bab895228b15b73415");
        String txHash2 = anonymousRecord.updateAccumulator(issuerIdentity.ontid, password, issuerIdentity.controls.get(0).getSalt(), 1, 1L, accumulator, witness, payer, ontSdk.DEFAULT_GAS_LIMIT, 2500);
        System.out.println("update accumulator tx hash: " + txHash2);

        Accumulator acc = anonymousRecord.getAccumulator(issuerIdentity.ontid, 1L, ontSdk.DEFAULT_GAS_LIMIT, 2500);
        System.out.println("get accumulator value: " + Helper.toHexString(acc.accumulator));
        System.out.println("get witness value: " + Helper.toHexString(acc.witness));

        String issuerId = anonymousRecord.getIssuerId(issuerPublicKey, ontSdk.DEFAULT_GAS_LIMIT, 2500);
        System.out.println("issuer ontid is: " + issuerId);

        byte[] pubkey = anonymousRecord.getPublicKey(issuerIdentity.ontid, ontSdk.DEFAULT_GAS_LIMIT, 2500);
        System.out.println("issuer public key is: " + Helper.toHexString(pubkey));

        String txHash3 = anonymousRecord.revoke(issuerIdentity.ontid, password, issuerIdentity.controls.get(0).getSalt(), 1, 1L, payer, ontSdk.DEFAULT_GAS_LIMIT, 2500);
        System.out.println("revoke tx Hash: " + txHash3);

        acc = anonymousRecord.getAccumulator(issuerIdentity.ontid, 1L, ontSdk.DEFAULT_GAS_LIMIT, 2500);
        System.out.println("get accumulator value: " + Helper.toHexString(acc.accumulator));
        System.out.println("get witness value: " + Helper.toHexString(acc.witness));

    }


    public static OntSdk getOntSdk() throws Exception {
        String ip = "http://polaris1.ont.io";
//        String ip = "http://127.0.0.1";
//        String ip = "http://54.222.182.88;
//        String ip = "http://101.132.193.149";
        String restUrl = ip + ":" + "20334";
        String rpcUrl = ip + ":" + "20336";
        String wsUrl = ip + ":" + "20335";

        OntSdk wm = OntSdk.getInstance();
        wm.setRpc(rpcUrl);
        wm.setRestful(restUrl);
        wm.setDefaultConnect(wm.getRestful());

        wm.openWalletFile("wallet.json");

        return wm;
    }
}

