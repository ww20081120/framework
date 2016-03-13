"use strict";

const {Component} = React;
const {Grid,Button,Message,Icon,Pager,DialogButton,Filter,Modal} = ReactUI;



let  Role  = React.createClass({

    getInitialState:function() {

        return {pageIndex: 1, pageSize: 10, totalCount: 0, data: []};
    },

    componentDidMount:function() {
        this.queryRole();

    },

    queryRole:function() {
        $.ajax({
            url: BASEPATH + "/permission/role/query",
            dataType: "json",
            data:{
                page:  this.__pageIndex  || this.state.pageIndex,
                pageSize: this.__pageSize ||  this.state.pageSize,
                roleName:this.__roleName  || null
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

    deleteRole:function(roleIds) {
        if (roleIds){
            Modal.confirm("确定要删除选定的角色吗?",()=> {
                $.ajax({
                    url: BASEPATH + "/permission/role/remove/",
                    method: 'post',
                    data: {
                        ids: roleIds
                    },
                    dataType: 'json',
                    success: function (data) {
                        if (data.code === 0) {
                            Message.show("删除成功", "success");
                            this.queryRole();
                        } else {
                            Message.show("删除失败", "error");
                        }


                    }.bind(this),
                    fail: function () {
                        Message.show("删除失败", "error");
                    }
                });
            })
        }

    },

    onPagerClick:function(e, data) {
        e.stopPropagation();
        this.__pageIndex = data.index;
        this.__pageSize = data.size;
        this.queryRole();
    },

    onFilterChange:function(filter){
        //处理清空操作
        if(filter.length == 0){
            this.__roleName = "";
        }
        //过滤查询
        for(let f of filter){
            this["__"+ f.name] = f.value;
        }
        this.queryRole();
    },

    roleOperateTemplate:function(data) {
        return (
            <div className="btn-group">
                <DialogButton className="btn-link" title="修改"
                              src={BASEPATH + "/permission/role/toModify/"+data.roleId}>
                    <i className="fa fa-edit"></i>
                </DialogButton>
                <button className="btn btn-link" title="删除" type="button"
                        onClick={function(e){e.stopPropagation(); this.deleteRole(data.roleId)}.bind(this)}>
                            <i className="fa fa-trash"></i>
                </button>
            </div>
        );
    },



    render:function () {
        const headers = [
            {name: 'roleName', sortAble: true, header: '角色名称'},
            {name: 'moduleCode', sortAble: true, header: '模块代码'},
            {name: 'createTime', sortAble: true, header: '创建时间'},
            {name: 'roleId', content: this.roleOperateTemplate, header: '操作'}
        ];
        const  filterOptions = [{
            label: '角色名称',
            name: 'roleName',
            ops: ['like']
        }]
        //let pagination = <Pagination size={this.state.pageSize} total={this.state.totalCount}/>;
        //<Button className="btn-default" title="批量删除角色"><Icon icon="trash"></Icon>&nbsp;删除角色</Button>
        //<Table ref="table" selectAble={true} striped={true} bordered={true} data={this.state.data} headers={headers}
        //      pagination={pagination}/>

        //let pager = <Pager size={3} total={this.state.totalCount}/>;

        return (

            <div className="x_panel">
                <div className="x_content">
                    <DialogButton className="btn-default" title="新增" src={BASEPATH + "/permission/role/toAdd" }><Icon
                        icon="plus"></Icon>&nbsp;新增</DialogButton>
                    <Filter onFilter={this.onFilterChange} style={{marginBottom:3}} options={filterOptions} />
                    <Grid className="table-hover table-striped" data={this.state.data} headers={headers}
                          />
                    <Pager onChange={this.onPagerClick} size={this.state.pageSize} index={this.state.pageIndex} total={this.state.totalCount}/>;
                </div>
            </div>
        );
    }

});

let  RoleManagerPage = React.createClass( {

    getInitialState:function() {
        return  {};
    },

    onRoleChange:function (roleId) {
        this.state = {roleId: roleId};
    },


    render:function () {
        return (
            <div className="row">
                <Role onRoleChange={this.onRoleChange}/>
            </div>

        );
    },
});

ReactDOM.render(<RoleManagerPage/>, document.querySelector('#mainContent'));