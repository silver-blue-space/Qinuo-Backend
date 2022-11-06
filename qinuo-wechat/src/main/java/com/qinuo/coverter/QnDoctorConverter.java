package com.qinuo.coverter;

import com.qinuo.common.core.domain.entity.SysUser;
import com.qinuo.domain.QnDoctor;
import com.qinuo.entity.QnDoctorDTO;
import com.qinuo.entity.QnDoctorEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.util.CollectionUtils;

import java.util.Map;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE)
public abstract class QnDoctorConverter {

    public static final QnDoctorConverter INSTANCE = Mappers.getMapper(QnDoctorConverter.class);

    /**
     * QnDoctorEntity ===> QnDoctor
     * @param entity
     * @return
     */
    public abstract QnDoctor toQnDoctor(QnDoctorEntity entity);

    /**
     * QnDoctorEntity ===> QnDoctor
     * @param qnDoctor
     * @return
     */
    public  abstract  QnDoctorEntity toQnDoctorEntity(QnDoctor qnDoctor);

    /***
     *  qnDoctor ===>QnDoctorDTO
     * @param qnDoctor
     * @param listUser
     * @return
     */
    public abstract QnDoctorDTO toQnDoctorDTO(QnDoctor qnDoctor, Map<Long, SysUser> listUser);
    @AfterMapping
    public void toQnDoctorDTO(QnDoctor source, Map<Long, SysUser> listMap, @MappingTarget QnDoctorDTO target) {
        if(!CollectionUtils.isEmpty(listMap) && listMap.containsKey(source.getSysUserId())){
            target.setDoctorName(listMap.get(source.getSysUserId()).getNickName());
        }
    }
}
