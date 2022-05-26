package epg.ddara_backend.controller;

import epg.ddara_backend.dto.ResponseDto;
import epg.ddara_backend.service.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
public class MainController {

    @Autowired
    MainService mainService;

    /**
     * 오늘 날짜 스케쥴 응답
     * @return
     */
    @GetMapping("/schedules/v1")
    public ResponseDto getSchedulesToday(){

        LocalDate date = LocalDate.now();
        String today = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        ResponseDto responseDto = mainService.getSchedules(today);

        return responseDto;
    }

    /**
     * 오늘 날짜 스케쥴 배치 및 응답
     * @return
     */
    @PutMapping("/schedules/v1")
    public ResponseDto putSchedulesToday(){

        ResponseDto responseDto = mainService.getSchedules();

        return responseDto;
    }

    /**
     * 타겟 날짜 스케쥴 응답
     * @param targetDay
     * @return
     */
    @GetMapping("/schedules/v1/{targetDay}")
    public ResponseDto getSchedulesTarget(@PathVariable("targetDay") String targetDay){

        ResponseDto responseDto = mainService.getSchedules(targetDay);

        return responseDto;
    }

    /**
     * 타겟 날짜 스케쥴로 배치 및 응답
     * @param targetDay
     * @return
     */
    @PutMapping("/schedules/v1/{targetDay}")
    public ResponseDto putSchedulesTarget(@PathVariable("targetDay") String targetDay){

        ResponseDto responseDto = mainService.putSchedules(targetDay);

        return responseDto;
    }
}
