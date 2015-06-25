namespace java com.fccfc.framework.message.api

// Attachment
struct Attachment {
1: optional i64 id;
2: required string type;
3: optional string name;
5: optional i64 fileSize;
6: required string url;
}

// Message 结构体
struct Message {
1: required list<string> receivers;
2: required string sender;
3: optional string subject;
4: optional string messageType;
5: optional string extendAttrs;
6: optional list<Attachment> attachments;
7: optional map<string, string> params;
}

service MessageService {
	i64 sendMessage(1:string templateCode, 2:Message message);
	
	void resendMessage(1:i64 messageId);
}