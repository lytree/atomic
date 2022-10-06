package top.lytree.annotations;


public enum ComplexConditionSign {

    //("精确比较")
    EQ,
    /**
     * like "%value%"
     */
    LIKE,
    /**
     * like "value%"
     */
    STARTING_WITH,
    /**
     * like "%value"
     */
    ENDING_WITH,
    /**
     * no like "%value%"
     */
    NOT_LIKE,
    //("大于")
    GT,
    //("小于")
    LT,
    //("大于等于")
    GTE,
    //("小于等于")
    LTE,
    //("在这部分参数中")
    IN,
    //("不等于")
    NEQ,
    //("是null")
    IS_NULL,
    //("不是null")
    IS_NOT_NULL,
}
