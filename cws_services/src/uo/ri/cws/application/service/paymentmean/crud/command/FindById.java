package uo.ri.cws.application.service.paymentmean.crud.command;

import java.util.Optional;

import alb.util.assertion.ArgumentChecks;
import uo.ri.conf.Factory;
import uo.ri.cws.application.repository.PaymentMeanRepository;
import uo.ri.cws.application.service.BusinessException;
import uo.ri.cws.application.service.paymentmean.PaymentMeanCrudService.PaymentMeanDto;
import uo.ri.cws.application.util.DtoAssembler;
import uo.ri.cws.application.util.command.Command;
import uo.ri.cws.domain.PaymentMean;

public class FindById implements Command<Optional<PaymentMeanDto>> {

    private String paymentMeanId;
    PaymentMeanRepository paymentMeanRepository = Factory.repository
	    .forPaymentMean();

    public FindById(String id) {
	ArgumentChecks.isNotEmpty(id);
	this.paymentMeanId = id;
    }

    @Override
    public Optional<PaymentMeanDto> execute() throws BusinessException {
	Optional<PaymentMean> pm = paymentMeanRepository
		.findById(paymentMeanId);
	if (pm.isEmpty()) {
	    return Optional.empty();
	}

	return Optional.of(DtoAssembler.toDto(pm.get()));
    }

}
