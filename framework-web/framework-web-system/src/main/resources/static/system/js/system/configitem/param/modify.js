'use strict';

const {Form, FormControl, Button,Icon} = ReactUI;

let ConfigParamModify = React.createClass({

    // 在组件挂载之前调用一次。返回值将会作为 this.state 的初始值。
    getInitialState: function () {
        return {data: []};
    },

    render: function () {
        return (
            <Form className="form-horizontal form-label-left" layout="aligned"
                  action={BASEPATH + "/system/config/param/modify"}
                  method="post">

                <input type="hidden" name="configItemId" value={configParam.configItemId}/>
                <input type="hidden" name="paramCode" value={configParam.paramCode}/>

                <FormControl labelClassName="col-md-3 col-sm-3 col-xs-12" wrapperClassName="col-md-6 col-sm-6 col-xs-12"
                             name="paramName" label="参数名称" placeholder="参数名称" value={configParam.paramName}
                             val-require={true} val-require-msg="参数名称不能为空"/>

                <FormControl labelClassName="col-md-3 col-sm-3 col-xs-12" wrapperClassName="col-md-6 col-sm-6 col-xs-12"
                             name="dataType" label="数据类型" placeholder="数据类型" value={configParam.dataType}
                             val-require={true} val-require-msg="数据类型不能为空"/>

                <FormControl labelClassName="col-md-3 col-sm-3 col-xs-12" wrapperClassName="col-md-6 col-sm-6 col-xs-12"
                             name="inputType" label="输入方式" placeholder="输入方式" value={configParam.inputType}
                             val-require={true} val-require-msg="输入方式不能为空"/>

                <FormControl labelClassName="col-md-3 col-sm-3 col-xs-12" type="textarea" value={configParam.remark}
                             wrapperClassName="col-md-6 col-sm-6 col-xs-12"
                             name="remark" label="备注" placeholder="备注"/>

                <FormControl name="btn" wrapperClassName="col-md-9 col-sm-9 col-xs-12 col-md-offset-3">
                    <Button type="submit" className="btn-success"> 提交</Button>
                </FormControl>

            </Form>
        );
    }
});

ReactDOM.render(<ConfigParamModify/>, document.querySelector('#formContent'));