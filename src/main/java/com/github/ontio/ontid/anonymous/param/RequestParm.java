package com.github.ontio.ontid.anonymous.param;

public class RequestParm {
    public byte[] master_secret;
    public String attributes;
    public byte[] nonce;
    public byte[] issuer_public;

    public RequestParm(byte[] master_secret, String attributes, byte[] nonce, byte[] issuer_public) {
        this.master_secret = master_secret;
        this.attributes = attributes;
        this.nonce = nonce;
        this.issuer_public = issuer_public;
    }
}
