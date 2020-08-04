package com.github.ontio.ontid.anonymous;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.ontid.CredentialStatus;
import com.github.ontio.ontid.CredentialStatusType;
import com.github.ontio.ontid.OntId2;
import com.github.ontio.ontid.ProofPurpose;
import com.github.ontio.ontid.anonymous.impl.*;
import com.github.ontio.ontid.anonymous.param.PresentationParam;
import com.github.ontio.ontid.anonymous.param.RequestParm;
import com.github.ontio.sdk.wallet.Identity;
import com.github.ontio.smartcontract.neovm.AnonymousRecord;
import com.sun.jna.Pointer;
import junit.framework.TestCase;

import java.util.Date;

public class AnonymousCredentialTest extends TestCase {

    static long gasLimit = 2000000;
    static long gasPrice = 2500;
    static String password = "passwordtest";

    public static final int ISSUER_PUBLIC_SIZE = 928;
    public static final int ACC_VALUE_SIZE = 96;
    public static final int ACC_WITNESS_SIZE = 96 * 3 + 48 + 8;
    public static final int MASTER_SECRET_SIZE = 32;
    public static final int NONCE_SIZE = 16;

    public void testCreateCredential() throws Exception {

        OntSdk ontSdk = getOntSdk();
        // set credential contract address
        ontSdk.neovm().credentialRecord().setContractAddress("52df370680de17bc5d4262c446f102a0ee0d6312");
        Library library = new LibraryImpl();

        Identity issuerIdentity = ontSdk.getWalletMgr().getWallet().getIdentity("did:ont:AJ4C9aTYxTGUhEpaZdPjFSqCqzMCqJDRUd");
        Account issuerSigner = ontSdk.getWalletMgr().getAccount(issuerIdentity.ontid, password, issuerIdentity.controls.get(0).getSalt());
        Identity ownerIdentity = ontSdk.getWalletMgr().getWallet().getIdentity("did:ont:AVe4zVZzteo6HoLpdBwpKNtDXLjJBzB9fv");
        Account ownerSigner = ontSdk.getWalletMgr().getAccount(ownerIdentity.ontid, password, ownerIdentity.controls.get(0).getSalt());

        AnonymousRecord anonymousRecord = new AnonymousRecord(ontSdk);

        OntId2 issuerId = new OntId2(issuerIdentity.ontid, issuerSigner, library, anonymousRecord, ontSdk.nativevm().ontId());
        OntId2 ownerId = new OntId2(ownerIdentity.ontid, ownerSigner, library, anonymousRecord, ontSdk.nativevm().ontId());

        System.out.println("-----------------------------------------------------------------------issuer generates key----------------------------------------------------------------------");
        Pointer issuer = library.new_issuer();
        byte[] issuerPublic = new byte[ISSUER_PUBLIC_SIZE];
        library.publish_issuer(issuerPublic, issuer);
        System.out.println(toHexString(issuerPublic));

        System.out.println("-----------------------------------------------------------------------owner generates key-----------------------------------------------------------------------");
        byte[] masterSecret = new byte[MASTER_SECRET_SIZE];
        library.new_master_secret(masterSecret);


        System.out.println("-----------------------------------------------------------------------issue credential1-----------------------------------------------------------------------");
        String attr = "{\"did\":\"did:ont:AVe4zVZzteo6HoLpdBwpKNtDXLjJBzB9fv\",\"name\":\"Alice\",\"age\":22,\"sex\":\"female\"}";
        byte[] nonce = new byte[NONCE_SIZE];
        RequestParm requestRaw = new RequestParm(masterSecret, attr, nonce, issuerPublic);
        Request request = new Request(library, requestRaw);
        String requestJSONStr = JSON.toJSONString(request, SerializerFeature.MapSortField);
        System.out.println(requestJSONStr);

        String[] type = new String[]{"RelationshipCredential"};

        Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24);

        // (String issuerId, Pointer issuer, Request request, String[] context, String[] type,
        //                                             CredentialStatusType statusType, String verificationMethod, ProofPurpose purpose, Date expiration)
        FormatCredential formatCredential = issuerId.createCredential(issuerIdentity.ontid, issuer, request, null, type, CredentialStatusType.SignatureContract, "attest", ProofPurpose.assertionMethod, null);
        String formatCredentialJSONStr = JSON.toJSONString(formatCredential, SerializerFeature.MapSortField);
        System.out.println(formatCredentialJSONStr);


