package io.mustelidae.otter.lutrogale.common

enum class ErrorCode(val summary: String) {

    H000("Human error"),

    HD00("Data not found"),
    HD01("No results found"),
    HD02("Data mismatch uri"),

    HA00("Unauthorized"),
    HA01("Unauthorized Data Relation"),
    HA02("Un matched id / pw"),

    HI00("Invalid Input"),
    HI01("Invalid argument"),
    HI02("Invalid header argument"),
    HI03("Invalid Api Key"),

    P000("policy error"),
    PC01("checkout error"),
    PD01("develop mistake error"),
    PL01("duplicate request"),
    PL02("expired"),
    PL03("Project has more than one menuNavigations"),

    S000("common system error"),
    SA00("async execute error"),
    SI01("illegal state error"),
    SD01("database access error"),

    C000("communication error"),
    CT01("connection timeout"),
    CT02("read timeout"),
}
