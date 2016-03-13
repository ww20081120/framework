"use strict";

const {Component} = React;
const {Icon,DialogButton,Form,FormControl} = ReactUI;

let InfoPage = React.createClass({

    getInitialState:function(){
        return {};
    },

    getSexValue:function(){
        switch (admin.gener){
            case "M":
                return "男";
            case "F":
                return "女";
            case "O":
            default:
                return "保密";
        }
    },

    render:function (){
        return (
            <Form className="form-horizontal form-label-left col-xs-12 navbar-text" layout="aligned">
                <FormControl type="text" labelClassName="col-md-4 col-sm-3 col-xs-5" wrapperClassName="col-md-4 col-sm-6 col-xs-12"
                             name="adminName" label="管理员姓名:" value={admin.adminName} disabled={"disabled"}/>
                <FormControl type="text" labelClassName="col-md-4 col-sm-3 col-xs-5" wrapperClassName="col-md-4 col-sm-6 col-xs-12"
                             name="adminName" label="性别:" value={this.getSexValue()} disabled={"disabled"}/>
                <FormControl type="text" labelClassName="col-md-4 col-sm-3 col-xs-5" wrapperClassName="col-md-4 col-sm-6 col-xs-12"
                             name="adminName" label="电子邮箱:" value={admin.email} disabled={"disabled"}/>
                <FormControl type="text" labelClassName="col-md-4 col-sm-3 col-xs-5" wrapperClassName="col-md-4 col-sm-6 col-xs-12"
                             name="adminName" label="联系电话:" value={admin.phone} disabled={"disabled"}/>
                <FormControl type="text" labelClassName="col-md-4 col-sm-3 col-xs-5" wrapperClassName="col-md-4 col-sm-6 col-xs-12"
                             name="adminName" label="联系地址:" value={admin.address} disabled={"disabled"}/>
                <FormControl name="btn" wrapperClassName="col-md-10 col-sm-9 col-xs-12 col-md-offset-4">
                    <DialogButton className="btn-default" title="修改"
                                  src={BASEPATH + "/permission/admin/toModify?adminId="+admin.adminId}>
                        修改个人信息
                    </DialogButton>
                    <DialogButton className="btn-default col-md-offset-1" title="修改"
                                  src={BASEPATH + "/permission/admin/toModifyPwd?adminId="+admin.adminId}>
                       修改密码
                    </DialogButton>
                </FormControl>

            </Form>
        );
    }
});



ReactDOM.render(<InfoPage/>,document.querySelector('#mainContent'));