package com.qinuo.coverter;

import com.qinuo.domain.QnDoctor;
import com.qinuo.entity.QnDoctorEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

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
}
