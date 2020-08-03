package com.github.ontio.ontid.anonymous.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.ontio.ontid.anonymous.Library;
import com.github.ontio.ontid.anonymous.param.PresentationParam;

@JSONType(orders = {"attributes", "proof"})
public class Presentation {

    @JSONField(jsonDirect = true)
    public String attributes;
    public String proof;

    public Presentation() {
    }

    public Presentation(String attributes, String proof) {
        this.attributes = attributes;
        this.proof = proof;
    }

    /**
     * @param library
     * @param presentationParam
     * @throws Exception
     */
    public Presentation(Library library, PresentationParam presentationParam) throws Exception {
        String presentJsonStr = library.present(presentationParam.credential, presentationParam.masterSecret,
                presentationParam.attributes, presentationParam.issuePublic, presentationParam.accumulator, presentationParam.nonce);
        Presentation presentation = JSONObject.parseObject(presentJsonStr, Presentation.class, Feature.OrderedField);
        this.attributes = presentation.attributes;
        this.proof = presentation.proof;
    }

    /**
     * @param library
     * @param issuerPublic
     * @param accumulator
     * @param nonce
     * @return
     * @throws Exception
     */
    public boolean verify(Library library, byte[] issuerPublic, byte[] accumulator, byte[] nonce) throws Exception {
        String presentationStr = JSON.toJSONString(this, SerializerFeature.MapSortField);
        int ok = library.verify(presentationStr, issuerPublic, accumulator, nonce);
        return ok == 0;
    }
}