        System.out.println("-----------------------------------------------------------------------show presentation-----------------------------------------------------------------------");
        Credential credential1 = formatCredential.extractCredential();
        byte[] acc = new byte[ACC_VALUE_SIZE];
        library.get_accumulator_value(acc, issuer);
        String attrPresent = "{\"did\":{\"value\":\"did:ont:AVe4zVZzteo6HoLpdBwpKNtDXLjJBzB9fv\",\"state\":\"disclose\"},\"name\":{\"value\":\"Alice\",\"state\":\"disclose\"},\"age\": {\"value\":22,\"state\":\"range\",\"range\":[18, 100]},\"sex\":{\"value\":\"female\",\"state\":\"hide\"}}";
        PresentationParam presentationParam = new PresentationParam(credential1, masterSecret, attrPresent, issuerPublic, acc, nonce);
        // PresentationParam presentationParam, String issuerId, String holder, String[] context,
        //                                                 String[] type, String verificationMethod, ProofPurpose purpose
        FormatPresentation formatPresentation = ownerId.createPresentation(presentationParam, issuerIdentity.ontid, ownerIdentity.ontid, null, type, "attest", ProofPurpose.assertionMethod);

        String formatPresentationJsonStr = JSON.toJSONString(formatPresentation, SerializerFeature.MapSortField);
        System.out.println(formatPresentationJsonStr);

        boolean ok = formatPresentation.verifyPresentation(library, issuerPublic, acc, nonce);
        System.out.println(ok ? "success" : "fail");

        System.out.println(toHexString(acc));

        System.out.println("-----------------------------------------------------------------------issue credential2-----------------------------------------------------------------------");
        byte[] masterSecret2 = new byte[MASTER_SECRET_SIZE];
        library.new_master_secret(masterSecret2);
        String attr2 = "{\"name\":\"Bob\",\"age\":22,\"sex\":\"male\"}";
        byte[] nonce2 = new byte[NONCE_SIZE];
        RequestParm requestRaw2 = new RequestParm(masterSecret2, attr2, nonce2, issuerPublic);
        Request request2 = new Request(library, requestRaw2);

        Date expiration2 = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
        FormatCredential formatCredential2 = issuerId.createCredential(issuerIdentity.ontid, issuer, request2, null, type, CredentialStatusType.SignatureContract, "attest", ProofPurpose.assertionMethod, expiration2);
        String formatCredentialJSONStr2 = JSON.toJSONString(formatCredential2, SerializerFeature.MapSortField);
        System.out.println(formatCredentialJSONStr2);

        byte[] acc2 = new byte[ACC_VALUE_SIZE];
        library.get_accumulator_value(acc2, issuer);
        System.out.println(toHexString(acc2));

        System.out.println("-----------------------------------------------------------------------update credential1's accumulator-----------------------------------------------------------------------");
        Credential credential = formatCredential.extractCredential();
        byte[] witness = credential.extractWitness(library);
        witness = credential.updateIssuerWitness(library, witness, issuer, 2L, (byte) 1);
        System.out.println(toHexString(witness));
        acc = new byte[ACC_VALUE_SIZE];
        library.get_accumulator_value(acc, issuer);
        credential.updateCredentialWitness(library, issuerPublic, witness, acc);
        formatCredential.updateFromatCredential(credential);
        System.out.println(toHexString(acc));

        System.out.println("-----------------------------------------------------------------------new credential1 -----------------------------------------------------------------------");
        String formatCredentialJSONStr3 = JSON.toJSONString(formatCredential, SerializerFeature.MapSortField);
        System.out.println(formatCredentialJSONStr3);

        System.out.println("-----------------------------------------------------------------------make credential1's presentation again-----------------------------------------------------------------------");
        Credential credential2 = formatCredential.extractCredential();
        String attrPresent2 = "{\"did\":{\"value\":\"did:ont:AVe4zVZzteo6HoLpdBwpKNtDXLjJBzB9fv\",\"state\":\"disclose\"},\"name\":{\"value\":\"Alice\",\"state\":\"disclose\"},\"age\": {\"value\":22,\"state\":\"range\",\"range\":[18, 100]},\"sex\":{\"value\":\"female\",\"state\":\"hide\"}}";
        PresentationParam presentationParam2 = new PresentationParam(credential2, masterSecret, attrPresent2, issuerPublic, acc, nonce);
        //FormatPresentation createPresentation(PresentationParam presentationParam, String issuerId, String holder, String[] context,
        //                                                 String[] type, String verificationMethod, ProofPurpose purpose)
        FormatPresentation formatPresentation2 = ownerId.createPresentation(presentationParam2, issuerIdentity.ontid, ownerIdentity.ontid, null, type, "attest", ProofPurpose.assertionMethod);

