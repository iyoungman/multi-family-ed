package kr.ac.skuniv.cosmoslab.multifamilyedu.model.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * MultiFamilyEdu_Android
 * Class: SignupDto
 * Created by hsl95 on 2018-11-27.
 * <p>
 * Description:
 */
@Getter
@Setter
public class SignupDto {
    private String userid;
    private String pw;
    private String name;
    private String mobile;

    public SignupDto(String userid, String pw, String name, String mobile) {
        this.userid = userid;
        this.pw = pw;
        this.name = name;
        this.mobile = mobile;
    }
}
