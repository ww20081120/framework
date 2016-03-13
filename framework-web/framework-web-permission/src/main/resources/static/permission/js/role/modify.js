'use strict';
let {Component} =  React;
const {Form, FormControl, Button,Icon} = ReactUI;

class RoleForm extends Component {

    constructor(props){
        super(props);
        this.state = {module:[]};
    }

    componentDidMount() {
        this.queryModule();

    }

    queryModule(){
        $.ajax({
            url: BASEPATH + "/common/module",
            dataType: "json",
            success: function (data) {
                this.setState({
                    module:data
                });

            }.bind(this)
        })

    }

    render() {

        return (

            <Form className="form-horizontal form-label-left" layout="aligned" action={BASEPATH + "/permission/role/modify"}
                  method="post">
                <input type="hidden" value={role.roleId} name="roleId"/>
                <FormControl labelClassName="col-md-3 col-sm-3 col-xs-12" wrapperClassName="col-md-6 col-sm-6 col-xs-12"
                             name="roleName" label="角色名称" placeholder="角色名称" value={role.roleName}
                             val-require={true} val-require-msg="角色名称不能为空"/>
                <FormControl type="select" name="moduleCode" label="所属模块" data={this.state.module} valueTpl="{moduleCode}" value={role.moduleCode}
                             optionTpl="{moduleName}" labelClassName="col-md-3 col-sm-3 col-xs-12" wrapperClassName="col-md-6 col-sm-6 col-xs-12"
                             val-require={true} val-require-msg="请选择模块" placeholder="请选择..."/>

                <FormControl name="btn" wrapperClassName="col-md-9 col-sm-9 col-xs-12 col-md-offset-3">
                    <Button type="submit" className="btn-success ">  提交</Button>
                </FormControl>

            </Form>

        );
    }
}


ReactDOM.render(<RoleForm/>, document.querySelector('.row'));