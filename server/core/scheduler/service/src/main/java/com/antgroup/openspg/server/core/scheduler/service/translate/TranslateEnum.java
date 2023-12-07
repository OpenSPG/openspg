package com.antgroup.openspg.server.core.scheduler.service.translate;

/**
 * Translate Enum
 *
 * @author yangjin
 * @Title: TranslateEnum.java
 * @Description:
 */
public enum TranslateEnum {
    /**
     * local dry run
     */
    LOCAL_DRY_RUN("localDryRunTranslate", "本地空跑任务"),
    /**
     * builder
     */
    KG_BUILDER("builderTranslate", "知识加工任务");

    private String type;
    private String description;

    TranslateEnum(String type, String description) {
        this.type = type;
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * get by name
     *
     * @param name
     * @return
     */
    public static TranslateEnum getByName(String name, TranslateEnum defaultValue) {
        for (TranslateEnum value : TranslateEnum.values()) {
            if (value.name().equalsIgnoreCase(name)) {
                return value;
            }
        }
        return defaultValue;
    }

    /**
     * get by name
     *
     * @param name
     * @return
     */
    public static TranslateEnum getByName(String name) {
        return getByName(name, null);
    }
}
