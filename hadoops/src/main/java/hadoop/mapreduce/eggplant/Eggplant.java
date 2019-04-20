package hadoop.mapreduce.eggplant;

import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Eggplant implements WritableComparable<Eggplant> {

    private String user_id = "";
    private String cid_sn = "";
    private String mobile_data_type = "";
    private String os_ver = "";
    private String mac = "";
    private String resolution = "";
    private String commit_time = "";
    private String sdk_ver = "";
    private String device_id_type = "";
    private String city = "";
    private String android_id = "";
    private String device_model = "";
    private String carrier = "";
    private String promotion_channel = "";
    private String app_ver_name = "";
    private String imei = "";
    private String app_ver_code = "";
    private String pid = "";
    private String net_type = "";
    private String device_id = "";
    private String app_device_id = "";
    private String release_channel = "";
    private String country = "";
    private String time_zone = "";
    private String os_name = "";
    private String manufacture = "";
    private String commit_id = "";
    private String app_token = "";
    private String account = "";
    private String app_id = "";
    private String build_num = "";
    private String language = "";

    public Eggplant() {
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCid_sn() {
        return cid_sn;
    }

    public void setCid_sn(String cid_sn) {
        this.cid_sn = cid_sn;
    }

    public String getMobile_data_type() {
        return mobile_data_type;
    }

    public void setMobile_data_type(String mobile_data_type) {
        this.mobile_data_type = mobile_data_type;
    }

    public String getOs_ver() {
        return os_ver;
    }

    public void setOs_ver(String os_ver) {
        this.os_ver = os_ver;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getCommit_time() {
        return commit_time;
    }

    public void setCommit_time(String commit_time) {
        this.commit_time = commit_time;
    }

    public String getSdk_ver() {
        return sdk_ver;
    }

    public void setSdk_ver(String sdk_ver) {
        this.sdk_ver = sdk_ver;
    }

    public String getDevice_id_type() {
        return device_id_type;
    }

    public void setDevice_id_type(String device_id_type) {
        this.device_id_type = device_id_type;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAndroid_id() {
        return android_id;
    }

    public void setAndroid_id(String android_id) {
        this.android_id = android_id;
    }

    public String getDevice_model() {
        return device_model;
    }

    public void setDevice_model(String device_model) {
        this.device_model = device_model;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getPromotion_channel() {
        return promotion_channel;
    }

    public void setPromotion_channel(String promotion_channel) {
        this.promotion_channel = promotion_channel;
    }

    public String getApp_ver_name() {
        return app_ver_name;
    }

    public void setApp_ver_name(String app_ver_name) {
        this.app_ver_name = app_ver_name;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getApp_ver_code() {
        return app_ver_code;
    }

    public void setApp_ver_code(String app_ver_code) {
        this.app_ver_code = app_ver_code;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getNet_type() {
        return net_type;
    }

    public void setNet_type(String net_type) {
        this.net_type = net_type;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getApp_device_id() {
        return app_device_id;
    }

    public void setApp_device_id(String app_device_id) {
        this.app_device_id = app_device_id;
    }

    public String getRelease_channel() {
        return release_channel;
    }

    public void setRelease_channel(String release_channel) {
        this.release_channel = release_channel;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTime_zone() {
        return time_zone;
    }

    public void setTime_zone(String time_zone) {
        this.time_zone = time_zone;
    }

    public String getOs_name() {
        return os_name;
    }

    public void setOs_name(String os_name) {
        this.os_name = os_name;
    }

    public String getManufacture() {
        return manufacture;
    }

    public void setManufacture(String manufacture) {
        this.manufacture = manufacture;
    }

    public String getCommit_id() {
        return commit_id;
    }

    public void setCommit_id(String commit_id) {
        this.commit_id = commit_id;
    }

    public String getApp_token() {
        return app_token;
    }

    public void setApp_token(String app_token) {
        this.app_token = app_token;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getBuild_num() {
        return build_num;
    }

    public void setBuild_num(String build_num) {
        this.build_num = build_num;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return user_id + "," + cid_sn + "," + mobile_data_type + "," + os_ver +
                "," + mac + "," + resolution + "," + commit_time +
                "," + sdk_ver + "," + device_id_type + "," + city +
                "," + android_id + "," + device_model + "," + carrier +
                "," + promotion_channel + "," + app_ver_name + "," + imei +
                "," + app_ver_code + "," + pid + "," + net_type +
                "," + device_id + "," + app_device_id + "," + release_channel +
                "," + country + "," + time_zone + "," + os_name +
                "," + manufacture + "," + commit_id + "," + app_token +
                "," + account + "," + app_id + "," + build_num +
                "," + language;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeUTF(this.user_id);
        output.writeUTF(this.cid_sn);
        output.writeUTF(this.mobile_data_type);
        output.writeUTF(this.os_ver);
        output.writeUTF(this.mac);
        output.writeUTF(this.resolution);
        output.writeUTF(this.commit_time);
        output.writeUTF(this.sdk_ver);
        output.writeUTF(this.device_id_type);
        output.writeUTF(this.city);
        output.writeUTF(this.android_id);
        output.writeUTF(this.device_model);
        output.writeUTF(this.carrier);
        output.writeUTF(this.promotion_channel);
        output.writeUTF(this.app_ver_name);
        output.writeUTF(this.imei);
        output.writeUTF(this.app_ver_code);
        output.writeUTF(this.pid);
        output.writeUTF(this.net_type);
        output.writeUTF(this.device_id);
        output.writeUTF(this.app_device_id);
        output.writeUTF(this.release_channel);
        output.writeUTF(this.country);
        output.writeUTF(this.time_zone);
        output.writeUTF(this.os_name);
        output.writeUTF(this.manufacture);
        output.writeUTF(this.commit_id);
        output.writeUTF(this.app_token);
        output.writeUTF(this.account);
        output.writeUTF(this.app_id);
        output.writeUTF(this.build_num);
        output.writeUTF(this.language);
    }

    @Override
    public void readFields(DataInput input) throws IOException {
        this.user_id = input.readUTF();
        this.cid_sn = input.readUTF();
        this.mobile_data_type = input.readUTF();
        this.os_ver = input.readUTF();
        this.mac = input.readUTF();
        this.resolution = input.readUTF();
        this.commit_time = input.readUTF();
        this.sdk_ver = input.readUTF();
        this.device_id_type = input.readUTF();
        this.city = input.readUTF();
        this.android_id = input.readUTF();
        this.device_model = input.readUTF();
        this.carrier = input.readUTF();
        this.promotion_channel = input.readUTF();
        this.app_ver_name = input.readUTF();
        this.imei = input.readUTF();
        this.app_ver_code = input.readUTF();
        this.pid = input.readUTF();
        this.net_type = input.readUTF();
        this.device_id = input.readUTF();
        this.app_device_id = input.readUTF();
        this.release_channel = input.readUTF();
        this.country = input.readUTF();
        this.time_zone = input.readUTF();
        this.os_name = input.readUTF();
        this.manufacture = input.readUTF();
        this.commit_id = input.readUTF();
        this.app_token = input.readUTF();
        this.account = input.readUTF();
        this.app_id = input.readUTF();
        this.build_num = input.readUTF();
        this.language = input.readUTF();
    }


    @Override
    public int compareTo(Eggplant o) {
        return this.user_id.compareTo(o.user_id);
    }
}
