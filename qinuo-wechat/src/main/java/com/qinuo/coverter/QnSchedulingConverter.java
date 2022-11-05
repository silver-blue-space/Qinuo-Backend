package com.qinuo.coverter;

import com.qinuo.common.utils.StringUtils;
import com.qinuo.domain.QnScheduling;
import com.qinuo.domain.QnSchedulingBatchSave;
import com.qinuo.entity.QnSchedulingEntity;
import com.qinuo.utils.LocalDateTimeUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.Objects;


/**
 * QnScheduling <==> QnSchedulingEntity 转换器 
 */
@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE)
public abstract class   QnSchedulingConverter {


    public static final QnSchedulingConverter INSTANCE = Mappers.getMapper(QnSchedulingConverter.class);

    public abstract QnScheduling toQnScheduling(QnSchedulingEntity qnSchedulingEntity, Map<Long,String>  colorMap);
    @AfterMapping
    public void toQnScheduling(QnSchedulingEntity source, Map<Long,String> colorMap, @MappingTarget QnScheduling target) {

        target.setTicketCount(source.getTicketCount());
        if(Objects.nonNull(source.getAttendTime())){
            target.setAttendTime(LocalDateTimeUtils.localTimeToString(source.getAttendTime(),LocalDateTimeUtils.HH_MM));
        }
        if(Objects.nonNull(source.getFinishTime())){
            target.setFinishTime(LocalDateTimeUtils.localTimeToString(source.getFinishTime(),LocalDateTimeUtils.HH_MM));
        }
        if(Objects.nonNull(source.getSchedulDate())){
            target.setSchedulDate(LocalDateTimeUtils.localDateToString(source.getSchedulDate(),LocalDateTimeUtils.YYYY_MM_DD));
        }
        if(!CollectionUtils.isEmpty(colorMap) && colorMap.containsKey(source.getCourseId())){
            target.setBackgroundColor(colorMap.get(source.getCourseId()));
        }
    }

    public abstract  QnSchedulingEntity toQnSchedulingEntity(QnScheduling qnScheduling);
    @AfterMapping
    public void toQnSchedulingEntity(QnScheduling source, @MappingTarget QnSchedulingEntity target) {

        target.setTicketCount(source.getTicketCount());
        if(StringUtils.isNotEmpty(source.getAttendTime())){
            target.setAttendTime(LocalDateTimeUtils.stringToLocalTime(source.getAttendTime()));
        }
        if(StringUtils.isNotEmpty(source.getFinishTime())){
            target.setFinishTime(LocalDateTimeUtils.stringToLocalTime(source.getFinishTime()));
        }
        if(StringUtils.isNotEmpty(source.getSchedulDate())){
            target.setSchedulDate(LocalDateTimeUtils.stringToLocalDate(source.getSchedulDate()));
        }
    }

    public abstract QnSchedulingEntity batchSaveDto2Entity(QnSchedulingBatchSave saveDTO);
}
