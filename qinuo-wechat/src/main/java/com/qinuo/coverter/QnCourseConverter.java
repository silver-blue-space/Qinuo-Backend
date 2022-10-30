package com.qinuo.coverter;

import com.qinuo.domain.QnCourse;
import com.qinuo.entity.QnCourseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE)
public abstract class QnCourseConverter {

    public static final QnCourseConverter INSTANCE = Mappers.getMapper(QnCourseConverter.class);

    /**
     * QnCourseEntity ===> QnCourse
     * @param entity
     * @return
     */
    public abstract QnCourse toQnCourse(QnCourseEntity entity);

    /**
     * QnCourse ===> QnCourseEntity
     * @param qnCourse
     * @return
     */
    public abstract QnCourseEntity toQnCourseEntity(QnCourse qnCourse);

}
