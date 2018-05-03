/*
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package com.hbasesoft.framework.job.manager.bean;

import java.io.Serializable;

import com.alibaba.fastjson.JSONObject;

/**
 * 注册中心配置.
 *
 * @author zhangliang
 */
public final class RegistryCenterConfiguration implements Serializable {

    private static final long serialVersionUID = -5996257770767863699L;

    private String id;

    private String name;

    private String zkAddressList;

    private String namespace;

    private String digest;

    private boolean activated;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getZkAddressList() {
        return zkAddressList;
    }

    public void setZkAddressList(String zkAddressList) {
        this.zkAddressList = zkAddressList;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

}
