namespace java com.fccfc.framework.config.api

// config 结构体
struct Config {
1: required string moduleCode;
2: required string configItemCode;
3: required string paramCode;
4: optional string paramValue;
}

service ConfigService {
	list<Config> queryAllConfig(1:string moduleCode);
	string queryConfig(1:string moduleCode, 2:string configItemCode, 3:string paramCode);
	void updateConfig(1:Config config);
	void addConfig(1:Config config);
}