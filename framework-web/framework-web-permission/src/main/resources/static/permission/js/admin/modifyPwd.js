'use strict';
let {Component} =  React;
const {Form, FormControl, Button} = ReactUI;

let InfoModifyPwdPage = React.createClass({
    getInitialState:function(){
        return {oldPwdCheck:false,oldPwdCheckTime:new Date().getTime()};
    },

    checkPwd:function(val, rule, defferred){
        //检查原密码
        if(new Date().getTime() - this.state.oldPwdCheckTime < 2000){
            return this.state.oldPwdCheck;
        }

        $.ajax({
            url:BASEPATH+"/permission/admin/checkPwd",
            method:"POST",
            async:false,
            data:{
                old_password:val,
            },
            success:function(data){
               this.setState({oldPwdCheck:data,oldPwdCheckTime:new Date().getTime()})
            }.bind(this)
        });
        return this.state.oldPwdCheck;
    },

    checkNewPwd:function(val,rule,deferred){
        let newPwd = $("input[name='new_password']").val();
        return newPwd === val;
    },


    render:function (){
        return (
            <Form className="form-horizontal form-label-left col-xs-12 navbar-text" layout="aligned" method="POST"
                  action={BASEPATH + "/permission/admin/modify/pwd"}>
                <input type="hidden" value={adminId} name="adminId"/>
                <FormControl type="password" labelClassName="col-md-4 col-sm-3 col-xs-5" wrapperClassName="col-md-4 col-sm-6 col-xs-12"
                             name="old_password" label="请输入原密码:"  val-require={true} placeholder={"请确认原密码"}
                             val-require-msg="原密码不能为空" val-self-fn={this.checkPwd}  val-self-msg="输入的密码与原密码不符合"/>
                <FormControl type="password" labelClassName="col-md-4 col-sm-3 col-xs-5" wrapperClassName="col-md-4 col-sm-6 col-xs-12"
                             name="new_password" label="请输入新密码" placeholder={"请输入新密码"} val-require={true}
                             val-require-msg="新密码不能为空"/>
                <FormControl type="password" labelClassName="col-md-4 col-sm-3 col-xs-5" wrapperClassName="col-md-4 col-sm-6 col-xs-12"
                             name="confirm_password" label="请确认密码:" placeholder={"请确认新密码"} val-require={true}
                             val-require-msg="确认密码不能为空" val-self-fn={this.checkNewPwd}  val-self-msg="两次输入的密码不一致"/>

                <FormControl name="btn" wrapperClassName="col-md-10 col-sm-9 col-xs-12 col-md-offset-4">
                    <Button type="submit" className="btn-success">  提交</Button>
                </FormControl>

            </Form>
        );
    }
});

ReactDOM.render(<InfoModifyPwdPage/>,document.querySelector(".row"));