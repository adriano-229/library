package com.adriano.library.controller.view;

import com.adriano.library.business.domain.entity.Loan;
import com.adriano.library.business.logic.service.LoanService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/loans")
public class LoanController extends BaseController<Loan, Long> {

    public LoanController(LoanService service) {
        super(service, "loans");
    }

    @Override
    protected Loan newInstance() {
        return new Loan();
    }
}

