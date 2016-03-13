'use strict';

const {Form, FormControl, Button,Icon} = ReactUI;

let ConfigItemModify = React.createClass({

    // 在组件挂载之前调用一次。返回值将会作为 this.state 的初始值。
    getInitialState: function () {
        return {module: []};
    },

    // 与 Ajax 交互
    componentDidMount: function () {
        this.queryModule();
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
                  action={BASEPATH + "/system/config/param/modify"}
                  method="post">

                <input type="hidden" name="configItemId" value={configValue.configItemId}/>
                <input type="hidden" name="paramCode" value={configValue.paramCode}/>

                <FormControl labelClassName="col-md-3 col-sm-3 col-xs-12" wrapperClassName="col-md-6 col-sm-6 col-xs-12"
                             name="configItemCode" label="配置项代码" placeholder="配置项代码" value={configItem.configItemCode}
                             val-require={true} val-require-msg="配置项代码不能为空"/>

                <FormControl labelClassName="col-md-3 col-sm-3 col-xs-12" wrapperClassName="col-md-6 col-sm-6 col-xs-12"
                             name="configItemName" label="配置项名称" placeholder="配置项名称" value={configItem.configItemName}
                             val-require={true} val-require-msg="配置项名称不能为空"/>

                <FormControl type="select" name="moduleCode" label="所属模块" data={this.state.module} valueTpl="{moduleCode}" value={configItem.moduleCode}
                             optionTpl="{moduleName}" labelClassName="col-md-3 col-sm-3 col-xs-12" wrapperClassName="col-md-6 col-sm-6 col-xs-12"
                             val-require={true} val-require-msg="请选择模块"/>

                <FormControl type="select" name="isVisiable" label="是否可见" valueTpl="{isVisiable}" value={configItem.isVisiable}
                             optionTpl="{isVisiable}" labelClassName="col-md-3 col-sm-3 col-xs-12"
                             wrapperClassName="col-md-6 col-sm-6 col-xs-12" data={["Y","N"]}
                             val-require={true}/>

                <FormControl labelClassName="col-md-3 col-sm-3 col-xs-12" type="textarea" wrapperClassName="col-md-6 col-sm-6 col-xs-12" value={configItem.remark}
                             name="remark" label="备注" placeholder="备注"/>

                <FormControl name="btn" wrapperClassName="col-md-9 col-sm-9 col-xs-12 col-md-offset-3">
                    <Button type="submit" className="btn-success"> 提交</Button>

                </FormControl>

            </Form>
        );
    }
});

ReactDOM.render(<ConfigItemModify/>, document.querySelector('#formContent'));