"use strict"

const {TreeGrid, Grid, DialogButton,Pager, Modal, Message, Icon} = ReactUI;

let Org = React.createClass({
    getInitialState: function () {
        return {data: []};
    },
    componentDidMount: function () {
        this.qryOrg();
        $(document).on('page.fresh.org', () => {
            this.qryOrg();
            $.utils.closePopup();
        }).on('click', '.tree-label', function(){
            $('.tree-label').css('background-color', '#fff');
            $(this).css('background-color', 'rgba(38, 185, 154, 0.16)');
        });

    },
    qryOrg: function () {
        $.ajax({
            url: BASEPATH + "/permission/org/list",
            success: (d) => {
                let data = $.utils.transData(d, 'orgId', 'parentOrgId', 'children');
                this.setState({data: data});
            },
            dataType: "json"
        });
    },
    deleteOrg: function (orgId) {
        if (orgId) {
            Modal.confirm("确定要删除选定的组织或部门吗?", ()=> {
                $.ajax({
                    url: BASEPATH + "/permission/org/remove/" + orgId,
                    method: 'post',
                    dataType: 'json',
                    success: function (data) {
                        if (data.code === 0) {
                            Message.show(<span><Icon icon="check"/> 删除组织或部门信息成功</span>, "success");
                            this.qryOrg();
                        } else {
                            Message.show(<span><Icon icon="frown-o"/> {data.message || "删除失败"}</span>, "error");
                        }
                    }.bind(this),
                    fail: function () {
                        Message.show(<span><Icon icon="frown-o"/> 删除失败</span>, "error");
                    }
                });
            });
        }
    },

    onOrgRowClick: function (data) {
        this.props.onOrgChange(data.orgId);
    },

    orgOperateTemplate: function (data) {
        return (
            <div className="btn-group">
                <DialogButton className="btn btn-link" title="新增部门"
                              src={BASEPATH + "/permission/org/toAdd?parentOrgId=" + data.orgId}><i
                    className="fa fa-plus"></i></DialogButton>
                <DialogButton className="btn btn-link" title="修改组织或部门"
                              src={BASEPATH + "/permission/org/toModify/" + data.orgId}><i
                    className="fa fa-edit"></i></DialogButton>
                <button onClick={function(e){e.stopPropagation();this.deleteOrg(data.orgId)}.bind(this)}
                        className="btn btn-link" type="button" title="删除组织或部门"><i className="fa fa-trash"></i></button>
            </div>
        );
    },

    render: function () {
        const headers = [
            {name: 'orgName', width: 240, sortAble: true, header: '组织机构名称'},
            {name: 'orgCode', width: 120, sortAble: true, header: '组织机构代码'},
            {name: 'orgId', content: this.orgOperateTemplate, header: '操作'}
        ];

        return (
            <div className="x_panel">
                <div className="x_title">
                    <h2>
                        组织列表
                        <small>organization list</small>
                    </h2>
                    <div className="clearfix"></div>
                </div>
                <div className="x_content">
                    <DialogButton className="btn btn-default" title="添加组织" src={BASEPATH + '/permission/org/toAdd'}><i
                        className="fa fa-plus"></i> 添加组织</DialogButton>

                    <TreeGrid ref="treeGrid" data={this.state.data} headers={headers} onClick={this.onOrgRowClick}
                              valueTpl="{orgId}" className="jambo_table" open={true}/>
                </div>
            </div>
        );
    }
});

