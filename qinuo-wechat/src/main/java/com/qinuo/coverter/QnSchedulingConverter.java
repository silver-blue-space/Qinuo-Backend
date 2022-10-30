package com.qinuo.coverter;

import com.qinuo.domain.QnScheduling;
import com.qinuo.entity.QnSchedulingEntity;
import org.mapstruct.factory.Mappers;

/**
 * QnScheduling <==> QnSchedulingEntity 转换器 
 */
public interface  QnSchedulingConverter {

    QnSchedulingConverter INSTANCE = Mappers.getMapper(QnSchedulingConverter.class);

    QnScheduling toQnScheduling(QnSchedulingEntity qnSchedulingEntity);

    QnSchedulingEntity toQnSchedulingEntity(QnScheduling qnScheduling);
}
