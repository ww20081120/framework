'use strict';
let {Component} =  React;
const {Form, FormControl, Button} = ReactUI;

class OrgForm extends Component {

    checkCode(value, rule, deferred) {
        if (value === "") {
            return true;
        }
        $.ajax({
            url: BASEPATH + "/permission/org/checkCode/" + value,
            success: function (data) {
                if (data && data.code === 0 && data.checkCode === true) {
                    deferred.resolve(rule);
                } else {
                    deferred.reject(rule);
                }
            },
            error: function () {
                deferred.reject(rule);
            },
            dataType: "json"
        });
    }

    render() {
        let pOrgInfo = [];
        if (parentOrgId !== '') {
            pOrgInfo.push(<input type="hidden" name="parentOrgId" value={parentOrgId}/>);
            pOrgInfo.push(<FormControl name="parentOrgName" label="父级组织" type="text"
                                       labelClassName="col-md-3 col-sm-3 col-xs-12"
                                       wrapperClassName="col-md-6 col-sm-6 col-xs-12"
                                       readOnly={true} value={parentOrgName}/>);
        }
        return (
            <Form className="form-horizontal form-label-left" layout="aligned" action={BASEPATH + "/permission/org/add"}
                  method="post">
                <input type="hidden" name="e" value="page.fresh.org"/>
                {pOrgInfo}
                <FormControl label="组织代码*" name="orgCode" type="alphanum" val-require={true} val-require-msg="组织代码不能为空"
                             val-range-min={3} val-range-max={20} val-cc-fn={this.checkCode.bind(this)} val-cc-msg="组织代码已存在" labelClassName="col-md-3 col-sm-3 col-xs-12"
                             wrapperClassName="col-md-6 col-sm-6 col-xs-12"
                             placeholder="组织代码"/>
                <FormControl label="组织名称*" name="orgName" type="text" val-require={true} val-require-msg="组织名称不能为空"
                             val-range-min={2} val-range-max={60} labelClassName="col-md-3 col-sm-3 col-xs-12"
                             wrapperClassName="col-md-6 col-sm-6 col-xs-12"
                             placeholder="组织名称"/>
                <FormControl name="btn" wrapperClassName="col-md-9 col-sm-9 col-xs-12 col-md-offset-3">
                    <Button type="submit" className="btn-success">提交</Button>
                </FormControl>
            </Form>
        );
    }
}


ReactDOM.render(<OrgForm/>, document.querySelector('#formContent'));