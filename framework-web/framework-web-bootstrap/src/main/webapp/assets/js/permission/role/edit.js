(function($) {

	require(
			[ "../../assets/lib/kendoUI/js/kendo.validator.js",
					"../../assets/lib/kendoUI/js/kendo.dropdownlist.js",
					"../../assets/lib/kendoUI/js/kendo.treeview.js" ],
			function() {
				function transList2TreeData(a, idStr, pidStr, chindrenStr) {
					var r = [], hash = {}, id = idStr, pid = pidStr, children = chindrenStr, i = 0, j = 0, len = a.length;
					for (; i < len; i++) {
						a[i].expanded = true;
						hash[a[i][id]] = a[i];
					}
					for (; j < len; j++) {
						var aVal = a[j], hashVP = hash[aVal[pid]];
						if (hashVP) {
							!hashVP[children] && (hashVP[children] = []);
							hashVP[children].push(aVal);
						} else {
							r.push(aVal);
						}
					}
					return r;
				}

				var Fn = {
					moduleDataOption : {
						optionLabel : {
							moduleName : "请选择...",
							moduleCode : ""
						},
						dataTextField : "moduleName",
						dataValueField : "moduleCode",
						dataSource : {
							type : "json",
							transport : {
								read : BASEPATH + 'module'
							}
						},
						change : function(e) {
							$
									.get(
											BASEPATH + 'resourcesconfig',
											{
												moduleCode : this.value()
											},
											function(data) {
												var tab = $("#resourcestab")
														.empty();
												var content = $(
														"#resourcescontent")
														.empty();
												var firstResourceId;
												if (data) {
													$
															.each(
																	data,
																	function(
																			index,
																			d) {
																		var liclass = (index == 0) ? "active"
																				: "";
																		if (index == 0) {
																			firstResourceId = d.resourceId
																		}
																		tab
																				.append('<li class="'
																						+ liclass
																						+ '"><a href="#" data-toggle="changetab" data-modulecode="'
																						+ d.moduleCode
																						+ '" data-resourceId="'
																						+ d.resourceId
																						+ '">'
																						+ d.ruleName
																						+ '</a></li>');
																		content
																				.append('<div id=tree'
																						+ d.resourceId
																						+ '>');
																		if (d.rule) {
																			var ruleJson = JSON
																					.parse(d.rule);
																			if (ruleJson.show == "TREE") {
																				Fn
																						.loadTree(
																								d.resourceId,
																								ruleJson,
																								d.resourceType);

																			}
																		}
																	});
													$("#resourcescontent>div")
															.hide();
													$("#tree" + firstResourceId)
															.show();
												}
											});
						}

					},
					loadTree : function(resourceId, rule, resourceType) {
						$("#tree" + resourceId)
								.kendoTreeView(
										{
											checkboxes : {
												template : '<input type="checkbox"'
														+ '#$.each(PERMISSIONS,function(index, p) {'
														+ 'if(p.resourceId==item.'
														+ rule.id
														+ '&&'
														+ resourceType
														+ '== p.resourceType){#'
														+ 'checked '
														+ '#}})#'
														+ 'data-type="'
														+ resourceType
														+ '" data-toggle="checkcolumn" value="#: item.'
														+ rule.id + ' #" />',

												checkChildren : true
											},

											dataSource : {
												type : "json",
												transport : {
													read : {
														url : BASEPATH
																+ 'resourcesconfig/querySqlByid',
														data : {
															resourceId : resourceId
														}
													}
												},
												schema : {
													data : function(data) {
														var dd = transList2TreeData(
																data || [],
																rule.id,
																rule.pid,
																"items");
														return dd;
													},
													model : {
														children : "items"
													}
												}
											},
											dataTextField : rule.text
										});
					},
					submit : function() {
						var form = $("#modifyForm"), validator = form
								.kendoValidator().data("kendoValidator");
						if (validator.validate()) {
							var ids = [];
							$('input[data-toggle="checkcolumn"]:checked').each(
									function() {
										ids.push({
											"id" : this.value,
											"type" : $(this).data().type
										});
									});
							if (ids.length === 0) {
								alert("请选择菜单！");
							} else {
								$("input[name='resources']").val(
										JSON.stringify(ids));
								form.submit();
							}
						}
					},
					init : function() {
						$('input[data-toggle="checkcolumn"]:checked').each(
								function() {
									ids.push({
										"id" : this.value,
										"type" : $(this).data().type
									});
								});

						$("#modifyForm").kendoValidator();
						$('#submitBtn').on('click', this.submit);
						var kk = $("#moduleCode").kendoDropDownList(
								this.moduleDataOption)
								.data("kendoDropDownList");
						kk.bind("dataBound", function(e) {
							kk.select(function(dataItem) {
								if (dataItem.moduleCode === moduleCodeSel) {
									Fn.moduleDataOption.change.call({
										value : function() {
											return moduleCodeSel;
										}
									});
									return true;
								}
							});
						});
						$(document)
								.on(
										'click',
										'[data-toggle="changetab"]',
										function() {
											var data = $(this).data();
											var modulecode = data.modulecode;
											var resourceId = data.resourceid;
											$("#addForm .active").removeClass(
													"active");
											$(this).parent().addClass("active");
											$("#resourcescontent>div").hide();
											$("#tree" + resourceId).show();
										});

					}
				};
				$(Fn.init.bind(Fn))
			})
})(jQuery);