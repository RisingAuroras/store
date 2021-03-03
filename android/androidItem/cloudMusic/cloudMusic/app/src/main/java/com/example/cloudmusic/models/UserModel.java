package com.example.cloudmusic.models;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * 每一个Realm模型都相当于数据库中的一个表，模型中的每个字段都相当于数据库中的一列，所以每一个字段都可
 * 以添加若干属性（eg：主键）
 */
public class UserModel extends RealmObject {

    @PrimaryKey
    private String phone;//主键
    @Required
    private String password;//不能缺少的(非空？)


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
