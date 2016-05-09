namespace java com.hbasesoft.framework.message.api

// 附件
struct Attachment {
1: optional i64 id;  //附件id
2: required string type;  //附件类型  text、voice、vedio、image
3: optional string name; //附件名称
5: optional i64 fileSize;  //附件大小
6: required string url; //附件的地址
}

// Message 消息题
struct Message {
1: required list<string> receivers;  // 接受人
2: required string sender;  //发送人
3: optional string subject; //标题
4: optional string messageType;  //消息类型：
5: optional string extendAttrs; //扩展属性
6: optional list<Attachment> attachments; //附件列表
7: optional map<string, string> params; //消息模板的参数
}

// 发送消息的服务
service MessageService {

	// 发送短消息，   
	// templateCode是消息模板编号   
	// message是消息内容
	// 返回参数为消息的id
	i64 sendMessage(1:string templateCode, 2:Message message);
	
	// 重发消息
	void resendMessage(1:i64 messageId);
}