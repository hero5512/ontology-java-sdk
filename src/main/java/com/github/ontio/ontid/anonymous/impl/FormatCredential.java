package com.github.ontio.ontid.anonymous.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.ontio.ontid.CredentialStatus;
import com.github.ontio.sdk.exception.SDKException;

import java.util.UUID;

@JSONType(orders = {"@context", "id", "type", "issuer", "issuanceDate", "expirationDate", "credentialSubject", "credentialStatus", "proof"})
public class FormatCredential {
    @JSONField(name = "@context")
    public String[] context;
    public String id;
    public String[] type;
    public String issuer;
    public String issuanceDate;
    public String expirationDate;
    @JSONField(jsonDirect = true)
    public String credentialSubject;
    public CredentialStatus credentialStatus;
    @JSONField(jsonDirect = true)
    public String proof;

    public FormatCredential() {
    }

    public FormatCredential(Credential credential, String[] context, String[] type, String issuer, String issuanceDate,
                            String expirationDate, String credentialSubject, CredentialStatus credentialStatus, AnonymousProof proof) {
        this.context = context;
        this.id = "urn:uuid:" + UUID.randomUUID().toString();
        this.type = type;
        this.issuer = issuer;
        this.issuanceDate = issuanceDate;
        this.expirationDate = expirationDate;
        this.credentialSubject = credentialSubject;
        this.credentialStatus = credentialStatus;
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
