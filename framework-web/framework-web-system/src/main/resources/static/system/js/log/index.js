"use strict"

const {Grid,DialogButton,Icon,Pager} = ReactUI;

let Log  = React.createClass({
    getInitialState: function () {
        return {pageIndex: 1, pageSize: 10, totalCount: 0, data: []};
    },
    queryLog: function(){
        $.ajax({
            url: BASEPATH + "/system/operatelog/query",
            data:{
                page:  this.__pageIndex  || this.state.pageIndex,
                pageSize: this.__pageSize ||  this.state.pageSize
            },

            dataType: "json",
            success: function (d) {
                this.setState({
                    pageIndex: d.pageIndex,
                    pageSize: d.pageSize,
                    totalCount: d.totalCount,
                    data: d.data
                });
            }.bind(this)
        });
    },

    componentDidMount:function() {
        this.queryLog();
    },

    onPagerClick: function (e, data) {
        this.__pageIndex = data.index;
        this.__pageSize = data.size;
        this.queryLog();
    },

    render:function() {
        const headers = [
            {name: 'operateLogId', sortAble: true, header: '操作日志标识'},
            {name: 'eventName', header: '事件名称'},
            {name: 'ip', header: 'ip地址'},
            {name: 'adminName', header: '管理员姓名'},
            {name: 'paramsValue', header: '参数'},
            {name: 'createTime',content:'{createTime}', header: '创建时间'}
        ];
        return (
            <div className="row">
                <div className="x_panel">
                    <div className="x_content">

                        <DialogButton className="btn-default" title="导出" src={BASEPATH + "/system/operatelog/export" }>
                            <Icon icon="download"/>&nbsp;导出
                        </DialogButton>
                        <Grid className="table-hover table-striped table-bordered jambo_table" data={this.state.data} selectAble={true}
                              headers={headers}/>
                        <Pager onChange={this.onPagerClick} index={this.state.pageIndex} size={this.state.pageSize}
                               total={this.state.totalCount}/>
                    </div>
                </div>
            </div>
        );
    }
});

ReactDOM.render(<Log/>,document.querySelector('#operateLog'));