package com.github.ontio.ontid.anonymous.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.ontio.ontid.anonymous.Library;
import com.github.ontio.sdk.exception.SDKException;
import com.sun.jna.Pointer;

import static com.github.ontio.ontid.anonymous.impl.LibraryImpl.ACC_WITNESS_SIZE;

@JSONType(orders = {"id", "signature", "witness"})
public class Credential {

    public Long id;
    public String signature;
    public String witness;

    public Credential() {
    }

    public Credential(Long id, String signature, String witness) {
        this.id = id;
        this.signature = signature;
        this.witness = witness;
    }

    /**
     * @param library
     * @param issuer
     * @param request
     * @throws Exception
     */
    public Credential(Library library, Pointer issuer, Request request) throws Exception {
        createCredential(library, issuer, request);
    }

    /**
     * @param library
     * @param issuerStr
     * @param request
     * @throws Exception
     */
    public Credential(Library library, String issuerStr, Request request) throws Exception {
        Pointer issuer = library.deserialize_issuer(issuerStr);
        this.createCredential(library, issuer, request);
    }

    private void createCredential(Library library, Pointer issuer, Request request) throws Exception {
        if (issuer == null || request == null) {
            throw new SDKException("param should not be null");
        }
        String requestJsonStr = JSON.toJSONString(request, SerializerFeature.MapSortField);
        String credentialJsonStr = library.issue_credential(issuer, requestJsonStr);
        Credential credential = JSONObject.parseObject(credentialJsonStr, Credential.class, Feature.OrderedField);
        this.id = credential.id;
        this.signature = credential.signature;
        this.witness = credential.witness;
    }

    public void updateCredentialWitness(Library library, byte[] issuerPublic, byte[] witness, byte[] accumulator) throws Exception {
        String credential = JSON.toJSONString(this, SerializerFeature.MapSortField);
        credential = library.update_credential_witness(credential, issuerPublic, witness, accumulator);
        if (credential == null || "".equals(credential)) {
            throw new SDKException("can not update witness in current credential");
        }
        Credential update = JSONObject.parseObject(credential, Credential.class, Feature.OrderedField);
        this.id = update.id;
        this.signature = update.signature;
        this.witness = update.witness;
    }

    /**
     * @param library
     * @param issuer
     * @param id
     * @throws Exception
     */
    public boolean revokeCredential(Library library, Pointer issuer, Long id) throws Exception {
        int ok = library.revoke_credential(issuer, id);
        return ok == 0;
    }

    /**
     * @param library
     * @param witness
     * @param issuer
     * @param id
     * @param isAdd
     * @return
     * @throws Exception
     */
    public byte[] updateIssuerWitness(Library library, byte[] witness, Pointer issuer, Long id, byte isAdd) throws Exception {
        byte[] update = new byte[ACC_WITNESS_SIZE];
        int ok = library.update_witness(witness, update, issuer, id, isAdd);
        if (ok != 0) {
            throw new SDKException("can not update witness in this issuer");
        }
        return update;
    }

    /**
     * @param library
     * @return
     * @throws Exception
     */
    public byte[] extractWitness(Library library) throws Exception {
        String credential = JSON.toJSONString(this, SerializerFeature.MapSortField);
        byte[] witness = new byte[ACC_WITNESS_SIZE];
        int ok = library.extract_witness(witness, credential);
        if (ok != 0) {
            throw new SDKException("can not extract witness in this credential");
        }
        return witness;
    }

}
