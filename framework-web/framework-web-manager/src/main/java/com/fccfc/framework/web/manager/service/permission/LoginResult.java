package com.fccfc.framework.web.manager.service.permission;

/**
 * <Description> <br>
 *
 * @author wk <br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015/11/4 <br>
 * @see com.fccfc.framework.web.manager.constant <br>
 * @since V1.0<br>
 */
public enum LoginResult {

    LOGIN_SUCCESS(true, 10001, "登录成功"),

    SYSTEM_ERROR(false, -10000, "系统异常"),
    USER_INCORRECT(false, -10001, "用户名或密码错误"),
    USER_STATUS_INCORRECT(false, -10002, "用户账户状态不正确"),
    USER_PWD_EXPIRED(false, -10003, "密码已过期");

    private boolean result;
    private int code;
    private String msg;

    LoginResult(boolean result, int code, String msg) {
        this.result = result;
        this.code = code;
        this.msg = msg;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
