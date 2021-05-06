package quan.editor.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by quanchangnai on 2021/4/29.
 */
@RestController
@RequestMapping("/config")
public class ConfigController {

    @RequestMapping("/list")
    public Object list() {
        List<Map<String, String>> configs = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            configs.add(Map.of("name", "config" + i));
        }
        return configs;
    }

}
