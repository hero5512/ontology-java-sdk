package com.github.ontio.ontid.anonymous.param;

import com.alibaba.fastjson.JSON;
import com.github.ontio.ontid.anonymous.impl.Credential;

public class PresentationParam {
    public String credential;
    public byte[] masterSecret;
    public String attributes;
    public byte[] issuePublic;
    public byte[] accumulator;
    public byte[] nonce;

    public PresentationParam(Credential credential, byte[] masterSecret, String attributes, byte[] issuePublic, byte[] accumulator, byte[] nonce) {
        this.credential = JSON.toJSONString(credential);
        this.masterSecret = masterSecret;
        this.attributes = attributes;
        this.issuePublic = issuePublic;
        this.accumulator = accumulator;
        this.nonce = nonce;
    }
}
