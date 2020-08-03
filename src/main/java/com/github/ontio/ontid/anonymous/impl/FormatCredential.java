package com.github.ontio.ontid.anonymous.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.ontio.sdk.exception.SDKException;

@JSONType(orders = {"@context", "issuer", "holder", "issuanceDate", "expirationDate", "credentialSubject", "proof"})
public class FormatCredential {
    @JSONField(name = "@context")
    public String[] context;
    public String issuer;
    public String holder;
    public String issuanceDate;
    public String expirationDate;
    @JSONField(jsonDirect = true)
    public String credentialSubject;
    @JSONField(jsonDirect = true)
    public String proof;

    public FormatCredential() {
    }

    /**
     * @param credential
     * @param context
     * @param proof
     */
    public FormatCredential(Credential credential, String[] context, String issuer, String holder, String issuanceDate,
                            String expirationDate, String credentialSubject, AnonymousProof proof) {
        this.context = context;
        this.issuer = issuer;
        this.holder = holder;
        this.issuanceDate = issuanceDate;
        this.expirationDate = expirationDate;
        this.credentialSubject = credentialSubject;
        proof.signature = JSON.toJSONString(credential, SerializerFeature.MapSortField);
        this.proof = JSON.toJSONString(proof, SerializerFeature.MapSortField);
    }

    public Credential extractCredential() throws SDKException {
        if (this.proof == null) {
            throw new SDKException("proof is null");
        }
        AnonymousProof proof = JSONObject.parseObject(this.proof, AnonymousProof.class, Feature.OrderedField);
        Credential credential = JSONObject.parseObject(proof.signature, Credential.class);
        return credential;
    }

    public void updateFromatCredential(Credential credential) throws SDKException {
        if (this.proof == null) {
            throw new SDKException("proof is null");
        }
        AnonymousProof proof = JSONObject.parseObject(this.proof, AnonymousProof.class, Feature.OrderedField);
        proof.signature = JSON.toJSONString(credential, SerializerFeature.MapSortField);
        this.proof = JSON.toJSONString(proof, SerializerFeature.MapSortField);
    }

}
