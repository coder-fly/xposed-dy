package com.spark.xposeddy.repository.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Unique;

@Entity
public class CommentBean implements Comparable<CommentBean> {
    @Id(autoincrement = true)
    private Long id;
    @Unique
    private String awemeId; // 视频的id，存储每个视频最新的评论
    private String uid; // 抖音uid
    private String typeCode; // 类型编码
    private String acc; // 抖音账号
    private String nick; // 抖音昵称
    private String sex; // 性别，0未知，1男，2女
    private String age; // 年龄
    private String works_num; // 作品数量
    private String area; // 所在地
    private String source; // 来源 1新增粉丝，2新增视频评论，3直播间评论，4直播间下单，5直播间刷礼物
    private String contentID; // 评论id
    private String content; // 评论
    private String relevant; // 来源达人，来源视频，举例：来源达人：UID+昵称；来源视频：视频ID,
    private Long create_time; // 时间戳

    @Generated(hash = 1851604560)
    public CommentBean(Long id, String awemeId, String uid, String typeCode,
            String acc, String nick, String sex, String age, String works_num,
            String area, String source, String contentID, String content,
            String relevant, Long create_time) {
        this.id = id;
        this.awemeId = awemeId;
        this.uid = uid;
        this.typeCode = typeCode;
        this.acc = acc;
        this.nick = nick;
        this.sex = sex;
        this.age = age;
        this.works_num = works_num;
        this.area = area;
        this.source = source;
        this.contentID = contentID;
        this.content = content;
        this.relevant = relevant;
        this.create_time = create_time;
    }

    @Generated(hash = 373728077)
    public CommentBean() {
    }

    public CommentBean setId(Long id) {
        this.id = id;
        return this;
    }

    public CommentBean setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public CommentBean setTypeCode(String typeCode) {
        this.typeCode = typeCode;
        return this;
    }

    public CommentBean setAcc(String acc) {
        this.acc = acc;
        return this;
    }

    public CommentBean setNick(String nick) {
        this.nick = nick;
        return this;
    }

    public CommentBean setSex(String sex) {
        this.sex = sex;
        return this;
    }

    public CommentBean setAge(String age) {
        this.age = age;
        return this;
    }

    public CommentBean setWorks_num(String works_num) {
        this.works_num = works_num;
        return this;
    }

    public CommentBean setArea(String area) {
        this.area = area;
        return this;
    }

    public CommentBean setSource(String source) {
        this.source = source;
        return this;
    }

    public CommentBean setContent(String content) {
        this.content = content;
        return this;
    }

    public CommentBean setRelevant(String relevant) {
        this.relevant = relevant;
        return this;
    }

    public CommentBean setCreate_time(Long create_time) {
        this.create_time = create_time;
        return this;
    }

    public Long getId() {
        return this.id;
    }

    public String getUid() {
        return uid;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public String getAcc() {
        return acc;
    }

    public String getNick() {
        return nick;
    }

    public String getSex() {
        return sex;
    }

    public String getAge() {
        return age;
    }

    public String getWorks_num() {
        return works_num;
    }

    public String getArea() {
        return area;
    }

    public String getSource() {
        return source;
    }

    public String getContent() {
        return content;
    }

    public String getRelevant() {
        return relevant;
    }

    public Long getCreate_time() {
        return create_time;
    }

    public String getAwemeId() {
        return this.awemeId;
    }

    public CommentBean setAwemeId(String awemeId) {
        this.awemeId = awemeId;
        return this;
    }

    public String getContentID() {
        return this.contentID;
    }

    public CommentBean setContentID(String contentID) {
        this.contentID = contentID;
        return this;
    }

    @Override
    public String toString() {
        return "CommentBean{" +
                "id=" + id +
                ", awemeId='" + awemeId + '\'' +
                ", uid='" + uid + '\'' +
                ", typeCode='" + typeCode + '\'' +
                ", acc='" + acc + '\'' +
                ", nick='" + nick + '\'' +
                ", sex='" + sex + '\'' +
                ", age='" + age + '\'' +
                ", works_num='" + works_num + '\'' +
                ", area='" + area + '\'' +
                ", source='" + source + '\'' +
                ", contentID='" + contentID + '\'' +
                ", content='" + content + '\'' +
                ", relevant='" + relevant + '\'' +
                ", create_time='" + create_time + '\'' +
                '}';
    }

    @Override
    public int compareTo(CommentBean another) {
        int compare = 0;
        try {
            compare = (int) (another.create_time - this.create_time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return compare;
    }
}
