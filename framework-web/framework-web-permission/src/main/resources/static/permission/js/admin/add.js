'use strict'

const {Form, FormControl, Icon, Button,Upload} = ReactUI;

let AdminForm = React.createClass({
    getInitialState: function () {
        return {gener:[{value:"O",name:"保密"},{value:"M",name:"男"},{value:"F",name:"女"}], headImg:""};
    },

    componentDidMount: function () {

    },

    uploadSuccess: function (d) {

    },


    render: function () {
        return (
            <Form className="form-horizontal form-label-left col-xs-12 navbar-text" action={BASEPATH + "/permission/admin/add"}
                  method="post">
                <input type="hidden" name="e" value="page.fresh.adminGrid"/>
                <input type="hidden" name="operatorPojo.dutyId" value={dutyId}/>
                <input type="hidden" name="headImg" value={this.state.headImg}/>


                <FormControl type="text" name="operatorPojo.userName" val-require val-require-msg="" val-minlen-length="2"
                                 val-maxlen-length="8" val-maxlen-msg="用户名长度过长" label="用户名*" placeholder="请输入用户名"
                             labelClassName="col-md-3 col-sm-3 col-xs-12" wrapperClassName="col-md-9 col-sm-9 col-xs-12"/>
                <FormControl type="password" name="operatorPojo.password" val-require val-require-msg=""
                             val-maxlen-length="14" label="密码*" placeholder="请输入密码" val-minlen-length="2"
                             labelClassName="col-md-3 col-sm-3 col-xs-12" wrapperClassName="col-md-9 col-sm-9 col-xs-12"/>
                <FormControl type="password" name="confpwd" val-require val-require-msg="" val-minlen-length="2"
                             val-maxlen-length="14" label="确认密码*" placeholder="请输入确认密码"
                             labelClassName="col-md-3 col-sm-3 col-xs-12" wrapperClassName="col-md-9 col-sm-9 col-xs-12"/>
                <FormControl type="text" name="adminName" val-require val-require-msg="" val-minlen-length="2"
                             val-maxlen-length="8" label="管理员姓名*" placeholder="请输入确认密码"
                             labelClassName="col-md-3 col-sm-3 col-xs-12" wrapperClassName="col-md-9 col-sm-9 col-xs-12"/>
                <FormControl type="select" labelClassName="col-md-4 col-sm-3 col-xs-5" wrapperClassName="col-md-4 col-sm-6 col-xs-12"
                             name="gener" label="性别:" data={this.state.gener} optionTpl="{name}" valueTpl="{value}" value="O"
                             placeholder={"请选择性别..."}/>
                <FormControl type="tel" labelClassName="col-md-4 col-sm-3 col-xs-5" wrapperClassName="col-md-4 col-sm-6 col-xs-12"
                             name="phone" label="联系电话:" value=""   max={11}/>
                <FormControl type="email" labelClassName="col-md-4 col-sm-3 col-xs-5" wrapperClassName="col-md-4 col-sm-6 col-xs-12"
                             name="email" label="电子邮箱:" value="" />
                <FormControl type="text" labelClassName="col-md-4 col-sm-3 col-xs-5" wrapperClassName="col-md-4 col-sm-6 col-xs-12"
                             name="address" label="联系地址:" value="" />
                <Upload action={ BASEPATH + "/common/resource/upload" } autoUpload={true}
                             labelClassName="col-md-4 col-sm-3 col-xs-5" wrapperClassName="col-md-4 col-sm-6 col-xs-12" onSuccess={this.uploadSuccess}
                             name="uploadHeadImg" accept="*" label="上传头像:" content={ <Button><Icon icon="upload" /> 选择文件</Button>} />
                <FormControl name="btn" wrapperClassName="col-md-9 col-sm-9 col-xs-12 col-md-offset-3" >
                    <Button type="submit" className="btn-success">提交</Button>
                </FormControl>
            </Form>
        );
    }
});

ReactDOM.render(<AdminForm/>, document.querySelector('#formContent'));