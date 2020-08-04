package com.github.ontio.ontid.anonymous.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.ontio.ontid.anonymous.Library;
import com.github.ontio.sdk.exception.SDKException;

import java.util.UUID;

@JSONType(orders = {"@context", "id", "type", "issuer", "holder", "proof"})
public class FormatPresentation {
    @JSONField(name = "@context")
    public String[] context;
    public String id;
    public String[] type;
    public String issuer;
    public String holder;
    @JSONField(jsonDirect = true)
    public String proof;

    public FormatPresentation() {
    }

    /**
     * @param presentation
     * @param context
     * @param issuer
     * @param holder
     * @param proof
     */
    public FormatPresentation(Presentation presentation, String[] context, String[] type, String issuer, String holder, AnonymousProof proof) {
        this.context = context;
        this.id = "urn:uuid:" + UUID.randomUUID().toString();
        this.type = type;
        this.issuer = issuer;
        this.holder = holder;
        proof.signature = JSON.toJSONString(presentation, SerializerFeature.MapSortField);
        this.proof = JSON.toJSONString(proof, SerializerFeature.MapSortField);
    }

    public Presentation extractPresentation() throws SDKException {
        if (this.proof == null) {
            throw new SDKException("proof is null");
        }
        AnonymousProof proof = JSONObject.parseObject(this.proof, AnonymousProof.class, Feature.OrderedField);
        Presentation presentation = JSONObject.parseObject(proof.signature, Presentation.class, Feature.OrderedField);
        return presentation;
    }

    public boolean verifyPresentation(Library library, byte[] issuerPublic, byte[] accumulator, byte[] nonce) throws Exception {
        if (proof == null) {
            throw new SDKException("proof is null");
        }
        AnonymousProof proof = JSONObject.parseObject(this.proof, AnonymousProof.class, Feature.OrderedField);
        Presentation presentation = JSONObject.parseObject(proof.signature, Presentation.class, Feature.OrderedField);
        String presentationJSONStr = JSONObject.toJSONString(presentation, SerializerFeature.MapSortField);
        return library.verify(presentationJSONStr, issuerPublic, accumulator, nonce) == 0;
    }
}
