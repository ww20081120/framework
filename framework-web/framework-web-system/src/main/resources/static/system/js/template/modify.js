'use strict';
let {Component} =  React;
const {Form, FormControl, Button,Icon} = ReactUI;

class TemplateForm extends Component {

    constructor(props){
        super(props);
        this.state = {};
    }

    render() {

        return (

            <Form className="form-horizontal form-label-left" layout="aligned" action={BASEPATH + "/system/messageTemplate/modify"}
                  method="post">
                <input type="hidden" value={template.messageTemplateId} name="messageTemplateId"/>
                <FormControl labelClassName="col-md-3 col-sm-3 col-xs-12" wrapperClassName="col-md-6 col-sm-6 col-xs-12"
                             name="messageTemplateCode" label="消息编码" placeholder="请输入消息编码" value={template.messageTemplateCode}
                             val-require={true} val-require-msg="消息编码不能为空" />
                <FormControl type="text" name="name" label="模板名称" labelClassName="col-md-3 col-sm-3 col-xs-12"
                             wrapperClassName="col-md-6 col-sm-6 col-xs-12" value={template.name}
                             val-require={true} val-require-msg="请输入模板名称" placeholder="请输入模板名称"/>
                <FormControl type="textarea" name="template" label="模板内容" labelClassName="col-md-3 col-sm-3 col-xs-12"
                             wrapperClassName="col-md-6 col-sm-6 col-xs-12" value={template.template}
                             val-require={true} val-require-msg="请输入模板内容" placeholder="请输入模板内容"/>

                <FormControl name="btn" wrapperClassName="col-md-9 col-sm-9 col-xs-12 col-md-offset-3">
                    <Button type="submit" className="btn-success ">  提交</Button>
                </FormControl>

            </Form>

        );
    }
}


ReactDOM.render(<TemplateForm/>, document.querySelector('.row'));