let Duty = React.createClass({

    getInitialState: function () {
        return {pageIndex: 1, pageSize: 5, totalCount: 0, data: []};
    },

    componentDidMount: function () {
        $(document).on('page.fresh.duty', function () {
            this.qryDuty();
            $.utils.closePopup();
        }.bind(this)).on('click', '#dutyDiv table tbody tr', function(){
            $('#dutyDiv table tbody tr').css('background-color', '#fff');
            $(this).css('background-color', 'rgba(38, 185, 154, 0.16)');
        });
    },

    componentWillReceiveProps: function () {
        this.oldOrgId = this.props.orgId;
    },

    componentDidUpdate: function () {
        if (this.oldOrgId !== this.props.orgId) {
            this.qryDuty();
        }
    },

    qryDuty: function () {
        if (!!this.props.orgId) {
            this.oldOrgId = this.props.orgId;
            $.ajax({
                url: BASEPATH + "/permission/duty/list",
                data: {
                    page: this.__pageIndex || this.state.pageIndex,
                    pageSize: this.__pageSize || this.state.pageSize,
                    orgId: this.props.orgId
                },
                success: function (d) {
                    this.setState(d);
                }.bind(this)
            });
        } else {
            this.setState({pageIndex: 1, pageSize: 5, totalCount: 0, data: []});
        }
    },

    deleteDuty: function (dutyId) {
        if (dutyId) {
            Modal.confirm("确定要删除选定的岗位信息吗?", ()=> {
                $.ajax({
                    url: BASEPATH + "/permission/duty/remove/" + dutyId,
                    method: 'post',
                    dataType: 'json',
                    success: function (data) {
                        if (data.code === 0) {
                            Message.show(<span><Icon icon="check"/> 删除岗位信息成功</span>, "success");
                            this.qryDuty();
                        } else {
                            Message.show(<span><Icon icon="frown-o"/> {data.message || "删除失败"}</span>, "error");
                        }
                    }.bind(this),
                    fail: function () {
                        Message.show(<span><Icon icon="frown-o"/> 删除失败</span>, "error");
                    }
                });
            });
        }
    },

    onPagerClick: function (e, data) {
        this.__pageIndex = data.index;
        this.__pageSize = data.size;
        this.qryDuty();
    },

    checkParam: function (e) {
        if (!this.props.orgId) {
            Modal.alert("请先选择组织或部门！");
            return false;
        }
        return true;
    },

    onDutyRowClick: function (e, data) {
        this.props.onDutyChange(data.dutyId);
    },

    dutyOperateTemplate: function (data) {
        return (
            <div className="btn-group">
                <DialogButton className="btn btn-link" title="修改岗位"
                              src={BASEPATH + "/permission/duty/toModify/" + data.dutyId}><i className="fa fa-edit"></i></DialogButton>
                <button onClick={function(e){e.stopPropagation();this.deleteDuty(data.dutyId)}.bind(this)}
                        className="btn btn-link" type="button" title="删除岗位"><i className="fa fa-trash"></i></button>
            </div>
        );
    },

    render: function () {
        const headers = [
            {name: 'dutyName', sortAble: true, header: '岗位名称'},
            {name: 'orgName', sortAble: true, header: '归属部门'},
            {name: 'dutyId', content: this.dutyOperateTemplate, header: '操作'}
        ];

        return (
            <div className="x_panel">
                <div className="x_title">
                    <h2>
                        部门岗位
                        <small>departments and positions</small>
                    </h2>
                    <div className="clearfix"></div>
                </div>
                <div id="dutyDiv" className="x_content">
                    <DialogButton beforeShow={this.checkParam} className="btn btn-default" title="新增岗位"
                                  src={BASEPATH + '/permission/duty/toAdd?orgId=' + this.props.orgId}><i
                        className="fa fa-plus"></i> 新增岗位</DialogButton>
                    <Grid className="table-hover table-striped table-bordered jambo_table" data={this.state.data}
                          headers={headers} onRowClick={this.onDutyRowClick}/>
                    <Pager onChange={this.onPagerClick} index={this.state.pageIndex} size={this.state.pageSize}
                           total={this.state.totalCount}/>
                </div>
            </div>
        );
    }
});

