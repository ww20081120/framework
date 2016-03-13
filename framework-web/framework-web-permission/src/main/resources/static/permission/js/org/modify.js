'use strict'

let {Component} =  React;
const {Form, FormControl, Button} = ReactUI;

var OrgForm = React.createClass({
    render: function () {
        var pOrgInfo = [];
        if (org.parentId) {
            pOrgInfo.push(<input type="hidden" name="parentOrgId" value={parentOrgId}/>);
            pOrgInfo.push(<FormControl name="parentOrgName" label="父级组织" type="text"
                                       labelClassName="col-md-3 col-sm-3 col-xs-12"
                                       wrapperClassName="col-md-6 col-sm-6 col-xs-12"
                                       readOnly={true} value={parentOrgName}/>);
        }
        return (
            <Form className="form-horizontal form-label-left" action={BASEPATH + "/permission/org/modify/"}
                  method="post">
                <input type="hidden" name="e" value="page.fresh.org"/>
                <input type="hidden" name="orgId" value={org.orgId}/>
                {pOrgInfo}
                <FormControl type="text" name="orgCode" readOnly={true} value={org.orgCode} label="组织代码*"
                             labelClassName="col-md-3 col-sm-3 col-xs-12"
                             wrapperClassName="col-md-9 col-sm-9 col-xs-12"/>
                <FormControl label="组织名称*" name="orgName" type="text" value={org.orgName} val-require={true}
                             val-require-msg="组织名称不能为空"
                             val-range-min={2} val-range-max={60} labelClassName="col-md-3 col-sm-3 col-xs-12"
                             wrapperClassName="col-md-6 col-sm-6 col-xs-12"
                             placeholder="组织名称"/>
                <FormControl name="btn" wrapperClassName="col-md-9 col-sm-9 col-xs-12 col-md-offset-3">
                    <Button type="submit" className="btn-success">提交</Button>
                </FormControl>
            </Form>
        );
    }
});

ReactDOM.render(<OrgForm/>, document.querySelector('#formContent'));