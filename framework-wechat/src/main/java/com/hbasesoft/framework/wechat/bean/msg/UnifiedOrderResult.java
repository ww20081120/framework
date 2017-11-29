/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.wechat.bean.msg;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.hbasesoft.framework.db.core.BaseEntity;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2016年9月10日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.vcc.common.bean <br>
 */
@XmlRootElement(name = "xml")
public class UnifiedOrderResult extends BaseEntity {

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 1218161196369155932L;

    // 返回状态码
    private String return_code;

    // 返回信息
    private String return_msg;

    // 公众账号ID
    private String appid;

    // 商户号
    private String mch_id;

    // 随机字符串
    private String nonce_str;

    // 签名
    private String sign;

    // 业务结果
    private String result_code;

    // 错误代码
    private String err_code;

    // 错误代码描述
    private String err_code_des;

    private String trade_type;

    private String prepay_id;

    private String code_url;
    
    private String mweb_url;

    public String getReturn_code() {
        return return_code;
    }

    @XmlElement(name = "return_code")
    public void setReturn_code(String return_code) {
        this.return_code = return_code;
    }

    public String getReturn_msg() {
        return return_msg;
    }

    @XmlElement(name = "return_msg")
    public void setReturn_msg(String return_msg) {
        this.return_msg = return_msg;
    }

    public String getAppid() {
        return appid;
    }

    @XmlElement(name = "appid")
    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getMch_id() {
        return mch_id;
    }

    @XmlElement(name = "mch_id")
    public void setMch_id(String mch_id) {
        this.mch_id = mch_id;
    }

    public String getNonce_str() {
        return nonce_str;
    }

    @XmlElement(name = "nonce_str")
    public void setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
    }

    public String getSign() {
        return sign;
    }

    @XmlElement(name = "sign")
    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getResult_code() {
        return result_code;
    }

    @XmlElement(name = "result_code")
    public void setResult_code(String result_code) {
        this.result_code = result_code;
    }

    public String getErr_code() {
        return err_code;
    }

    @XmlElement(name = "err_code")
    public void setErr_code(String err_code) {
        this.err_code = err_code;
    }

    public String getErr_code_des() {
        return err_code_des;
    }

    @XmlElement(name = "err_code_des")
    public void setErr_code_des(String err_code_des) {
        this.err_code_des = err_code_des;
    }

    public String getTrade_type() {
        return trade_type;
    }

    @XmlElement(name = "trade_type")
    public void setTrade_type(String trade_type) {
        this.trade_type = trade_type;
    }

    public String getPrepay_id() {
        return prepay_id;
    }

    @XmlElement(name = "prepay_id")
    public void setPrepay_id(String prepay_id) {
        this.prepay_id = prepay_id;
    }

    public String getCode_url() {
        return code_url;
    }

    @XmlElement(name = "code_url")
    public void setCode_url(String code_url) {
        this.code_url = code_url;
    }

    public String getMweb_url() {
        return mweb_url;
    }

    @XmlElement(name = "mweb_url")
    public void setMweb_url(String mweb_url) {
        this.mweb_url = mweb_url;
    }

    
}
