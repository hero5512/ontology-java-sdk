package com.github.ontio.smartcontract.nativevm;

import com.alibaba.fastjson.JSONObject;
import com.github.ontio.OntSdk;
import com.github.ontio.OntSdkTest;
import com.github.ontio.account.Account;
import com.github.ontio.common.Common;
import com.github.ontio.common.Helper;
import com.github.ontio.core.ontid.Attribute;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.sdk.info.AccountInfo;
import com.github.ontio.sdk.info.IdentityInfo;
import com.github.ontio.sdk.wallet.Identity;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class NativeOntIdTxTest {
    OntSdk ontSdk;
    String password = "111111";
    Account payer;
    com.github.ontio.account.Account payerAcct;
    Identity identity;
    Account account;
    String walletFile = "NativeOntIdTxTest.json";

    @Before
    public void setUp() throws Exception {
        ontSdk = OntSdk.getInstance();
        ontSdk.setRestful(OntSdkTest.URL);
        ontSdk.setDefaultConnect(ontSdk.getRestful());
        ontSdk.openWalletFile(walletFile);
//        ontSdk.setSignatureScheme(SignatureScheme.SHA256WITHECDSA);
        payer = new Account(SignatureScheme.SHA256WITHECDSA);
        payerAcct = payer;
        identity = ontSdk.getWalletMgr().createIdentity(password);
        account = new com.github.ontio.account.Account(SignatureScheme.SHA256WITHECDSA);
//        ontSdk.nativevm().ontId().sendRegister(identity,password,payerAcct,ontSdk.DEFAULT_GAS_LIMIT,0);
        Thread.sleep(6000);

    }

    @After
    public void removeWallet() {
        File file = new File(walletFile);
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("delete wallet file success");
            }
        }
    }

    @Test
    public void sendRegister() throws Exception {

        Transaction tx = ontSdk.nativevm().ontId().makeRegister(identity.ontid, account.serializePublicKey(), payer.getAddressU160().toBase58(), ontSdk.DEFAULT_GAS_LIMIT, 0);
        ontSdk.addSign(tx, account);
        ontSdk.addSign(tx, payerAcct);
        ontSdk.getConnect().sendRawTransaction(tx);

        Identity identity2 = ontSdk.getWalletMgr().createIdentity(password);

        ontSdk.nativevm().ontId().sendRegister(identity2.ontid, account, payerAcct, ontSdk.DEFAULT_GAS_LIMIT, 0);

        Identity identity3 = ontSdk.getWalletMgr().createIdentity(password);
        Attribute[] attributes = new Attribute[1];
        attributes[0] = new Attribute("key2".getBytes(), "value2".getBytes(), "type2".getBytes());
        ontSdk.nativevm().ontId().sendRegisterWithAttrs(identity3.ontid, attributes, account, payerAcct, ontSdk.DEFAULT_GAS_LIMIT, 0);

        Thread.sleep(6000);
        String ddo = ontSdk.nativevm().ontId().sendGetDDO(identity.ontid);
        Assert.assertTrue(ddo.contains(identity.ontid));

        String dd02 = ontSdk.nativevm().ontId().sendGetDDO(identity3.ontid);
        Assert.assertTrue(dd02.contains("key2"));

        String keystate = ontSdk.nativevm().ontId().sendGetKeyState(identity.ontid, 1);
        Assert.assertNotNull(keystate);

        //merkleproof
        Object merkleproof = ontSdk.nativevm().ontId().getMerkleProof(tx.hash().toHexString());
        boolean b = ontSdk.nativevm().ontId().verifyMerkleProof(JSONObject.toJSONString(merkleproof));
        Assert.assertTrue(b);

        //claim
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("Issuer", identity.ontid);
        map.put("Subject", identity2.ontid);

        Map clmRevMap = new HashMap();
        clmRevMap.put("typ", "AttestContract");
        clmRevMap.put("addr", identity.ontid.replace(Common.didont, ""));

        String claim = ontSdk.nativevm().ontId().createOntIdClaim(identity.ontid, account, "claim:context", map, map, clmRevMap, System.currentTimeMillis() / 1000 + 100000);
        boolean b2 = ontSdk.nativevm().ontId().verifyOntIdClaim(claim);
        Assert.assertTrue(b2);
    }

    @Test
    public void sendAddPubkey() throws Exception {
        IdentityInfo info = ontSdk.getWalletMgr().createIdentityInfo(password);
        IdentityInfo info2 = ontSdk.getWalletMgr().createIdentityInfo(password);
        Transaction tx = ontSdk.nativevm().ontId().makeAddPubKey(identity.ontid, account.serializePublicKey(), Helper.hexToBytes(info.pubkey), "", payer.getAddressU160().toBase58(), ontSdk.DEFAULT_GAS_LIMIT, 0);
        ontSdk.signTx(tx, identity.ontid, password, identity.controls.get(0).getSalt());
        ontSdk.addSign(tx, payerAcct);
        ontSdk.getConnect().sendRawTransaction(tx);

        ontSdk.nativevm().ontId().sendAddPubKey(identity.ontid, account, account.serializePublicKey(), info2.pubkey, payerAcct, ontSdk.DEFAULT_GAS_LIMIT, 0);

        Thread.sleep(6000);
        String ddo = ontSdk.nativevm().ontId().sendGetDDO(identity.ontid);
        Assert.assertTrue(ddo.contains(info.pubkey));
        Assert.assertTrue(ddo.contains(info2.pubkey));

        String publikeys = ontSdk.nativevm().ontId().sendGetPublicKeys(identity.ontid);
        Assert.assertNotNull(publikeys);

        Transaction tx2 = ontSdk.nativevm().ontId().makeRemovePubKey(identity.ontid, account.serializePublicKey(), account.serializePublicKey(), payer.getAddressU160().toBase58(), ontSdk.DEFAULT_GAS_LIMIT, 0);
        ontSdk.signTx(tx2, identity.ontid, password, identity.controls.get(0).getSalt());
        ontSdk.addSign(tx2, payerAcct);
        ontSdk.getConnect().sendRawTransaction(tx2);

        ontSdk.nativevm().ontId().sendRemovePubKey(identity.ontid, account.serializePublicKey(), account, payerAcct, ontSdk.DEFAULT_GAS_LIMIT, 0);
        Thread.sleep(6000);
        String ddo3 = ontSdk.nativevm().ontId().sendGetDDO(identity.ontid);
        Assert.assertFalse(ddo3.contains(info.pubkey));
        Assert.assertFalse(ddo3.contains(info2.pubkey));
    }

    @Test
    public void sendAddAttributes() throws Exception {
        Attribute[] attributes = new Attribute[1];
        attributes[0] = new Attribute("key1".getBytes(), "value1".getBytes(), "String".getBytes());
        Transaction tx = ontSdk.nativevm().ontId().makeAddAttributes(identity.ontid, attributes, account.serializePublicKey(), payer.getAddressU160().toBase58(), ontSdk.DEFAULT_GAS_LIMIT, 0);
        ontSdk.signTx(tx, identity.ontid, password, identity.controls.get(0).getSalt());
        ontSdk.addSign(tx, payerAcct);
        ontSdk.getConnect().sendRawTransaction(tx);

        Attribute[] attributes2 = new Attribute[1];
        attributes2[0] = new Attribute("key99".getBytes(), "value99".getBytes(), "String".getBytes());
        ontSdk.nativevm().ontId().sendAddAttributes(identity.ontid, attributes2, account, payerAcct, ontSdk.DEFAULT_GAS_LIMIT, 0);

        Thread.sleep(6000);
        String ddo = ontSdk.nativevm().ontId().sendGetDDO(identity.ontid);
        Assert.assertTrue(ddo.contains("key1"));
        Assert.assertTrue(ddo.contains("key99"));

        String attribute = ontSdk.nativevm().ontId().sendGetAttributes(identity.ontid);
        Assert.assertTrue(attribute.contains("key1"));

        Transaction tx2 = ontSdk.nativevm().ontId().makeRemoveAttribute(identity.ontid, account.serializePublicKey(), account.serializePublicKey(), payer.getAddressU160().toBase58(), ontSdk.DEFAULT_GAS_LIMIT, 0);
        ontSdk.signTx(tx2, identity.ontid, password, identity.controls.get(0).getSalt());
        ontSdk.addSign(tx2, payerAcct);
        ontSdk.getConnect().sendRawTransaction(tx2);

        ontSdk.nativevm().ontId().sendRemoveAttribute(identity.ontid, account.serializePublicKey(), account, payerAcct, ontSdk.DEFAULT_GAS_LIMIT, 0);
        Thread.sleep(6000);

        String ddo2 = ontSdk.nativevm().ontId().sendGetDDO(identity.ontid);
        Assert.assertFalse(ddo2.contains("key1"));
        Assert.assertFalse(ddo2.contains("key99"));

    }

    @Test
    public void sendAddRecovery() throws Exception {
        Identity identity = ontSdk.getWalletMgr().createIdentity(password);
        ontSdk.nativevm().ontId().sendRegister(identity.ontid, account, payerAcct, ontSdk.DEFAULT_GAS_LIMIT, 0);

        Identity identity2 = ontSdk.getWalletMgr().createIdentity(password);
        ontSdk.nativevm().ontId().sendRegister(identity2.ontid, account, payerAcct, ontSdk.DEFAULT_GAS_LIMIT, 0);

        Thread.sleep(6000);


        Transaction tx = ontSdk.nativevm().ontId().makeAddPubKey(identity.ontid, account.serializePublicKey(), account.serializePublicKey(), account.getAddressU160().toBase58(), payer.getAddressU160().toBase58(), ontSdk.DEFAULT_GAS_LIMIT, 0);
        ontSdk.signTx(tx, identity.ontid, password, identity.controls.get(0).getSalt());
        ontSdk.addSign(tx, payerAcct);
        ontSdk.getConnect().sendRawTransaction(tx);

        Thread.sleep(6000);
        String ddo = ontSdk.nativevm().ontId().sendGetDDO(identity.ontid);

        String ddo2 = ontSdk.nativevm().ontId().sendGetDDO(identity2.ontid);


        AccountInfo info2 = ontSdk.getWalletMgr().createAccountInfo(password);

        String ddo3 = ontSdk.nativevm().ontId().sendGetDDO(identity.ontid);
        String ddo4 = ontSdk.nativevm().ontId().sendGetDDO(identity2.ontid);
        Assert.assertTrue(ddo4.contains(info2.addressBase58));
    }
}