let Employee = React.createClass({

    getInitialState: function () {
        return {pageIndex: 1, pageSize: 20, totalCount: 0, data: []};
    },

    componentWillReceiveProps: function () {
        this.oldOrgId = this.props.orgId;
        this.oldDutyId = this.props.dutyId;
    },

    componentDidUpdate: function () {
        if (this.oldOrgId !== this.props.orgId || this.oldDutyId !== this.props.dutyId) {
            this.qryEmployee();
        }
    },
    componentDidMount: function () {
        $(document).on('page.fresh.amdinGrid', function () {
            console.log("fresh.....");
            this.qryEmployee();
            $.utils.closePopup();
        }.bind(this));
    },

    qryEmployee: function () {
        if (!!this.props.orgId) {
            this.oldOrgId = this.props.orgId;
            this.oldDutyId = this.props.dutyId;
            $.ajax({
                url: BASEPATH + "/permission/admin/list",
                data: {
                    page: this.__pageIndex || this.state.pageIndex,
                    pageSize: this.__pageSize || this.state.pageSize,
                    orgId: this.props.orgId,
                    dutyId: this.props.dutyId
                },
                success: function (d) {
                    this.__pageIndex = this.__pageSize = null;
                    this.setState(d);
                }.bind(this)
            });
        } else {
            this.setState({pageIndex: 1, pageSize: 20, totalCount: 0, data: []});
        }
    },

    checkParam: function () {
        if (!this.props.dutyId) {
            Modal.alert("请先选择岗位！");
            return false;
        }
        return true;
    },

    onPagerClick: function (e, data) {
        this.__pageIndex = data.index;
        this.__pageSize = data.size;
        this.qryEmployee();
    },

    employeeOperateTemplate: function (data) {
        return (
            <div className="btn-group">
                <DialogButton beforeShow={this.checkParam} className="btn btn-link" title="修改员工信息"
                              src={BASEPATH + "/permission/admin/toModify/"}><i
                    className="fa fa-edit"></i></DialogButton>
                <button className="btn btn-link" type="button" title="删除员工信息"><i className="fa fa-trash"></i></button>
            </div>
        );
    },

    render: function () {
        const headers = [
            {name: 'adminName', sortAble: true, header: '管理员名称'},
            {name: 'gener', sortAble: true, header: '性别'},
            {name: 'email', sortAble: true, header: '电子邮件'},
            {name: 'phone', sortAble: true, header: '电话'},
            {name: 'createTime', sortAble: true, header: '创建时间'},
            {name: 'adminId', content: this.employeeOperateTemplate, header: '操作'}
        ];

        return (
            <div className="x_panel">
                <div className="x_title">
                    <h2>
                        员工信息
                        <small>employee information</small>
                    </h2>
                    <div className="clearfix"></div>
                </div>
                <div className="x_content">
                    <DialogButton beforeShow={this.checkParam} className="btn btn-default" title="新增员工信息"
                                  src={BASEPATH + '/permission/admin/toAdd?orgId=' + this.props.orgId + '&dutyId=' + this.props.dutyId}><i
                        className="fa fa-plus"></i> 新增员工</DialogButton>

                    <button className="btn btn-default" title="批量删除"><i className="fa fa-trash"></i>批量删除</button>

                    <Grid className="table-hover table-striped table-bordered jambo_table" data={this.state.data} selectAble={true}
                          headers={headers}/>
                    <Pager onChange={this.onPagerClick} index={this.state.pageIndex} size={this.state.pageSize}
                           total={this.state.totalCount}/>
                </div>
            </div>
        );
    }
});

let OrgManagerPage = React.createClass({

    getInitialState: function () {
        return {};
    },

    onOrgChange: function (orgId) {
        this.setState({orgId});
    },

    onDutyChange: function (dutyId) {
        this.setState({dutyId});
    },

    render: function () {
        return (
            <div className="row">
                <div className="col-md-7 col-sm-7 col-xs-12">
                    <Org onOrgChange={this.onOrgChange}/>
                </div>
                <div className="col-md-5 col-sm-5 col-xs-12">
                    <Duty onDutyChange={this.onDutyChange} orgId={this.state.orgId}/>
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <Employee orgId={this.state.orgId} dutyId={this.state.dutyId}/>
                </div>
            </div>
        );
    }
});

ReactDOM.render(<OrgManagerPage/>, document.querySelector('#org'));