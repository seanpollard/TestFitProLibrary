package com.ifit.sfit.sparky;

/**
 * Created by jc.almonte on 7/30/14.
 */
public abstract class TestCommons {
    protected UpdateResultView listener;
    protected String res = "";

    public void setUpdateResultViewListener(UpdateResultView listener) {
        this.listener = listener;
    }

    public interface UpdateResultView {
        public void onUpdate(String msg);
    }

    public void appendMessage(String msg) {
        res += msg;
        listener.onUpdate(res);
    }
}