        String formatPresentationJsonStr2 = JSON.toJSONString(formatPresentation2, SerializerFeature.MapSortField);
        System.out.println(formatPresentationJsonStr2);

        ok = formatPresentation2.verifyPresentation(library, issuerPublic, acc, nonce);
        System.out.println(ok ? "success" : "fail");
        System.out.println(toHexString(acc));


        System.out.println("-----------------------------------------------------------------------make credential2's presentation again-----------------------------------------------------------------------");
        String attrPresent3 = "{\"name\":{\"value\":\"Bob\",\"state\":\"disclose\"},\"age\": {\"value\":22,\"state\":\"range\",\"range\":[18, 100]},\"sex\":{\"value\":\"male\",\"state\":\"hide\"}}";
        Credential credential6 = formatCredential2.extractCredential();
        PresentationParam presentationParam3 = new PresentationParam(credential6, masterSecret2, attrPresent3, issuerPublic, acc, nonce);
        FormatPresentation formatPresentation3 = ownerId.createPresentation(presentationParam3, issuerIdentity.ontid, ownerIdentity.ontid, null, type, "attest", ProofPurpose.assertionMethod);

        String formatPresentationJsonStr3 = JSON.toJSONString(formatPresentation3, SerializerFeature.MapSortField);
        System.out.println(formatPresentationJsonStr3);

        ok = formatPresentation3.verifyPresentation(library, issuerPublic, acc, nonce);
        System.out.println(ok ? "success" : "fail");
        System.out.println(toHexString(acc));


        System.out.println("-----------------------------------------------------------------------revoke credential1-----------------------------------------------------------------------");
        Credential credential3 = formatCredential.extractCredential();
        ok = credential3.revokeCredential(library, issuer, 1L);
        System.out.println(ok ? "success to revoke credential" : "fail to revoke credential");
        System.out.println(toHexString(acc));

        System.out.println("-----------------------------------------------------------------------update credential2's accumulator-----------------------------------------------------------------------");
        Credential credential4 = formatCredential2.extractCredential();
        witness = credential4.extractWitness(library);
        witness = credential4.updateIssuerWitness(library, witness, issuer, 1L, (byte) 0);
        acc = new byte[ACC_VALUE_SIZE];
        library.get_accumulator_value(acc, issuer);
        credential4.updateCredentialWitness(library, issuerPublic, witness, acc);
        formatCredential2.updateFromatCredential(credential4);
        System.out.println(toHexString(acc));

        System.out.println("-----------------------------------------------------------------------make credential2's presentation-----------------------------------------------------------------------");
        String attrPresent4 = "{\"name\":{\"value\":\"Bob\",\"state\":\"disclose\"},\"age\": {\"value\":22,\"state\":\"range\",\"range\":[18, 100]},\"sex\":{\"value\":\"male\",\"state\":\"hide\"}}";
        Credential credential7 = formatCredential2.extractCredential();
        PresentationParam presentationParam4 = new PresentationParam(credential7, masterSecret2, attrPresent4, issuerPublic, acc, nonce);
        FormatPresentation formatPresentation4 = ownerId.createPresentation(presentationParam4, issuerIdentity.ontid, ownerIdentity.ontid, null, type, "attest", ProofPurpose.assertionMethod);

        String formatPresentationJsonStr4 = JSON.toJSONString(formatPresentation4, SerializerFeature.MapSortField);
        System.out.println(formatPresentationJsonStr4);

        ok = formatPresentation4.verifyPresentation(library, issuerPublic, acc, nonce);
        System.out.println(ok ? "success" : "fail");
        System.out.println(toHexString(acc));
    }

    private static final char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * @param bytes
     * @return
     */
    public static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        int num;
        for (byte b : bytes) {
            num = b < 0 ? 256 + b : b;
            sb.append(HEX_CHAR[num / 16]).append(HEX_CHAR[num % 16]);
        }
        return sb.toString();
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
