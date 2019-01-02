package kr.ac.skuniv.cosmoslab.multifamilyedu.model.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * MultiFamilyEdu_Android
 * Class: WordInfoDto
 * Created by hsl95 on 2019-01-01.
 * <p>
 * Description:
 */
@Getter
@Setter
public class WordInfoDto {
    private List<String> wordlist;
    private Map<String, Boolean> wordpassinfo;

    public WordInfoDto() {
    }

    public WordInfoDto(List<String> wordlist, Map<String, Boolean> wordpassinfo) {
        this.wordlist = wordlist;
        this.wordpassinfo = wordpassinfo;
    }
}
