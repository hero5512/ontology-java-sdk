package com.github.ontio.smartcontract.neovm;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.common.Helper;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.smartcontract.neovm.abi.AbiFunction;
import com.github.ontio.smartcontract.neovm.abi.BuildParams;
import com.github.ontio.smartcontract.neovm.abi.Parameter;
import com.github.ontio.smartcontract.neovm.anonymous.Accumulator;


public class AnonymousRecord {
    private OntSdk sdk;
    private String contractAddress = "8bf29d2df5e2d561edf38ca341bac896550233c1";

    public static final int ISSUER_PUBLIC_SIZE = 928;
    public static final int ACC_VALUE_SIZE = 96;
    public static final int ACC_WITNESS_SIZE = 96 * 3 + 48 + 8;

    public AnonymousRecord(OntSdk sdk) {
        this.sdk = sdk;
    }

    public void setContractAddress(String codeHash) {
        this.contractAddress = codeHash.replace("0x", "");
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public String bind(String ownerId, String pwd, byte[] salt, int index, byte[] publicKey, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (ownerId == null || "".equals(ownerId) || pwd == null || "".equals(pwd) || publicKey == null || publicKey.length != ISSUER_PUBLIC_SIZE) {
            throw new SDKException(ErrorCode.ParamError);
        }
        if (gaslimit < 0 || gasprice < 0) {
            throw new SDKException(ErrorCode.ParamErr("gaslimit or gasprice is less than 0"));
        }
        String funcName = "Bind";
        Parameter issuerIdParam = new Parameter("issuerId", Parameter.Type.String, ownerId);
        Parameter indexParam = new Parameter("index", Parameter.Type.Integer, index);
        Parameter publicKeyParam = new Parameter("publicKey", Parameter.Type.ByteArray, publicKey);
        AbiFunction func = new AbiFunction(funcName, issuerIdParam, indexParam, publicKeyParam);
        byte[] params = BuildParams.serializeAbiFunction(func);
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(Helper.reverse(contractAddress), null, params, payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);
        // sign transaction
        sdk.signTx(tx, ownerId, pwd, salt);
        sdk.addSign(tx, payerAcct);
        boolean flag = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (flag) {
            return tx.hash().toString();
        }
        return null;
    }


    public String updateAccumulator(String ownerId, String pwd, byte[] salt, int index, long id, byte[] accumulator, byte[] witness, Account payerAcct, long gaslimit, long gasprice) throws Exception {

        if (ownerId == null || "".equals(ownerId) || pwd == null || "".equals(pwd) || id <= 0 || accumulator == null || accumulator.length != ACC_VALUE_SIZE
                || witness == null || witness.length != ACC_WITNESS_SIZE) {
            throw new SDKException(ErrorCode.ParamError);
        }
        if (gaslimit < 0 || gasprice < 0) {
            throw new SDKException(ErrorCode.ParamErr("gaslimit or gasprice is less than 0"));
        }
        String funcName = "UpdateAccumulator";
        Parameter issuerIdParam = new Parameter("issuerId", Parameter.Type.String, ownerId);
        Parameter indexParam = new Parameter("index", Parameter.Type.Integer, index);
        Parameter idParam = new Parameter("id", Parameter.Type.Integer, id);
        Parameter accumulatorParam = new Parameter("accumulator", Parameter.Type.ByteArray, accumulator);
        Parameter witnessParam = new Parameter("witness", Parameter.Type.ByteArray, witness);
        AbiFunction func = new AbiFunction(funcName, issuerIdParam, indexParam, idParam, accumulatorParam, witnessParam);
        byte[] params = BuildParams.serializeAbiFunction(func);
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(Helper.reverse(contractAddress), null, params, payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);

        sdk.signTx(tx, ownerId, pwd, salt);
        sdk.addSign(tx, payerAcct);
        boolean flag = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (flag) {
            return tx.hash().toString();
        }
        return null;
    }

    public String revoke(String ownerId, String pwd, byte[] salt, int index, long id, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (ownerId == null || "".equals(ownerId) || pwd == null || "".equals(pwd) || id <= 0) {
            throw new SDKException(ErrorCode.ParamError);
        }
        if (gaslimit < 0 || gasprice < 0) {
            throw new SDKException(ErrorCode.ParamErr("gaslimit or gasprice is less than 0"));
        }
        String funcName = "Revoke";
        Parameter issuerIdParam = new Parameter("issuerId", Parameter.Type.String, ownerId);
        Parameter indexParam = new Parameter("index", Parameter.Type.Integer, index);
        Parameter idParam = new Parameter("id", Parameter.Type.Integer, id);
        AbiFunction func = new AbiFunction(funcName, issuerIdParam, indexParam, idParam);
        byte[] params = BuildParams.serializeAbiFunction(func);
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(Helper.reverse(contractAddress), null, params, payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);

        sdk.signTx(tx, ownerId, pwd, salt);
        sdk.addSign(tx, payerAcct);
        boolean flag = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (flag) {
            return tx.hash().toString();
        }
        return null;
    }

    public Accumulator getAccumulator(String ownerId, long id, long gaslimit, long gasprice) throws Exception {
        if (ownerId == null || "".equals(ownerId)) {
            throw new SDKException(ErrorCode.ParamError);
        }
        if (gaslimit < 0 || gasprice < 0) {
            throw new SDKException(ErrorCode.ParamErr("gaslimit or gasprice is less than 0"));
        }
        String funcName = "GetAccumulator";
        Parameter issuerIdParam = new Parameter("issuerId", Parameter.Type.String, ownerId);
        Parameter idParam = new Parameter("id", Parameter.Type.Integer, id);
        AbiFunction func = new AbiFunction(funcName, issuerIdParam, idParam);
        byte[] params = BuildParams.serializeAbiFunction(func);

        Transaction tx = sdk.vm().makeInvokeCodeTransaction(Helper.reverse(contractAddress), null, params, null, gaslimit, gasprice);

        Object obj = sdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
        JSONArray res = ((JSONObject) obj).getJSONArray("Result");
        byte[] accumulator = Helper.hexToBytes((String) res.get(0));
        byte[] witness = Helper.hexToBytes((String) res.get(1));
        return new Accumulator(accumulator, witness);
    }

    public byte[] getPublicKey(String ownerId, long gaslimit, long gasprice) throws Exception {
        if (ownerId == null || "".equals(ownerId)) {
            throw new SDKException(ErrorCode.ParamError);
        }
        if (gaslimit < 0 || gasprice < 0) {
            throw new SDKException(ErrorCode.ParamErr("gaslimit or gasprice is less than 0"));
        }
        String funcName = "GetPublicKey";
        Parameter issuerIdParam = new Parameter("issuerId", Parameter.Type.String, ownerId);
        AbiFunction func = new AbiFunction(funcName, issuerIdParam);
        byte[] params = BuildParams.serializeAbiFunction(func);

        Transaction tx = sdk.vm().makeInvokeCodeTransaction(Helper.reverse(contractAddress), null, params, null, gaslimit, gasprice);

        Object obj = sdk.getConnect().sendRawTransactionPreExec(tx.toHexString());

        byte[] res = Helper.hexToBytes(((JSONObject) obj).getString("Result"));
        return res;
    }

    public String getIssuerId(byte[] publicKey, long gaslimit, long gasprice) throws Exception {
        if (publicKey == null || publicKey.length != ISSUER_PUBLIC_SIZE) {
            throw new SDKException(ErrorCode.ParamError);
        }
        if (gaslimit < 0 || gasprice < 0) {
            throw new SDKException(ErrorCode.ParamErr("gaslimit or gasprice is less than 0"));
        }
        String funcName = "GetIssuerId";
        Parameter issuerIdParam = new Parameter("issuerId", Parameter.Type.ByteArray, publicKey);
        AbiFunction func = new AbiFunction(funcName, issuerIdParam);
        byte[] params = BuildParams.serializeAbiFunction(func);

        Transaction tx = sdk.vm().makeInvokeCodeTransaction(Helper.reverse(contractAddress), null, params, null, gaslimit, gasprice);

        Object obj = sdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
        String res = Helper.hexStringToString(((JSONObject) obj).getString("Result"));
        return res;
    }

}
