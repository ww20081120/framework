"use strict";

const {Component} = React;
const {Grid,Button,Message,Icon,Pager,DialogButton,Filter,Modal} = ReactUI;



let  MessageTemplate  = React.createClass({

    getInitialState:function() {

        return {pageIndex: 1, pageSize: 10, totalCount: 0, data: []};
    },

    componentDidMount:function() {
        this.queryMessageTemplate();

    },

    queryMessageTemplate:function() {
        $.ajax({
            url: BASEPATH + "/system/messageTemplate/list",
            dataType: "json",
            data:{
                page:  this.__pageIndex  || this.state.pageIndex,
                pageSize: this.__pageSize ||  this.state.pageSize
            },
            success: function (d) {
                this.__pageIndex = this.__pageSize = null;
                this.setState({
                    pageIndex: d.pageIndex,
                    pageSize: d.pageSize,
                    totalCount: d.totalCount,
                    data: d.data
                });

            }.bind(this)
        })
    },

    deleteMessageTemplate:function(messageIds) {
        if (messageIds){
            Modal.confirm("确定要删除选定的消息模板吗?",()=>{
                $.ajax({
                    url: BASEPATH + "/system/messageTemplate/remove/",
                    method: 'post',
                    data: {
                        ids: messageIds
                    },
                    dataType: 'json',
                    success: function (data) {
                        if(data.checkCode){
                            Message.show("删除成功","success");
                            this.queryMessageTemplate();
                        }else{
                            Message.show("删除失败", "error");
                        }
                    }.bind(this),
                    fail: function () {
                        Message.show("删除失败", "error");
                    }
                });
            });
        }
    },

    onPagerClick:function(e, data) {
        e.stopPropagation();
        this.__pageIndex = data.index;
        this.__pageSize = data.size;
        this.queryRole();
    },



    messageTemplateOperateTemplate:function(data) {
        return (
            <div className="btn-group">
                <DialogButton className="btn-link" title="修改"
                              src={BASEPATH + "/system/messageTemplate/toMod?messageTemplateId="+data.messageTemplateId}>
                    <i className="fa fa-edit"></i>
                </DialogButton>
                <button className="btn btn-link" title="删除" type="button"
                        onClick={function(e){e.stopPropagation(); this.deleteMessageTemplate(data.messageTemplateId)}.bind(this)}>
                    <i className="fa fa-trash"></i>
                </button>
            </div>
        );
    },



    render:function () {
        const headers = [
            {name: 'messageTemplateCode', sortAble: true, header: '消息模板编码'},
            {name: 'name', sortAble: true, header: '名称'},
            {name: 'template', sortAble: true, header: '模板内容'},
            {name: 'messageTemplateId', content: this.messageTemplateOperateTemplate, header: '操作'}
        ];
        const  filterOptions = [{
            label: '消息模板编码',
            name: 'messageTemplateCode',
            ops: ['like']
        },{
            label: '名称',
            name: 'name',
            ops: ['like']
        }]


        return (

            <div className="x_panel">
                <div className="x_content">
                    <DialogButton className="btn-default" title="新增" src={BASEPATH + "/system/messageTemplate/toAdd" }><Icon
                        icon="plus"></Icon>&nbsp;新增</DialogButton>
                    <Grid className="table-hover table-striped table-bordered jambo_table" data={this.state.data} headers={headers}
                    />
                    <Pager onChange={this.onPagerClick} size={this.state.pageSize} index={this.state.pageIndex} total={this.state.totalCount}/>;
                </div>
            </div>
        );
    }

});

let  MessageTemplateManagerPage = React.createClass( {

    getInitialState:function() {
        return  {};
    },

    onMessageTemplateChange:function (roleId) {
        this.state = {roleId: roleId};
    },


    render:function () {
        return (
            <div className="row">
                <MessageTemplate onMessageTemplateChange={this.onMessageTemplateChange}/>
            </div>

        );
    },
});

ReactDOM.render(<MessageTemplateManagerPage/>, document.querySelector('#mainContent'));