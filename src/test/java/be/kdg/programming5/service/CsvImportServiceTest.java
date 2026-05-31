package be.kdg.programming5.service;

import be.kdg.programming5.business.domain.Intersection;
import be.kdg.programming5.business.domain.TrafficLight;
import be.kdg.programming5.business.services.CsvImportService;
import be.kdg.programming5.business.services.IntersectionService;
import be.kdg.programming5.business.services.TrafficLightService;
import be.kdg.programming5.enums.Direction;
import be.kdg.programming5.enums.IntersectionTypes;
import be.kdg.programming5.enums.TrafficLightStatus;
import be.kdg.programming5.enums.TrafficLightType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class CsvImportServiceTest {

    @Mock
    private TrafficLightService trafficLightService;

    @Mock
    private IntersectionService intersectionService;

    @InjectMocks
    private CsvImportService csvImportService;

    @Test
    void importTrafficLightsAsyncShouldSaveValidRowsAndSkipInvalidRows() {
        Intersection intersection = new Intersection(
                50.0, 4.0, IntersectionTypes.CROSSROADS, 4,
                true, LocalDate.of(2020, 1, 1), true, "/images/test.png"
        );
        given(intersectionService.getIntersectionById(1)).willReturn(intersection);

        byte[] csvBytes = """
                status,installationDate,direction,type,rightArrow,intersectionId
                ACTIVE,2023-03-15,N,COLLISION,false,1
                INVALID,2023-03-15,N,COLLISION,false,1
                """.getBytes(StandardCharsets.UTF_8);

        csvImportService.importTrafficLightsAsync(csvBytes);

        ArgumentCaptor<TrafficLight> trafficLightCaptor = ArgumentCaptor.forClass(TrafficLight.class);
        then(trafficLightService).should(times(1)).addTrafficLight(trafficLightCaptor.capture());

        TrafficLight saved = trafficLightCaptor.getValue();
        assertEquals(TrafficLightStatus.ACTIVE, saved.getStatus());
        assertEquals(Direction.N, saved.getDirection());
        assertEquals(TrafficLightType.COLLISION, saved.getType());
        assertSame(intersection, saved.getIntersection());
    }
}
