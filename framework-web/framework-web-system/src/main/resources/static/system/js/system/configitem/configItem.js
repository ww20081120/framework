"use strict";

const {Grid, Button, Message, Icon, Pager, DialogButton, Modal, Form, FormControl} = ReactUI;

let ConfigItem = React.createClass({

    // 在组件挂载之前调用一次。返回值将会作为 this.state 的初始值。
    getInitialState: function () {
        return {pageIndex: 1, pageSize: 15, totalCount: 0, data: []};
    },

    // 与 Ajax 交互
    componentDidMount: function () {
        this.queryConfigItem();
        $(document).on('click', '.configItem tbody >tr', function () {
            $('.configItem tbody >tr').css('background-color', '#fff')
            $(this).css('background-color', 'rgba(38, 185, 154, 0.16)');
        });
    },

    // 查询方法
    queryConfigItem: function () {
        $.ajax({
            url: BASEPATH + "/system/config/queryConfigItemPager",
            dataType: "json",
            data: {
                page: this.__pageIndex || this.state.pageIndex,
                pageSize: this.__pageSize || this.state.pageSize
            },
            success: function (d) {
                this.setState({
                    pageIndex: d.pageIndex,
                    pageSize: d.pageSize,
                    totalCount: d.totalCount,
                    data: d.data
                });
            }.bind(this)
        })
    },

    deleteConfigItem: function (configItemId) {
        if (configItemId) {
            Modal.confirm("确定要删除选定的配置项信息吗?", ()=> {
                $.ajax({
                    url: BASEPATH + "/system/config/item/remove",
                    method: 'post',
                    data: {
                        configItemId: configItemId
                    },
                    dataType: 'json',
                    success: function (data) {
                        if (data.code === 0) {
                            Message.show(<span><Icon icon="check"/> 删除配置项信息成功</span>, "success");
                            this.queryConfigItem();
                        } else {
                            Message.show(<span><Icon icon="frown-o"/> {data.message || "删除失败"}</span>, "error");
                        }
                    }.bind(this),
                    fail: function () {
                        Message.show(<span><Icon icon="frown-o"/> 删除失败</span>, "error");
                    }
                });
            });
        }
    },

    // 点击分页组件触发
    onConfigItemPagerClick: function (e, data) {
        this.__pageIndex = data.index;
        this.__pageSize = data.size;
        this.queryConfigItem();
    },

    // 点击一行 触发 事件
    onConfigItemRowClick: function (e, data) {
        // Modal.alert(data.configItemId);
        this.props.onConfigItemChange(data.configItemId);
    },

    // 自定义组件（操作中的 修改 与 删除）
    configItemTemplate: function (data) {
        return (
            <div className="btn-group">
                <DialogButton className="btn-link" title="修改配置项"
                              src={BASEPATH + "/system/config/item/toModify?configItemId=" + data.configItemId}>
                    <i className="fa fa-edit"></i></DialogButton>
                <button className="btn btn-link" type="button" title="删除"
                        onClick={function(e){e.stopPropagation(); this.deleteConfigItem(data.configItemId)}.bind(this)}>
                    <i className="fa fa-trash"></i>
                </button>
            </div>
        );
    },

    render: function () {
        const headers = [
            {name: 'moduleCode', sortAble: true, header: '业务模块代码'},
            {name: 'configItemCode', sortAble: true, header: '配置项代码'},
            {name: 'configItemName', sortAble: true, header: '配置项名称'},
            {name: 'configItemId', content: this.configItemTemplate, header: '操作'}
        ];

        return (
            <div className="x_panel">
                <div className="x_title">
                    <h2>
                        配置项配置信息
                        <small>ConfigItem information</small>
                    </h2>
                    <div className="clearfix"></div>
                </div>
                <div className="x_content">
                    <DialogButton className="btn-default" title="添加配置项"
                                  src={BASEPATH + "/system/config/item/toAdd" }><Icon icon="plus"/>&nbsp;
                        添加配置项</DialogButton>

                    <DialogButton className="btn-default" title="导入"
                                  src={BASEPATH + "/system/config/item/toImport" }><Icon icon="download"/>&nbsp;
                        导入</DialogButton>
                    <Button className="btn-default" title="导出"
                            onClick={()=>window.location.href= BASEPATH + "/system/config/item/expor" }><Icon
                        icon="upload"/>&nbsp;
                        导出</Button>
                    <Grid className="table-hover table-striped table-bordered jambo_table configItem" id="configItem"
                          data={this.state.data} onRowClick={this.onConfigItemRowClick}
                          headers={headers}/>
                    <Pager onChange={this.onConfigItemPagerClick} index={this.state.pageIndex}
                           size={this.state.pageSize}
                           total={this.state.totalCount}/>
                </div>
            </div>
        );
    }
});

