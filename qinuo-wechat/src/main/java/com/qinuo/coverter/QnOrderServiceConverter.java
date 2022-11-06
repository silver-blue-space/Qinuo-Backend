package com.qinuo.coverter;


import com.qinuo.domain.QnOrder;
import com.qinuo.entity.QnOrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;


@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE)
public abstract class QnOrderServiceConverter {
    public static final QnOrderServiceConverter INSTANCE = Mappers.getMapper(QnOrderServiceConverter.class);


    /**
     * QnOrderEntity ===> QnOrder
     * @param entity
     * @return
     */
    public abstract QnOrder toQnOrder(QnOrderEntity entity);

    /**
     * QnOrder ===> QnOrderEntity
     * @param qnOrder
     * @return
     */
    public abstract QnOrderEntity toQnOrderEntity(QnOrder qnOrder);
}
