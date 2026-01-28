### 特性

* 支持redis、内存等多种缓存实现，适应不同场景需求
* 支持注解和工具类两种方式操作缓存，使用方便
* 提供分布式锁和排它锁功能，满足并发控制需求
* 支持字节级别的缓存操作，提升性能

### 快速上手

1、在pom.xml中引入framework-cache-redis模块

```
<dependency>
	<groupId>com.hbasesoft.framework</groupId>
	<artifactId>framework-cache-redis</artifactId>
	<version>${project.parent.version}</version>
</dependency>
```

2、配置application.yml文件

```
cache: #缓存配置
  model: REDIS
  redis:
    address: 127.0.0.1:6379
```

### 注解使用缓存

在Public方法上使用@Cache注解，当请求该方法时会先检查缓存中是否有数据，有则直接返回，没有则执行方法。

> Cache.java

```
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Cache {
    /**
     * 缓存的key，支持Velocity语法，可以用@Key引用方法参数
     * 例如：key = "test:${key}"，其中${key}会被方法参数中@Key("key")的值替换
     */
    String key();

    /**
     * 缓存超时时间，单位秒，0为不超时
     * 如果设置为0，缓存永不过期，除非手动删除
     */
    int expireTime() default 0;
}
```

> @Key注解说明

```
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Key {
    /**
     * 参数名，用于在Velocity模板语法中引用
     * 例如：@Key("userId")的参数可以在缓存key中使用${userId}引用
     */
    String value() default "";
}
```

> TestServiceImpl.java

```
@Service
public class TestServiceImpl implements TestService {
    @Cache(key = "test:${key}", expireTime = 600)
    public String getTestContent(@Key("key") final String key) {
        System.out.println("执行方法");
        return DateUtil.getCurrentTimestamp() + ":" + key;
    }
}
```

缓存注解使用说明：
1. `key`参数支持Velocity模板语法，可以引用方法参数
2. `expireTime`参数设置缓存过期时间，单位为秒
3. 如果方法有返回值，框架会自动将其缓存；下次调用时直接从缓存获取

### 工具类使用缓存

通过com.hbasesoft.framework.cache.core.CacheHelper类获取com.hbasesoft.framework.cache.core.ICache对象

> CacheHelper.java

```
public final class CacheHelper {

    // ==================== 缓存操作 ====================
    /**
     * 获取缓存实例
     * 根据配置的cache.model自动选择缓存实现
     */
    public static ICache getCache();

    /**
     * 获取缓存数据，自动处理缓存逻辑
     * @param key 缓存key
     * @param invoker 缓存未命中时的回调函数
     * @return 缓存数据
     */
    public static <T> T getCacheData(final String key, final Invoker<T> invoker);

    /**
     * 获取缓存数据，自动处理缓存逻辑（带过期时间）
     * @param key 缓存key
     * @param seconds 过期时间（秒）
     * @param invoker 缓存未命中时的回调函数
     * @return 缓存数据
     */
    public static <T> T getCacheData(final String key, final int seconds, final Invoker<T> invoker);

    // ==================== 锁操作 ====================
    /**
     * 获取锁实例
     * @param lockName 锁名称
     * @return Lock对象
     */
    public static Lock getLock(final String lockName);

    /**
     * 执行需要加锁的代码块
     * @param key 锁key
     * @param seconds 锁超时时间（秒）
     * @param invoker 需要执行的业务逻辑
     * @return 执行结果
     */
    public static <T> T lock(final String key, final int seconds, final Invoker<T> invoker);

    // ==================== 工具方法 ====================
    /**
     * 构建缓存key
     * @param paths 路径参数，自动用分隔符连接
     * @return 构建好的key
     */
    public static String buildKey(final String... paths);
}
```

> ICache.java

