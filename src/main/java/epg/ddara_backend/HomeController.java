package epg.ddara_backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String main(){
        return "따뜻한 라디오 - BACKEND API";
    }
}
