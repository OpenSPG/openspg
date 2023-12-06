package com.antgroup.openspg.server.common.model.scheduler;

/**
 * Env
 *
 * @author yangjin
 * @Title: Env.java
 * @Description:
 */
public enum Env {

    /**
     * local
     */
    LOCAL("local"),

    /**
     * single
     */
    SINGLE("single"),

    /**
     * dev
     */
    DEV("dev"),

    /**
     * stable
     */
    STABLE("stable"),

    /**
     * pre
     */
    PRE("pre"),

    /**
     * gray
     */
    GRAY("gray"),

    /**
     * prod
     */
    PROD("prod");

    private String value;

    Env(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