```
public interface ICache {
    // ==================== 基础缓存操作 ====================
    <T> T get(String key); // 获取缓存
    <T> void put(String key, T t); // 设置缓存，默认不过期
    <T> void put(String key, int seconds, T t); // 设置缓存，并设置超时时间
    void remove(String key); // 删除缓存
    boolean hasKey(String key); // 检查key是否存在
    void clear(); // 清空所有缓存

    // ==================== 字节级缓存操作 ====================
    byte[] get(byte[] key); // 获取缓存（字节）
    void put(byte[] key, int seconds, byte[] value); // 设置缓存（字节）
    void remove(byte[] key); // 删除缓存（字节）

    // ==================== 节点缓存操作 ====================
    <T> Map<String, T> getNode(String nodeName, Class<T> clazz); // 获取整个节点的数据
    Map<String, String> getNode(String nodeName); // 获取整个节点的数据（String类型）
    <T> void putNode(String nodeName, Map<String, T> node); // 设置节点缓存，默认不过期
    <T> void putNode(String nodeName, int seconds, Map<String, T> node); // 设置节点缓存，并设置超时时间
    void removeNode(String nodeName); // 删除节点

    // ==================== 节点值操作 ====================
    <T> T getNodeValue(String nodeName, String key); // 获取节点中的某个值
    <T> void putNodeValue(String nodeName, String key, T t); // 设置节点中的某个值，默认不过期
    <T> void putNodeValue(String nodeName, int seconds, String key, T t); // 设置节点中的某个值，并设置超时时间
    void removeNodeValue(String nodeName, String key); // 删除节点中的某个值
    boolean hasNodeKey(String hashKey, String subTaskCode); // 检查节点中是否存在某个key

    // ==================== 计数器操作 ====================
    long increment(String key); // 计数器+1，默认不过期
    long increment(String key, long startNum); // 设置计数器初始值，默认不过期
    long increment(int seconds, String key); // 计数器+1，设置过期时间
    long increment(int seconds, String key, long startNum); // 计数器+1，设置过期时间和初始值

    // ==================== 原生缓存对象 ====================
    Object getNativeCache(); // 获取原生缓存对象
}
```

> 使用示例

```
@Service
public class CacheExampleService {

    /**
     * 使用工具类操作缓存
     */
    public String getCachedData(String key) {
        ICache cache = CacheHelper.getCache();

        // 从缓存获取数据
        String cachedValue = cache.get("myCacheKey:" + key);
        if (cachedValue != null) {
            return cachedValue;
        }

        // 缓存未命中，执行业务逻辑
        String result = performBusinessLogic(key);

        // 将结果放入缓存，设置10分钟过期
        cache.put("myCacheKey:" + key, 600, result);

        return result;
    }

    /**
     * 使用CacheHelper.getCacheData()方法简化缓存操作
     */
    public String getCachedDataWithHelper(String key) {
        return CacheHelper.getCacheData("myCacheKey:" + key, 600, () -> {
            // 缓存未命中时执行的业务逻辑
            return performBusinessLogic(key);
        });
    }

    /**
     * 使用节点方式操作缓存
     */
    public Map<String, String> getNodeData(String nodeName) {
        ICache cache = CacheHelper.getCache();

        // 获取整个节点的数据
        Map<String, String> nodeData = cache.getNode(nodeName, String.class);
        if (nodeData != null && !nodeData.isEmpty()) {
            return nodeData;
        }

        // 节点数据为空，重新加载
        Map<String, String> newData = loadNodeData(nodeName);

        // 将数据放入节点缓存
        cache.putNode(nodeName, 3600, newData); // 1小时过期

        return newData;
    }

    /**
     * 使用节点值操作
     */
    public String getNodeValue(String nodeName, String nodeKey) {
        ICache cache = CacheHelper.getCache();

        // 获取节点中的某个值
        String value = cache.getNodeValue(nodeName, nodeKey);
        if (value == null) {
            // 缓存未命中，执行业务逻辑
            value = loadNodeValue(nodeName, nodeKey);
            // 放入缓存，设置10分钟过期
            cache.putNodeValue(nodeName, 600, nodeKey, value);
        }

        return value;
    }

    /**
     * 使用计数器操作
     */
    public long incrementCounter(String counterKey) {
        ICache cache = CacheHelper.getCache();

        // 计数器+1，如果不存在则创建，设置1小时过期
        return cache.increment(3600, counterKey);
    }

    /**
     * 检查缓存是否存在
     */
    public boolean checkCacheExists(String key) {
        ICache cache = CacheHelper.getCache();
        return cache.hasKey(key);
    }

    /**
     * 清空缓存
     */
    public void clearAllCache() {
        ICache cache = CacheHelper.getCache();
        cache.clear();
    }

    private String performBusinessLogic(String key) {
        // 模拟业务逻辑
        return "Result for " + key;
    }

    private Map<String, String> loadNodeData(String nodeName) {
        // 模拟加载节点数据
        Map<String, String> data = new HashMap<>();
        data.put("key1", "value1");
        data.put("key2", "value2");
        return data;
    }

    private String loadNodeValue(String nodeName, String nodeKey) {
        // 模拟加载节点值
        return "Value for " + nodeName + ":" + nodeKey;
    }
}
```