// 配置项参数
let ConfigParam = React.createClass({

    // 在组件挂载之前调用一次。返回值将会作为 this.state 的初始值。
    getInitialState: function () {
        return {data: []};
    },

    componentWillReceiveProps: function () {
        this.configItemId = this.props.configItemId;
    },

    componentDidUpdate: function () {
        if (this.configItemId !== this.props.configItemId) {
            this.queryConfigParam();
            $(document).on('click', '.configParam tbody >tr', function () {
                $('.configParam tbody >tr').css('background-color', '#fff')
                $(this).css('background-color', 'rgba(38, 185, 154, 0.16)');
            });
        }
    },

    // 查询 配置项参数 Ajax
    queryConfigParam: function () {
        if (!!this.props.configItemId) {
            this.configItemId = this.props.configItemId;
            $.ajax({
                url: BASEPATH + "/system/config/queryItemParamPager",
                dataType: "json",
                data: {
                    page: this.__pageIndex || this.state.pageIndex,
                    pageSize: this.__pageSize || this.state.pageSize,
                    configItemId: this.props.configItemId,
                    paramCode: this.props.paramCode
                },
                success: function (d) {
                    this.__pageIndex = this.__pageSize = null;
                    this.setState(d);
                }.bind(this)
            });
        } else {
            this.setState({pageIndex: 1, pageSize: 20, totalCount: 0, data: []});
        }
    },

    checkConfigParam: function () {
        if (!this.props.configItemId) {
            Modal.alert("请先选择配置项！");
            return false;
        }
        return true;
    },

    deleteConfigParam: function (configItemId, paramCode) {
        if (configItemId && paramCode) {
            Modal.confirm("确定要删除选定的配置项参数信息吗?", ()=> {
                $.ajax({
                    url: BASEPATH + "/system/config/param/remove",
                    method: 'post',
                    data: {
                        configItemId: configItemId,
                        paramCode: paramCode
                    },
                    dataType: 'json',
                    success: function (data) {
                        if (data.code === 0) {
                            Message.show(<span><Icon icon="check"/> 删除配置参数成功</span>, "success");
                            this.queryConfigParam();
                        } else {
                            Message.show(<span><Icon icon="frown-o"/> {data.message || "删除失败"}</span>, "error");
                        }
                    }.bind(this),
                    fail: function () {
                        Message.show(<span><Icon icon="frown-o"/> 删除失败</span>, "error");
                    }
                });
            });
        }
    },

    onConfigParamPagerClick: function (e, data) {
        this.__pageIndex = data.index;
        this.__pageSize = data.size;
    },

    onConfigParamRowClick: function (e, data) {
        Modal.alert(data.configItemId + "  ==========================  " +data.paramCode);
        this.props.onConfigParamChange(data.configItemId, data.paramCode);
    },

    configParamTemplate: function (data) {
        return (
            <div className="btn-group">
                <DialogButton className="btn-link" title="修改配置参数"
                              src={BASEPATH + "/system/config/param/toModify?configItemId=" + data.configItemId + "&paramCode=" + data.paramCode}>
                    <i className="fa fa-edit"></i></DialogButton>
                <button className="btn btn-link" type="button" title="删除"
                        onClick={function(e){e.stopPropagation(); this.deleteConfigParam(data.configItemId, data.paramCode)}.bind(this)}>
                    <i className="fa fa-trash"></i></button>
            </div>
        );
    },

    render: function () {

        const headers = [
            {name: 'paramCode', sortAble: true, header: '参数编码'},
            {name: 'paramName', sortAble: true, header: '参数名称'},
            {name: 'paramValue', sortAble: true, header: '参数取值'},
            {name: 'defaultParamValue', sortAble: true, header: '缺省值'},
            {name: 'valueScript', sortAble: true, header: '取值校验规则'},
            {name: 'configItemId', content: this.configParamTemplate, header: '操作'}
        ];

        return (
            <div className="x_panel">
                <div className="x_title">
                    <h2>
                        配置项参数信息
                        <small>ConfigParam information</small>
                    </h2>
                    <div className="clearfix"></div>
                </div>
                <div className="x_content">
                    <DialogButton beforeShow={this.checkConfigParam} className="btn-default" title="添加参数"
                                  src={BASEPATH + "/system/config/param/toAdd?configItemId=" + this.props.configItemId }><Icon
                        icon="plus"/>&nbsp;添加参数</DialogButton>
                    <DialogButton className="btn-default" title="导入"
                                  src={BASEPATH + "/system/config/param/toImport" }><Icon icon="download"/>&nbsp;
                        导入</DialogButton>
                    <Button className="btn-default" title="导出"
                            onClick={()=>window.location.href= BASEPATH + "/system/config/param/export" }><Icon
                        icon="upload"/>&nbsp;
                        导出</Button>
                    <Grid className="table-hover table-striped table-bordered jambo_table configParam"
                          data={this.state.data}
                          headers={headers} onRowClick={this.onConfigParamRowClick}/>
                    <Pager onChange={this.onConfigParamPagerClick} index={this.state.pageIndex}
                           size={this.state.pageSize}
                           total={this.state.totalCount}/>
                </div>
            </div>
        );
    }
});

