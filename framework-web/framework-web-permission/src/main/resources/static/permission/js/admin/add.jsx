;(function($){
	'use strict';
	var DutyForm = React.createClass({
		getInitialState : function(){
			return {roleData : []};
		},
		
		componentDidMount: function(){
			this.qryRole();
		},
		
		qryRole : function(){
			$.ajax({
				url : BASEPATH + "/permission/role/list",
				success : function(d){
					this.setState({roleData : d});
				}.bind(this),
				dataType : "json"
			});
		},
		render: function(){
			return (
				<Form className="form-horizontal form-label-left" action={BASEPATH + "/permission/duty/add"} method="post">
					<input type="hidden" name="e" value="page.fresh.duty" />
					<input type="hidden" name="orgId" value={orgInfo.orgId} />
					<Input type="text" disabled="disabled" value={orgInfo.orgName} label="所属组织或部门" labelClassName="col-md-3 col-sm-3 col-xs-12" wrapperClassName="col-md-9 col-sm-9 col-xs-12"/>
			        <Input isVal={true} type="text" name="dutyName" val-require val-require-msg="岗位名称不能为空" val-maxlen-length="60" val-maxlen-msg="岗位名称长度过长" label="岗位名称*" placeholder="岗位名称" labelClassName="col-md-3 col-sm-3 col-xs-12" wrapperClassName="col-md-9 col-sm-9 col-xs-12"/>
			        <Input isVal={true} type="select" name="roleList" field="roleId" text="roleName" data={this.state.roleData} val-require val-require-msg="角色不能为空" placeholder="请选择角色信息" label="岗位所属角色" labelClassName="col-md-3 col-sm-3 col-xs-12" wrapperClassName="col-md-9 col-sm-9 col-xs-12"/>
			        <Input type="submit" className="btn btn-success btn-lg" wrapperClassName="col-md-9 col-sm-9 col-xs-12 col-md-offset-3 col-sm-offset-3">提交</Input>
				</Form>
			);
		}
	});

	React.render(<DutyForm/> ,document.querySelector('#formContent'));
})(jQuery);