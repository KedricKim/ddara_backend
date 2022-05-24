package epg.ddara_backend.controller;

import epg.ddara_backend.dto.ResponseDto;
import epg.ddara_backend.service.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @Autowired
    MainService mainService;

    @GetMapping("/get")
    public ResponseDto main(){
        ResponseDto asd = mainService.getApi();
        return asd;
    }
}
