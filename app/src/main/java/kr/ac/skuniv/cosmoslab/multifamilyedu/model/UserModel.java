package kr.ac.skuniv.cosmoslab.multifamilyedu.model;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

/**
 * MultiFamilyEdu_Android
 * Class: UserModel
 * Created by youngjun on 2018-11-27.
 * <p>
 * Description:
 */
@Getter
@Setter
public class UserModel {
    @SerializedName("userid")
    private String id;
    @SerializedName("pw")
    private String pw;
    @SerializedName("name")
    private String name;
    @SerializedName("mobile")
    private String mobile;
    @SerializedName("level")
    private String level;
    @SerializedName("count")
    private String count;
}
