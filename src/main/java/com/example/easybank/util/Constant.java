package com.example.easybank.util;

public class Constant {
    private Constant() {
        throw new AssertionError("Cannot instantiate utility class");
    }

    // * Basic Routes
    public static final String API = "/api";
    public static final String AUTH = "/auth";
    public static final String BILL = "/bill";
    public static final String CARD = "/card";
    public static final String ACCOUNT = "/account";
    public static final String TRANSACTION = "/transaction";

    // * Method Routed
    public static final String REGISTER = "/register";
    public static final String LOGIN = "/login";
    public static final String WHO_AM_I = "/whoami";
    public static final String FIND_OWN = "/findown";
    public static final String PAY = "/pay";
    public static final String FIND_ALL = "/findall";
    public static final String FIND = "/find";

    public static final String CREATE = "/create";
    public static final String FIND_BY_ID = "/findbyid";
    public static final String DELETE = "/delete";
    public static final String EDIT = "/edit";
    public static final String DEPOSIT = "/deposit";

    public static final String ADMIN = "/admin";
    public static final String USER_LIST = "/userlist";
    public static final String CHANGE_ROLE = "/changerole";
    public static final String ID = "/{id}";


}