### 分布式锁

当使用Redis作为缓存时，框架提供了基于Redis的分布式锁功能。我们提供了@CacheLock注解来使用该功能。

> CacheLock.java

```
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CacheLock {
    /**
     * 锁名称，用于标识锁的类型
     * 同一锁名称的多个方法会互斥执行
     */
    String value();

    /**
     * 附属关键字，用于区分不同的锁实例
     * 支持Velocity语法，可以用@Key引用方法参数
     * 例如：key = "${userId}"，会根据不同的userId创建不同的锁
     */
    String key();

    /**
     * 获取锁的超时时间（毫秒）
     * 如果超过这个时间还未获取到锁，会抛出异常
     * 默认2000毫秒（2秒）
     */
    int timeOut() default 2000;
}
```

> LuckDrawServiceImpl.java

```
@Service
public class LuckDrawService {
    @CacheLock(value = "ShakeActivity", key = "${activityCode}", timeOut = 5000)
    public int luckDraw(@Key("activityCode") final String activityCode, final String userCode) {
        // 抽奖逻辑
        // 在分布式环境下，同一时间只有一个线程能执行此方法
        return doLuckDraw(activityCode, userCode);
    }

    private int doLuckDraw(String activityCode, String userCode) {
        // 实际的抽奖实现
        return new Random().nextInt(100);
    }
}
```

除了使用注解，还可以直接使用API方式：

```
@Service
public class CacheLockExampleService {

    public void doSomethingWithLock(String key) {
        Lock lock = CacheHelper.getLock();
        String lockKey = "myLock:" + key;

        try {
            // 获取锁，等待最多5秒
            if (lock.tryLock(lockKey, 5000)) {
                try {
                    // 执行需要加锁的业务逻辑
                    performBusinessLogic(key);
                } finally {
                    // 释放锁
                    lock.unLock(lockKey);
                }
            } else {
                // 获取锁失败
                throw new RuntimeException("获取锁失败");
            }
        } catch (Exception e) {
            throw new RuntimeException("锁操作异常", e);
        }
    }

    private void performBusinessLogic(String key) {
        // 模拟业务逻辑
        System.out.println("执行业务逻辑: " + key);
    }
}
```

### 排它锁

框架还提供了@DulplicateLock注解来实现排它锁，用于防止重复执行相同的操作，例如防止重复提交。

> DulplicateLock.java

```
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DulplicateLock {
    /**
     * 锁名称，用于标识锁的类型
     * 同一锁名称的多个方法会防止重复执行
     */
    String name();

    /**
     * 附属关键字，用于区分不同的锁实例
     * 支持Velocity语法，可以用@Key引用方法参数
     * 例如：key = "${orderNo}"，会根据不同的orderNo防止重复提交
     */
    String key();

    /**
     * 锁过期时间（秒）
     * 锁在指定时间后自动失效，允许再次执行
     * 默认10秒
     */
    int expireTime() default 10;
}
```

> ZhanWeiZiServiceImpl.java

```
@Service
public class ZhanWeiZiService {
    @DulplicateLock(name = "seats", key = "${no}", expireTime = 30)
    public void rob(@Key("no") final int no, final String name) {
        // 占位逻辑
        // 在锁有效期内，相同no的请求不会重复执行
        doRob(no, name);
    }

    private void doRob(int no, String name) {
        // 实际的占位实现
        System.out.println(name + " 占据位置 " + no);
    }
}
```

### 缓存配置说明

框架支持多种缓存实现：

1. **Redis缓存** - 分布式缓存，适合集群环境
2. **内存缓存** - 本地缓存，性能最高但不支持分布式
3. **简单缓存** - 基础内存缓存实现

配置示例：

```
# Redis缓存配置
cache:
  model: REDIS
  redis:
    address: 127.0.0.1:6379
    # 可选配置
    # password: your_password
    # database: 0
    # timeout: 2000

# 内存缓存配置
cache:
  model: MEMORY

# 简单缓存配置
cache:
  model: SIMPLE
```

