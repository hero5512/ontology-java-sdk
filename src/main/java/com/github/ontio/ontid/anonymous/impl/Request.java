package com.github.ontio.ontid.anonymous.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.parser.Feature;
import com.github.ontio.ontid.anonymous.Library;
import com.github.ontio.ontid.anonymous.param.RequestParm;


@JSONType(orders = {"attributes", "nonce", "nym", "zkp"})
public class Request {
    
    @JSONField(jsonDirect = true)
    public String attributes;
    public String nonce;
    public String nym;
    public String zkp;

    public Request() {
    }

    public Request(String attributes, String nonce, String nym, String zkp) {
        this.attributes = attributes;
        this.nonce = nonce;
        this.nym = nym;
        this.zkp = zkp;
    }

    /**
     * @param library
     * @param requestRaw
     * @throws Exception
     */
    public Request(Library library, RequestParm requestRaw) throws Exception {
        String requestJsonStr = library.make_issue_request(requestRaw.master_secret, requestRaw.attributes, requestRaw.nonce, requestRaw.issuer_public);
        Request request = JSONObject.parseObject(requestJsonStr, Request.class, Feature.OrderedField);
        this.attributes = request.attributes;
        this.nonce = request.nonce;
        this.nym = request.nym;
        this.zkp = request.zkp;
    }
}
