package com.github.ontio.ontid.anonymous.impl;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.github.ontio.ontid.ProofPurpose;

@JSONType(orders = {"type", "created", "verificationMethod", "proofPurpose", "signature"})
public class AnonymousProof {
    public String type;
    public String created;
    public String verificationMethod;
    public ProofPurpose proofPurpose;
    @JSONField(jsonDirect = true)
    public String signature;

    public AnonymousProof() {
    }

    public AnonymousProof(String type, String created, String verificationMethod, ProofPurpose proofPurpose, String signature) {
        this.type = type;
        this.created = created;
        this.verificationMethod = verificationMethod;
        this.proofPurpose = proofPurpose;
        this.signature = signature;
    }
}