### 使用场景

#### 1. 数据库查询缓存
```java
@Service
public class UserService {

    @Cache(key = "user:${userId}", expireTime = 1800)
    public User getUserById(@Key("userId") Long userId) {
        return userDao.findById(userId);
    }

    @Cache(key = "user:username:${username}", expireTime = 1800)
    public User getUserByUsername(@Key("username") String username) {
        return userDao.findByUsername(username);
    }
}
```

#### 2. API接口缓存
```java
@Service
public class ApiService {

    @Cache(key = "api:weather:${city}", expireTime = 600)
    public WeatherData getWeatherData(@Key("city") String city) {
        return externalApi.getWeather(city);
    }

    @Cache(key = "api:exchange:rate", expireTime = 3600)
    public ExchangeRate getExchangeRate() {
        return externalApi.getExchangeRate();
    }
}
```

#### 3. 会话数据缓存
```java
@Service
public class SessionService {

    @Cache(key = "session:${sessionId}", expireTime = 3600)
    public SessionInfo getSessionInfo(@Key("sessionId") String sessionId) {
        return sessionRepository.findById(sessionId);
    }
}
```

#### 4. 商品信息缓存
```java
@Service
public class ProductService {

    @Cache(key = "product:${productId}", expireTime = 7200)
    public Product getProductDetail(@Key("productId") Long productId) {
        return productRepository.findById(productId);
    }

    @Cache(key = "category:products:${categoryId}", expireTime = 1800)
    public List<Product> getProductsByCategory(@Key("categoryId") Long categoryId) {
        return productRepository.findByCategory(categoryId);
    }
}
```

#### 5. 分布式锁使用示例
```java
@Service
public class OrderService {

    @CacheLock(value = "order:create", key = "${orderNo}", timeOut = 5000)
    public Order createOrder(@Key("orderNo") String orderNo, OrderDTO orderDTO) {
        // 创建订单逻辑，保证同一订单号不会被重复创建
        return orderRepository.save(order);
    }

    @DulplicateLock(name = "order:payment", key = "${orderNo}", expireTime = 30)
    public void processPayment(@Key("orderNo") String orderNo, PaymentDTO paymentDTO) {
        // 处理支付逻辑，防止重复支付
        paymentService.process(orderNo, paymentDTO);
    }
}
```

### 最佳实践

#### 1. **合理设置过期时间**
- 热点数据：较长时间缓存（30分钟-2小时）
- 一般数据：中等时间缓存（10-30分钟）
- 冷数据：短时间缓存（1-5分钟）
- 配置数据：可以长时间缓存（1小时以上）

#### 2. **避免缓存雪崩**
- 不同类型的数据设置不同的过期时间
- 在缓存key中添加随机后缀，避免同时失效
- 对热点数据使用多级缓存

#### 3. **使用分布式锁保护关键操作**
- 在分布式环境下使用@CacheLock保护资源竞争
- 对于防止重复操作，使用@DulplicateLock
- 设置合理的锁超时时间，避免死锁

#### 4. **监控缓存命中率**
- 定期检查缓存使用效果
- 对命中率低的缓存重新评估策略
- 监控缓存内存使用情况

#### 5. **避免缓存穿透**
- 对空结果也进行缓存，但设置较短过期时间（1-5分钟）
- 使用布隆过滤器预先判断数据是否存在
- 对不存在的key返回统一默认值

#### 6. **缓存命名规范**
- 使用有意义的key名称，便于维护
- key中使用冒号分隔不同层级
- 避免使用特殊字符和空格
- 使用统一的key前缀，便于管理

#### 7. **内存管理**
- 大数据量使用节点缓存，避免过多单个key
- 定期清理过期和不常用的缓存
- 监控内存使用，及时调整缓存策略

#### 8. **数据一致性**
- 重要数据变更时及时清除相关缓存
- 使用事务注解保证缓存和数据库的一致性
- 考虑使用CacheUpdate注解自动管理缓存

#### 9. **错误处理**
- 捕获缓存异常，降级到原始数据源
- 对缓存异常进行重试机制
- 记录缓存操作日志，便于排查问题

#### 10. **性能优化**
- 合理使用字节级缓存提升性能
- 批量操作使用节点缓存减少网络开销
- 使用CacheHelper.getCacheData()简化代码