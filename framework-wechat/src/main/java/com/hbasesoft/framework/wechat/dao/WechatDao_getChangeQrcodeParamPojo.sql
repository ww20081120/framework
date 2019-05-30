SELECT
	id,
	qrcode_params_id,
	create_time,
	employee_code,
	app_id,
	subs_code,
	datas,
	org_code,
	type,
	garden_code,
	qrcode_url,
	is_used
FROM
	t_vcc_change_qrcode_param
WHERE
	1=1
#if($orgCode)
	AND org_code = :orgCode
#end
#if($appId)
	AND app_id = :appId
#end
#if($usedFlag)
	AND is_used = :usedFlag
#end
ORDER BY create_time
limit 1