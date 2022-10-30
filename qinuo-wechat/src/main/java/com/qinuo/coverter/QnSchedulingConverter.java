package com.qinuo.coverter;

import com.qinuo.domain.QnScheduling;
import com.qinuo.entity.QnSchedulingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * QnScheduling <==> QnSchedulingEntity 转换器 
 */
@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE)
public abstract class   QnSchedulingConverter {

    public static final QnSchedulingConverter INSTANCE = Mappers.getMapper(QnSchedulingConverter.class);

    public abstract QnScheduling toQnScheduling(QnSchedulingEntity qnSchedulingEntity);

    public abstract  QnSchedulingEntity toQnSchedulingEntity(QnScheduling qnScheduling);
}