// 配置项数值
let ConfigValue = React.createClass({

    // 定义初始状态
    getInitialState: function () {
        return {data: []};
    },

    // 已加载组件收到新的参数时调用
    componentWillReceiveProps: function () {
        this.configItemId = this.props.configItemId;
        this.paramCode = this.props.paramCode;
    },


    render: function () {
        return (
            <div className="x_panel">
                <div className="x_title">
                    <h2>
                        配置项详细信息
                        <small>ConfigDetail Information</small>
                    </h2>
                    <div className="clearfix"></div>
                </div>
                <div className="x_content">
                    <Form className="form-horizontal form-label-left" layout="aligned"
                          action={BASEPATH + "/system/config/param/modify"}
                          method="post">

                        <input type="hidden" name="configItemId"/>
                        <input type="hidden" name="paramCode"/>

                        <FormControl labelClassName="col-md-3 col-sm-3 col-xs-12" name="key"
                                     wrapperClassName="col-md-6 col-sm-6 col-xs-12" value={this.props.configItemCode}
                                     label="KEY值" readOnly={true} placeholder="KEY值"/>

                        <FormControl labelClassName="col-md-3 col-sm-3 col-xs-12"
                                     wrapperClassName="col-md-6 col-sm-6 col-xs-12"
                                     name="value" label="取值" placeholder="取值"
                                     val-require={true} val-require-msg="配置项代码不能为空"/>

                        <FormControl labelClassName="col-md-3 col-sm-3 col-xs-12" type="textarea"
                                     wrapperClassName="col-md-6 col-sm-6 col-xs-12"
                                     name="remark" label="备注" placeholder="备注"/>

                        <FormControl name="btn" wrapperClassName="col-md-9 col-sm-9 col-xs-12 col-md-offset-3">
                            <Button type="submit" className="btn-success"> 修改</Button>

                        </FormControl>
                    </Form>
                </div>
            </div>
        );
    }
});

let ConfigManagerPage = React.createClass({

    // 初始化状态
    getInitialState: function () {
        return {};
    },

    onConfigItemChange: function (configItemId) {
        this.setState({configItemId: configItemId});
    },

    onConfigParamChange: function (configItemId, paramCode) {
        this.setState({configItemId: configItemId, paramCode: paramCode});
    },

    onConfigValueChange: function (configItemId, paramCode) {
        this.setState({configItemId: configItemId, paramCode: paramCode});
    },

    render: function () {
        return (
            <div className="row">
                <div className="col-md-5 col-sm-5 col-xs-12">
                    <ConfigItem onConfigItemChange={this.onConfigItemChange}/>
                </div>
                <div className="col-md-7 col-sm-7 col-xs-12">
                    <ConfigParam onConfigParamChange={this.onConfigParamChange} configItemId={this.state.configItemId}/>
                    <ConfigValue onConfigValueChange={this.onConfigValueChange} configItemId={this.state.configItemId}
                                 paramCode={this.state.paramCode}/>
                </div>
            </div>
        );
    }
});

ReactDOM.render(<ConfigManagerPage/>, document.querySelector('#config'));