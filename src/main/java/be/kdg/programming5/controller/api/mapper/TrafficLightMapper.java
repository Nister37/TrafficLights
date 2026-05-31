package be.kdg.programming5.controller.api.mapper;

import be.kdg.programming5.business.domain.TrafficLight;
import be.kdg.programming5.controller.api.dto.IntersectionTrafficLightDto;
import be.kdg.programming5.controller.api.dto.TrafficLightDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * MapStruct mapper for TrafficLight entity to DTO conversions.
 * Used for trivial mappings only. Complex mappings are handled manually.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TrafficLightMapper {

    @Mapping(target = "intersectionId", source = "intersection.id")
    TrafficLightDto toTrafficLightDto(TrafficLight trafficLight);

    List<TrafficLightDto> toTrafficLightDtoList(List<TrafficLight> trafficLights);

    @Mapping(target = "intersectionId", source = "intersection.id")
    @Mapping(target = "ownerUsername", source = "owner.username")
    IntersectionTrafficLightDto toIntersectionTrafficLightDto(TrafficLight trafficLight);

    List<IntersectionTrafficLightDto> toIntersectionTrafficLightDtoList(List<TrafficLight> trafficLights);
}

