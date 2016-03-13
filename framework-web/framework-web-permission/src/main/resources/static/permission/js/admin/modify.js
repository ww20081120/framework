'use strict';
let {Component} =  React;
const {Form, FormControl, Button} = ReactUI;

let InfoModifyPage = React.createClass({
    getInitialState:function(){
        return {gener:[{value:"O",name:"保密"},{value:"M",name:"男"},{value:"F",name:"女"}]};
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
            <Form className="form-horizontal form-label-left col-xs-12 navbar-text" layout="aligned" method="POST"
                action={BASEPATH + "/permission/admin/modify"}>
                <input type="hidden" value={admin.adminId} name="adminId"/>
                <FormControl type="text" labelClassName="col-md-4 col-sm-3 col-xs-5" wrapperClassName="col-md-4 col-sm-6 col-xs-12"
                             name="adminName" label="管理员姓名:" value={admin.adminName} val-require={true}
                             val-require-msg="管理员姓名不能为空"/>
                <FormControl type="select" labelClassName="col-md-4 col-sm-3 col-xs-5" wrapperClassName="col-md-4 col-sm-6 col-xs-12"
                             name="gener" label="性别:" data={this.state.gener} valueTpl="{value}" value={admin.gener}
                             optionTpl="{name}" placeholder={"请选择性别..."}
                             />
                <FormControl type="email" labelClassName="col-md-4 col-sm-3 col-xs-5" wrapperClassName="col-md-4 col-sm-6 col-xs-12"
                             name="email" label="电子邮箱:" value={admin.email}/>
                <FormControl type="tel" labelClassName="col-md-4 col-sm-3 col-xs-5" wrapperClassName="col-md-4 col-sm-6 col-xs-12"
                             name="phone" label="联系电话:" value={admin.phone}   max={11}/>
                <FormControl type="text" labelClassName="col-md-4 col-sm-3 col-xs-5" wrapperClassName="col-md-4 col-sm-6 col-xs-12"
                             name="address" label="联系地址:" value={admin.address} />
                <FormControl name="btn" wrapperClassName="col-md-10 col-sm-9 col-xs-12 col-md-offset-4">
                    <Button type="submit" className="btn-success">  提交</Button>
                </FormControl>

            </Form>
        );
    }
});

ReactDOM.render(<InfoModifyPage/>,document.querySelector(".row"));