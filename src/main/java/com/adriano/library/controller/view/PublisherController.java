package com.adriano.library.controller.view;

import com.adriano.library.business.domain.entity.Publisher;
import com.adriano.library.business.logic.service.PublisherService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/publishers")
public class PublisherController extends BaseController<Publisher, Long> {

    public PublisherController(PublisherService service) {
        super(service, "publishers");
    }

    @Override
    protected Publisher newInstance() {
        return new Publisher();
    }
}

