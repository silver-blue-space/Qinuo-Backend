package com.qinuo.domain;


import com.qinuo.domain.QnDoctor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class QnDoctorDTO extends QnDoctor {

    /** 用户昵称 */
    private String doctorName;


}
