# xposed-dy 基于Xposed的抖音爬虫

### 主要功能列表
> - 采集指定达人最新视频的评论 done
> - 采集指定视频的评论 done
> - 采集直播间互动消息 done
> - 采集直播间礼物消息 done
> - dy限制后自动重启 done
> - dy风控后一键新机、清除缓存 doing
> - 支持与服务器交互 done
   - 获取指定的达人、视频列表
   - 上传视频评论消息、直播间消息

### 项目介绍
```
目标版本：dy 15.3.0
下载地址：http://www.anzhi.com/pkg/2332_com.ss.android.ugc.aweme.html
```
- aidl，跨进程大文本传输
- java
    - component，界面组件
    - floatwindow，dy爬虫控制悬浮窗
    - net，网络请求模块
    - persist，数据持久化-程序配置
    - repository，数据持久化-最新评论缓存
    - util，常用工具
    - xposed
     - HookMain，hook入口
     - HttpHelper，简易的http库，供hook模块使用
     - dy，dy hook部分
     - phone，一键新机
     - provider 跨进程共享
       - PropertyProvider，跨进程共享程序配置
       - DbProvider，跨进程共享视频评论缓存
     - receiver 跨进程通信
       - AppBroadcast，app对xp发送的广播
       - AppReceiver，AppBroadcast广播的接收器
       - XpBroadcast，xp对外发送的广播
       - XpReceiver，XpBroadcast广播的接收器


### hook-评论
```
达人uid -> 达人sec_uid -> 达人的视频列表 -> 视频的评论列表
com.ss.android.ugc.aweme.profile.api.o - ProfileManager
com.ss.android.ugc.aweme.profile.api.AwemeApi
com.ss.android.ugc.aweme.comment.api.CommentApi
```

### hook-直播消息
```
com.ss.ugc.live.sdk.message.MessageManager
    - dispatchMessage(IMessage iMessage)

com.bytedance.android.livesdkapi.depend.f.a - MessageType
    public enum MessageType {
        HELLO(0, "Hello"),
        SETTING(0, "Setting"),
        GET_SETTING(0, "GetSettting"),
        REQUEST_RECONNECT(0, "RequestReconnect"),
        DEFAULT(0, "--default--"),
        DIGG(0, "WebcastDiggMessage"),
        GIFT(0, "WebcastGiftMessage"), // 礼物消息
        GIFT_GROUP(0, "GiftGroupMessage"),
        GROUP_SHOW_USER_UPDATE(0, "WebcastGroupShowUserUpdateMessage"),
        EXHIBITION_TOP_LEFT(0, "WebcastExhibitionTopLeftMessage"),
        EXHIBITION_CHAT(0, "WebcastExhibitionChatMessage"),
        SYSTEM(0, "SystemMessage"),
        CHAT(0, "WebcastChatMessage"), // 互动消息
        ...
    }
```

### 风控处理
```
视频列表轻微风控，直接重启dy，重新开始
视频列表严重风控，自动清除缓存、一键新机，然后人工切换ip，模拟一个全新的运行环境
```

### 一键新机
```
hook Build、Build.VERSION、TelephonyManager、NetworkInfo、WifiInfo、Display
hook 隐藏类 SystemProperties
hook native层 __system_property_get
```

### 声明
```
本项目仅供学习使用，不用做任何其他途径
```
