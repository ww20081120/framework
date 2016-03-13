'use strict';

const {Form, FormControl, Button,Icon} = ReactUI;

let ConfigItemForm = React.createClass({

    // 在组件挂载之前调用一次。返回值将会作为 this.state 的初始值。
    getInitialState: function () {
        return {directory: [], module:[]};
    },

    // 与 Ajax 交互
    componentDidMount: function () {
        this.queryDirectory();
        this.queryModule();
    },

    queryDirectory: function () {
        $.ajax({
            url: BASEPATH + "/system/config/queryDirectoryCode",
            dataType: "json",
            success: function (data) {
                this.setState({
                    directory: data
                });
            }.bind(this)
        })
    },

    queryModule: function () {
        $.ajax({
            url: BASEPATH + "/system/config/queryModuleCode",
            dataType: "json",
            success: function (data) {
                this.setState({
                    module: data
                });
            }.bind(this)
        })
    },

    render: function () {
        return (
            <Form className="form-horizontal form-label-left" layout="aligned"
                  action={BASEPATH + "/system/config/item/add"}
                  method="post">
                <FormControl type="select" name="directoryCode" label="所属目录" data={this.state.directory}
                             valueTpl="{directoryCode}"
                             optionTpl="{directoryName}" labelClassName="col-md-3 col-sm-3 col-xs-12"
                             wrapperClassName="col-md-6 col-sm-6 col-xs-12"
                             val-require={true} val-require-msg="请选择目录" placeholder="请选择..."/>

                <FormControl type="select" name="moduleCode" label="所属模块" data={this.state.module}
                             valueTpl="{moduleCode}"
                             optionTpl="{moduleName}" labelClassName="col-md-3 col-sm-3 col-xs-12"
                             wrapperClassName="col-md-6 col-sm-6 col-xs-12"
                             val-require={true} val-require-msg="请选择模块" placeholder="请选择..."/>

                <FormControl labelClassName="col-md-3 col-sm-3 col-xs-12" wrapperClassName="col-md-6 col-sm-6 col-xs-12"
                             name="configItemCode" label="配置项代码" placeholder="配置项代码"
                             val-require={true} val-require-msg="配置项代码不能为空"/>

                <FormControl labelClassName="col-md-3 col-sm-3 col-xs-12" wrapperClassName="col-md-6 col-sm-6 col-xs-12"
                             name="configItemName" label="配置项名称" placeholder="配置项名称"
                             val-require={true} val-require-msg="配置项名称不能为空"/>

                <FormControl type="select" name="isVisiable" label="是否可见" valueTpl="{isVisiable}"
                             optionTpl="{isVisiable}" labelClassName="col-md-3 col-sm-3 col-xs-12"
                             wrapperClassName="col-md-6 col-sm-6 col-xs-12" data={["Y","N"]}
                             val-require={true} val-require-msg="请选择是否可见" placeholder="请选择..."/>

                <FormControl labelClassName="col-md-3 col-sm-3 col-xs-12" type="textarea" wrapperClassName="col-md-6 col-sm-6 col-xs-12"
                             name="remark" label="备注" placeholder="备注"/>

                <FormControl name="btn" wrapperClassName="col-md-9 col-sm-9 col-xs-12 col-md-offset-3">
                    <Button type="submit" className="btn-success"> 提交</Button>

                </FormControl>

            </Form>
        );
    }
});

ReactDOM.render(<ConfigItemForm/>, document.querySelector('#formContent'));