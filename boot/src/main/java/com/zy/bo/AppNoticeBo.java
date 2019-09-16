package com.zy.bo;

import com.gexin.rp.sdk.base.impl.Target;

import java.util.List;

public class AppNoticeBo {

    private List<Target> targets;
    private com.zy.bo.OaMessage message;

    public List<Target> getTargets() {
        return targets;
    }

    public void setTargets(List<Target> targets) {
        this.targets = targets;
    }

    public com.zy.bo.OaMessage getMessage() {
        return message;
    }

    public void setMessage(OaMessage message) {
        this.message = message;
    }
}
